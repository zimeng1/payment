package com.mc.payment.core.service.manager.wallet;

import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import com.mc.payment.core.service.model.req.merchant.GenerateWalletReq;

/**
 * @author Conor
 * @since 2024-12-31 17:24:47.436
 */
public interface FireBlocksWalletManager {
    /**
     * 基于商户配置中的自动生成钱包配置,自动生成钱包(待使用状态)
     */
    void autoGenerateWalletJob();

    /**
     * 基于前端提交的请求,生成钱包,(待创建的),完整的生成钱包需要定时任务generateWalletJob去处理
     *
     * @param req
     * @param purposeTypeEnum
     */
    void generateWallet(GenerateWalletReq req, PurposeTypeEnum purposeTypeEnum);

    /**
     * 扫描待创建的钱包,生成钱包
     */
    void generateWalletJob();

    /**
     * 取商户配置的可用资产,生成钱包
     * 用于第一次商户配置资产时,初始化钱包的,默认是10个入金,3个出金
     *
     * @param merchantId
     */
    void initGenerateWallet(String merchantId);
}
