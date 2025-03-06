package com.mc.payment.core.service.model.enums;

import com.mc.payment.gateway.adapter.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
 */
@Getter
public enum ChannelSubTypeEnum {
    //ITEM_0(0, "BlockATM"),
    UNDECIDED(-1, "未确定", null, ""),
    FIRE_BLOCKS(1, "FireBlocks", FireBlocksPaymentGatewayAdapter.class, ""),
    OFA_PAY(2, "OFAPay", OfaPayPaymentGatewayAdapter.class, "/openapi/webhook/ofaPay/withdraw"),
    PAY_PAL(3, "PayPal", PayPalPaymentGatewayAdapter.class, "/openapi/webhook/paypal/withdrawal"),
    PASS_TO_PAY(4, "PassToPay", PassToPayPaymentGatewayAdapter.class, ""),
    EZEEBILL(5, "Ezeebill", EzeebillPaymentGatewayAdapter.class, "/openapi/webhook/ezeebill/withdrawal"),
    CHEEZEE_PAY(6, "CheezeePay", CheezeePayPaymentGatewayAdapter.class, "/openapi/webhook/cheezeePay/withdraw");

    private final int code;
    private final String desc;
    private final Class paymentGatewayClass;
    private final String callUrl;



    ChannelSubTypeEnum(int code, String desc, Class paymentGatewayClass, String callUrl) {
        this.code = code;
        this.desc = desc;
        this.paymentGatewayClass = paymentGatewayClass;
        this.callUrl = callUrl;
    }


    public static ChannelSubTypeEnum getEnum(int code) {
        for (ChannelSubTypeEnum anEnum : ChannelSubTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum;
            }
        }
        return null;
    }

    public static ChannelSubTypeEnum getEnumByDesc(String desc) {
        for (ChannelSubTypeEnum anEnum : ChannelSubTypeEnum.values()) {
            if (anEnum.getDesc().equals(desc)) {
                return anEnum;
            }
        }
        return null;
    }

    public static ChannelSubTypeEnum getEnumByCode(Integer code) {
        for (ChannelSubTypeEnum anEnum : ChannelSubTypeEnum.values()) {
            if (code.equals(anEnum.getCode())) {
                return anEnum;
            }
        }

        return null;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ChannelSubTypeEnum anEnum : ChannelSubTypeEnum.values()) {
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
            ChannelSubTypeEnum anEnum = getEnum(Integer.parseInt(s));
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
