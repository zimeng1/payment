-- 新增权限配置
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (42, 'channel-walletLog-query', '通道钱包日志-查', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (43, 'merchant-walletLog-query', '商户钱包日志-查', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (44, 'receiveWebhookLog-query', 'webhook日志-查', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (45, 'protocol-config-query', '加密货币正则配置-查', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (46, 'protocol-config-add', '加密货币正则配置-增', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (47, 'protocol-config-remove', '加密货币正则配置-删', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);
INSERT INTO `mc_payment`.`mcp_sys_permission` (`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                               `update_by`, `update_time`)
VALUES (48, 'protocol-config-update', '加密货币正则配置-改', '', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP);

-- 新增出入金账户类型数据字典配置
INSERT INTO `mc_payment`.`mcp_dict`(`id`, `dict_code`, `dict_desc`, `category_code`, `category_desc`, `sort_no`)
VALUES (23, '2', '出入金账户', 'ACCOUNT_TYPE', '账户类型', 2);