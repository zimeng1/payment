package com.mc.payment.gateway.channels.paypal.model.req;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mc.payment.gateway.channels.paypal.constants.PaypalConstants;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.text.DecimalFormat;

/**
 * PayPalWithdrawalReq
 *
 * @author GZM
 * @since 2024/11/7 下午7:21
 */
@Data
public class PayPalWithdrawalReq extends GatewayWithdrawalReq {

    public static PayPalWithdrawalReq valueOf(GatewayWithdrawalReq gatewayWithdrawalReq) {
        PayPalWithdrawalReq payPalWithdrawalReq = new PayPalWithdrawalReq();
        BeanUtils.copyProperties(gatewayWithdrawalReq,payPalWithdrawalReq);
        return payPalWithdrawalReq;
    }

    public JSONObject buildRequestBody() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("items", getItems());
        jsonObject.set("sender_batch_header", getBatchHeader());
        return jsonObject;
    }

    private JSONArray getItems() {
        JSONObject item = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject amount = new JSONObject();
        amount.set("value",new DecimalFormat("#.00").format(Double.parseDouble(getAmount())));
        amount.set("currency", getAssetName());
        item.set("amount", amount);
        item.set("sender_item_id", getTransactionId());
        item.set("recipient_type", PaypalConstants.RECIPIENT_TYPE_WITHDRAWAL);
        item.set("purpose", PaypalConstants.PURPOSE_WITHDRAWAL);
        item.set("note", PaypalConstants.NOTE_WITHDRAWAL);
        item.set("receiver", getAddress());
        items.add(item);
        return items;
    }

    private JSONObject getBatchHeader() {
        JSONObject senderBatchHeader = new JSONObject();
        senderBatchHeader.set("sender_batch_id", getTransactionId());
        senderBatchHeader.set("email_subject", PaypalConstants.EMAIL_SUBJECT_WITHDRAWAL);
        senderBatchHeader.set("email_message", PaypalConstants.EMAIL_MESSAGE_WITHDRAWAL);
        return senderBatchHeader;
    }


}
