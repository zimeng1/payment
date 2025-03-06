package com.mc.payment.core.service.model.req;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Date;
import java.util.Set;

public class ChannelSaveReqTest {

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Test a valid request
        ChannelSaveReq validReq = new ChannelSaveReq("Channel1", 1, 0, 0, 1, "Param1", new Date(), new Date(), "John Doe", "1234567890");
        Set<ConstraintViolation<ChannelSaveReq>> violations = validator.validate(validReq);
        Assertions.assertTrue(violations.isEmpty());

        // Test an invalid request with name exceeding the max length
        ChannelSaveReq invalidReq1 = new ChannelSaveReq(StringUtils.repeat("a", 21), validReq.getStatus(), validReq.getChannelType(), validReq.getChannelSubType(), validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
        violations = validator.validate(invalidReq1);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[通道名称]长度不能超过20", violations.iterator().next().getMessage());

        // Test an invalid request with status being null
        ChannelSaveReq invalidReq2 = new ChannelSaveReq(validReq.getName(), null, validReq.getChannelType(), validReq.getChannelSubType(), validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
        violations = validator.validate(invalidReq2);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[通道状态]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with channelType being null
        ChannelSaveReq invalidReq3 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), null, validReq.getChannelSubType(), validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
        violations = validator.validate(invalidReq3);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[通道类型]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with channelSubType being null
        ChannelSaveReq invalidReq4 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), validReq.getChannelType(), null, validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
        violations = validator.validate(invalidReq4);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[通道子类型]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with priority being null
        ChannelSaveReq invalidReq5 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), validReq.getChannelType(), validReq.getChannelSubType(), null, validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
        violations = validator.validate(invalidReq5);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[优先级]不能为空", violations.iterator().next().getMessage());

        // Test an invalid request with param exceeding the max length
//        ChannelSaveReq invalidReq6 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), validReq.getChannelType(), validReq.getChannelSubType(), validReq.getPriority(), StringUtils.repeat("a", 256), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq6);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[通道参数]长度不能超过255", violations.iterator().next().getMessage());

        // Test an invalid request with contact exceeding the max length
        ChannelSaveReq invalidReq7 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), validReq.getChannelType(), validReq.getChannelSubType(), validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), StringUtils.repeat("a", 21), validReq.getContactTel());
        violations = validator.validate(invalidReq7);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[联系人]长度不能超过20", violations.iterator().next().getMessage());

        // Test an invalid request with contactTel exceeding the max length
        ChannelSaveReq invalidReq8 = new ChannelSaveReq(validReq.getName(), validReq.getStatus(), validReq.getChannelType(), validReq.getChannelSubType(), validReq.getPriority(), validReq.getParam(), validReq.getExpirationDateStart(), validReq.getExpirationDateEnd(), validReq.getContact(), StringUtils.repeat("a", 21));
        violations = validator.validate(invalidReq8);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals("[联系方式]长度不能超过20", violations.iterator().next().getMessage());
    }
}
