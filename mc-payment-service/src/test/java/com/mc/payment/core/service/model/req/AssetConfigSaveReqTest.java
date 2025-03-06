package com.mc.payment.core.service.model.req;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class AssetConfigSaveReqTest {

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Test a valid request
        AssetConfigSaveReq validReq = new AssetConfigSaveReq("BTC", "BRC20", 1, "0x1234567890abcdef","网络协议");
        Set<ConstraintViolation<AssetConfigSaveReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

        // Test an invalid request with assetName exceeding the max length
        AssetConfigSaveReq invalidReq1 = new AssetConfigSaveReq(StringUtils.repeat("BTC", 21), "BRC20", 1, "0x1234567890abcdef","网络协议");
        violations = validator.validate(invalidReq1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[资产名称]长度不能超过20", violations.iterator().next().getMessage());

        // Test an invalid request with assetNet being null
        AssetConfigSaveReq invalidReq2 = new AssetConfigSaveReq("BTC", null, 1, "0x1234567890abcdef","网络协议");
        violations = validator.validate(invalidReq2);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[资产网络]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with tokenAddress exceeding the max length
        AssetConfigSaveReq invalidReq3 = new AssetConfigSaveReq("BTC", "BRC20", 1, StringUtils.repeat("0x", 128),"网络协议");
        violations = validator.validate(invalidReq3);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[合约地址]长度不能超过255", violations.iterator().next().getMessage());

        // Test an invalid request with status out of range
        AssetConfigSaveReq invalidReq4 = new AssetConfigSaveReq("BTC", "BRC20", 2, "0x1234567890abcdef","网络协议");
        violations = validator.validate(invalidReq4);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[资产状态]必须为0或1,0:禁用,1:激活", violations.iterator().next().getMessage());

        AssetConfigSaveReq invalidReq5 = new AssetConfigSaveReq("BTC", "null", 1, "0x1234567890abcdef",null);
        violations = validator.validate(invalidReq5);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[网络协议]不能为空", violations.iterator().next().getMessage());


        AssetConfigSaveReq invalidReq6 = new AssetConfigSaveReq("BTC", "BRC20", 1, "0x1234567890abcdef",StringUtils.repeat("BTC", 21));
        violations = validator.validate(invalidReq6);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[网络协议]长度不能超过20", violations.iterator().next().getMessage());
    }


}
