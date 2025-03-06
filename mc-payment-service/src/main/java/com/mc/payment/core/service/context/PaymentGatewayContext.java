package com.mc.payment.core.service.context;

import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.gateway.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class PaymentGatewayContext {

    private final Map<Integer, PaymentGateway> strategyMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    @Autowired
    public PaymentGatewayContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        ChannelSubTypeEnum[] values = ChannelSubTypeEnum.values();
        for (ChannelSubTypeEnum type : values) {
//            if(Objects.isNull(type.getPaymentGatewayClass())){
//                continue;
//            }
//            PaymentGateway paymentGateway = applicationContext.getBean(type.getPaymentGatewayClass());
//            strategyMap.put(type.getCode(), paymentGateway);
        }
    }

    public PaymentGateway getGatewayAdapter(Integer channelSubType){
        PaymentGateway strategy = strategyMap.get(channelSubType);
        if (strategy != null) {
            return strategy;
        }
        throw new IllegalArgumentException("没有该类处理通道的处理实现类，请检查配置！");
    }
}