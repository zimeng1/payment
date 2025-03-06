package com.mc.payment.gateway.channels.passtopay.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.passtopay.model.req.PassToPayCreateOrderReq;
import com.mc.payment.gateway.channels.passtopay.model.rsp.PassToPayCreateOrderRsp;
import com.mc.payment.gateway.channels.passtopay.util.SignatureGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * https://passtopay.io/api-f70d29f5231b483da80c5c21d98cb594
 *
 * @author Conor
 * @since 2024-10-15 15:21:28.431
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PassToPayServiceImpl implements PassToPayService {
    @Value("${app.passToPay-api-base-url}")
    private String apiBaseUrl = "https://pay.pass2pay.io";
    @Value("${app.passToPay-mch-no}")
    private String mchNo = "M1729061265";
    @Value("${app.passToPay-app-id}")
    private String appId = "670f61911112906f6f70771a";
    @Value("${app.passToPay-secret-key}")
    private String secretKey = "4j3jictdjsbg881f7b9vl8lrj3w0sesokm2q2j7csyyjs6esc77paif21f6rh4e5q6ewjp9n6uho767382bw13dr9wvcpuwcafwb24ppy069qiqtfkn255hw3jodzomf";

    @Override
    public RetResult<PassToPayCreateOrderRsp> createOrder(PassToPayCreateOrderReq req) {
        RetResult<PassToPayCreateOrderRsp> result = new RetResult<>();
        Map<String, String> map = req.convertMap();
        String resultStr = null;
        try {
            //商家編號，在管理後台可查看。 示例值：M1621873433953
            map.put("mchNo", mchNo);
            // 应用id 在管理後台可查看。 示例值：60cc09bce4b0f1c0b83761c9
            map.put("appId", appId);
            // 簽名類型，目前僅支援MD5方式。 示例值：MD5
            map.put("signType", "MD5");
            // 簽名值，詳細請參考請求簽名。 示例值：5f4dcc3b5aa765d61d8327deb882cf99
            map.put("sign", SignatureGenerator.generateSignature(map, secretKey));


            resultStr = HttpUtil.createPost(apiBaseUrl + "/api/pay/cashierOrder")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(map))
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(resultStr);
            Integer code = jsonObject.getInt("code");
            String msg = jsonObject.getStr("msg");
            String data = jsonObject.getStr("data");
            if (code == 0) {
                PassToPayCreateOrderRsp passToPayCreateOrderRsp = JSONUtil.toBean(data, PassToPayCreateOrderRsp.class);
                if (StrUtil.isBlank(passToPayCreateOrderRsp.getErrMsg())) {
                    result = RetResult.data(passToPayCreateOrderRsp);
                } else {
                    result = RetResult.error(passToPayCreateOrderRsp.getErrCode() + ":" + passToPayCreateOrderRsp.getErrMsg());
                }
            } else {
                result = RetResult.error(msg);
            }
        } catch (Exception e) {
            log.error("PassToPayService.createOrder error", e);
            result.setMsg("PassToPayService.createOrder error." + e.getMessage());
        } finally {
            log.info("PassToPayService.createOrder req:{},resultStr: {}", map, resultStr);
        }
        return result;
    }
}
