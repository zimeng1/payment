package com.mc.payment.core.service.model.req;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class AccountUpdateReqTest {

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Prepare a valid request
        AccountUpdateReq validReq = new AccountUpdateReq();
//        validReq.setAccountType(0);
        validReq.setChannelSubType(0);
//        validReq.setAssetIdList("1,2,3");
        validReq.setMerchantId("merchant1");
//        validReq.setContractDate(new Date());
//        validReq.setReserveRatio(BigDecimal.ZERO);
//        validReq.setDefaultStatus(0);
//        validReq.setWalletAddress("0x1234567890abcdef");
        validReq.setId("validId");

        // Test a valid request
        Set<ConstraintViolation<AccountUpdateReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

        // Test an invalid request with id being null
        AccountUpdateReq invalidReq = new AccountUpdateReq();
//        invalidReq.setAccountType(validReq.getAccountType());
        invalidReq.setChannelSubType(validReq.getChannelSubType());
//        invalidReq.setAssetIdList(validReq.getAssetIdList());
        invalidReq.setMerchantId(validReq.getMerchantId());
//        invalidReq.setContractDate(validReq.getContractDate());
//        invalidReq.setReserveRatio(validReq.getReserveRatio());
//        invalidReq.setDefaultStatus(validReq.getDefaultStatus());
//        invalidReq.setWalletAddress(validReq.getWalletAddress());
        invalidReq.setId(null);
        violations = validator.validate(invalidReq);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("[账户id]不能为空", violations.iterator().next().getMessage());
    }
}
