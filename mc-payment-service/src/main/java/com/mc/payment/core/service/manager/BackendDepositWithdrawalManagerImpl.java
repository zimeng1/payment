package com.mc.payment.core.service.manager;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.api.model.rsp.QueryAssetRsp;
import com.mc.payment.api.model.rsp.QueryAssetSupportedBankRsp;
import com.mc.payment.api.model.rsp.WithdrawalRsp;
import com.mc.payment.api.util.AKSKUtil;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.req.BackendDepositRequestReq;
import com.mc.payment.core.service.model.req.BackendQueryAssetReq;
import com.mc.payment.core.service.model.req.BackendQueryAssetSupportedBankReq;
import com.mc.payment.core.service.model.req.BackendWithdrawalRequestReq;
import com.mc.payment.core.service.service.IMerchantService;
import com.mc.payment.core.service.util.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 提供给后台管理使用的出入金功能
 * 主要是封装web端的参数,调用openapi中出入金相关的服务
 *
 * @author Conor
 * @since 2024-11-25 14:22:12.180
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BackendDepositWithdrawalManagerImpl implements BackendDepositWithdrawalManager {
    public static final String MCPAYMENT = "MCPayment-";
    private final IMerchantService merchantService;
    private final AppConfig appConfig;

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 校验商户id是否存当前登录用户所属的商户
     *
     * @param merchantId
     */
    private static void verifyMerchant(String merchantId) {
        Optional<Object> userMerchantIdsObj = Optional.ofNullable(StpUtil.getExtra("userMerchantIds"));
        List<String> userMerchantIds = userMerchantIdsObj
                .map(obj -> Arrays.stream(obj.toString().split(",")).collect(Collectors.toList()))
                .orElseThrow(() -> new BusinessException("当前登录用户无权限操作该商户"));

        if (!userMerchantIds.contains("*") && !userMerchantIds.contains(merchantId)) {
            throw new BusinessException("当前登录用户无权限操作该商户");
        }
    }

    @NotNull
    private MerchantEntity getMerchantEntity(String merchantId) {
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        if (merchantEntity == null) {
            throw new BusinessException("商户不存在");
        }
        return merchantEntity;
    }

    @Nullable
    private static <T> T getRsp(String body, Class<T> clazz) {
        if (!JSONUtil.isTypeJSONObject(body)) {
            throw new BusinessException(body);
        }
        JSONObject result = JSONUtil.parseObj(body);
        if (result.getInt("code") != 200) {
            throw new BusinessException(result.getStr("msg"));
        }
        return JSONUtil.toBean(result.getJSONObject("data"), clazz);
    }

    @Nullable
    private static <T> List<T> getRspList(String body, Class<T> clazz) {
        JSONObject result = JSONUtil.parseObj(body);
        if (result.getInt("code") != 200) {
            throw new BusinessException(result.getStr("msg"));
        }
        return JSONUtil.toList(result.getJSONArray("data"), clazz);
    }

    /**
     * 调用openapi
     *
     * @param uri
     * @param paramMap
     * @param paymentOpenapiAccessKey
     * @param paymentOpenapiSecretKey
     * @return
     */
    private String callOpenApi(String uri, Map<String, Object> paramMap, String paymentOpenapiAccessKey, String paymentOpenapiSecretKey) {
        String body = null;
        String url = null;
        try {
            // 如果是本地启动,则调用本地的openapi
            if (CommonConstant.DEV.equals(active)) {
                url = "http://localhost:8000" + uri;
            } else {
                url = appConfig.getPaymentRealend() + uri;
            }
            String timestamp = String.valueOf(System.currentTimeMillis());

            HttpRequest httpRequest = HttpUtil.createPost(url)
                    .header("Content-Type", "application/json")
                    .header("X-Access-Key", paymentOpenapiAccessKey)
                    .header("X-Signature", AKSKUtil.calculateHMAC(paymentOpenapiAccessKey + timestamp + uri,
                            paymentOpenapiSecretKey))
                    .header("X-Timestamp", timestamp)
                    .header("X-RequestURI", uri);
            body = httpRequest.body(JSONUtil.toJsonStr(paramMap)).execute().body();
        } catch (Exception e) {
            log.error("调用openapi报错", e);
            throw new BusinessException("调用payment接口报错", e);
        } finally {
            log.info("调用openapi请求参数: url={}, paramMap={},paymentOpenapiAccessKey={},body:{}", url, paramMap,
                    paymentOpenapiAccessKey, body);
        }
        return body;
    }

    /**
     * 查询资产
     * 1. 校验商户id是否存当前登录用户所属的商户
     * 2. 根据商户id查询ak/sk配置
     * 3. 调用openapi查询资产
     *
     * @param req
     * @return
     */
    @Override
    public List<QueryAssetRsp> queryAsset(BackendQueryAssetReq req) {
        verifyMerchant(req.getMerchantId());
        MerchantEntity merchantEntity = this.getMerchantEntity(req.getMerchantId());
        // 调用openapi查询资产
        String uri = "/openapi/v1/queryAsset";
        String paymentOpenapiAccessKey = merchantEntity.getAccessKey();
        String paymentOpenapiSecretKey = merchantEntity.getSecretKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("assetType", req.getAssetType());

        String body = this.callOpenApi(uri, paramMap, paymentOpenapiAccessKey, paymentOpenapiSecretKey);
        return getRspList(body, QueryAssetRsp.class);
    }

    /**
     * 查询支持的银行
     *
     * @param req
     * @return
     */
    @Override
    public List<QueryAssetSupportedBankRsp> queryAssetSupportedBank(BackendQueryAssetSupportedBankReq req) {
        verifyMerchant(req.getMerchantId());
        MerchantEntity merchantEntity = this.getMerchantEntity(req.getMerchantId());
        // 调用openapi查询支持的银行
        String uri = "/openapi/v1/queryAssetSupportedBank";
        String paymentOpenapiAccessKey = merchantEntity.getAccessKey();
        String paymentOpenapiSecretKey = merchantEntity.getSecretKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("paymentType", req.getPaymentType());
        paramMap.put("assetName", req.getAssetName());
        paramMap.put("netProtocol", req.getNetProtocol());

        String body = this.callOpenApi(uri, paramMap, paymentOpenapiAccessKey, paymentOpenapiSecretKey);
        return getRspList(body, QueryAssetSupportedBankRsp.class);
    }

    /**
     * 入金申请
     *
     * @param req
     * @return
     */
    @Override
    public DepositRsp depositRequest(BackendDepositRequestReq req, HttpServletRequest request) {
        verifyMerchant(req.getMerchantId());
        MerchantEntity merchantEntity = this.getMerchantEntity(req.getMerchantId());
        // 调用openapi入金申请
        String uri = "/openapi/v1/deposit/request";
        String paymentOpenapiAccessKey = merchantEntity.getAccessKey();
        String paymentOpenapiSecretKey = merchantEntity.getSecretKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("assetType", req.getAssetType());
        paramMap.put("amount", req.getAmount());
        paramMap.put("assetName", req.getAssetName());
        paramMap.put("netProtocol", req.getNetProtocol());
        paramMap.put("bankCode", req.getBankCode());

        // 加上前缀用来区分是后台发起的
        paramMap.put("trackingId", MCPAYMENT + IdUtil.fastSimpleUUID());
        paramMap.put("businessName", "后管入金");
        paramMap.put("userSelectable", 0);
        paramMap.put("webhookUrl", appConfig.getPaymentRealend() + "/openapi/webhook/mcPayment/deposit");
        // 跳入金申请页面
        paramMap.put("successPageUrl", appConfig.getPaymentDomain() + "/mc-payment/capital/enter");
        paramMap.put("remark", "后管入金");
        // 半小时
        paramMap.put("activeTime", 1800000);
        // 加上前缀用来区分是后台发起的
        paramMap.put("userId", MCPAYMENT + StpUtil.getExtra("userId"));
        paramMap.put("userIp", IPUtil.getClientIP(request));
        // 不跳过收银页面
        paramMap.put("skipPage", 0);

        String body = this.callOpenApi(uri, paramMap, paymentOpenapiAccessKey, paymentOpenapiSecretKey);
        return getRsp(body, DepositRsp.class);
    }

    /**
     * 出金申请
     *
     * @param req
     * @param request
     * @return
     */
    @Override
    public WithdrawalRsp withdrawalRequest(BackendWithdrawalRequestReq req, HttpServletRequest request) {
        verifyMerchant(req.getMerchantId());
        MerchantEntity merchantEntity = this.getMerchantEntity(req.getMerchantId());
        // 调用openapi出金申请
        String uri = "/openapi/v1/withdrawal/request";
        String paymentOpenapiAccessKey = merchantEntity.getAccessKey();
        String paymentOpenapiSecretKey = merchantEntity.getSecretKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("assetType", req.getAssetType());
        paramMap.put("assetName", req.getAssetName());
        paramMap.put("netProtocol", req.getNetProtocol());
        paramMap.put("bankCode", req.getBankCode());
        paramMap.put("bankName", req.getBankName());
        paramMap.put("accountName", req.getAccountName());
        paramMap.put("bankNum", req.getBankNum());
        paramMap.put("amount", req.getAmount());
        paramMap.put("address", req.getAddress());

        // 加上前缀用来区分是后台发起的
        paramMap.put("trackingId", MCPAYMENT + IdUtil.fastSimpleUUID());
        paramMap.put("userSelectable", 0);
        paramMap.put("webhookUrl", appConfig.getPaymentRealend() + "/openapi/webhook/mcPayment/withdrawal");
        paramMap.put("remark", "后管出金");
        // 加上前缀用来区分是后台发起的
        paramMap.put("userId", MCPAYMENT + StpUtil.getExtra("userId"));
        paramMap.put("userIp", IPUtil.getClientIP(request));
        if ("BRL".equals(req.getAssetName()) && "PIX".equals(req.getNetProtocol())) {
            Map<String, String> extraMap = new HashMap<>();
            extraMap.put("pixType", req.getPixType());
            extraMap.put("pixAccount", req.getPixAccount());
            extraMap.put("taxNumber", req.getTaxNumber());
            paramMap.put("extraMap", extraMap);
        }

        String body = this.callOpenApi(uri, paramMap, paymentOpenapiAccessKey, paymentOpenapiSecretKey);
        return getRsp(body, WithdrawalRsp.class);
    }
}
