package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 任务状态,[0:待执行,1:执行中,2:已完成,3:失败]
 */
@Getter
public enum JobPlanStatusEnum {
    AWAIT(0, "待执行"),
    ING(1, "执行中"),
    FINISH(2, "已完成"),
    FAIL(3, "失败");

    private final int code;
    private final String desc;

    JobPlanStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (JobPlanStatusEnum anEnum : JobPlanStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}