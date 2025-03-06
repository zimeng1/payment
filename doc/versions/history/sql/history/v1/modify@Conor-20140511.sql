# 执行脚本 2024年5月11日 钱包表新增商户id字段
ALTER TABLE `mc_payment`.`mcp_wallet` ADD COLUMN `merchant_id` bigint(0) NOT NULL COMMENT '账户签约的商户的ID' AFTER `id`;

UPDATE mcp_wallet t1
    LEFT JOIN mcp_account t2 ON t1.account_id = t2.id
SET t1.merchant_id = t2.merchant_id;

CREATE TABLE `mcp_job_plan` (
                                `id` bigint NOT NULL,
                                `job_handler` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务标识',
                                `param` json NOT NULL COMMENT '任务所需参数',
                                `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '任务状态,[0:待执行,1:执行中,2:已完成,3:失败]',
                                `log_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务日志',
                                `job_start_time` timestamp NULL DEFAULT NULL COMMENT '任务开始时间',
                                `job_end_time` timestamp NULL DEFAULT NULL COMMENT '任务结束时间',
                                `execute_time` int NOT NULL DEFAULT '0' COMMENT '执行耗时ms',
                                `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_job_handler` (`job_handler`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务计划表';

