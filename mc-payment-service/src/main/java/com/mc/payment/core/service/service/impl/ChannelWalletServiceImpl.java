package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.mapper.ChannelWalletMapper;
import com.mc.payment.core.service.model.dto.FireBlocksWalletSyncInfoDto;
import com.mc.payment.core.service.model.enums.AccountStatusEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.ChannelWalletPageReq;
import com.mc.payment.core.service.model.rsp.ChannelWalletExportRsp;
import com.mc.payment.core.service.service.ChannelWalletLogService;
import com.mc.payment.core.service.service.ChannelWalletService;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.service.MerchantWalletService;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAccountVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAssetVo;
import com.mc.payment.gateway.channels.ofapay.model.req.OfaPayQueryBalanceReq;
import com.mc.payment.gateway.channels.ofapay.model.rsp.OfaPayQueryBalanceRsp;
import com.mc.payment.gateway.channels.ofapay.service.OfaPayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_channel_wallet(通道钱包)】的数据库操作Service实现
 * @createDate 2024-08-14 13:44:50
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelWalletServiceImpl extends ServiceImpl<ChannelWalletMapper, ChannelWalletEntity>
        implements ChannelWalletService {
    private final ChannelWalletLogService channelWalletLogService;
    private final MerchantWalletService merchantWalletService;
    private final OfaPayService ofaPayService;
    private final FireBlocksAPI fireBlocksAPI;
    private final IAccountService accountService;


    @Override
    public BasePageRsp<ChannelWalletEntity> page(ChannelWalletPageReq req) {
        Page<ChannelWalletEntity> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }

    /**
     * 同步余额
     */
    @Override
    public void syncBalance() {
        syncBalanceByOfaPay();
        syncBalanceByFireBlocksAll();
    }

    private void syncBalanceByOfaPay() {
        List<ChannelWalletEntity> list = this.list(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                .eq(ChannelWalletEntity::getChannelSubType, ChannelSubTypeEnum.OFA_PAY.getCode()));
        for (ChannelWalletEntity walletEntity : list) {
            OfaPayQueryBalanceReq req = null;
            OfaPayQueryBalanceRsp ofaPayQueryBalanceRsp = null;
            try {
//                String apiCredential = walletEntity.getApiCredential();
//                JSONObject jsonObject = JSONUtil.parseObj(apiCredential);
//                String scode = jsonObject.getStr("scode");
                // ofapay通道钱包的地址,就是scode
                req = new OfaPayQueryBalanceReq(walletEntity.getWalletAddress());
                ofaPayQueryBalanceRsp = ofaPayService.queryBalance(req);

                BigDecimal balance = NumberUtil.toBigDecimal(ofaPayQueryBalanceRsp.getBalance());

                this.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                        .set(ChannelWalletEntity::getBalance, balance)
                        .eq(ChannelWalletEntity::getId, walletEntity.getId()));
                // 记录日志
                BigDecimal changeBalance = balance.subtract(walletEntity.getBalance());
                channelWalletLogService.asyncSaveLog(walletEntity.getId(), changeBalance, BigDecimal.ZERO, "同步余额", new Date());
            } catch (Exception e) {
                log.error("syncBalanceByOfaPay error", e);
            } finally {
                log.info("syncBalanceByOfaPay walletId:{},req:{},retResult:{}", walletEntity.getId(), req, ofaPayQueryBalanceRsp);
            }
        }
    }

    /**
     * 同步fireblocks钱包余额
     */
    @Override
    public void syncBalanceByFireBlocksAll() {
        List<AccountEntity> list = accountService.list(Wrappers.lambdaQuery(AccountEntity.class)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(AccountEntity::getStatus, AccountStatusEnum.GENERATE_SUCCESS.getCode())
                .orderByDesc(AccountEntity::getMerchantId));
        for (AccountEntity accountEntity : list) {
            syncBalanceByFireBlocks(accountEntity.getId());
        }
    }


    @Override
    public void syncBalanceByFireBlocks(String accountId) {
        List<FireBlocksWalletSyncInfoDto> fireBlocksWalletSyncInfoDtos = baseMapper.queryFireBlocksWalletSyncInfo(accountId);
        if (CollUtil.isEmpty(fireBlocksWalletSyncInfoDtos)) {
            log.info("fireBlocksWalletSyncInfoDtos is empty,accountId:{}", accountId);
            return;
        }
        Map<String, FireBlocksWalletSyncInfoDto> fireBlocksWalletSyncInfoDtoMap = fireBlocksWalletSyncInfoDtos.stream()
                .collect(Collectors.toMap(FireBlocksWalletSyncInfoDto::getChannelAssetName, Function.identity()));
        String vaultAccountId = fireBlocksWalletSyncInfoDtos.get(0).getExternalId();
        RetResult<VaultAccountVo> vaultAccountVoRetResult = fireBlocksAPI.queryAccount(vaultAccountId);
        if (!vaultAccountVoRetResult.isSuccess()) {
            log.error("fireBlocksAPI.queryAccount error,accountId:{},vaultAccountId:{},retResult:{}", accountId, vaultAccountId, vaultAccountVoRetResult);
            return;
        }
        List<VaultAssetVo> assets = vaultAccountVoRetResult.getData().getAssets();
        for (VaultAssetVo asset : assets) {
            String assetId = asset.getId();
            BigDecimal balance = NumberUtil.toBigDecimal(asset.getTotal());
            // 可用于转账的资金
            BigDecimal available = NumberUtil.toBigDecimal(asset.getAvailable());
            // 冻结资金
            BigDecimal freezeAmount = balance.subtract(available);

            FireBlocksWalletSyncInfoDto infoDto = fireBlocksWalletSyncInfoDtoMap.get(assetId);
            if (infoDto == null) {
                log.info("infoDto is null,assetId:{}", assetId);
                continue;
            }
            try {
                ChannelWalletEntity channelWalletEntity = this.getById(infoDto.getChannelWalletId());
                if (channelWalletEntity == null) {
                    log.info("channelWalletEntity is null,channelWalletId:{}", infoDto.getChannelWalletId());
                    continue;
                }
                this.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                        .set(ChannelWalletEntity::getBalance, balance)
                        .set(ChannelWalletEntity::getFreezeAmount, freezeAmount)
                        .eq(ChannelWalletEntity::getId, infoDto.getChannelWalletId()));

                // 记录日志
                BigDecimal changeBalance = balance.subtract(channelWalletEntity.getBalance());
                BigDecimal changeFreezeAmount = freezeAmount.subtract(channelWalletEntity.getFreezeAmount());
                Future<String> logFuture = channelWalletLogService.asyncSaveLog(infoDto.getChannelWalletId(), changeBalance, changeFreezeAmount, "同步余额", new Date());

                String logId = logFuture.get();
                //fireblocks通道的出金钱包,则要更新对应商户的出金余额
                merchantWalletService.syncChannelFireBlocksWithdrawWalletBalance(infoDto.getChannelWalletId(), logId, balance);

            } catch (Exception e) {
                log.error("syncBalanceByFireBlocks error", e);
            }
        }
    }

    @Override
    public void export(ChannelWalletPageReq req, HttpServletResponse response) {
        ClassPathResource resource = new ClassPathResource("/template/channelWallet.xlsx");
        List<ChannelWalletExportRsp> list = baseMapper.queryExportInfo(req);
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "通道钱包" + formatDate + ".xlsx";
        try {
            InputStream ChannelWalletStream = resource.getInputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(ChannelWalletStream).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelWalletEntity getOne(String assetName, String netProtocol, String walletAddress) {
        return lambdaQuery().eq(ChannelWalletEntity::getAssetName, assetName).eq(ChannelWalletEntity::getNetProtocol, netProtocol)
                .eq(ChannelWalletEntity::getWalletAddress, walletAddress).one();
    }
}




