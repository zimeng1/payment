package com.mc.payment.gateway.channels.ezeebill.model.req;

import com.mc.payment.gateway.channels.ezeebill.util.EzeebillUtil;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * EzeebillWithdrawalReq
 *
 * @author GZM
 * @since 2024/11/1 下午2:33
 */
@Data
public class EzeebillWithdrawalReq extends GatewayWithdrawalReq {

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

    /**
     * 操作ID，用于认证
     */
    private String operatorId;

    /**
     * 操作密码
     */
    private String password;

    /**
     * 密钥
     */
    private String secretKey;


    public static EzeebillWithdrawalReq valueOf(GatewayWithdrawalReq gatewayWithdrawalReq) {
        EzeebillWithdrawalReq ezeebillWithdrawalReq = new EzeebillWithdrawalReq();
        BeanUtils.copyProperties(gatewayWithdrawalReq,ezeebillWithdrawalReq);
        return ezeebillWithdrawalReq;
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("merch_id", getMerchId());
        map.put("merch_order_id", getTransactionId());
        map.put("merch_txn_id", getTransactionId());
        map.put("term_id", getTermId());
        map.put("access_id", getAccessId());
        map.put("pay_type", "IB");
        map.put("currency", EzeebillUtil.getCurrencyCode(getAssetName()));
        map.put("amount", (long)Double.parseDouble(getAmount())*100);//ezeebill支付单位为分
        map.put("pay_to_bank_name", getBankName());
        map.put("pay_to_acc_name", getAccountName());
        map.put("pay_to_acc_no", getAddress());
        map.put("pay_to_bank_code", getBankCode());
        map.put("pay_to_branch_number", getBankNum());
        map.put("operator_id", getOperatorId());
        map.put("password", getPassword());
        map.put("action", "PAYOUT");
        map.put("version", "1.0");

        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(map);
        map.put("secure_hash", EzeebillUtil.generateSignature(sortedMap, getSecretKey()));
        return map;
    }
}
