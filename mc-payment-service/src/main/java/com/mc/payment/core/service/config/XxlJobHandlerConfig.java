package com.mc.payment.core.service.config;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.mc.payment.core.service.entity.JobPlanEntity;
import com.mc.payment.core.service.facade.*;
import com.mc.payment.core.service.manager.wallet.FireBlocksWalletManager;
import com.mc.payment.core.service.model.dto.EmailJobParamDto;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.JobPlanHandlerEnum;
import com.mc.payment.core.service.model.enums.JobPlanStatusEnum;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.XxlJobUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class XxlJobHandlerConfig {

    private final AccountServiceFacade accountServiceFacade;
    private final ExternalServiceFacade externalServiceFacade;
    private final ChannelServiceFacade channelServiceFacade;
    private final IJobPlanService jobPlanService;
    private final MerchantServiceFacade merchantServiceFacade;
    private final AssetConfigServiceFacade assetConfigServiceFacade;
    private final IAssetLastQuoteService assetLastQuoteService;
    private final IWalletBalanceLogService walletBalanceLogService;
    private final EmailServiceFacade emailServiceFacade;
    private final WithdrawalRecordServiceFacade withdrawalRecordServiceFacade;
    private final CurrencyRateService currencyRateService;
    private final ChannelWalletService channelWalletService;
    private final WalletServiceFacade walletServiceFacade;
    private final MerchantWalletService merchantWalletService;
    private final FireBlocksWalletManager fireBlocksWalletManager;


    /**
     * 刷新钱包余额
     */
    @XxlJob("refreshWalletBalance")
    public void refreshWalletBalanceJob() throws Exception {
        // 获取当前节点的index 与 总节点数
//        int shardIndex = XxlJobHelper.getShardIndex();
//        int shardTotal = XxlJobHelper.getShardTotal();
//        XxlJobUtil.log("当前节点的index:{},总节点数:{}", shardIndex, shardTotal);
        // walletService.refreshWalletBalanceJob(shardIndex, shardTotal);
        channelWalletService.syncBalance();
    }

    /**
     * scanDepositRequest 解锁超时的钱包
     */
    @XxlJob("scanDepositRequest")
    public void scanDepositRequestJobHandler() throws Exception {
        externalServiceFacade.scanDepositRequest();
    }

    /**
     * channelExpiration 通道有效期失效,处理关闭任务
     */
    @XxlJob("channelExpiration")
    public void channelExpirationJobHandler() throws Exception {
        channelServiceFacade.channelExpiration();
    }

    /**
     * createAccount 创建账户
     *
     * @throws Exception
     */
    @Deprecated
    @XxlJob("createAccount")
    public void createAccountJobHandler() throws Exception {
//        accountServiceFacade.generateAccountJob(); 2025年1月3日 作废 换成新的 fireBlocksWalletManager.generateWalletJob
    }

    /**
     * createWallet 创建钱包
     *
     * @throws Exception
     */
    @XxlJob("createWallet")
    public void createWalletJobHandler() throws Exception {
//        walletServiceFacade.generateFireBlocksWalletJob(); 2025年1月3日 作废 换成新的

        // 对手动创建的钱包进行生成
        fireBlocksWalletManager.generateWalletJob();
    }

    @XxlJob("autoGenerateWalletJob")
    public void autoGenerateWalletJob() throws Exception {
        fireBlocksWalletManager.autoGenerateWalletJob();

    }

    @XxlJob("sendEmail")
    public void sendEmailJobHandler() throws Exception {
        List<JobPlanEntity> list = jobPlanService.listByLimit(JobPlanHandlerEnum.SEND_EMAIL, JobPlanStatusEnum.AWAIT.getCode(), 100);
        for (JobPlanEntity jobPlanEntity : list) {
            jobPlanService.update(jobPlanEntity.getId(), JobPlanStatusEnum.ING, "");
            EmailJobParamDto paramDto = JSONUtil.toBean(jobPlanEntity.getParam(), EmailJobParamDto.class);
            ImmutablePair<Boolean, String> immutablePair = emailServiceFacade.sendSimpleMessage(paramDto.getRecipientMail(), paramDto.getSubject(), paramDto.getContent());
            if (immutablePair.left) {
                jobPlanService.update(jobPlanEntity.getId(), JobPlanStatusEnum.FINISH, immutablePair.right);
            } else {
                jobPlanService.update(jobPlanEntity.getId(), JobPlanStatusEnum.FAIL, immutablePair.right);
            }
        }
    }

    @Deprecated
    @XxlJob("scanCreateWalletJobPlan")
    public void scanCreateWalletJobPlanJobHandler() throws Exception {
        //  merchantServiceFacade.scanCreateWalletJobPlan();
    }

    /**
     * 刷新资产预估费
     *
     * @throws Exception
     */
    @XxlJob("refreshTheEstimatedFeeForAssets")
    public void refreshTheEstimatedFeeForAssetsJobHandler() throws Exception {
        // 获取当前节点的index 与 总节点数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        XxlJobUtil.log("当前节点的index:{},总节点数:{}", shardIndex, shardTotal);
        assetConfigServiceFacade.refreshTheEstimatedFeeForAssets(shardIndex, shardTotal);
    }

    /**
     * 刷新资产最新报价
     *
     * @throws Exception
     */
    @XxlJob("refreshTheEstimatedFeeForAsset")
    public void refreshTheEstimatedFeeForAsset() throws Exception {
        assetLastQuoteService.refreshTheEstimatedFeeForAsset();
    }

    /**
     * 分片广播定时任务-监控钱包余额变动
     *
     * @throws Exception
     */
    @XxlJob("monitorWalletBalanceChange")
    public void monitorWalletBalanceChangeJob() throws Exception {
        // 获取当前节点的index 与 总节点数

        walletBalanceLogService.monitorWalletBalanceChangeJob();
    }

    /**
     * 扫描生成商户备付金告警
     * 扫描商户的出金地址是否低于设置的备付金,如果是则生成告警邮箱任务计划
     */
    @XxlJob("scanMerchantReserveRatio")
    public void scanMerchantReserveRatio() throws Exception {
        merchantServiceFacade.scanMerchantReserveRatioJob();
    }

    /**
     * 扫描出金余额不足的数据, 关闭超过两小时的数据状态, 修改为失败, 如果是半个月内, 就走刷新余额, 如果余额充足就走出金审核流程.
     */
    @XxlJob("scanInsufficientBalance")
    public void scanInsufficientBalance() throws Exception {
        withdrawalRecordServiceFacade.scanInsufficientBalanceJob();
    }

    /**
     * 扫描超时的出金中的出金申请,重新触发fireblocks的回调
     * 1.扫描出金中的出金申请--超过30分钟仍然是出金中的
     * 2.重新触发fireblocks的回调
     * 3.若无法触发,则标记为失败,并且解锁冻结的余额
     */
    @XxlJob("scanWithdrawRequestByTimeout")
    public void scanWithdrawRequestByTimeout() throws Exception {
        externalServiceFacade.scanWithdrawRequestByTimeout();
    }

    /**
     * 一些风控扫描,以及触发相关邮件预警
     *
     * @throws Exception
     */
    @XxlJob("riskScan")
    public void riskScan() throws Exception {
        withdrawalRecordServiceFacade.riskScan();
    }

    /**
     * 刷新汇率
     *
     * @throws Exception
     */
    @XxlJob("refreshCurrencyRate")
    public void refreshCurrencyRate() throws Exception {
        currencyRateService.refreshCurrencyRate();
    }

    /**
     * 补充钱包数量
     *
     * @throws Exception
     */
    @XxlJob("replenishWalletQuantity")
    public void replenishWalletQuantity() throws Exception {
        //merchantServiceFacade.replenishFireBlocksWalletQuantityJob();
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.OFA_PAY);
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.PAY_PAL);
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.PASS_TO_PAY);
    }

    /**
     * 商户钱包快照
     *
     * @throws Exception
     */
    @XxlJob("merchantWalletSnapshot")
    public void merchantWalletSnapshot() throws Exception {
        merchantWalletService.scanMerchantWallet();
    }

    /**
     * 执行出金增加时效
     *
     * @throws Exception
     */
    @XxlJob("payoutTimeoutLimit")
    public void payoutTimeoutLimit() throws Exception {
        StopWatch stopWatch = new StopWatch("余额不足，出金超时终止任务");
        stopWatch.start();
        withdrawalRecordServiceFacade.payoutTimeoutLimit();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }
}