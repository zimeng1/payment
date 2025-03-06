package com.mc.payment.gateway.channels.ezeebill.model.req;

import com.mc.payment.gateway.channels.ezeebill.util.EzeebillUtil;
import com.mc.payment.gateway.model.req.GatewayDepositReq;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * EzeebillCreateOrderReq
 *
 * @author GZM
 * @since 2024/10/18 下午7:46
 */
@Data
public class EzeebillOrderReq extends GatewayDepositReq {

    /**
     * 商户标识符(MID)
     */
    private String merchId;

    /**
     * 虚拟终端标识符
     */
    private String termId;

    /**
     * 访问标识ID
     */
    private String accessId;

//    /**
//     * 密钥
//     */
//    private String secretKey;

    /**
     * 父类转换为子类
     * @param gatewayDepositReq
     * @return
     */
    public static EzeebillOrderReq convertParent(GatewayDepositReq gatewayDepositReq) {
        EzeebillOrderReq ezeebillOrderReq = new EzeebillOrderReq();
        BeanUtils.copyProperties(gatewayDepositReq,ezeebillOrderReq);
        ezeebillOrderReq.setCurrency(EzeebillUtil.getCurrencyCode(gatewayDepositReq.getCurrency()));
        return ezeebillOrderReq;
    }

    /**
     * 转为请求API的Map
     * @return
     */
    public Map<String, Object> convertToMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("merch_id", getMerchId());
        map.put("merch_order_id", getTransactionId());
        map.put("term_id", getTermId());
        map.put("access_id", getAccessId());
        map.put("currency", getCurrency());
        map.put("amount", (long)Double.parseDouble(getAmount())*100);//ezeebill支付单位为分
        map.put("return_url", getSuccessPageUrl());
        map.put("bill_to_first_name", "constants");
        map.put("pay_type", "IB");
        map.put("locale", "zh_TW");
        map.put("version", "1.0");
        map.put("action", "SALE_ONLY");

//        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//        sortedMap.putAll(map);
//        map.put("secure_hash", EzeebillUtil.generateSignature(sortedMap, getSecretKey()));
        return map;
    }
}
