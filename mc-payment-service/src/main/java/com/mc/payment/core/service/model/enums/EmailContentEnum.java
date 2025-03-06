package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * @author Marty
 * @since 2024/6/19 11:35
 */
@Getter
public enum EmailContentEnum {

    RISK_ALARM("riskAlarm", "", ""),

    RESET_PASSWORD("resetPassword", "密码重置成功通知",
            "尊敬的用户：\n" +
                    "您好！\n" +
                    "您的账号：%s，密码已成功重置。" +
                    "重置密码：%s \n" +
                    "如果您没有进行此操作，请立即联系我们的客服团队。\n" +
                    "如您有任何疑问或需要进一步的帮助，请随时联系我们。\n" +
                    "谢谢。"),

    INSUFFICIENT_BALANCE("insufficientBalance", "出金账户余额不足，请及时补充余额",
            "您好：\n" +
                    "监控到，商户：%s" +
                    "，备付金低于预警值。\n" +
                    "为确保能够按时完成出金请求，避免延迟，建议您采取以下行动：\n" +
                    "检查出金账户的余额，确保备付金满足要求。\n" +
                    "如果余额不足，请尽快注入足够的资金到出金账户中。"),

    WITHDRAWAL_OP_FAIL("withdrawalOpFail", "出金操作失败，订单挂起，请尽快处理",
            "您好：\n" +
                    "跟踪ID：[%s]\n" +
                    "出金商户：[%s]\n" +
                    "挂起原因：出金账户地址余额不足。\n" +
                    "建议您尽快处理挂起的订单并完成出金操作。");

    private final String code;
    private final String subject;
    private final String content;

    EmailContentEnum(String code, String subject, String content) {
        this.code = code;
        this.subject = subject;
        this.content = content;
    }

    public static EmailContentEnum getEnumByCode(String code) {
        for (EmailContentEnum anEnum : EmailContentEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }
}
