package com.mc.payment.core.service.model.req;

import org.junit.jupiter.api.Test;

public class MerchantSaveReqTest {

    // todo 二期 用@CsvSource简化测试用例
    @Test
    public void testValidation() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();

//        // Test a valid request
//        MerchantSaveReq validReq = new MerchantSaveReq("Merchant1", "Channel1,Channel2,Channel3", "Param1", 1, 0, "SettlementSubject1", "SettlementInfo1", "SettlementEmail1", "Contact1", "ContactTel1");
//        Set<ConstraintViolation<MerchantSaveReq>> violations = validator.validate(validReq);
//        Assertions.assertTrue(violations.isEmpty());
//
//        // Test an invalid request with name being blank
//        MerchantSaveReq invalidReq1 = new MerchantSaveReq("", validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq1);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户名称]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with name exceeding the max length
//        MerchantSaveReq invalidReq2 = new MerchantSaveReq(StringUtils.repeat("a", 21), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq2);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户名称]长度不能超过20", violations.iterator().next().getMessage());
//
//        // Test an invalid request with channelIdList being blank
//        MerchantSaveReq invalidReq3 = new MerchantSaveReq(validReq.getName(), "", validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq3);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[通道id集合,数据用英文逗号隔开]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with channelIdList exceeding the max length
//        MerchantSaveReq invalidReq4 = new MerchantSaveReq(validReq.getName(), StringUtils.repeat("a", 256), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq4);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[通道id集合,数据用英文逗号隔开]长度不能超过255", violations.iterator().next().getMessage());
//
//        // Test an invalid request with param being blank
//        MerchantSaveReq invalidReq5 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), "", validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq5);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户参数]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with param exceeding the max length
//        MerchantSaveReq invalidReq6 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), StringUtils.repeat("a", 256), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq6);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户参数]长度不能超过255", violations.iterator().next().getMessage());
//
//        // Test an invalid request with status being null
//        MerchantSaveReq invalidReq7 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), null, validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq7);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户状态]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with status not in range
//        MerchantSaveReq invalidReq8 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), 2, validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq8);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户状态]必须为[0:禁用,1:激活]", violations.iterator().next().getMessage());
//
//        // Test an invalid request with businessScope being null
//        MerchantSaveReq invalidReq9 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), null, validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq9);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[业务范围]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with businessScope not in range
//        MerchantSaveReq invalidReq10 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), 2, validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq10);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[业务范围]必须为[0:Crypto]", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementSubject being blank
//        MerchantSaveReq invalidReq11 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), "", validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq11);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算主体]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementSubject exceeding the max length
//        MerchantSaveReq invalidReq12 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), StringUtils.repeat("a", 21), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq12);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算主体]长度不能超过20", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementInfo being blank
//        MerchantSaveReq invalidReq13 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), "", validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq13);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算信息]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementInfo exceeding the max length
//        MerchantSaveReq invalidReq14 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), StringUtils.repeat("a", 21), validReq.getSettlementEmail(), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq14);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算信息]长度不能超过20", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementEmail being blank
//        MerchantSaveReq invalidReq15 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), "", validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq15);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算对接人邮箱]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with settlementEmail exceeding the max length
//        MerchantSaveReq invalidReq16 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), StringUtils.repeat("a", 41), validReq.getContact(), validReq.getContactTel());
//        violations = validator.validate(invalidReq16);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[结算对接人邮箱]长度不能超过40", violations.iterator().next().getMessage());
//
//        // Test an invalid request with contact being blank
//        MerchantSaveReq invalidReq17 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), "", validReq.getContactTel());
//        violations = validator.validate(invalidReq17);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户联系人]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with contact exceeding the max length
//        MerchantSaveReq invalidReq18 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), StringUtils.repeat("a", 21), validReq.getContactTel());
//        violations = validator.validate(invalidReq18);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户联系人]长度不能超过20", violations.iterator().next().getMessage());
//
//        // Test an invalid request with contactTel being blank
//        MerchantSaveReq invalidReq19 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), "");
//        violations = validator.validate(invalidReq19);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户联系方式]不能为空", violations.iterator().next().getMessage());
//
//        // Test an invalid request with contactTel exceeding the max length
//        MerchantSaveReq invalidReq20 = new MerchantSaveReq(validReq.getName(), validReq.getChannelIdList(), validReq.getParam(), validReq.getStatus(), validReq.getBusinessScope(), validReq.getSettlementSubject(), validReq.getSettlementInfo(), validReq.getSettlementEmail(), validReq.getContact(), StringUtils.repeat("a", 21));
//        violations = validator.validate(invalidReq20);
//        Assertions.assertFalse(violations.isEmpty());
//        Assertions.assertEquals("[商户联系方式]长度不能超过20", violations.iterator().next().getMessage());
    }
}
