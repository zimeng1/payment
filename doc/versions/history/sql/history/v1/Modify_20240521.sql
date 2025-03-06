CREATE TABLE `mcp_wallet_balance_log` (
                                          `id` bigint NOT NULL,
                                          `wallet_id` bigint NOT NULL COMMENT '钱包id',
                                          `current_balance` decimal(32,20) NOT NULL COMMENT '本次余额',
                                          `previous_balance` decimal(32,20) NOT NULL COMMENT '上一次余额',
                                          `current_freeze_amount` decimal(32,20) NOT NULL COMMENT '本次冻结金额',
                                          `previous_freeze_amount` decimal(32,20) NOT NULL COMMENT '上一次冻结金额',
                                          `wallet_update_time` timestamp NOT NULL COMMENT '钱包更新时间',
                                          `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='钱包余额变动表';