CREATE TABLE `mcp_protocol_config` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `net_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议',
                                       `regular_expression` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '正则表达式',
                                       `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                       `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                       `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='协议钱包地址正则表达式配置表';

INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (1, 'Bitcoin', '^(1[1-9A-HJ-NP-Za-km-z]{25,34}|3[1-9A-HJ-NP-Za-km-z]{25,34}|bc1[0-9A-HJ-NP-Za-km-z]{39})$\r\n', '', '2024-07-05 14:01:40', '', '2024-07-05 14:03:31');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (2, 'ERC20', '^0x[0-9a-fA-F]{40}$', '', '2024-07-05 14:04:03', '', '2024-07-05 14:04:03');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (3, 'BEP20', '^0x[a-fA-F0-9]{40}$', '', '2024-07-05 14:04:24', '', '2024-07-05 14:04:24');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (4, 'Arbitrum', '^0x[a-fA-F0-9]{40}$', '', '2024-07-05 14:04:38', '', '2024-07-05 14:04:38');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (5, 'Solana', '^[1-9A-HJ-NP-Za-km-z]{32,44}$', '', '2024-07-05 14:05:00', '', '2024-07-05 14:05:00');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (6, 'Dogecoin', '^D[5-9A-HJ-NP-Ua-km-z]{25,34}$', '', '2024-07-05 14:05:15', '', '2024-07-05 14:05:15');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (7, 'EOS', '^[a-z1-5]{12}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (8, 'Ronin', '^ronin:[0-9a-fA-F]{40}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (9, 'Ripple', '^r[0-9a-zA-Z]{24,34}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (10, 'Cardano', '^(addr)[a-zA-Z0-9]{58}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (11, 'Avalanche C-Chain', '^X[1-9A-HJ-NP-Za-km-z]{39}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (12, 'Polkadot', '^D[1-9A-HJ-NP-Za-km-z]{47}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (13, 'TRC20', '^T[1-9A-HJ-NP-Za-km-z]{33}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (14, 'Polygon', '^0x[a-fA-F0-9]{40}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (15, 'Litecoin', '^[LM][1-9A-HJ-NP-Za-km-z]{26,33}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (16, 'NEAR Protocol', '^N[1-9A-HJ-KM-Za-km-z]{65}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (17, 'Ethereum Classic', '^0x[a-fA-F0-9]{40}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (18, 'Cosmos', '^(cosmos|cosmosvaloper)[a-zA-Z0-9]{39}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (19, 'Hedera Hashgraph', '^0x[0-9A-Fa-f]{48}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (20, 'Stellar', '^G[A-Z2-7]{55}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (21, 'Algorand', '^[A-Z2-7]{58}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');
INSERT INTO `mc_payment`.`mcp_protocol_config`(`id`, `net_protocol`, `regular_expression`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (22, 'Bitcoin Cash', '^[qp][A-Za-z0-9]{41}$', '', '2024-07-05 14:06:44', '', '2024-07-05 14:06:44');


DELETE FROM mcp_sys_role_permission_relation WHERE role_code = 'merchant' AND permission_code IN ('channelAsset-query','channel-query','channelCost-query','registerAsset-query','registerAsset-add');

# 以上代码测试环境已执行 2024-07-05 14:06:44