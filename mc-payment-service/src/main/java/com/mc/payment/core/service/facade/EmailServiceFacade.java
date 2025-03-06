package com.mc.payment.core.service.facade;

import com.mc.message.dto.mail.BatchSendMailDTO;
import com.mc.message.feign.MailClient;
import com.mc.message.resp.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceFacade {

    private final MailClient mailClient;

    public ImmutablePair<Boolean, String> sendSimpleMessage(String to, String subject, String text) {
        ImmutablePair<Boolean, String> result = ImmutablePair.of(true, "邮件发送成功");
        try {
            BatchSendMailDTO mailDTO = new BatchSendMailDTO();
            mailDTO.setAddressList(Arrays.asList(to.split(",")));
            mailDTO.setSubject(subject);
            mailDTO.setContent(text);
            mailDTO.setIfTemplate(0);
            Result sendMailResult = mailClient.sendBatchMail(mailDTO);
            if (!"200".equals(sendMailResult.getCode())) {
                result = ImmutablePair.of(false, sendMailResult.getDesc());
                log.error("邮件发送失败,result:[{}]", sendMailResult);
            }
        } catch (Exception e) {
            result = ImmutablePair.of(false, "邮件发送异常," + e.getMessage());
            log.error("邮件发送异常,to:[{}],subject:[{}],text:[{}]", to, subject, text, e);
        }
        return result;
    }
}
