package com.mc.payment.gateway.channels.ezeebill.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * EzeebillConfig
 *
 * @author GZM
 * @since 2024/10/20 上午2:23
 */
@Component
public class EzeebillConfig {

    /**
     * Ezeebill的商户标识符(印度CP)
     */
    @Value("${app.ezeebill-merch-id-cp-inr}")
    private String merch_id_cp_inr;

    /**
     * Ezeebill的商户标识符(马来西亚)
     */
    @Value("${app.ezeebill-merch-id-myr}")
    private String merch_id_myr;

    /**
     * Ezeebill的商户标识符(印度尼西亚)
     */
    @Value("${app.ezeebill-merch-id-idr}")
    private String merch_id_idr;

    /**
     * Ezeebill的商户标识符(越南)
     */
    @Value("${app.ezeebill-merch-id-vnd}")
    private String merch_id_vnd;

    /**
     * Ezeebill的商户标识符(泰国)
     */
    @Value("${app.ezeebill-merch-id-thb}")
    private String merch_id_thb;

    /**
     * Ezeebill的商户标识符(印度P5)
     */
    @Value("${app.ezeebill-merch-id-p5-inr}")
    private String merch_id_p5_inr;

    /**
     * 虚拟终端标识符
     */
    @Getter
    @Value("${app.term-id}")
    private String term_id;

    /**
     * 访问标识ID 印度CP
     */
    @Value("${app.ezeebill-access-id-cp-inr}")
    private String access_id_cp_inr;

    /**
     * 访问标识ID 马来西亚
     */
    @Value("${app.ezeebill-access-id-myr}")
    private String access_id_myr;

    /**
     * 访问标识ID 印度尼西亚
     */
    @Value("${app.ezeebill-access-id-idr}")
    private String access_id_idr;

    /**
     * 访问标识ID 越南
     */
    @Value("${app.ezeebill-access-id-vnd}")
    private String access_id_vnd;

    /**
     * 访问标识ID 泰国
     */
    @Value("${app.ezeebill-access-id-thb}")
    private String access_id_thb;

    /**
     * 访问标识ID 印度P5
     */
    @Value("${app.ezeebill-access-id-p5-inr}")
    private String access_id_p5_inr;

    /**
     * HashKey 印度CP
     */
    @Value("${app.ezeebill-hash-key-cp-inr}")
    private String hash_key_cp_inr;

    /**
     * HashKey 马来西亚
     */
    @Value("${app.ezeebill-hash-key-myr}")
    private String hash_key_myr;

    /**
     * HashKey 印尼
     */
    @Value("${app.ezeebill-hash-key-idr}")
    private String hash_key_idr;

    /**
     * HashKey 越南
     */
    @Value("${app.ezeebill-hash-key-vnd}")
    private String hash_key_vnd;

    /**
     * HashKey 泰国
     */
    @Value("${app.ezeebill-hash-key-thb}")
    private String hash_key_thb;

    /**
     * HashKey 印度P5
     */
    @Value("${app.ezeebill-hash-key-p5-inr}")
    private String hash_key_p5_inr;

    /**
     * HashKey 印度P5
     */
    @Getter
    @Value("${app.ezeebill-operator-id}")
    private String operator_id;

    /**
     * HashKey 印度P5
     */
    @Getter
    @Value("${app.ezeebill-password}")
    private String password;


    /**
     * 获取指定币种MerchId
     * @param currency
     * @return
     */
    public String getMerch_id(String currency) {
        Map<String, String> map = Map.of(
                "INR", merch_id_cp_inr,
                "MYR", merch_id_myr,
                "IDR", merch_id_idr,
                "VND", merch_id_vnd,
                "THB", merch_id_thb
        );
        return map.get(currency);
    }

    /**
     * 获取指定币种AccessId
     * @param currency
     * @return
     */
    public String getAccess_id(String currency) {
        Map<String, String> map = Map.of(
                "INR", access_id_cp_inr,
                "MYR", access_id_myr,
                "IDR", access_id_idr,
                "VND", access_id_vnd,
                "THB", access_id_thb
        );
        return map.get(currency);
    }

    /**
     * 获取指定币种HashKey
     * @param currency
     * @return
     */
    public String getHashKey(String currency) {
        //回传的值为转换过的3位数字
        Map<String, String> map = Map.of(
                "356", hash_key_cp_inr,
                "458", hash_key_myr,
                "360", hash_key_idr,
                "704", hash_key_vnd,
                "764", hash_key_thb
        );
        return map.get(currency);
    }

}
