CREATE TABLE `mc_payment`.`mcp_channel_wallet`  (
                                                    `id` bigint(0) NOT NULL,
                                                    `asset_type` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '资产类型,[0:加密货币,1:法定货币]',
                                                    `channel_sub_type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '通道子类型,[1:FireBlocks,2:OFAPay]',
                                                    `asset_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产类型/币种',
                                                    `net_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议/支付类型',
                                                    `wallet_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账户地址',
                                                    `balance` decimal(32, 20) NOT NULL COMMENT '余额',
                                                    `freeze_amount` decimal(32, 20) NOT NULL COMMENT '冻结金额',
                                                    `api_credential` json NOT NULL COMMENT '通道接口凭据加密信息Json,如API密钥',
                                                    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                                    `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]',
                                                    `status_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '状态描述',
                                                    `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                    `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                    PRIMARY KEY (`id`) USING BTREE,
                                                    UNIQUE INDEX `uk_type_asset_addr`(`asset_type`, `channel_sub_type`, `asset_name`, `net_protocol`, `wallet_address`) USING BTREE COMMENT '通道钱包唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通道钱包' ROW_FORMAT = Dynamic;

CREATE TABLE `mc_payment`.`mcp_channel_wallet_log`  (
                                                        `id` bigint(0) NOT NULL,
                                                        `wallet_id` bigint(0) NOT NULL COMMENT '钱包id',
                                                        `change_balance` decimal(32, 20) NOT NULL COMMENT '变动余额',
                                                        `change_freeze_amount` decimal(32, 20) NOT NULL COMMENT '变动冻结金额',
                                                        `wallet_update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '钱包更新时间',
                                                        `wallet_update_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '钱包更新原因',
                                                        `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                        `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                        `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                        `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通道钱包日志表' ROW_FORMAT = Dynamic;

CREATE TABLE `mc_payment`.`mcp_merchant_wallet`  (
                                                     `id` bigint(0) NOT NULL,
                                                     `merchant_id` bigint(0) NOT NULL COMMENT '账户签约的商户的ID',
                                                     `account_id` bigint(0) NOT NULL COMMENT '账号id',
                                                     `account_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账户名称',
                                                     `asset_type` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '资产类型,[0:加密货币,1:法定货币]',
                                                     `channel_sub_type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '通道子类型,[1:FireBlocks,2:OFAPay]',
                                                     `asset_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产类型/币种',
                                                     `net_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议/支付网络',
                                                     `purpose_type` tinyint(0) NOT NULL COMMENT '用途类型,[0:入金,1:出金]',
                                                     `wallet_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账户地址',
                                                     `balance` decimal(32, 20) NOT NULL COMMENT '余额',
                                                     `freeze_amount` decimal(32, 20) NOT NULL COMMENT '冻结金额',
                                                     `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
                                                     `channel_wallet_id` bigint(0) NOT NULL COMMENT '通道钱包id',
                                                     `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]',
                                                     `status_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '状态描述',
                                                     `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                     `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                     `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                     `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     INDEX `uk_id`(`merchant_id`, `account_id`, `channel_sub_type`, `asset_type`, `asset_name`, `net_protocol`, `purpose_type`, `wallet_address`) USING BTREE COMMENT '商户钱包唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户钱包' ROW_FORMAT = Dynamic;

CREATE TABLE `mc_payment`.`mcp_merchant_wallet_log`  (
                                                         `id` bigint(0) NOT NULL,
                                                         `wallet_id` bigint(0) NOT NULL COMMENT '钱包id',
                                                         `change_event_type` tinyint(0) NOT NULL COMMENT '变动事件类型,[0:入金,1:出金,2:通道钱包同步]',
                                                         `change_balance` decimal(32, 20) NOT NULL COMMENT '变动余额',
                                                         `change_freeze_amount` decimal(32, 20) NOT NULL COMMENT '变动冻结金额',
                                                         `wallet_update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '钱包更新时间',
                                                         `wallet_update_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '钱包更新原因',
                                                         `correlation_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联id,具体由变动事件类型决定',
                                                         `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                         `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                         `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                         `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户钱包日志表' ROW_FORMAT = Dynamic;


ALTER TABLE `mc_payment`.`mcp_account`
    ADD COLUMN `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]' AFTER `external_id`,
    ADD COLUMN `status_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '状态描述' AFTER `status`;

update mcp_account SET `status` =3;

CREATE TABLE `mcp_receive_webhook_log` (
                                           `id` bigint NOT NULL,
                                           `webhook_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Webhook类型',
                                           `request_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '接收到的数据',
                                           `headers` text COLLATE utf8mb4_general_ci COMMENT '请求头',
                                           `ip_address` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '发起方的ip地址',
                                           `signature` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '签名',
                                           `response_body` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '响应内容',
                                           `receive_time` timestamp NOT NULL COMMENT '接收时间',
                                           `execution_time` int unsigned NOT NULL DEFAULT '0' COMMENT '执行耗时ms',
                                           `exception_message` text COLLATE utf8mb4_general_ci COMMENT '异常信息',
                                           `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                           `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='外部webhook记录表';


INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`) VALUES (39, 'channel-wallet-query', '通道钱包-查');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`) VALUES (40, 'channel-wallet-export', '通道钱包-导');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`) VALUES (41, 'wallet-export', '商户钱包-导');
UPDATE `mc_payment`.`mcp_sys_permission` SET `permission_name` = '商户钱包-查' WHERE `id` = 25;
UPDATE `mc_payment`.`mcp_sys_permission` SET `permission_name` = '商户钱包-改' WHERE `id` = 24;
UPDATE `mc_payment`.`mcp_sys_permission` SET `permission_name` = '商户钱包-增' WHERE `id` = 23;
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`) VALUES (31, 'merchant', 'wallet-export');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`) VALUES (32, 'salesman', 'wallet-export');

ALTER TABLE `mc_payment`.`mcp_merchant_wallet`
    ADD COLUMN `deadline` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '截止时间,默认为创建时间' AFTER `status_msg`;