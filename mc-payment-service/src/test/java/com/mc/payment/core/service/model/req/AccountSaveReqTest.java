package com.mc.payment.core.service.model.req;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class AccountSaveReqTest {

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AccountSaveReq validReq = new AccountSaveReq();
//        validReq.setAccountType(0);
        validReq.setChannelSubType(0);
//        validReq.setAssetIdList("1,2,3");
        validReq.setMerchantId("merchant1");
//        validReq.setContractDate(new Date());
//        validReq.setReserveRatio(BigDecimal.ZERO);
//        validReq.setDefaultStatus(0);
//        validReq.setWalletAddress("0x1234567890abcdef");

        // Test a valid request
        Set<ConstraintViolation<AccountSaveReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

//        // Test an invalid request with assetIdList exceeding the max length
//        AccountSaveReq invalidReq1 = new AccountSaveReq(validReq);
//        invalidReq1.setAssetIdList(StringUtils.repeat("a", 256));
//        violations = validator.validate(invalidReq1);
//        Assertions.assertEquals(1, violations.size());
//        Assertions.assertEquals("[资产id集合,数据用英文逗号隔开]长度不能超过255", violations.iterator().next().getMessage());

        // Test an invalid request with merchantId being null
        AccountSaveReq invalidReq2 = new AccountSaveReq(validReq);
        invalidReq2.setMerchantId(null);
        violations = validator.validate(invalidReq2);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[账户签约的商户的ID]不能为空", violations.iterator().next().getMessage());

//        // Test an invalid request with contractDate being null
//        AccountSaveReq invalidReq3 = new AccountSaveReq(validReq);
//        invalidReq3.setContractDate(null);
//        violations = validator.validate(invalidReq3);
//        Assertions.assertEquals(1, violations.size());
//        Assertions.assertEquals("[账户签约时间]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with reserveRatio being negative
//        AccountSaveReq invalidReq4 = new AccountSaveReq(validReq);
//        invalidReq4.setReserveRatio(BigDecimal.valueOf(-1));
//        violations = validator.validate(invalidReq4);
//        Assertions.assertEquals(1, violations.size());
//        Assertions.assertEquals("[商户的储备金]不能小于0", violations.iterator().next().getMessage());

        // Test an invalid request with walletAddress exceeding the max length
//        AccountSaveReq invalidReq5 = new AccountSaveReq(validReq);
//        invalidReq5.setWalletAddress(StringUtils.repeat("a", 256));
//        violations = validator.validate(invalidReq5);
//        Assertions.assertEquals(1, violations.size());
//        Assertions.assertEquals("[钱包地址]长度不能超过255", violations.iterator().next().getMessage());
    }
}
