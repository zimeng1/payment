package com.mc.payment.core.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Conor
 * @since 2024/5/23 上午10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailJobParamDto {
    /**
     * 收件人邮件地址
     */
    private String recipientMail;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
}
