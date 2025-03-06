package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.EmailTemplateEntity;

import java.util.Map;


public interface IEmailTemplateService extends IService<EmailTemplateEntity> {

    Map<String, EmailTemplateEntity> getEmailTemplateCacheMap();

}
