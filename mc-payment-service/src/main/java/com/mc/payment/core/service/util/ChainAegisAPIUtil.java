package com.mc.payment.core.service.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.core.service.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ChainAegis 服务接口工具类
 * <p>
 * <a href="https://chainaegis.gitbook.io/lang-zh">接口文档</a>
 *
 * @author Conor
 * @since 2024/5/15 下午5:13
 */
@Slf4j
@Component
public class ChainAegisAPIUtil {
    @Autowired
    private AppConfig appConfig;

    /**
     * 反洗钱校验,判断地址是否在黑名单中
     * <p>
     * https://chainaegiskyt.gitbook.io/cn
     *
     * @param address
     * @return true 在黑名单
     */
    public boolean getBlacklistLabelCheckUrl(String address) {
        if ("0x8c3FE01862Ce981816B2043598fC454A2400CDDd".equals(address)) {
            // 测试入金使用
            return true;
        }
        try {
            String url = appConfig.getChainAegisUrl() + "/kyt/execute";
            Map<String, Object> param = new HashMap<>();
            param.put("appCode", "xsintlg");
            param.put("apiKey", "xsintlg-20240509xxes$1");
            Map<String, Object> businessParam = new HashMap<>();
            businessParam.put("txnId", IdUtil.fastSimpleUUID());
            businessParam.put("txnIP", "192.168.0.1");
            businessParam.put("senderId", "1");
            businessParam.put("senderAddress", address);
            businessParam.put("receiverId", "1");
            businessParam.put("receiverAddress", address);
            businessParam.put("txnQuantity", "1");
            businessParam.put("txnTokenPriceUsd", "1");
            businessParam.put("txnTokenName", "1");
            businessParam.put("txnTokenSymbol", "1");
            businessParam.put("network", "1");
            businessParam.put("txnType", "1");

            param.put("businessParam", businessParam);
            String body = HttpUtil.post(url, JSONUtil.toJsonStr(param));
            log.info("getBlacklistLabelCheckUrl url:{},body:{}",url, body);

            if (JSONUtil.isTypeJSONObject(body)) {
                // "triggeredResult":"Pass" 说明无风险
                JSONObject jsonObject = JSONUtil.parseObj(body);
                if (jsonObject.getInt("code") == 200) {
                    String triggeredResult = jsonObject.getJSONObject("data").getStr("triggeredResult");
                    return !"Pass".equals(triggeredResult);
                }
            }
        } catch (Exception e) {
            log.error("getBlacklistLabelCheckUrl error", e);
        } finally {
            log.info("getBlacklistLabelCheckUrl address:{}", address);
        }
        return false;
    }


}
