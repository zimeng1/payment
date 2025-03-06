package com.mc.payment.core.service.model.req;

import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import lombok.Data;

@Data
public class ProcessDepositReq extends DepositReq {
    private String merchantId;
    private String merchantName;
    private ChannelSubTypeEnum channelSubTypeEnum;

    public static ProcessDepositReq valueOf(DepositReq req) {
        ProcessDepositReq processDepositReq = new ProcessDepositReq();
        processDepositReq.setTrackingId(req.getTrackingId());
        processDepositReq.setBusinessName(req.getBusinessName());
        processDepositReq.setAssetType(req.getAssetType());
        processDepositReq.setAmount(req.getAmount());
        processDepositReq.setAssetName(req.getAssetName());
        processDepositReq.setUserSelectable(req.getUserSelectable());
        processDepositReq.setNetProtocol(req.getNetProtocol());
        processDepositReq.setBankCode(req.getBankCode());
        processDepositReq.setWebhookUrl(req.getWebhookUrl());
        processDepositReq.setSuccessPageUrl(req.getSuccessPageUrl());
        processDepositReq.setRemark(req.getRemark());
        processDepositReq.setActiveTime(req.getActiveTime());
        processDepositReq.setText1(req.getText1());
        processDepositReq.setText2(req.getText2());
        processDepositReq.setText3(req.getText3());
        processDepositReq.setText4(req.getText4());
        processDepositReq.setUserId(req.getUserId());
        processDepositReq.setUserIp(req.getUserIp());
        processDepositReq.setSkipPage(req.getSkipPage());
        return processDepositReq;
    }


}
