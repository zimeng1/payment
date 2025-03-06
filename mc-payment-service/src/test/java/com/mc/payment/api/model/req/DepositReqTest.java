package com.mc.payment.api.model.req;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DepositReqTest {

    private DepositReq depositReq;

    @BeforeEach
    public void setUp() {
        depositReq = new DepositReq();
        depositReq.setTrackingId("trackingId");
        depositReq.setBusinessName("businessName");
        depositReq.setAssetType(0);
        depositReq.setAmount(new BigDecimal("100"));
        depositReq.setAssetName("assetName");
        depositReq.setUserSelectable(0);
        depositReq.setWebhookUrl("https://yourdomain.com/webhook");
        depositReq.setSuccessPageUrl("https://yourdomain.com/success");
        depositReq.setActiveTime(0);
    }

    @Test
    public void validate_UserSelectableZeroAndNetProtocolBlank_ShouldThrowException() {
        depositReq.setUserSelectable(0);
        depositReq.setNetProtocol("");
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_UserSelectableZeroAndNetProtocolNotBlank_ShouldPass() {
        depositReq.setUserSelectable(0);
        depositReq.setNetProtocol("netProtocol");
        depositReq.validate(); // 不应抛出异常
    }

    @Test
    public void validate_SkipPageOneAndUserSelectableNotZero_ShouldThrowException() {
        depositReq.setSkipPage(1);
        depositReq.setUserSelectable(1);
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_SkipPageOneAndUserSelectableZeroAndNetProtocolBlank_ShouldThrowException() {
        depositReq.setSkipPage(1);
        depositReq.setUserSelectable(0);
        depositReq.setNetProtocol("");
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_SkipPageOneAndUserSelectableZeroAndNetProtocolNotBlank_ShouldPass() {
        depositReq.setSkipPage(1);
        depositReq.setUserSelectable(0);
        depositReq.setNetProtocol("netProtocol");
        depositReq.validate(); // 不应抛出异常
    }

    @Test
    public void validate_NetProtocolBlankAndBankCodeNotBlank_ShouldThrowException() {
        depositReq.setNetProtocol("");
        depositReq.setBankCode("bankCode");
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_NetProtocolNotBlankAndBankCodeNotBlank_ShouldPass() {
        depositReq.setNetProtocol("netProtocol");
        depositReq.setBankCode("bankCode");
        depositReq.validate(); // 不应抛出异常
    }

    @Test
    public void validate_InvalidWebhookUrl_ShouldThrowException() {
        depositReq.setWebhookUrl("invalidUrl");
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_InvalidSuccessPageUrl_ShouldThrowException() {
        depositReq.setSuccessPageUrl("invalidUrl");
        assertThrows(IllegalArgumentException.class, depositReq::validate);
    }

    @Test
    public void validate_ValidWebhookAndSuccessPageUrls_ShouldPass() {
        depositReq.setUserSelectable(1);
        depositReq.setWebhookUrl("https://yourdomain.com/webhook");
        depositReq.setSuccessPageUrl("https://yourdomain.com/success");
        depositReq.validate(); // 不应抛出异常
    }
}
