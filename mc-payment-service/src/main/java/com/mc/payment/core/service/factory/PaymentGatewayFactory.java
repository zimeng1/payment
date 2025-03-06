package com.mc.payment.core.service.factory;


import com.mc.payment.common.constant.BusinessExceptionInfoEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.gateway.PaymentGateway;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentGatewayFactory implements ApplicationContextAware{

    private static volatile Map<Integer, PaymentGateway> strategies;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        strategies = new ConcurrentHashMap<>();
        ChannelSubTypeEnum[] values = ChannelSubTypeEnum.values();
        for (ChannelSubTypeEnum value : values) {
            if(Objects.isNull(value.getPaymentGatewayClass())){
                continue;
            }
            strategies.put(value.getCode(), (PaymentGateway) applicationContext.getBean(value.getPaymentGatewayClass()));
        }
    }

    public static PaymentGateway get(int type) {
        PaymentGateway paymentGateway = strategies.get(type);
        if(Objects.isNull(paymentGateway)){
            throw new BusinessException(BusinessExceptionInfoEnum.Inner_Exception.getMessage());
        }
        return paymentGateway;
    }
}
