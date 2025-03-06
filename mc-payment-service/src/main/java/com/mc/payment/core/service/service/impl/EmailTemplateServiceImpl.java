package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.EmailTemplateEntity;
import com.mc.payment.core.service.mapper.EmailTemplateMapper;
import com.mc.payment.core.service.service.IEmailTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplateEntity> implements IEmailTemplateService {

    private Map<String,EmailTemplateEntity> emailTemplateMap = new HashMap<>();

    @PostConstruct
    public void init(){
        this.emailTemplateMap = this.list().stream().collect(Collectors.toMap(EmailTemplateEntity::getType, emailTemplateEntity -> emailTemplateEntity));
    }

    public Map<String, EmailTemplateEntity> getEmailTemplateCacheMap(){
        return emailTemplateMap;
    }
}
