package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.model.req.ChannelWalletPageReq;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Conor
 * @description 针对表【mcp_channel_wallet(通道钱包)】的数据库操作Service
 * @createDate 2024-08-14 13:44:50
 */
public interface ChannelWalletService extends IService<ChannelWalletEntity> {
    /**
     * 同步余额
     */
    void syncBalance();

    BasePageRsp<ChannelWalletEntity> page(ChannelWalletPageReq req);


    void syncBalanceByFireBlocksAll();

    void syncBalanceByFireBlocks(String accountId);

    void export(ChannelWalletPageReq req, HttpServletResponse response);

    ChannelWalletEntity getOne(String assetName, String netProtocol, String walletAddress);
}
