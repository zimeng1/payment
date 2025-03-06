package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 资源类型,[0:接口,1:UI,2:菜单,3:路由]
 */
@Getter
public enum ResourceTypeEnum {
    //资源类型,[0:接口,1:UI,2:菜单,3:路由]
    API(0, "接口"),
    UI(1, "UI"),
    MENU(2, "菜单"),
    ROUTER(3, "路由");

    private final int code;
    private final String desc;

    ResourceTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ResourceTypeEnum anEnum : ResourceTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}