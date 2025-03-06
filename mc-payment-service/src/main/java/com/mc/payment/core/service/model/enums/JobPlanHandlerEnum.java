package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 任务状态,[0:待执行,1:执行中,2:已完成,3:失败]
 */
@Getter
public enum JobPlanHandlerEnum {
    CREATE_ACCOUNT( "createAccount"),
    CREATE_WALLET( "createWallet"),
    SEND_EMAIL( "sendEmail"),
    ;

    private final String jobHandler;

    JobPlanHandlerEnum( String jobHandler) {
        this.jobHandler = jobHandler;
    }

}