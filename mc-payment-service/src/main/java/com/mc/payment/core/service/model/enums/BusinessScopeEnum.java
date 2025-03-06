package com.mc.payment.core.service.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Marty
 * @since 2024/5/10 11:41
 */
@Getter
public enum BusinessScopeEnum {
    ITEM_0(0, "加密货币"),
    ITEM_1(1, "证券"),
    ITEM_2(2, "指数"),
    ITEM_3(3, "贵金属"),
    ITEM_4(4, "法币");

    private final int code;
    private final String desc;

    BusinessScopeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static BusinessScopeEnum getEnum(int code) {
        for (BusinessScopeEnum anEnum : BusinessScopeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum;
            }
        }
        return null;
    }

    public static BusinessScopeEnum getEnumByDesc(String desc) {
        for (BusinessScopeEnum anEnum : BusinessScopeEnum.values()) {
            if (anEnum.getDesc().equals(desc)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (BusinessScopeEnum anEnum : BusinessScopeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }


    public static String getEnumDescByString(String codes) {
        //将codes的格式是这样的"0,1,2,3", 按逗号分隔成数组再循环getEnum,拼接成字符串返回
        if (StringUtils.isBlank(codes)) {
            return "";
        }
        String[] split = codes.split(",");
        StringBuilder desc = new StringBuilder();
        for (String s : split) {
            if (StringUtils.isBlank(s) || !StringUtils.isNumeric(s)) {
                continue;
            }
            BusinessScopeEnum anEnum = getEnum(Integer.parseInt(s));
            if (anEnum != null) {
                desc.append(anEnum.getDesc()).append(",");
            }
        }
        if (desc.length() > 1) {
            desc.deleteCharAt(desc.length() - 1);
        }
        return desc.toString();
    }
}
