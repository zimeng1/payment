ALTER TABLE `mc_payment`.`mcp_merchant_channel_asset`
    ADD COLUMN `generate_wallet_status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否自动生成钱包,[0:否,1:是]' AFTER `withdrawal_status`;

ALTER TABLE `mc_payment`.`mcp_merchant_channel_asset`
    ADD COLUMN `generate_wallet_le_quantity` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '生成钱包小于等于阈值' AFTER `generate_wallet_status`;

ALTER TABLE `mc_payment`.`mcp_merchant_channel_asset`
    ADD COLUMN `generate_wallet_quantity` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '生成钱包数量' AFTER `generate_wallet_le_quantity`;