package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.model.enums.AccountTypeEnum;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Marty
 * @since 2024/5/8 11:02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAnalyzeServiceFacade {
    private final IAccountService accountService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final IDepositRecordService depositRecordService;
    private final IAssetLastQuoteService assetLastQuoteService;
    private final IMerchantService merchantService;
    private final MerchantWalletService merchantWalletService;
    final DateTimeFormatter FORMATTER_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 商户分析
     * 1.先查出账号信息(这里分为有无(资产和钱包地址)两种)来选择哪种方式查询, 然后根据账号信息查询相关数据. 然后再查商户数量.
     * 2.根据有(资产和钱包地址)来选择哪种方式查询出入金记录表.
     * 3.如果是无赛选条件的, 需要查商户表数量
     * 4.查询赛选条件下的实时余额
     * 5 根据单币种还是多币种的情况处理-6
     * 6.汇总和整理信息
     *
     * @param req
     * @return
     */
    public MerchantAnalyzeRsp analyze(MerchantQueryReq req) {
        //1.查账号信息, 这里分为有无(资产和钱包地址)两种
        MerchantAnalyzeRsp result = new MerchantAnalyzeRsp();
        List<AccountEntity> accounts = accountService.listByMerchantIds(req);
        int size = accounts.size();
        // 只有有账号了, 才会继续查询下面的流程, 不然都返回0;
        if (size > 0) {
            // 2.根据有(资产和钱包地址)来选择哪种方式查询出入金记录表.
            Set<String> accountIdSet = new HashSet<>();
            Set<String> merchantIdSet = new HashSet<>();
            for (AccountEntity account : accounts) {
                accountIdSet.add(account.getId());
                merchantIdSet.add(account.getMerchantId());
            }
            List<DepositRecordEntity> depositRecords = depositRecordService.listByMerchantIdsAntTime(accountIdSet, req);
            List<WithdrawalRecordEntity> withdrawalRecords =
                    withdrawalRecordService.listByMerchantIdsAntTime(accountIdSet, req);

            //3.如果是无赛选条件的, 需要查商户表数量
            if (isAnalyzeQueryReqEmpty(req)) {
                long count = merchantService.getBaseMapper().selectCount(Wrappers.emptyWrapper());
                result.setMerchantCount((int) count);
            } else {
                result.setMerchantCount(merchantIdSet.size());
            }
            result.setAccountCount(accountIdSet.size());
            result.setDepositCount(depositRecords.size());
            result.setWithdrawalCount(withdrawalRecords.size());

            //4. 查询赛选条件下的实时余额
            List<MerchantWalletEntity> walletEntities =
                    merchantWalletService.queryBalanceSumByAssetOrAddr(accountIdSet, req);
            if (CollectionUtils.isEmpty(walletEntities)) {
                log.warn("[analyze], walletEntities is empty, accountIdSet:{}, req:{}", accountIdSet, req);
                return result;
            }
            Set<String> symbols =
                    walletEntities.stream().map(entry -> entry.getAssetName() + AssetConstants.AN_USDT).collect(Collectors.toSet());
            //查询汇率, ps:symbolAndMaxPriceMap的key是assetName+USDT, value是rate, 其中如果是'USDTUSDT'的话, rate就是1
            Map<String, BigDecimal> symbolAndMaxPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbols);

            //5 根据单币种还是多币种的情况来汇总和整理信息, 如果是单币种下面的操作
            if (CollectionUtils.isNotEmpty(symbols) && symbols.size() == 1) {
                return getAnalyzeBySingleAsset(req, walletEntities, result, depositRecords, withdrawalRecords,
                        symbolAndMaxPriceMap);
            }
            // 如果是多币种, 就返回usdt单位
            result.setAssetName(AssetConstants.AN_USDT);
            result.setFeeAssetName(AssetConstants.AN_USDT);

            //6.数据梳理  下面的就计算和统计, 不含IO ---------------------
            //6.0 统计实时钱包的余额
            Map<String, BigDecimal> assetNameBalanceMap =
                    walletEntities.stream().filter(item -> (item.getBalance().compareTo(BigDecimal.ZERO) > 0))
                            .collect(Collectors.groupingBy(MerchantWalletEntity::getAssetName,
                                    Collectors.reducing(BigDecimal.ZERO, walletEntity -> {
                                                BigDecimal rate =
                                                        symbolAndMaxPriceMap.get(walletEntity.getAssetName() + AssetConstants.AN_USDT) == null ? BigDecimal.ZERO : symbolAndMaxPriceMap.get(walletEntity.getAssetName() + AssetConstants.AN_USDT);
                                                return walletEntity.getBalance().multiply(rate);
                                            },
                                            BigDecimal::add)));
            BigDecimal addrAmountSum = assetNameBalanceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            //6.1 计算各类型总金额
            BigDecimal[] depSum = getDepositAmountsSum(depositRecords, false);
            BigDecimal[] witSum = getWithdrawalAmountsSum(withdrawalRecords, false);
            BigDecimal depositAmount = depSum[0];
            BigDecimal withdrawalAmount = witSum[0];
            BigDecimal channelFee = depSum[1].add(witSum[1]);
            BigDecimal gasFee = depSum[2].add(witSum[2]);

            //6.2 处理饼状图数据 (ps:20240527: 产品要求: 历史总余额*历史汇率 改成将历史总余额*实时汇率) (ps:20240611: 产品要求: 将历史总余额*实时汇率 改成实时余额*实时汇率 )
//            List<MerchantAssetRsp> merchtAssetList = getMerchtAssetList(depositRecords, withdrawalRecords,
//            symbolAndMaxPriceMap);
            List<MerchantAssetRsp> merchtAssetList = assetNameBalanceMap.entrySet().stream().map(entry -> {
                MerchantAssetRsp merchantAssetRsp = new MerchantAssetRsp();
                merchantAssetRsp.setAssetName(entry.getKey());
                merchantAssetRsp.setBalanceSumUsdt(entry.getValue());
                return merchantAssetRsp;
            }).toList();

            //6.3 计算历史总余额 (ps:20240527: 产品要求将历史总余额改为历史累加余额, 非最新一条钱包记录的余额和.)
            BigDecimal historyAddrAmountSum = getHistoryAddrAmountSum(depositRecords, withdrawalRecords);

            //6.4 处理柱状图数据
            List<MerchantDayInOutRsp> merchantDayInOutList = getMerchntDayInOutList(req, depositRecords,
                    withdrawalRecords, false);

            //6.5  处理活跃账号(地址) 返回出入金次数最多的前30条, 哪怕全是出金都可以.
            List<ActiveAccountAssetRsp> actionList = getActionList(depositRecords, withdrawalRecords);

            //7. 封装响应
            result.setAddrAmount(addrAmountSum);
            result.setHistoryAddrAmount(historyAddrAmountSum);
            result.setDepositAmount(depositAmount);
            result.setWithdrawalAmount(withdrawalAmount);
            result.setChannelFee(channelFee);
            result.setGasFee(gasFee);

            result.setMerchantDayInOutList(merchantDayInOutList);
            result.setAssetDataList(merchtAssetList);
            result.setActiveAccountList(actionList);
        }
        return result;
    }

    /**
     * 这里是单币规则
     *
     * @param req
     * @param walletEntities
     * @param result
     * @param depositRecords
     * @param withdrawalRecords
     * @return
     */
    @NotNull
    private MerchantAnalyzeRsp getAnalyzeBySingleAsset(MerchantQueryReq req,
                                                       List<MerchantWalletEntity> walletEntities,
                                                       MerchantAnalyzeRsp result,
                                                       List<DepositRecordEntity> depositRecords,
                                                       List<WithdrawalRecordEntity> withdrawalRecords, Map<String,
            BigDecimal> symbolAndMaxPriceMap) {
        //获取实时余额
        BigDecimal addrAmountSum =
                walletEntities.stream().map(MerchantWalletEntity::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setAddrAmount(addrAmountSum);

        // 获取历史记录余额
        BigDecimal depHistoryAddrAmountSum =
                depositRecords.stream().filter(item -> StringUtils.isNotBlank(item.getWalletId()))
                        .collect(Collectors.groupingBy(DepositRecordEntity::getWalletId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(DepositRecordEntity::getCreateTime)),
                                        maxRecord -> maxRecord.map(DepositRecordEntity::getAddrBalance).orElse(BigDecimal.ZERO)
                                )))
                        .values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal witHistoryAddrAmountSum =
                withdrawalRecords.stream().filter(item -> StringUtils.isNotBlank(item.getWalletId()))
                        .collect(Collectors.groupingBy(WithdrawalRecordEntity::getWalletId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(WithdrawalRecordEntity::getCreateTime)),
                                        maxRecord -> maxRecord.map(WithdrawalRecordEntity::getAddrBalance).orElse(BigDecimal.ZERO)
                                )))
                        .values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal historyAddrAmountSum = depHistoryAddrAmountSum.add(witHistoryAddrAmountSum);
        result.setHistoryAddrAmount(historyAddrAmountSum);

        // 4种类型金额
        BigDecimal[] depSum = getDepositAmountsSum(depositRecords, true);
        BigDecimal[] witSum = getWithdrawalAmountsSum(withdrawalRecords, true);
        BigDecimal depositAmount = depSum[0];
        BigDecimal withdrawalAmount = witSum[0];
        BigDecimal channelFee = depSum[1].add(witSum[1]);
        BigDecimal gasFee = depSum[2].add(witSum[2]);

        result.setDepositAmount(depositAmount);
        result.setWithdrawalAmount(withdrawalAmount);
        result.setChannelFee(channelFee);
        result.setGasFee(gasFee);

        // 处理饼状图数据 (ps:20240611: 产品要求: 将历史总余额 改成实时余额)
        MerchantWalletEntity walletEntity = walletEntities.get(0);
        List<MerchantAssetRsp> merchtAssetList = new ArrayList<>();
        MerchantAssetRsp merchantAssetRsp = new MerchantAssetRsp();
        merchantAssetRsp.setAssetName(walletEntity.getAssetName());
        merchantAssetRsp.setBalanceSum(addrAmountSum);
        merchantAssetRsp.setBalanceSumUsdt(symbolAndMaxPriceMap.get(walletEntity.getAssetName() + AssetConstants.AN_USDT) == null ? addrAmountSum : addrAmountSum.multiply(symbolAndMaxPriceMap.get(walletEntity.getAssetName() + AssetConstants.AN_USDT)));
        merchtAssetList.add(merchantAssetRsp);
        result.setAssetDataList(merchtAssetList);

        // 处理柱状图数据
        List<MerchantDayInOutRsp> merchantDayInOutList = getMerchntDayInOutList(req, depositRecords,
                withdrawalRecords, true);
        result.setMerchantDayInOutList(merchantDayInOutList);

        // 处理活跃账号(地址) 返回出入金次数最多的前30条, 哪怕全是出金都可以.
        List<ActiveAccountAssetRsp> actionList = getActionList(depositRecords, withdrawalRecords);
        result.setActiveAccountList(actionList);

        //设置币种名
        if (CollectionUtils.isNotEmpty(depositRecords)) {
            DepositRecordEntity depositRecordEntity = depositRecords.get(0);
            result.setAssetName(depositRecordEntity.getAssetName());
            result.setFeeAssetName(depositRecordEntity.getFeeAssetName());
        } else if (CollectionUtils.isNotEmpty(withdrawalRecords)) {
            WithdrawalRecordEntity withdrawalRecordEntity = withdrawalRecords.get(0);
            result.setAssetName(withdrawalRecordEntity.getAssetName());
            result.setFeeAssetName(withdrawalRecordEntity.getFeeAssetName());
        } else {
            // 没有出入金记录时, 手续费什么的都是0, 币种单位就直接取钱包的币种名就行了.
            result.setAssetName(walletEntity.getAssetName());
            result.setFeeAssetName(walletEntity.getAssetName());
        }

        // 查下汇率, 这里是单币种, 查当前汇率既可以.ps: 暂无这个需求
/*        if(AssetConstants.AN_USDT.equals(result.getAssetName())) {
            result.setRate(BigDecimal.ONE);
        }else {
            BigDecimal rate = assetLastQuoteService.getExchangeRate(result.getAssetName(), true);
            result.setRate(rate);
        }*/
        return result;
    }


    private BigDecimal getHistoryAddrAmountSum(List<DepositRecordEntity> depositRecords,
                                               List<WithdrawalRecordEntity> withdrawalRecords) {
        //将depositRecords 循环, 按照destinationAddress分组, 取每组的最大时间的addrBalance, 然后乘以rate, 最后将结果相加. 返回累加总和, 不返回map
        Map<String, BigDecimal> depAddrBalanceMap =
                depositRecords.stream().filter(item -> StringUtils.isNotBlank(item.getWalletId()))
                        .collect(Collectors.groupingBy(
                                DepositRecordEntity::getWalletId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(DepositRecordEntity::getCreateTime)),
                                        maxRecord -> maxRecord.map(depositRecordEntity ->
                                                depositRecordEntity.getAddrBalance().multiply(depositRecordEntity.getRate())).orElse(BigDecimal.ZERO)
                                )
                        ));
        Map<String, BigDecimal> witAddrBalanceMap =
                withdrawalRecords.stream().filter(item -> StringUtils.isNotBlank(item.getWalletId()))
                        .collect(Collectors.groupingBy(
                                WithdrawalRecordEntity::getWalletId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(WithdrawalRecordEntity::getCreateTime)),
                                        maxRecord -> maxRecord.map(depositRecordEntity ->
                                                depositRecordEntity.getAddrBalance().multiply(depositRecordEntity.getRate())).orElse(BigDecimal.ZERO)
                                )
                        ));
        //累加depAddrBalanceMap的value值
        BigDecimal depAddrAmountSum = depAddrBalanceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal witAddrBalanceSum = witAddrBalanceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return depAddrAmountSum.add(witAddrBalanceSum);
    }

    private List<MerchantDayInOutRsp> getMerchntDayInOutList(MerchantQueryReq req,
                                                             List<DepositRecordEntity> depositRecords,
                                                             List<WithdrawalRecordEntity> withdrawalRecords,
                                                             boolean isNotHandleRate) {
        Date dateByDayNum = getDateByDayNum(req.getTimeEnd(), req.getShowDayNum());
        Map<String, List<DepositRecordEntity>> depositMay =
                depositRecords.stream().filter(item -> item.getCreateTime().after(dateByDayNum))
                        .collect(Collectors.groupingBy(depositRecordEntity -> DateUtil.formatDate(depositRecordEntity.getCreateTime())));
        Map<String, List<WithdrawalRecordEntity>> withdrawalMap =
                withdrawalRecords.stream().filter(item -> item.getCreateTime().after(dateByDayNum))
                        .collect(Collectors.groupingBy(depositRecordEntity -> DateUtil.formatDate(depositRecordEntity.getCreateTime())));

        LocalDate endDay = req.getTimeEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<MerchantDayInOutRsp> merchantDayInOutList = new ArrayList<>();
        for (int i = req.getShowDayNum() - 1; i >= 0; i--) {
//        for (int i = 0; i < req.getShowDayNum(); i++) {
            MerchantDayInOutRsp data = new MerchantDayInOutRsp();
            LocalDate date = endDay.minusDays(i);
            String day = date.format(FORMATTER_YMD);
            data.setDay(day);

            //入金
            List<DepositRecordEntity> depositRecordEntities = depositMay.get(day);
            if (CollectionUtils.isNotEmpty(depositRecordEntities)) {
                data.setDepositDayCount(depositRecordEntities.size());
                BigDecimal[] tempSum = getDepositAmountsSum(depositRecordEntities, isNotHandleRate);
                data.setDepositDayAmount(tempSum[0]);
                data.setGasFeeDayAmount(tempSum[1]);
                data.setChannelFeeDayAmount(tempSum[2]);
            }

            //出金
            List<WithdrawalRecordEntity> withdrawalRecordEntities = withdrawalMap.get(day);
            if (CollectionUtils.isNotEmpty(withdrawalRecordEntities)) {
                data.setWithdrawalDayCount(withdrawalRecordEntities.size());
                BigDecimal[] tempSum = getWithdrawalAmountsSum(withdrawalRecordEntities, isNotHandleRate);
                data.setWithdrawalDayAmount(tempSum[0]);
                data.setGasFeeDayAmount(data.getGasFeeDayAmount().add(tempSum[1]));
                data.setGasFeeDayAmount(data.getChannelFeeDayAmount().add(tempSum[2]));
            }
            merchantDayInOutList.add(data);
        }
        return merchantDayInOutList;
    }


    /**
     * 根据出入金记录, 获取前30条活跃账户信息(支持双排序) count desc, createTime asc
     * eg:
     * A有5次，5次中最早的一条是今天10：00：00；
     * B也有5次，5次中最早的一条是今天10：01：00
     * 那就优先显示A
     *
     * @param depositRecords
     * @param withdrawalRecords
     * @return
     */
    private List<ActiveAccountAssetRsp> getActionList(List<DepositRecordEntity> depositRecords,
                                                      List<WithdrawalRecordEntity> withdrawalRecords) {
        // 将depositRecords集合按destinationAddress统计该地址的次数最后一次的时间
        Map<String, ActiveAccountAssetRsp> addressInfoMap = new HashMap<>();
        for (DepositRecordEntity record : depositRecords) {
            String address = record.getDestinationAddress();
            if (addressInfoMap.containsKey(address)) {
                ActiveAccountAssetRsp info = addressInfoMap.get(address);
                info.setCount(info.getCount() + 1);
                if (record.getCreateTime().before(info.getCreateTime())) {
                    info.setCreateTime(record.getCreateTime());
                }
            } else {
                addressInfoMap.put(address, new ActiveAccountAssetRsp(address, AccountTypeEnum.DEPOSIT.getCode(), 1,
                        record.getAccountId(), record.getCreateTime()));
            }
        }

        for (WithdrawalRecordEntity record : withdrawalRecords) {
            String address = record.getSourceAddress();
            if (addressInfoMap.containsKey(address)) {
                ActiveAccountAssetRsp info = addressInfoMap.get(address);
                info.setCount(info.getCount() + 1);
                if (record.getCreateTime().before(info.getCreateTime())) {
                    info.setCreateTime(record.getCreateTime());
                }
            } else {
                addressInfoMap.put(address, new ActiveAccountAssetRsp(address, AccountTypeEnum.WITHDRAWAL.getCode(),
                        1, record.getAccountId(), record.getCreateTime()));
            }
        }

        //将addressInfoMap的value转换成list
        List<ActiveAccountAssetRsp> actionList = new ArrayList<>(addressInfoMap.values());

        //将actionList按照count倒序排序,如果有相同的再按照时间正序排序并只取前30条
        actionList.sort((o1, o2) -> {
            if (o1.getCount().equals(o2.getCount())) {
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
            return o2.getCount() - o1.getCount();
        });
        return actionList.stream().limit(30).toList();
    }

    private BigDecimal[] getWithdrawalAmountsSum(List<WithdrawalRecordEntity> withdrawalRecords,
                                                 boolean isNotHandleRate) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalChannelFee = BigDecimal.ZERO;
        BigDecimal totalGasFee = BigDecimal.ZERO;
        if (isNotHandleRate) {
            for (WithdrawalRecordEntity record : withdrawalRecords) {
                totalAmount = totalAmount.add(record.getAmount());
                totalChannelFee = totalChannelFee.add(record.getChannelFee());
                totalGasFee = totalGasFee.add(record.getGasFee());
            }
        } else {
            for (WithdrawalRecordEntity record : withdrawalRecords) {
                totalAmount = totalAmount.add(record.getAmount().multiply(record.getRate()));
                totalChannelFee = totalChannelFee.add(record.getChannelFee().multiply(record.getFeeRate()));
                totalGasFee = totalGasFee.add(record.getGasFee().multiply(record.getFeeRate()));
            }
        }
        return new BigDecimal[]{totalAmount, totalChannelFee, totalGasFee};
    }

    /**
     * 获取入金总金额, 渠道费(平台费), 链上交易费
     *
     * @param depositRecords  入金记录
     * @param isNotHandleRate 是否不处理汇率, true:不处理汇率
     * @return
     */
    private BigDecimal[] getDepositAmountsSum(List<DepositRecordEntity> depositRecords, boolean isNotHandleRate) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalChannelFee = BigDecimal.ZERO;
        BigDecimal totalGasFee = BigDecimal.ZERO;
        if (isNotHandleRate) {
            for (DepositRecordEntity record : depositRecords) {
                totalAmount = totalAmount.add(record.getAmount());
                totalChannelFee = totalChannelFee.add(record.getChannelFee());
                totalGasFee = totalGasFee.add(record.getGasFee());
            }
        } else {
            for (DepositRecordEntity record : depositRecords) {
                totalAmount = totalAmount.add(record.getAmount().multiply(record.getRate()));
                totalChannelFee = totalChannelFee.add(record.getChannelFee().multiply(record.getRate()));
                totalGasFee = totalGasFee.add(record.getGasFee().multiply(record.getFeeRate()));
            }
        }
        return new BigDecimal[]{totalAmount, totalChannelFee, totalGasFee};
    }

    /**
     * 获取前dayNum-1天0点0分0秒的日期
     *
     * @param date
     * @param dayNum
     * @return
     */
    private Date getDateByDayNum(Date date, Integer dayNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (dayNum > 1) {
            cal.add(Calendar.DATE, -(dayNum - 1)); // Subtract 5 days
        }
        cal.set(Calendar.HOUR_OF_DAY, 0); // Set hour to 0
        cal.set(Calendar.MINUTE, 0); // Set minute to 0
        cal.set(Calendar.SECOND, 0); // Set second to 0
        cal.set(Calendar.MILLISECOND, 0); // Set millisecond to 0
        return cal.getTime();
    }


    /**
     * 判断请求参数是否为空
     *
     * @param req
     * @return
     */
    private Boolean isAnalyzeQueryReqEmpty(MerchantQueryReq req) {
        return CollectionUtils.isEmpty(req.getAssetNameList()) && CollectionUtils.isEmpty(req.getAddrList())
                && CollectionUtils.isEmpty(req.getAccountTypeList()) && CollectionUtils.isEmpty(req.getAccountIdList())
                && CollectionUtils.isEmpty(req.getMerchantIdList());
    }

    /**
     * 商户分析-赛选条件数据联动接口
     * 先查账号和商户, 再处理账号类型, 再查钱包, 最后处理钱包信息
     *
     * @param req
     * @return
     */
    public AnalyzeSelectionRsp getAnalyzeSelection(MerchantQueryReq req) {
        AnalyzeSelectionRsp result = new AnalyzeSelectionRsp();

        // 先查账号, 再查商户, 再处理账号类型, 再查钱包, 最后处理钱包信息
        List<AccountEntity> accounts = accountService.listByMerchantIds(req);
        boolean accountsFlag = CollectionUtils.isNotEmpty(accounts);
        if (CollectionUtils.isEmpty(req.getAssetNameList()) && CollectionUtils.isEmpty(req.getAddrList())
                && CollectionUtils.isEmpty(req.getAccountTypeList()) && CollectionUtils.isEmpty(req.getAccountIdList())) {
            LambdaQueryWrapper<MerchantEntity> query =
                    Wrappers.lambdaQuery(MerchantEntity.class).select(MerchantEntity::getId, MerchantEntity::getName,
                            MerchantEntity::getStatus);
            if (CollectionUtils.isNotEmpty(req.getMerchantIdList())) {
                query.in(MerchantEntity::getId, req.getMerchantIdList());
            }
            result.setMerchantList(merchantService.getBaseMapper().selectList(query));
        } else {
            if (accountsFlag) {
                Set<String> merchantIdSet =
                        accounts.stream().map(AccountEntity::getMerchantId).collect(Collectors.toSet());
                result.setMerchantList(merchantService.getBaseMapper().selectBatchIds(merchantIdSet));
            }
        }
        if (accountsFlag) {
            Set<Integer> accountTypeSet =
                    accounts.stream().map(AccountEntity::getAccountType).collect(Collectors.toSet());
            //循环accountTypeSet, 转换成AccountEntity
            result.setAccountTypeList(accountTypeSet.stream().map(accountType -> {
                AccountEntity accountEntity = new AccountEntity();
                accountEntity.setAccountType(accountType);
                return accountEntity;
            }).toList());
        }
        result.setAccountList(accounts);

        if (accountsFlag) {
            Set<String> accountIdSet = accounts.stream().map(AccountEntity::getId).collect(Collectors.toSet());
            result.setWalletList(merchantWalletService.queryBalanceSumByAssetOrAddr(accountIdSet, req));
        }

        if (CollectionUtils.isNotEmpty(result.getWalletList())) {
            //先去重再返回
            Set<String> assetNameSet =
                    result.getWalletList().stream().map(MerchantWalletEntity::getAssetName).collect(Collectors.toSet());
            result.setAssetList(new ArrayList<>(assetNameSet));
        }
        //用户标识查询
        LambdaQueryWrapper<DepositRecordEntity> depositQuery = new LambdaQueryWrapper<>();
        depositQuery.select(DepositRecordEntity::getUserId).isNotNull(DepositRecordEntity::getUserId)
                .apply("user_id != ''");
        List<String> list = depositRecordService.listObjs(depositQuery);
        LambdaQueryWrapper<WithdrawalRecordEntity> withdrawalQuery = new LambdaQueryWrapper<>();
        withdrawalQuery.select(WithdrawalRecordEntity::getUserId).isNotNull(WithdrawalRecordEntity::getUserId)
                .apply("user_id != ''");
        List<String> withdralList = withdrawalRecordService.listObjs(withdrawalQuery);
        list.addAll(withdralList);
        if (CollUtil.isNotEmpty(list)) {
            list = list.stream().distinct().collect(Collectors.toList());
        }
        result.setUserIdList(list);
        return result;
    }


}
