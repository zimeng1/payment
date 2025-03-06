package com.mc.payment.gateway.channels.passtopay.service;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.passtopay.model.req.PassToPayCreateOrderReq;
import com.mc.payment.gateway.channels.passtopay.model.rsp.PassToPayCreateOrderRsp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassToPayServiceTest {

    @InjectMocks
    private PassToPayServiceImpl passToPayService;

    //非单元测试,仅用于调试测试接口时使用
    @Test
    void testCreateOrder() {
        PassToPayCreateOrderReq req = new PassToPayCreateOrderReq();
//        req.setMchNo("M1711098244");
//        req.setAppId("65fd49849652d47734fbdec8");
        req.setMchOrderNo("MchOrderNo" + System.currentTimeMillis());
        req.setWayCode("ALI_WAP");
//        req.setWayCode("ALI_QR");
        req.setAmount(100000);
        req.setCurrency("CNY");
        req.setReqTime(System.currentTimeMillis());
        req.setVersion("1.1");
//        req.setSignType("MD5");
        // 用户id 这里随机生成 防止上游风控(一天内超过5笔超时订单,禁止下单)
        req.setCustNo("C" + System.currentTimeMillis());
        req.setRegisterTime(0L);
//        req.setUserName("");
//        req.setMbrTel("");
//        req.setIdNo("");
        req.setNotifyUrl("https://test-gateway.mcconnects.com/mc-payment/openapi/webhook/passToPay/deposit");
        req.setReturnUrl("https://www.yourserver.com/return");
        // 秒
        req.setExpiredTime(3600);
        req.setExtParam("");

//        String signature = SignatureGenerator.generateSignature(req.convertMap(),
//                "DgLkNtNyjeaMeemSUjlUYJKvbJCxwZpfOiiHjkc2G3MoKDsoQvbCRwhfPPG2L1MVakx2VgULDmMlTGwcGtrnASehWopaLsTISAHgLeSBxPfTDTFfAhxOGfKXD0ovuxu1");

//        req.setSign(signature);

        RetResult<PassToPayCreateOrderRsp> result = passToPayService.createOrder(req);

        Assertions.assertTrue(result.isSuccess());

    }
}