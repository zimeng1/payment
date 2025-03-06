package com.mc.payment.core.service.model.req;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

class WalletSaveReqTest {
    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Test a valid request
        WalletSaveReq validReq = new WalletSaveReq("1", "BTC", "walletAddress", BigDecimal.TEN, "privateKey", "remark", "externalId");
        Set<ConstraintViolation<WalletSaveReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

        // Test an invalid request with accountId null
        WalletSaveReq invalidReq1 = new WalletSaveReq(null, validReq.getAssetName(), validReq.getWalletAddress(), validReq.getBalance(), validReq.getPrivateKey(), "remark", "externalId");
        violations = validator.validate(invalidReq1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[账号id]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with accountId exceeding the max length
        WalletSaveReq invalidReq1_1 = new WalletSaveReq(StringUtils.repeat("1", 21), "BTC", "walletAddress", BigDecimal.TEN, "privateKey", "remark", "externalId");
        violations = validator.validate(invalidReq1_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[账号id]长度不能超过20", violations.iterator().next().getMessage());

        // Test an invalid request with assetName null
        WalletSaveReq invalidReq3 = new WalletSaveReq(validReq.getAccountId(), null, validReq.getWalletAddress(), validReq.getBalance(), validReq.getPrivateKey(), "remark", "externalId");
        violations = validator.validate(invalidReq3);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[资产名称]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with assetName exceeding the max length
        WalletSaveReq invalidReq3_1 = new WalletSaveReq("1", StringUtils.repeat("BTC", 21), "walletAddress", BigDecimal.TEN, "privateKey", "remark", "externalId");
        violations = validator.validate(invalidReq3_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[资产名称]长度不能超过20", violations.iterator().next().getMessage());

        // Test an invalid request with walletAddress null
        WalletSaveReq invalidReq4 = new WalletSaveReq(validReq.getAccountId(), validReq.getAssetName(), null, validReq.getBalance(), validReq.getPrivateKey(), "remark", "externalId");
        violations = validator.validate(invalidReq4);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[钱包地址]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with walletAddress exceeding the max length
        WalletSaveReq invalidReq4_1 = new WalletSaveReq("1", "BTC", StringUtils.repeat("walletAddress", 255), BigDecimal.TEN, "privateKey", "remark", "externalId");
        violations = validator.validate(invalidReq4_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[钱包地址]长度不能超过255", violations.iterator().next().getMessage());

        // Test an invalid request with balance null
        WalletSaveReq invalidReq5 = new WalletSaveReq(validReq.getAccountId(), validReq.getAssetName(), validReq.getWalletAddress(), null, validReq.getPrivateKey(), "remark", "externalId");
        violations = validator.validate(invalidReq5);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[余额]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with privateKey null
        WalletSaveReq invalidReq6 = new WalletSaveReq(validReq.getAccountId(), validReq.getAssetName(), validReq.getWalletAddress(), validReq.getBalance(), null, "remark", "externalId");
        violations = validator.validate(invalidReq6);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[私钥]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with privateKey exceeding the max length
        WalletSaveReq invalidReq6_1 = new WalletSaveReq("1", "BTC", "walletAddress", BigDecimal.TEN, StringUtils.repeat("privateKey", 255), "remark", "externalId");
        violations = validator.validate(invalidReq6_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[私钥]长度不能超过255", violations.iterator().next().getMessage());
        // Test an invalid request with remark null
        WalletSaveReq invalidReq7 = new WalletSaveReq(validReq.getAccountId(), validReq.getAssetName(), validReq.getWalletAddress(), validReq.getBalance(), "privateKey", null, "externalId");
        violations = validator.validate(invalidReq7);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[备注]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with remark exceeding the max length
        WalletSaveReq invalidReq7_1 = new WalletSaveReq("1", "BTC", "walletAddress", BigDecimal.TEN, "privateKey", StringUtils.repeat("remark", 255), "externalId");
        violations = validator.validate(invalidReq7_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[备注]长度不能超过255", violations.iterator().next().getMessage());


        // Test an invalid request with externalId exceeding the max length
        WalletSaveReq invalidReq8_1 = new WalletSaveReq("1", "BTC", "walletAddress", BigDecimal.TEN, "privateKey", "remark", StringUtils.repeat("externalId", 50));
        violations = validator.validate(invalidReq8_1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[外部系统钱包id]长度不能超过50", violations.iterator().next().getMessage());


    }
}