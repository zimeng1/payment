package com.mc.payment.gateway.channels.fireblocks.model.constant;

/**
 * FireBlocks webhook 常量池
 *
 * @author Marty
 * @since 2024/04/15 19:25
 */
public interface FireBlocksWebHookConstant {
    /**
     * webhook通知的类型
     */
    String HEADER_SIGNATURE = "Fireblocks-Signature";

    /**
     * Vault Asset Balance Updated 保险库资产余额已更新
     */
    String VAULT_BALANCE_UPDATE = "VAULT_BALANCE_UPDATE";

    /**
     * Transaction Status Updated  交易状态已更新
     */
    String TRANSACTION_STATUS_UPDATED = "TRANSACTION_STATUS_UPDATED";


}
