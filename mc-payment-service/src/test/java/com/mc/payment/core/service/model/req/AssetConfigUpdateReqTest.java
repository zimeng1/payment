package com.mc.payment.core.service.model.req;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class AssetConfigUpdateReqTest {

    @Test
    public void testIdValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AssetConfigUpdateReq validReq = new AssetConfigUpdateReq("validId", "BRC20", "0x1234567890abcdef", 1);

        // Test a valid request
        Set<ConstraintViolation<AssetConfigUpdateReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

        // Test an invalid request with id being null
        AssetConfigUpdateReq invalidReq = new AssetConfigUpdateReq(null, validReq.getAssetNet(), null, validReq.getStatus());
        violations = validator.validate(invalidReq);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[资产id]不能为空", violations.iterator().next().getMessage());
    }

    @Test
    public void testAssetNetValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AssetConfigUpdateReq validReq = new AssetConfigUpdateReq("validId", "BRC20", "0x1234567890abcdef", 1);

        // Test an invalid request with assetNet being null
        AssetConfigUpdateReq invalidReq = new AssetConfigUpdateReq(validReq.getId(), null, null, validReq.getStatus());
        Set<ConstraintViolation<AssetConfigUpdateReq>> violations = validator.validate(invalidReq);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[资产网络]不能为空", violations.iterator().next().getMessage());
    }

    @Test
    public void testStatusValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AssetConfigUpdateReq validReq = new AssetConfigUpdateReq("validId", "BRC20", "0x1234567890abcdef", 1);

        // Test an invalid request with status being null
        AssetConfigUpdateReq invalidReq = new AssetConfigUpdateReq(validReq.getId(), validReq.getAssetNet(), null, null);
        Set<ConstraintViolation<AssetConfigUpdateReq>> violations = validator.validate(invalidReq);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[资产状态]必须为0或1,0:禁用,1:激活", violations.iterator().next().getMessage());
    }

    @Test
    public void testTokenAddressValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AssetConfigUpdateReq validReq = new AssetConfigUpdateReq("validId", "BRC20", "0x1234567890abcdef", 1);

        // Test an invalid request with tokenAddress being null
        AssetConfigUpdateReq invalidReq = new AssetConfigUpdateReq(validReq.getId(), validReq.getAssetNet(), null, validReq.getStatus());
        Set<ConstraintViolation<AssetConfigUpdateReq>> violations = validator.validate(invalidReq);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[合约地址]不能为空", violations.iterator().next().getMessage());
    }
}
