# 新增唯一索引,修改ip白名单的字段类型
ALTER TABLE `mc_payment`.`mcp_merchant`
    MODIFY COLUMN `ip_whitelist` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ip白名单,[英文逗号隔开]' AFTER `webhook_url`,
    ADD UNIQUE INDEX `uk_name`(`name`),
    ADD UNIQUE INDEX `uk_access_key`(`access_key`);
# 以上 test环境已执行 2024年6月18日 11点20分
INSERT INTO `mcp_sys_permission` VALUES (37, 'registerAsset-query', '通道资产ID-查', '', '2024-06-25 10:18:29', '', '2024-06-25 10:18:29');
INSERT INTO `mcp_sys_permission` VALUES (38, 'registerAsset-add', '通道资产ID-增', '', '2024-06-25 10:18:29', '', '2024-06-25 10:18:29');

INSERT INTO `mcp_sys_role_permission_relation` VALUES (28, 'merchant', 'registerAsset-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (29, 'merchant', 'registerAsset-add', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
# 以上 test环境已执行 2024年6月25日 10点40分