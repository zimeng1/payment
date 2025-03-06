ALTER TABLE `mc_payment`.`mcp_asset_config` MODIFY COLUMN `asset_net` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产网络' AFTER `asset_name`;

ALTER TABLE `mc_payment`.`mcp_asset_config` MODIFY COLUMN `fee_asset_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手续费资产名称,[如:BTC]' AFTER `net_protocol`;

ALTER TABLE `mc_payment`.`mcp_channel_asset` DROP INDEX `uk_sub_type_asset_protocol`;

ALTER TABLE `mc_payment`.`mcp_channel_asset` MODIFY COLUMN `channel_asset_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '通道资产名称' AFTER `channel_sub_type`;

ALTER TABLE `mc_payment`.`mcp_channel_asset` ADD COLUMN `asset_net` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产网络' AFTER `asset_name`;

ALTER TABLE `mc_payment`.`mcp_channel_asset` ADD UNIQUE INDEX `uk_sub_type_channel_asset_name`(`channel_sub_type`, `channel_asset_name`) USING BTREE;


-- 维护新增asset_net字段的数据
update mcp_channel_asset t1
    LEFT JOIN mcp_asset_config t2 ON t1.asset_name = t2.asset_name
        AND t1.net_protocol = t2.net_protocol
SET t1.asset_net = t2.asset_net
WHERE
    t2.asset_net IS NOT NULL;


