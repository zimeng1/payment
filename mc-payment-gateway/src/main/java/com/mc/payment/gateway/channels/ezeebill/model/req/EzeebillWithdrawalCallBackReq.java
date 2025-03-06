package com.mc.payment.gateway.channels.ezeebill.model.req;

import com.mc.payment.gateway.channels.ezeebill.constants.EzeebillConstants;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * EzeebillWithdrawalCallBackReq
 *
 * @author GZM
 * @since 2024/11/5 上午10:52
 */
@Data
public class EzeebillWithdrawalCallBackReq {
    private String txn_message;
    private String merch_id;
    private String locale;
    private String term_id;
    private String merch_order_id;
    private String txn_time;
    private String merch_txn_id;
    private String currency;
    private String version;
    private Integer amount;
    private String dr_count;
    private String secure_hash;
    private String txn_status;
    private String action;
    private String accept_time;
    private String txn_no;
    private Integer txn_response_code;
    private String pay_type;
    private String card_num;
    private String merch_data;
    private String bill_to_first_name;
    private String bill_to_last_name;
    private String bill_to_company;
    private String bill_to_street;
    private String bill_to_street2;
    private String bill_to_city;
    private String bill_to_state_code;
    private String bill_to_postal_code;
    private String bill_to_country_code;
    private String ship_to_first_name;
    private String ship_to_last_name;
    private String ship_to_company;
    private String ship_to_street;
    private String ship_to_street2;
    private String ship_to_city;
    private String ship_to_state_code;
    private String ship_to_postal_code;
    private String ship_to_country_code;
    private String pay_item;
    private String batch_no;
    private String acq_response_code;
    private String auth_id;
    private String receipt_no;
    private String authorized_amount;
    private String captured_amount;
    private String refunded_amount;
    private String original_amount;
    private String vat_rate;
    private Integer vat_amount;
    private String bill_to_email_address;
    private String customer_ip_address;
    private String pay_to_bank_code;


    public boolean isSuccess() {
        return EzeebillConstants.RSP_SUCCESS_CODE == txn_response_code;
    }

    public Map<String,Object> convertToMap(){
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    map.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("req convert failed", e);
            }
        }
        return map;
    }
}
