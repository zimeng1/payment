ALTER TABLE `mc_payment`.`mcp_channel_cost`
    DROP COLUMN `channel_id`,
    DROP COLUMN `asset_id`,
    ADD COLUMN `cost_precision`   tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '精度' AFTER `billing_cycle`,
    ADD COLUMN `channel_sub_type` tinyint(0)          NOT NULL DEFAULT 0 COMMENT '通道子类型' AFTER `cost_precision`;

CREATE TABLE `mcp_channel_cost_asset`
(
    `id`               bigint                                                       NOT NULL,
    `cost_id`          bigint unsigned                                              NOT NULL DEFAULT '0' COMMENT '成本规则id',
    `business_action`  tinyint unsigned                                             NOT NULL DEFAULT '0' COMMENT '业务动作,[0:入金,1:出金]',
    `channel_sub_type` tinyint                                                      NOT NULL DEFAULT '0' COMMENT '通道子类型',
    `asset_name`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产名称,[如:BTC]',
    `net_protocol`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议',
    `create_by`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_by`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`      timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_asset` (`business_action`, `channel_sub_type`, `asset_name`, `net_protocol`) COMMENT '相同业务通道资产唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='成本规则支持的资产';

ALTER TABLE `mc_payment`.`mcp_pay_protocol`
    ADD COLUMN `regular_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '正则表达式' AFTER `status`;

update mcp_pay_protocol t1
    INNER JOIN mcp_protocol_config t2 ON t1.net_protocol = t2.net_protocol
SET t1.regular_expression = t2.regular_expression;

ALTER TABLE mc_payment.mcp_merchant
    ADD billing_type tinyint unsigned DEFAULT 0 NOT NULL COMMENT '商户结算机制,[0:基础费率/笔,1:累加费用/笔,2:固定费率/笔,3:阶梯费率/月交易金额]';
ALTER TABLE mc_payment.mcp_merchant
    CHANGE billing_type billing_type tinyint unsigned DEFAULT 0 NOT NULL COMMENT '商户结算机制,[0:基础费率/笔,1:累加费用/笔,2:固定费率/笔,3:阶梯费率/月交易金额]' AFTER withdrawal_audit;
ALTER TABLE mc_payment.mcp_merchant
    ADD additional_fee decimal(32, 20) unsigned DEFAULT 0.00000000000000000000 NOT NULL COMMENT '累加费用/固定费率';
ALTER TABLE mc_payment.mcp_merchant
    CHANGE additional_fee additional_fee decimal(32, 20) unsigned DEFAULT 0.00000000000000000000 NOT NULL COMMENT '累加费用/固定费率' AFTER billing_type;
ALTER TABLE mc_payment.mcp_merchant
    ADD currency varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' NOT NULL COMMENT '费用的货币单位,[例如“USD”或“SOL”]';
ALTER TABLE mc_payment.mcp_merchant
    CHANGE currency currency varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' NOT NULL COMMENT '费用的货币单位,[例如“USD”或“SOL”]' AFTER additional_fee;
ALTER TABLE mc_payment.mcp_merchant
    ADD tiered_rate json NULL COMMENT '阶梯费率的配置，包含范围和对应费率';
ALTER TABLE mc_payment.mcp_merchant
    CHANGE tiered_rate tiered_rate json NULL COMMENT '阶梯费率的配置，包含范围和对应费率' AFTER currency;
ALTER TABLE mc_payment.mcp_merchant
    CHANGE tiered_rate tiered_rate_json json NULL COMMENT '阶梯费率的配置，包含范围和对应费率';

