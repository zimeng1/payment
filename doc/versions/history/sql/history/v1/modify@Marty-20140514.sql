-- 线上环境初始化脚本05124

-- 新增出金记录表-交易id字段
ALTER TABLE mcp_withdrawal_record ADD COLUMN transaction_id varchar(40) default ''  not null comment 'fireblocks返回的交易id';


ALTER TABLE `mc_payment`.`mcp_asset_config` MODIFY COLUMN `min_deposit_amount` decimal(32, 20) NOT NULL COMMENT '最小入金金额' AFTER `token_address`;

ALTER TABLE `mc_payment`.`mcp_asset_config` MODIFY COLUMN `min_withdrawal_amount` decimal(32, 20) NOT NULL COMMENT '最小出金金额' AFTER `min_deposit_amount`;

ALTER TABLE `mc_payment`.`mcp_asset_config` ADD COLUMN `estimate_fee` decimal(32, 20) NOT NULL COMMENT '预估费,单位:USDT' AFTER `min_withdrawal_amount`;
ALTER TABLE `mc_payment`.`mcp_asset_config` ADD COLUMN `un_estimate_fee` decimal(32, 20) NOT NULL COMMENT '未转换汇率预估费,单位:费率币种' AFTER `estimate_fee`;
ALTER TABLE `mc_payment`.`mcp_asset_config` ADD COLUMN `default_estimate_fee` decimal(32, 20) NOT NULL COMMENT '默认预估费' AFTER `estimate_fee`;

CREATE TABLE `mc_payment`.`mcp_asset_last_quote`  (
                                                      `id` bigint(0) NOT NULL,
                                                      `symbol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '品种/币对',
                                                      `bid` decimal(32, 20) NOT NULL COMMENT '卖价',
                                                      `ask` decimal(32, 20) NOT NULL COMMENT '买价',
                                                      `tick_time` timestamp(0) NULL DEFAULT NULL COMMENT '最新报价时间',
                                                      `data_source` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据来源,[0:MT5,1:币安]',
                                                      `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                      `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                      `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                      `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      INDEX `uk_symbol_data_source`(`symbol`, `data_source`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资产最新报价表' ROW_FORMAT = Dynamic;

