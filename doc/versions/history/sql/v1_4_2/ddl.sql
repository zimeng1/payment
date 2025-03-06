ALTER TABLE `mc_payment`.`mcp_merchant`
    ADD COLUMN `force_withdrawal_audit` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否强制出金审核,[0:否,1:是]' AFTER `channel_sub_types`;