package com.mc.payment.core.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.MerchantWalletSnapshotEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.mapper.MerchantWalletMapper;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.dto.CountAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantAvailableWalletDto;
import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.MerchantWalletStatusEnum;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.CommonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_wallet(商户钱包)】的数据库操作Service实现
 * @createDate 2024-08-14 13:56:23
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MerchantWalletServiceImpl extends ServiceImpl<MerchantWalletMapper, MerchantWalletEntity>
        implements MerchantWalletService {
    private final MerchantWalletLogService merchantWalletLogService;
    private final IAssetLastQuoteService assetLastQuoteService;
    private final IDepositRecordService depositRecordService;
    private final IMerchantWalletSnapshotService merchantWalletSnapshotService;

    @Override
    public BasePageRsp<MerchantWalletRsp> page(WalletPageReq req) {
        Page<MerchantWalletRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }

    /**
     * 获取一个可用的钱包
     * <p>
     * 优先可用余额最多,冻结金额最小的钱包
     *
     * @param getAvailableWalletDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MerchantWalletEntity getAvailableWallet(GetAvailableWalletDto getAvailableWalletDto) {
        MerchantWalletEntity walletEntity = this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                .eq(StrUtil.isNotBlank(getAvailableWalletDto.getMerchantId()), MerchantWalletEntity::getMerchantId, getAvailableWalletDto.getMerchantId())
                .eq(MerchantWalletEntity::getAssetType, getAvailableWalletDto.getAssetType())
                .eq(MerchantWalletEntity::getChannelSubType, getAvailableWalletDto.getChannelSubType())
                .eq(MerchantWalletEntity::getAssetName, getAvailableWalletDto.getAssetName())
                .eq(MerchantWalletEntity::getNetProtocol, getAvailableWalletDto.getNetProtocol())
                .eq(MerchantWalletEntity::getPurposeType, getAvailableWalletDto.getPurposeType())
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                .orderByDesc(MerchantWalletEntity::getBalance)
                .orderByAsc(MerchantWalletEntity::getFreezeAmount)
                .last("LIMIT 1"));
        if (getAvailableWalletDto.isLock() && walletEntity != null) {
            // 锁定钱包
            this.lockWallet(walletEntity.getId());
        }
        return walletEntity;
    }

    /**
     * 获取加密货币出金钱包
     * <p/>
     * 要获取两个,一个用于出金,一个用于手续费币种扣费
     *
     * @param merchantId
     * @param channelSubType
     * @param assetName
     * @param netProtocol
     * @param feeAssetName
     * @return left:出金钱包, right:手续费币种扣费钱包
     */
    @Override
    public CryptoWithdrawWalletRsp getCryptoWithdrawWalletAndFreeze(String merchantId, Integer channelSubType,
                                                                    String assetName, String netProtocol,
                                                                    String feeAssetName, BigDecimal amount, BigDecimal feeAmount) {
        log.info("getCryptoWithdrawWalletAndFreeze:{},{},{},{},{},{},{}", merchantId, channelSubType, assetName, netProtocol, feeAssetName, amount, feeAmount);
        CryptoWithdrawWalletRsp rsp = null;
        if (StrUtil.equals(assetName, feeAssetName)) {
            // 同种币结算手续费
            BigDecimal decimal = NumberUtil.add(amount, feeAmount);
            MerchantWalletEntity merchantWalletEntity = this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                    .eq(MerchantWalletEntity::getMerchantId, merchantId)
                    .eq(MerchantWalletEntity::getAssetType, 0)
                    .eq(MerchantWalletEntity::getChannelSubType, channelSubType)
                    .eq(MerchantWalletEntity::getAssetName, assetName)
                    .eq(MerchantWalletEntity::getNetProtocol, netProtocol)
                    .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode())
                    .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                    .geSql(MerchantWalletEntity::getBalance, decimal + "+freeze_amount")
                    .last("ORDER BY balance - freeze_amount DESC LIMIT 1"));
            if (merchantWalletEntity != null) {
                rsp = new CryptoWithdrawWalletRsp();
                rsp.setWalletId(merchantWalletEntity.getId());
                rsp.setFreezeWalletId(merchantWalletEntity.getId());
                rsp.setBalance(merchantWalletEntity.getBalance());
                rsp.setAccountId(merchantWalletEntity.getAccountId());
                rsp.setWalletAddress(merchantWalletEntity.getWalletAddress());
                this.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FREEZE, "", merchantWalletEntity.getId(), BigDecimal.ZERO, decimal, "出金冻结金额");
            }
        } else {
            List<MerchantWalletEntity> list = this.list(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                    .eq(MerchantWalletEntity::getMerchantId, merchantId)
                    .eq(MerchantWalletEntity::getAssetType, 0)
                    .eq(MerchantWalletEntity::getChannelSubType, channelSubType)
                    .eq(MerchantWalletEntity::getAssetName, assetName)
                    .eq(MerchantWalletEntity::getNetProtocol, netProtocol)
                    .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode())
                    .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                    .geSql(MerchantWalletEntity::getBalance, amount + "+freeze_amount")
                    .last("ORDER BY balance - freeze_amount DESC"));
            for (MerchantWalletEntity merchantWalletEntity : list) {
                // 测试环境 对erc20这个协议起了两个叫法  所有这里做了一个转换
                String feeNetProtocol = "Sepolia".equals(netProtocol) ? "ERC20" : netProtocol;
                MerchantWalletEntity freeMerchantWalletEntity = this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                        .eq(MerchantWalletEntity::getMerchantId, merchantId)
                        .eq(MerchantWalletEntity::getAccountId, merchantWalletEntity.getAccountId())
                        .eq(MerchantWalletEntity::getAssetType, 0)
                        .eq(MerchantWalletEntity::getChannelSubType, channelSubType)
                        .eq(MerchantWalletEntity::getAssetName, feeAssetName)
                        .eq(MerchantWalletEntity::getNetProtocol, feeNetProtocol)
                        .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode())
                        .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                        .geSql(MerchantWalletEntity::getBalance, feeAmount + "+freeze_amount")
                        .last("ORDER BY balance - freeze_amount DESC LIMIT 1"));
                if (freeMerchantWalletEntity != null) {
                    rsp = new CryptoWithdrawWalletRsp();
                    rsp.setWalletId(merchantWalletEntity.getId());
                    rsp.setFreezeWalletId(freeMerchantWalletEntity.getId());
                    rsp.setBalance(merchantWalletEntity.getBalance());
                    rsp.setAccountId(merchantWalletEntity.getAccountId());
                    rsp.setWalletAddress(merchantWalletEntity.getWalletAddress());
                    this.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FREEZE, "", merchantWalletEntity.getId(), BigDecimal.ZERO, amount, "出金冻结金额");
                    this.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FREEZE, "", freeMerchantWalletEntity.getId(), BigDecimal.ZERO, feeAmount, "出金手续费冻结金额");
                    break;
                }
            }
        }
        return rsp;
    }

    /**
     * 获取一个可用的出金钱包,并且冻结相应金额
     *
     * @param merchantId
     * @param channelSubType
     * @param assetName
     * @param netProtocol
     * @param amount
     * @return
     */
    @Override
    public MerchantWalletEntity getWithdrawWalletAndFreeze(String merchantId, Integer channelSubType, Integer assetType, String assetName,
                                                           String netProtocol, BigDecimal amount) {
        MerchantWalletEntity merchantWalletEntity = this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getMerchantId, merchantId)
                .eq(MerchantWalletEntity::getAssetType, assetType)
                .eq(MerchantWalletEntity::getChannelSubType, channelSubType)
                .eq(MerchantWalletEntity::getAssetName, assetName)
                .eq(MerchantWalletEntity::getNetProtocol, netProtocol)
                .in(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode(), PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode())
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                .geSql(MerchantWalletEntity::getBalance, amount + "+freeze_amount")
                .last("ORDER BY balance - freeze_amount DESC LIMIT 1"));
        if (merchantWalletEntity != null) {
            merchantWalletEntity.setFreezeAmount(merchantWalletEntity.getFreezeAmount().add(amount));
            this.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FREEZE, "", merchantWalletEntity.getId(), BigDecimal.ZERO, amount, "出金冻结金额");
        }
        return merchantWalletEntity;
    }

    /**
     * 获取一个可用的出金钱包
     *
     * @param withdrawalRecord
     * @return
     */
    @Override
    public MerchantWalletEntity getWithdrawWallet(WithdrawalRecordEntity withdrawalRecord) {
        return this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getMerchantId, withdrawalRecord.getMerchantId())
                .eq(MerchantWalletEntity::getAssetType, withdrawalRecord.getAssetType())
                .eq(MerchantWalletEntity::getChannelSubType, withdrawalRecord.getChannelSubType())
                .eq(MerchantWalletEntity::getAssetName, withdrawalRecord.getAssetName())
                .eq(MerchantWalletEntity::getNetProtocol, withdrawalRecord.getNetProtocol())
                .in(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode(), PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode())
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                .last("ORDER BY balance - freeze_amount DESC LIMIT 1"));
    }

    /**
     * 变更余额
     *
     * @param changeEventTypeEnum 事件类型
     * @param correlationId       关联id
     * @param walletId            钱包id
     * @param amount              变更金额,正数为增加,负数为减少
     * @param msg                 变更原因
     * @return
     */
    @Override
    public boolean changeBalance(ChangeEventTypeEnum changeEventTypeEnum, String correlationId, String walletId, BigDecimal amount, String msg) {
        if (amount == null) {
            throw new IllegalArgumentException("balance is null");
        }
        boolean update = this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getId, walletId)
                .setIncrBy(MerchantWalletEntity::getBalance, amount));
        if (update) {
            merchantWalletLogService.asyncSaveLog(MerchantWalletLogReq.builder()
                    .walletId(walletId)
                    .changeBalance(amount)
                    .changeFreezeAmount(BigDecimal.ZERO)
                    .walletUpdateMsg(msg)
                    .walletUpdateTime(new Date())
                    .correlationId(correlationId)
                    .changeEventTypeEnum(changeEventTypeEnum)
                    .build());
        }
        return update;
    }

    /**
     * 变更余额和冻结金额
     *
     * @param changeEventTypeEnum 事件类型
     * @param correlationId       关联id
     * @param walletId            钱包id
     * @param balance             变更余额,正数为增加,负数为减少
     * @param freezeAmount        变更冻结金额,正数为增加,负数为减少
     * @param msg                 变更原因
     * @return
     */
    @Override
    public boolean changeBalanceAndAmount(ChangeEventTypeEnum changeEventTypeEnum, String correlationId, String walletId, BigDecimal balance, BigDecimal freezeAmount, String msg) {
        if (balance == null || freezeAmount == null) {
            throw new IllegalArgumentException("balance or freezeAmount is null");
        }
        boolean update = this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getId, walletId)
                .setIncrBy(balance.compareTo(BigDecimal.ZERO) != 0, MerchantWalletEntity::getBalance, balance)
                .setIncrBy(freezeAmount.compareTo(BigDecimal.ZERO) != 0, MerchantWalletEntity::getFreezeAmount, freezeAmount));
        if (update) {
            merchantWalletLogService.asyncSaveLog(MerchantWalletLogReq.builder()
                    .walletId(walletId)
                    .changeBalance(balance)
                    .changeFreezeAmount(freezeAmount)
                    .walletUpdateMsg(msg)
                    .walletUpdateTime(new Date())
                    .correlationId(correlationId)
                    .changeEventTypeEnum(changeEventTypeEnum)
                    .build());
        }
        return update;
    }


    /**
     * 锁定钱包
     *
     * @param walletId
     * @return
     */
    @Override
    public boolean lockWallet(String walletId) {
        return this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getId, walletId)
                .set(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.LOCK_ING.getCode()));
    }

    /**
     * 解锁钱包并且进入冷却中
     *
     * @param walletIds
     * @return
     */
    @Override
    public boolean unlockAndCollWallet(List<String> walletIds) {
        return this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .in(MerchantWalletEntity::getId, walletIds)
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.LOCK_ING.getCode())
                .set(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.COOL_ING.getCode()));
    }

    /**
     * 解锁钱包并且进入冷却中
     *
     * @param walletIds   钱包id集合
     * @param coolingTime 冷却时间,单位:毫秒,设置为零则立即解冻,恢复到可用状态
     * @return
     */
    @Override
    public boolean unlockAndCollWallet(List<String> walletIds, long coolingTime) {
        Date deadline = new Date(System.currentTimeMillis() + coolingTime);
        int status = coolingTime >= 0 ? MerchantWalletStatusEnum.COOL_ING.getCode() : MerchantWalletStatusEnum.USED_WAIT.getCode();

        return this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .in(MerchantWalletEntity::getId, walletIds)
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.LOCK_ING.getCode())
                .set(MerchantWalletEntity::getStatus, status)
                .set(MerchantWalletEntity::getDeadline, deadline));
    }

    /**
     * 解除冷却中钱包
     *
     * @param walletIds
     * @return
     */
    @Override
    public boolean recoverCoolWallet(List<String> walletIds) {
        return this.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .in(MerchantWalletEntity::getId, walletIds)
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.COOL_ING.getCode())
                .set(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode()));
    }


    @Override
    public String generateWalletQRCode(GenerateWalletQRCodeReq req) {
        return QrCodeUtil.generateAsBase64(req.getWalletAddress(), new QrConfig(260, 260), "png");
    }

    @Override
    public List<WalletBalanceRsp> walletBalanceList(String merchantId, WalletBalanceReq req) {
        List<MerchantWalletEntity> list = this.list(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getMerchantId, merchantId)
                .eq(req.getAccountType() != null, MerchantWalletEntity::getPurposeType, req.getAccountType())
                .eq(StrUtil.isNotBlank(req.getAssetName()), MerchantWalletEntity::getAssetName, req.getAssetName())
                .eq(StrUtil.isNotBlank(req.getAssetName()), MerchantWalletEntity::getAssetName, req.getNetProtocol())
                .eq(StrUtil.isNotBlank(req.getWalletAddress()), MerchantWalletEntity::getAssetName, req.getWalletAddress())
        );
        return list.stream().map(WalletBalanceRsp::valueOf).toList();
    }

    /**
     * 查询账户的所有资产余额,并且换算成U
     *
     * @param accountIds
     * @return 余额 单位:U
     */
    @Override
    public BigDecimal queryBalanceSum(List<String> accountIds) {
        List<MerchantWalletEntity> walletEntityList = this.list(Wrappers.lambdaQuery(MerchantWalletEntity.class).in(MerchantWalletEntity::getAccountId, accountIds).gt(MerchantWalletEntity::getBalance, BigDecimal.ZERO));
        // key : assetName, value: balance
        Map<String, BigDecimal> assetBalanceMap = walletEntityList.stream().collect(Collectors.toMap(MerchantWalletEntity::getAssetName, MerchantWalletEntity::getBalance, BigDecimal::add));
        List<String> assetNameList = assetBalanceMap.keySet().stream().toList();
        Map<String, BigDecimal> symbolAndMaxPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(assetNameList);
        if (CollUtil.isNotEmpty(assetBalanceMap)) {
            assetBalanceMap.forEach((assetName, balance) -> {
//                BigDecimal usdtRate = assetLastQuoteService.getExchangeRate(assetName, true);
                BigDecimal usdtRate = CommonUtil.getRateByNameAndMap(assetName, symbolAndMaxPriceMap);
                assetBalanceMap.put(assetName, balance.multiply(usdtRate));
            });
            return assetBalanceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 查询钱包余额, 根据条件查询
     *
     * @param accountIdSet
     * @param req
     * @return 余额 单位:U
     */
    @Override
    public List<MerchantWalletEntity> queryBalanceSumByAssetOrAddr(Set<String> accountIdSet, MerchantQueryReq req) {

        return baseMapper.queryBalanceSumByAssetOrAddr(accountIdSet, req);
    }

    @Override
    public List<WalletBalanceSumRsp> walletBalanceSum(String merchantId, WalletBalanceSumReq req) {
        return baseMapper.walletBalanceSum(merchantId, req);
    }

    /**
     * fireblocks通道的商户出金钱包,需要依托于通道钱包的余额进行更新余额
     *
     * @param channelWalletId 通道钱包id
     * @param logId           通道钱包变更日志id
     * @param balance         余额
     */
    @Override
    public void syncChannelFireBlocksWithdrawWalletBalance(String channelWalletId, String logId, BigDecimal balance) {
        MerchantWalletEntity merchantWalletEntity = this.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode())
                .eq(MerchantWalletEntity::getChannelWalletId, channelWalletId));
        if (merchantWalletEntity != null) {
            BigDecimal changeBalance = balance.subtract(merchantWalletEntity.getBalance());
            this.changeBalance(ChangeEventTypeEnum.FIREBLOCKS_SYNC, logId, merchantWalletEntity.getId(), changeBalance, "同步通道余额");
        }
    }

    @Override
    public void export(WalletPageReq req, HttpServletResponse response) {
        ClassPathResource resource = new ClassPathResource("/template/merchantWallet.xlsx");
        List<MerchantWalletExportRsp> list = baseMapper.queryExportInfo(req);
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "商户钱包" + formatDate + ".xlsx";
        try {
            InputStream merchantWalletStream = resource.getInputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(merchantWalletStream).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 统计可用钱包数量信息,加密货币,且是入金钱包,通道为fireblocks
     *
     * @param leCount 低于等于该数量的钱包数量信息
     * @return
     */
    @Override
    public List<CountAvailableWalletDto> countAvailableWallet(int leCount) {
        return baseMapper.countAvailableWallet(leCount);
    }

    /**
     * 统计可用钱包数量信息,加密货币,通道为fireblocks
     *
     * @param leCount         低于等于该数量的钱包数量信息
     * @param depositWithdraw true 入金钱包, false 出金钱包
     * @return
     */
    @Override
    public List<CountAvailableWalletDto> countAvailableWalletFireBlocks(int leCount, boolean depositWithdraw) {
        return depositWithdraw ? baseMapper.countAvailableDepositWalletFireBlocks(leCount)
                : baseMapper.countAvailableWithdrawalWalletFireBlocks(leCount);
    }

    @Override
    public void scanMerchantWallet() {
        List<MerchantWalletEntity> walletList = baseMapper.getSnapshotInfo();
        if (CollUtil.isNotEmpty(walletList)) {
            List<MerchantWalletSnapshotEntity> list =
                    BeanUtil.copyToList(walletList, MerchantWalletSnapshotEntity.class);
            merchantWalletSnapshotService.saveBatch(list);
        }
        //删除30天之前快照
        List<MerchantWalletSnapshotEntity> delList = merchantWalletSnapshotService.lambdaQuery()
                .apply("DATE_SUB(NOW(),INTERVAL 1 MONTH) > create_time").list();
        if (CollUtil.isNotEmpty(delList)) {
            merchantWalletSnapshotService.removeBatchByIds(delList);
        }
    }

    @Override
    public MerchantWalletEntity selectByIdForUpdate(String id) {
        return baseMapper.selectByIdForUpdate(id);
    }

    @Override
    public List<MerchantAvailableWalletDto> queryAvailableWallet(String merchantId, Integer channelSubType) {
        return baseMapper.queryAvailableWallet(merchantId, channelSubType);
    }
}




