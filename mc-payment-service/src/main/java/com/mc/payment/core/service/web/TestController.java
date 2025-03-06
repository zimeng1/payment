package com.mc.payment.core.service.web;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mc.message.dto.mail.BatchSendMailDTO;
import com.mc.message.feign.MailClient;
import com.mc.message.resp.Result;
import com.mc.payment.api.PaymentFeignClient;
import com.mc.payment.api.util.AKSKUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.config.XxlJobHandlerConfig;
import com.mc.payment.core.service.config.aspect.LogExecutionTime;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.EmailTemplateEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.facade.AssetConfigServiceFacade;
import com.mc.payment.core.service.facade.MerchantServiceFacade;
import com.mc.payment.core.service.facade.WithdrawalRecordServiceFacade;
import com.mc.payment.core.service.manager.deposit.DepositManagerImpl;
import com.mc.payment.core.service.manager.wallet.FireBlocksWalletManager;
import com.mc.payment.core.service.model.dto.CryptoDepositEventDetailVo;
import com.mc.payment.core.service.model.dto.CryptoDepositEventVo;
import com.mc.payment.core.service.model.dto.CryptoWithdrawalEventVo;
import com.mc.payment.core.service.model.dto.EmailJobParamByReserveDto;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.EmailContentEnum;
import com.mc.payment.core.service.model.rsp.CryptoWithdrawWalletRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.service.impl.MerchantServiceImpl;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.QueryTransactionsByIdReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.AssetTypeVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.TransactionVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAccountVo;
import com.mc.payment.fireblocksapi.util.FireBlocksUtil;
import com.mc.payment.gateway.adapter.FireBlocksPaymentGatewayAdapter;
import com.mc.payment.gateway.adapter.OfaPayPaymentGatewayAdapter;
import com.mc.payment.gateway.channels.ofapay.service.OfaPayService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 认证
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@SaCheckRole("admin")
@Slf4j
@Tag(name = "测试")
@RestController
@LogExecutionTime
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/test")
public class TestController extends BaseController {

    @Autowired
    private AssetConfigServiceFacade assetConfigServiceFacade;

    @Autowired
    private FireBlocksUtil fireBlocksUtil;

    @Autowired
    private FireBlocksAPI fireBlocksAPI;

    @Operation(summary = "心跳测试")
    @GetMapping("/heartbeat")
    public RetResult<Object> heartbeat() {
        // 检查下服务是否正常启动, 会不会网关潮汕
        return RetResult.data("心跳测试: " + new Date());
    }

    @Operation(summary = "AK/SK签名生成")
    @GetMapping("/sign")
    public RetResult<Map<String, String>> sign(String ak, String requestURI, String sk) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("signature", AKSKUtil.calculateHMAC(ak + timestamp + requestURI, sk));

        return RetResult.data(map);
    }

    @Operation(summary = "打印告警日志")
    @PostMapping("/printLog")
    public RetResult printLog(@RequestBody String message) {
        MonitorLogUtil.log(message);
        return RetResult.ok();
    }

    @Operation(summary = "AK/SK签名校验")
    @GetMapping("/sign/check")
    public RetResult<Map<String, String>> sign(String ak, String requestURI, String timestamp, String sk, String signatureReq) {
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        String signature = AKSKUtil.calculateHMAC(ak + timestamp + requestURI, sk);
        map.put("signature", signature);
        map.put("signatureReq", signatureReq);
        map.put("前面是否一致", signature.equals(signatureReq) ? "是" : "否");
        return RetResult.data(map);
    }

    @Operation(summary = "触发一次webhook回调的模拟数据")
    @GetMapping("/webhook")
    public RetResult<Object> webhook(String webhookUrl, String event) {
        // 组装webhook参数
        Map<String, Object> param = new HashMap<>();
        param.put("event", event);
        if (WebhookEventConstants.DEPOSIT_EVENT.equals(event)) {
            CryptoDepositEventVo eventVo = new CryptoDepositEventVo();
            eventVo.setAmount(BigDecimal.ZERO);
//            eventVo.setGasFee(BigDecimal.ZERO);
            eventVo.setChannelFee(BigDecimal.ZERO);
            eventVo.setSourceAddress("");
            eventVo.setDestinationAddress("");
            eventVo.setAssetName("");
            eventVo.setAssetNet("");
            List<CryptoDepositEventDetailVo> detailList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                CryptoDepositEventDetailVo detailVo = new CryptoDepositEventDetailVo();
                detailVo.setTxHash("" + i);
                detailVo.setAssetName("asd" + i);
                detailVo.setNetProtocol("asdf" + i);
                detailVo.setSourceAddress("adf" + i);
                detailVo.setDestinationAddress("ss" + i);
                detailVo.setAmount(new BigDecimal("10"));
//                detailVo.setNetworkFee(new BigDecimal("1110"));
//                detailVo.setServiceFee(new BigDecimal("120"));
                detailVo.setCreateTime(new Date());
                detailList.add(detailVo);
            }

            eventVo.setDetailList(detailList);
            eventVo.setStatus(0);
            eventVo.setTrackingId("");

            eventVo.setStatus(1);
            eventVo.setTrackingId("trackingId");
            param.put("data", eventVo);
        } else if (WebhookEventConstants.WITHDRAWAL_EVENT.equals(event)) {
            CryptoWithdrawalEventVo eventVo = new CryptoWithdrawalEventVo();
            eventVo.setAmount(BigDecimal.ZERO);
//            eventVo.setGasFee(BigDecimal.ZERO);
            eventVo.setTxHash("TxHash");
            eventVo.setChannelFee(new BigDecimal("1"));
            eventVo.setWalletAddress("WalletAddress");
            eventVo.setAssetName("AssetNam");
            eventVo.setAssetNet("AssetNet");
            eventVo.setStatus(0);
            eventVo.setTrackingId("");

            eventVo.setStatus(1);
            eventVo.setTrackingId("94d50dc5-d4ac-4004-8917-f374b160c841");

            eventVo.setRealGasFee(new BigDecimal("1"));
            eventVo.setItselfGasFee(new BigDecimal("2"));
            eventVo.setGasFeeToU(new BigDecimal("3"));
            eventVo.setRealServerFee(new BigDecimal("1"));
            eventVo.setItselfServerFee(new BigDecimal("2"));
            eventVo.setServerFeeToU(new BigDecimal("3"));
            eventVo.setRealChannelFee(new BigDecimal("2"));
            eventVo.setItselfChannelFee(new BigDecimal("4"));
            eventVo.setChannelFeeToU(new BigDecimal("6"));
            eventVo.setRealUnit("实际扣费币种单位");
            eventVo.setItselfUnit("本币单位");
            eventVo.setUUnit("USDT");

            param.put("data", eventVo);
        }

        // 触发webhook
        String postResponse = HttpUtil.post(webhookUrl, JSONUtil.toJsonStr(param));
        log.info("test webhook:{},event:{},param:{},response:{}", webhookUrl, event, param, postResponse);
        if (!"Webhook received".equals(postResponse)) {
            return RetResult.error("应返回:Webhook received,实际返回:" + postResponse);
        }
        return RetResult.data(postResponse);
    }


    @Operation(summary = "测试时接收webhook回调")
    @PostMapping("/webhook/test")
    public String webhookTest(@RequestBody Map<String, Object> param) {
        log.info("test webhook,param:{}", param);
        return "Webhook received";
    }

    @Autowired
    private XxlJobHandlerConfig xxlJobHandlerConfig;

    @Operation(summary = "触发定时任务")
    @GetMapping("/triggerJob/{jobName}")
    public String triggerJob(@PathVariable String jobName) {
        try {
            // 使用反射来调用方法
            Method method = XxlJobHandlerConfig.class.getMethod(jobName);
            method.invoke(xxlJobHandlerConfig);

            return "Job triggered successfully";
        } catch (Exception e) {
            return "Failed to trigger job: " + e.getMessage();
        }
    }


    @Operation(summary = "querySupportedAssets")
    @GetMapping("/querySupportedAssets")
    public RetResult<List<AssetTypeVo>> querySupportedAssets() {
        return fireBlocksAPI.querySupportedAssets();
    }

    @Operation(summary = "resetClient")
    @GetMapping("/resetClient")
    public RetResult<List<AssetTypeVo>> resetClientByUriChange(int urlChange) {
        if (urlChange == 0) {
            fireBlocksUtil.resetClientByUriChange();
        } else {
            fireBlocksUtil.resetClient();
        }
        return RetResult.ok();
    }

    @Autowired
    private WithdrawalRecordServiceFacade withdrawalRecordServiceFacade;


    @Operation(summary = "risk")
    @GetMapping("/risk")
    public void risk() {
        withdrawalRecordServiceFacade.riskScan();
    }

    @Autowired
    private ChannelWalletService channelWalletService;
    @Autowired
    private MerchantWalletService merchantWalletService;

    @Operation(summary = "syncBalance")
    @GetMapping("/syncBalance")
    public void syncBalance() {
        channelWalletService.syncBalance();
    }

    @Operation(summary = "getCryptoWithdrawWallet")
    @GetMapping("/getCryptoWithdrawWallet")
    public CryptoWithdrawWalletRsp getCryptoWithdrawWallet(String merchantId, Integer channelSubType,
                                                           String assetName, String netProtocol,
                                                           String feeAssetName, BigDecimal amount, BigDecimal feeAmount) {
        return merchantWalletService.getCryptoWithdrawWalletAndFreeze(merchantId, channelSubType, assetName, netProtocol, feeAssetName, amount, feeAmount);
    }

    @Autowired
    private FireBlocksPaymentGatewayAdapter fireBlocksPaymentGatewayAdapter;

    @Operation(summary = "queryTransaction")
    @GetMapping("/queryTransaction")
    public RetResult<GatewayQueryWithdrawalRsp> queryTransaction(String txId) {
        GatewayQueryWithdrawalReq gatewayQueryWithdrawalReq = new GatewayQueryWithdrawalReq();
        gatewayQueryWithdrawalReq.setTransactionId(txId);
        return fireBlocksPaymentGatewayAdapter.queryWithdrawal(gatewayQueryWithdrawalReq);
    }

    @Operation(summary = "queryTransaction2")
    @GetMapping("/queryTransaction2")
    public RetResult<TransactionVo> queryTransaction2(String txId) {
        QueryTransactionsByIdReq queryTransactionsByIdReq = new QueryTransactionsByIdReq();
        queryTransactionsByIdReq.setTxId(txId);
        return fireBlocksAPI.queryTransactionsById(queryTransactionsByIdReq);
    }

    @Autowired
    private OfaPayService ofaPayService;
    @Autowired
    private OfaPayPaymentGatewayAdapter ofaPayPaymentGatewayAdapter;

    // queryBalance
    @Operation(summary = "ofaPayQueryBalance")
    @GetMapping("/ofaPay/queryBalance")
    public RetResult<GatewayQueryBalanceRsp> ofaPayQueryBalance(String code) {
//        fireBlocksPaymentGatewayAdapter.queryBalance(new QueryBalanceReq());
        return ofaPayPaymentGatewayAdapter.queryBalance(new GatewayQueryBalanceReq(code, null));
    }

    @Operation(summary = "fireBlocksQueryBalance")
    @GetMapping("/fireBlocks/queryBalance")
    public RetResult<GatewayQueryBalanceRsp> fireBlocksQueryBalance(String accountId, String assetId) {
        return fireBlocksPaymentGatewayAdapter.queryBalance(new GatewayQueryBalanceReq(accountId, assetId));
    }

    @Operation(summary = "deposit")
    @PostMapping("/deposit")
    public RetResult<GatewayDepositRsp> deposit(@RequestBody GatewayDepositReq req) {
        return ofaPayPaymentGatewayAdapter.deposit(req);
    }


    @Operation(summary = "queryDeposit")
    @PostMapping("/queryDeposit")
    public RetResult<GatewayQueryDepositRsp> deposit(@RequestBody GatewayQueryDepositReq req) {
        return ofaPayPaymentGatewayAdapter.queryDeposit(req);
    }

    @Operation(summary = "withdrawal")
    @PostMapping("/withdrawal")
    public RetResult<GatewayWithdrawalRsp> withdrawal(@RequestBody GatewayWithdrawalReq req) {
        return ofaPayPaymentGatewayAdapter.withdrawal(req);
    }

    @Operation(summary = "fireblockWithdrawal")
    @PostMapping("/fireblockWithdrawal")
    public RetResult<GatewayWithdrawalRsp> fireblockWithdrawal(@RequestBody GatewayWithdrawalReq req) {
        return fireBlocksPaymentGatewayAdapter.withdrawal(req);
    }

    @Operation(summary = "queryWithdrawal")
    @PostMapping("/queryWithdrawal")
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(@RequestBody GatewayQueryWithdrawalReq req) {
        return ofaPayPaymentGatewayAdapter.queryWithdrawal(req);
    }

    @Autowired
    private PaymentFeignClient paymentFeignClient;

    @Operation(summary = "depositFeign")
    @PostMapping("/depositFeign")
    public RetResult<com.mc.payment.api.model.rsp.DepositRsp> depositFeign(@RequestBody com.mc.payment.api.model.req.DepositReq req) {
        return paymentFeignClient.deposit(req);
    }

    @Autowired
    private CurrencyRateService currencyRateService;

    @Operation(summary = "refreshCurrencyRate")
    @GetMapping("/refreshCurrencyRate")
    public void refreshCurrencyRate() {
        currencyRateService.refreshCurrencyRate();
    }

    @Operation(summary = "syncBalanceByFireBlocks")
    @GetMapping("/syncBalanceByFireBlocks")
    public void syncBalanceByFireBlocks(String accountId) {
        channelWalletService.syncBalanceByFireBlocks(accountId);
//        channelWalletService.syncBalanceByFireBlocksAll();

    }

    @Autowired
    MerchantServiceImpl merchantService;

    @Operation(summary = "syncBalanceByFireBlocks")
    @GetMapping("/gen")
    public void gen() {
//        channelWalletService.syncBalanceByFireBlocks(accountId);
        //merchantService.afterMerchantCreateCheezeePayHandle("1818857020563296258", "prop-trading", AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.CHEEZEE_PAY);
    }

    @Autowired
    private MailClient mailClient;

    @Autowired
    private IJobPlanService jobPlanService;

    @Autowired
    private IEmailTemplateService emailTemplateService;

    @Autowired
    private MerchantServiceFacade merchantServiceFacade;

    @GetMapping("/sendEmail")
    public void sendEmail() {

        /**{"content": "您好：<br>跟踪ID：[8927qwq32384728s92342334]<br/>出金商户：[TEST-LinerBem]<br>挂起原因：出金账户地址余额不足。<br>建议您尽快处理挂起的订单并完成出金操作。", "subject": "出金操作失败，订单挂起，请尽快处理", "recipientMail": "61@qq.com"}
         *
         */

//        List<JobPlanEntity> list = jobPlanService.listByLimit(JobPlanHandlerEnum.SEND_EMAIL, JobPlanStatusEnum.AWAIT.getCode(), 100);
//        JobPlanEntity jobPlanEntity = list.get(0);


        BatchSendMailDTO mailDTO = new BatchSendMailDTO();

        Map<String, EmailTemplateEntity> emailTemplateCacheMap = emailTemplateService.getEmailTemplateCacheMap();

        EmailTemplateEntity emailTemplateEntity = emailTemplateCacheMap.get(EmailContentEnum.INSUFFICIENT_BALANCE.getCode());
        MerchantEntity merchantEntity = merchantService.getBaseMapper().selectById("1829086498631618562");
        //替换参数
        String assetMsg = StringUtils.isBlank(merchantEntity.getName()) ? "" : "，您的资产：" + "SOL" + "，网络协议：" + "xxlJob";

        //String replaceFirst = emailTemplateEntity.getContent().replaceFirst("%s", initialEntity.getContent());
        //String replaceFirst1 = replaceFirst.replaceFirst("%s", merchantEntity.getName() + assetMsg);
        EmailJobParamByReserveDto emailJobParamDto = new EmailJobParamByReserveDto(merchantEntity.getAlarmEmail(),
                EmailContentEnum.INSUFFICIENT_BALANCE.getSubject(),
                emailTemplateEntity.getContent().replaceFirst("%s", merchantEntity.getName() + assetMsg),
                merchantEntity.getId(),
                "SOL");

        mailDTO.setAddressList(Arrays.asList("scorpio648654167@gmail.com,lqc110551@gmail.com,1779359377@qq.com".split(",")));
        mailDTO.setSubject("\"出金操作失败，订单挂起，请尽快处理\"");
        mailDTO.setContent(emailJobParamDto.getContent());
        mailDTO.setIfTemplate(0);

        Result sendMailResult = mailClient.sendBatchMail(mailDTO);
        System.out.println(sendMailResult.getCode());
        System.out.println(sendMailResult.getDesc());
    }


    @GetMapping("/sendNotice")
    public void sendNotice() {
        withdrawalRecordServiceFacade.riskScan();
    }

    @Autowired
    private DepositManagerImpl depositManager;


//    @Autowired
//    private OfaPayConfig ofaPayConfig;
//
//    @Operation(summary = "test222")
//    @GetMapping("/test222")
//    public void test222() throws Exception {
//        List<ChannelAssetEntity> list = channelAssetService.list(Wrappers.lambdaQuery(ChannelAssetEntity.class)
//                .eq(ChannelAssetEntity::getChannelSubType, ChannelSubTypeEnum.OFA_PAY.getCode()));
//        for (ChannelAssetEntity channelAssetEntity : list) {
//            AssetConfigEntity assetConfig = assetConfigService.getOne(1, channelAssetEntity.getAssetName(), channelAssetEntity.getNetProtocol());
//            String scode = assetConfig.getTokenAddress();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.set("scode", scode);
//            channelAssetEntity.setChannelCredential(jsonObject.toString());
//        }
//        channelAssetService.updateBatchById(list);
//    }


    @Operation(summary = "queryAccount")
    @GetMapping("/queryAccount")
    public RetResult<VaultAccountVo> queryAccount(String vaultAccountId) {
        return fireBlocksAPI.queryAccount(vaultAccountId);
    }

    @Operation(summary = "replenishWalletQuantityJob")
    @GetMapping("/replenishWalletQuantityJob")
    public RetResult replenishWalletQuantityJob() {
//        merchantServiceFacade.replenishFireBlocksWalletQuantityJob();

        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.OFA_PAY);
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.PAY_PAL);
        merchantServiceFacade.replenishWalletQuantityJob(ChannelSubTypeEnum.PASS_TO_PAY);
        return RetResult.ok();
    }

    @Autowired
    private MerchantChannelAssetService merchantChannelAssetService;

    @Operation(summary = "merchantWallet")
    @GetMapping("/merchantWallet")
    public RetResult scanDepositRequest() {
        ChannelSubTypeEnum channelSubTypeEnum = merchantChannelAssetService.choosePaymentChannel("1818857020563296258", 1,
                "HKD",
                "PayPal", true);
        return RetResult.ok();
    }

    @Operation(summary = "riskScan")
    @GetMapping("/riskScan")
    public void riskScan() {
        withdrawalRecordServiceFacade.riskScan();
    }

    @Autowired
    private FireBlocksWalletManager fireBlocksWalletManager;

    @Operation(summary = "autoGenerateWalletJob")
    @GetMapping("/autoGenerateWalletJob")
    public void autoGenerateWalletJob() {
        fireBlocksWalletManager.autoGenerateWalletJob();
    }

    @Operation(summary = "generateWalletJob")
    @GetMapping("/generateWalletJob")
    public void generateWalletJob() {
        fireBlocksWalletManager.generateWalletJob();
    }


    @Operation(summary = "initGenerateWallet")
    @GetMapping("/initGenerateWallet")
    public void initGenerateWallet(@RequestParam("merchantId") String merchantId) {
        fireBlocksWalletManager.initGenerateWallet(merchantId);
    }


}
