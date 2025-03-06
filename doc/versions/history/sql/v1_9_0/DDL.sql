CREATE TABLE `mcp_platform_asset`
(
    `id`          bigint                                                       NOT NULL,
    `asset_type`  tinyint unsigned                                             NOT NULL DEFAULT '0' COMMENT '资产类型,[0:加密货币,1:法定货币]',
    `asset_name`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产名称,[如:BTC]',
    `status`      tinyint unsigned                                             NOT NULL DEFAULT '1' COMMENT '资产状态,[0:禁用,1:激活]',
    `icon_data`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '图标数据,[base64编码]',
    `create_by`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time` timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_name` (`asset_type`, `asset_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='平台资产';

CREATE TABLE `mcp_pay_protocol`
(
    `id`           bigint                                                       NOT NULL,
    `asset_type`   tinyint unsigned                                             NOT NULL DEFAULT '0' COMMENT '资产类型,[0:加密货币,1:法定货币]',
    `net_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '加密货币-网络协议',
    `asset_net`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '加密货币-资产网络',
    `icon_data`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '图标数据,[base64编码]',
    `status`       tinyint unsigned                                             NOT NULL DEFAULT '1' COMMENT '资产状态,[0:禁用,1:激活]',
    `create_by`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`  timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_by`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`  timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pay_protocol` (`asset_type`, `net_protocol`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='支付协议数据表(法币-支付类型/加密货币-网络协议)';


CREATE TABLE `mcp_channel_asset_config`
(
    `id`                    bigint                                                        NOT NULL,
    `channel_sub_type`      tinyint                                                       NOT NULL DEFAULT '0' COMMENT '通道子类型',
    `asset_type`            tinyint unsigned                                              NOT NULL DEFAULT '0' COMMENT '资产类型,[0:加密货币,1:法定货币]',
    `channel_asset_name`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '通道资产名称',
    `channel_net_protocol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '通道资产网络协议/支付类型',
    `asset_name`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '资产名称',
    `net_protocol`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '加密货币网络协议/法币支付类型',
    `asset_net`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '资产网络',
    `min_deposit_amount`    decimal(32, 20) unsigned                                      NOT NULL COMMENT '最小入金金额',
    `min_withdrawal_amount` decimal(32, 20) unsigned                                      NOT NULL COMMENT '最小出金金额',
    `max_deposit_amount`    decimal(32, 20) unsigned                                      NOT NULL COMMENT '最大入金金额',
    `max_withdrawal_amount` decimal(32, 20) unsigned                                      NOT NULL COMMENT '最大出金金额',
    `token_address`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '合约地址',
    `test_hash_url`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT
        '哈希查询地址(Testnet)',
    `main_hash_url`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT
        '哈希查询地址(Mainnet)',
    `fee_asset_name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '手续费币种',
    `estimate_fee`          decimal(32, 20) unsigned                                      NOT NULL COMMENT '预估费,[单位:当前币种]',
    `un_estimate_fee`       decimal(32, 20) unsigned                                      NOT NULL COMMENT '未转换汇率预估费,[单位:手续费币种]',
    `default_estimate_fee`  decimal(32, 20) unsigned                                      NOT NULL COMMENT '默认预估费/手续费兜底值,[单位:当前币种]',
    `channel_credential`    json                                                          NOT NULL COMMENT '通道凭据信息',
    `status`                tinyint unsigned                                              NOT NULL DEFAULT '1' COMMENT '资产状态,[0:禁用,1:激活]',
    `create_by`             varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`           timestamp(3)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_by`             varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`           timestamp(3)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `uk_type_channel_asset` (`channel_sub_type`, `asset_type`, `asset_name`, `net_protocol`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='通道资产配置(1.9.0基于mcp_channel_asset和mcp_asset_config迁移)';


ALTER TABLE `mc_payment`.`mcp_merchant`
    MODIFY COLUMN `param` json NULL COMMENT '商户参数' AFTER `name`;

ALTER TABLE `mc_payment`.`mcp_merchant`
    MODIFY COLUMN `deposit_audit` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '入金审核[0否,1是]' AFTER `force_withdrawal_audit`,
    MODIFY COLUMN `withdrawal_audit` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '出金审核[0否,1是]' AFTER `deposit_audit`;

ALTER TABLE `mc_payment`.`mcp_merchant`
    DROP COLUMN `force_withdrawal_audit`;

# cheezeepay出金增加的支付方式

INSERT INTO `mc_payment`.`mcp_asset_config` (`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                             `fee_asset_name`, `token_address`, `min_deposit_amount`,
                                             `min_withdrawal_amount`, `estimate_fee`, `un_estimate_fee`,
                                             `default_estimate_fee`, `status`, `deleted`, `create_by`, `create_time`,
                                             `update_by`, `update_time`, `max_deposit_amount`, `max_withdrawal_amount`,
                                             `hash_url`)
VALUES (117, 1, 'INR', 'BANK_IN', 'BANK_IN', 'INR', '', 0.00000000000000000000, 0.00000000000000000000,
        0.00000000000000000000, 0.00000000000000000000, 0.00000000000000000000, 1, 0, '', '2024-10-18 20:26:26', '',
        '2024-10-20 13:39:11', 100000.00000000000000000000, 0.00000000000000000000, '');
INSERT INTO `mc_payment`.`mcp_asset_config` (`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                             `fee_asset_name`, `token_address`, `min_deposit_amount`,
                                             `min_withdrawal_amount`, `estimate_fee`, `un_estimate_fee`,
                                             `default_estimate_fee`, `status`, `deleted`, `create_by`, `create_time`,
                                             `update_by`, `update_time`, `max_deposit_amount`, `max_withdrawal_amount`,
                                             `hash_url`)
VALUES (119, 1, 'BRL', 'PIX', 'PIX', 'BRL', '', 0.00000000000000000000, 0.00000000000000000000, 0.00000000000000000000,
        0.00000000000000000000, 0.00000000000000000000, 1, 0, '', '2024-10-18 20:26:26', '', '2024-10-20 13:39:11',
        100000.00000000000000000000, 0.00000000000000000000, '');
INSERT INTO `mc_payment`.`mcp_asset_config` (`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                             `fee_asset_name`, `token_address`, `min_deposit_amount`,
                                             `min_withdrawal_amount`, `estimate_fee`, `un_estimate_fee`,
                                             `default_estimate_fee`, `status`, `deleted`, `create_by`, `create_time`,
                                             `update_by`, `update_time`, `max_deposit_amount`, `max_withdrawal_amount`,
                                             `hash_url`)
VALUES (120, 1, 'THB', 'BANK_TH', 'BANK_TH', 'THB', '', 0.00000000000000000000, 0.00000000000000000000,
        0.00000000000000000000, 0.00000000000000000000, 0.00000000000000000000, 1, 0, '', '2024-10-18 20:26:26', '',
        '2024-10-20 13:39:11', 100000.00000000000000000000, 0.00000000000000000000, '');
INSERT INTO `mc_payment`.`mcp_asset_config` (`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                             `fee_asset_name`, `token_address`, `min_deposit_amount`,
                                             `min_withdrawal_amount`, `estimate_fee`, `un_estimate_fee`,
                                             `default_estimate_fee`, `status`, `deleted`, `create_by`, `create_time`,
                                             `update_by`, `update_time`, `max_deposit_amount`, `max_withdrawal_amount`,
                                             `hash_url`)
VALUES (121, 1, 'IDR', 'BANK_ID', 'BANK_ID', 'IDR', '', 0.00000000000000000000, 0.00000000000000000000,
        0.00000000000000000000, 0.00000000000000000000, 0.00000000000000000000, 1, 0, '', '2024-10-18 20:26:26', '',
        '2024-10-20 13:39:11', 100000.00000000000000000000, 0.00000000000000000000, '');


INSERT INTO `mc_payment`.`mcp_channel_asset` (`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                              `channel_credential`, `net_protocol`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (1116, 6, 'INR_BANK_IN', 'INR', 'BANK_IN', 'null', 'BANK_IN', '', '2024-10-20 19:00:35', '',
        '2024-10-20 22:46:30');
INSERT INTO `mc_payment`.`mcp_channel_asset` (`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                              `channel_credential`, `net_protocol`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (1117, 6, 'BRL_PIX', 'BRL', 'PIX', 'null', 'PIX', '', '2024-10-20 19:00:35', '', '2024-10-20 22:46:30');
INSERT INTO `mc_payment`.`mcp_channel_asset` (`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                              `channel_credential`, `net_protocol`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (1118, 6, 'THB_BANK_TH', 'THB', 'BANK_TH', 'null', 'BANK_TH', '', '2024-10-20 19:00:35', '',
        '2024-10-20 22:46:30');
INSERT INTO `mc_payment`.`mcp_channel_asset` (`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                              `channel_credential`, `net_protocol`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (1119, 6, 'IDR_BANK_ID', 'IDR', 'BANK_ID', 'null', 'BANK_ID', '', '2024-10-20 19:00:35', '',
        '2024-10-20 22:46:30');

# withdrawal_record表新增extra_map字段

ALTER TABLE mcp_withdrawal_record
    ADD COLUMN extra_map VARCHAR(500) DEFAULT NULL AFTER audit_status;


ALTER TABLE `mc_payment`.`mcp_merchant`
    MODIFY COLUMN `settlement_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '结算信息' AFTER `settlement_subject`;

ALTER TABLE `mc_payment`.`mcp_merchant`
    MODIFY COLUMN `settlement_subject` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '结算主体' AFTER `business_scope`;