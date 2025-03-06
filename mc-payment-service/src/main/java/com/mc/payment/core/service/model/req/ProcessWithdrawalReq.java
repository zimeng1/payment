package com.mc.payment.core.service.model.req;

import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import lombok.Data;

@Data
public class ProcessWithdrawalReq extends WithdrawalReq {
    private String merchantId;
    private String merchantName;
    private ChannelSubTypeEnum channelSubTypeEnum;

    public static ProcessWithdrawalReq valueOf(WithdrawalReq req) {
        ProcessWithdrawalReq processWithdrawalReq = new ProcessWithdrawalReq();
        processWithdrawalReq.setTrackingId(req.getTrackingId());
        processWithdrawalReq.setAssetType(req.getAssetType());
        processWithdrawalReq.setUserSelectable(req.getUserSelectable());
        processWithdrawalReq.setAssetName(req.getAssetName());
        processWithdrawalReq.setNetProtocol(req.getNetProtocol());
        processWithdrawalReq.setBankCode(req.getBankCode());
        processWithdrawalReq.setBankName(req.getBankName());
        processWithdrawalReq.setAccountName(req.getAccountName());
        processWithdrawalReq.setBankNum(req.getBankNum());
        processWithdrawalReq.setAmount(req.getAmount());
        processWithdrawalReq.setAddress(req.getAddress());
        processWithdrawalReq.setWebhookUrl(req.getWebhookUrl());
        processWithdrawalReq.setRemark(req.getRemark());
        processWithdrawalReq.setText1(req.getText1());
        processWithdrawalReq.setText2(req.getText2());
        processWithdrawalReq.setText3(req.getText3());
        processWithdrawalReq.setText4(req.getText4());
        processWithdrawalReq.setUserId(req.getUserId());
        processWithdrawalReq.setUserIp(req.getUserIp());
        processWithdrawalReq.setExtraMap(req.getExtraMap());
        return processWithdrawalReq;
    }
}
