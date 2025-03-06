ALTER TABLE `mc_payment`.`mcp_user`
    COMMENT = '系统账号表';

ALTER TABLE `mc_payment`.`mcp_user`
    CHANGE COLUMN `password` `password_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码哈希' AFTER `user_name`;

ALTER TABLE `mc_payment`.`mcp_user`
    ADD COLUMN `role_code`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码' AFTER `email`;

ALTER TABLE `mc_payment`.`mcp_user`
    ADD COLUMN `merchant_rel_type` tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT '所属商户关联类型,[0:全部商户,1:部分商户]' AFTER `role_code`;

ALTER TABLE `mc_payment`.`mcp_user`
    MODIFY COLUMN `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT '用户状态,[0:禁用,1:激活]' AFTER `merchant_rel_type`;

ALTER TABLE `mc_payment`.`mcp_user`
    ADD COLUMN `last_login_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '最后登录时间' AFTER `status`;

ALTER TABLE `mc_payment`.`mcp_user`
    ADD COLUMN `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后登录ip' AFTER `last_login_time`;

ALTER TABLE `mc_payment`.`mcp_user`
    ADD COLUMN `history_password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最近3次的历史密码哈希值,英文逗号分隔' AFTER `last_login_ip`;
## 初始化历史密码
UPDATE mcp_user
SET history_password_hash = password_hash;
# 用户状态 设置为激活
UPDATE mcp_user
SET `status` = 1;
CREATE TABLE `mc_payment`.`mcp_user_merchant_relation`
(
    `id`          bigint(0)                                                    NOT NULL,
    `user_id`     bigint(0)                                                    NOT NULL COMMENT '系统账号id',
    `merchant_id` bigint(0)                                                    NOT NULL COMMENT '商户id',
    `create_by`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` timestamp(0)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_by`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time` timestamp(0)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_merchant_id` (`user_id`, `merchant_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统账号所属商户表'
  ROW_FORMAT = Dynamic;


## mcp_channel_asset 新增唯一索引,限制同一渠道下的资产名称和网络协议唯一
ALTER TABLE `mc_payment`.`mcp_channel_asset` ADD UNIQUE INDEX `uk_sub_type_asset_name_protocol`(`channel_sub_type`, `asset_name`, `net_protocol`) USING BTREE;

## 角色权限相关
CREATE TABLE `mcp_sys_permission`  (
                                       `id` bigint(0) NOT NULL,
                                       `permission_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '权限码',
                                       `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '权限名称',
                                       `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                       `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                       `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                       `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `uk_permission_code`(`permission_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of mcp_sys_permission
-- ----------------------------
INSERT INTO `mcp_sys_permission` VALUES (1, '*', '全部权限', '', '2024-06-04 16:23:58', '', '2024-06-04 16:23:58');
INSERT INTO `mcp_sys_permission` VALUES (2, 'asset-add', '平台资产-增', '', '2024-06-04 16:50:52', '', '2024-06-04 16:50:52');
INSERT INTO `mcp_sys_permission` VALUES (3, 'asset-update', '平台资产-改', '', '2024-06-04 16:51:15', '', '2024-06-04 16:51:15');
INSERT INTO `mcp_sys_permission` VALUES (4, 'asset-query', '平台资产-查', '', '2024-06-04 16:51:41', '', '2024-06-04 16:51:41');
INSERT INTO `mcp_sys_permission` VALUES (5, 'channelAsset-add', '通道资产-增', '', '2024-06-04 16:52:30', '', '2024-06-04 17:05:01');
INSERT INTO `mcp_sys_permission` VALUES (6, 'channelAsset-update', '通道资产-改', '', '2024-06-04 16:53:25', '', '2024-06-04 17:05:10');
INSERT INTO `mcp_sys_permission` VALUES (7, 'channelAsset-query', '通道资产-查', '', '2024-06-04 16:53:43', '', '2024-06-04 17:05:15');
INSERT INTO `mcp_sys_permission` VALUES (8, 'channel-add', '通道管理-增', '', '2024-06-04 16:54:53', '', '2024-06-04 16:54:53');
INSERT INTO `mcp_sys_permission` VALUES (9, 'channel-update', '通道管理-改', '', '2024-06-04 16:55:12', '', '2024-06-04 16:55:12');
INSERT INTO `mcp_sys_permission` VALUES (10, 'channel-query', '通道管理-查', '', '2024-06-04 16:55:36', '', '2024-06-04 16:55:36');
INSERT INTO `mcp_sys_permission` VALUES (11, 'channelCost-add', '平台成本-增', '', '2024-06-04 16:56:26', '', '2024-06-04 17:05:32');
INSERT INTO `mcp_sys_permission` VALUES (12, 'channelCost-update', '平台成本-改', '', '2024-06-04 16:57:05', '', '2024-06-04 17:05:33');
INSERT INTO `mcp_sys_permission` VALUES (13, 'channelCost-query', '平台成本-查', '', '2024-06-04 16:57:22', '', '2024-06-04 17:05:39');
INSERT INTO `mcp_sys_permission` VALUES (14, 'merchant-add', '商户管理-增', '', '2024-06-04 17:00:42', '', '2024-06-04 17:00:42');
INSERT INTO `mcp_sys_permission` VALUES (15, 'merchant-update', '商户管理-改', '', '2024-06-04 17:00:42', '', '2024-06-04 17:00:42');
INSERT INTO `mcp_sys_permission` VALUES (16, 'merchant-query', '商户管理-查', '', '2024-06-04 17:00:42', '', '2024-06-04 17:00:42');
INSERT INTO `mcp_sys_permission` VALUES (20, 'account-add', '账户管理-增', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (21, 'account-update', '账户管理-改', '', '2024-06-04 17:18:29', '', '2024-06-04 17:28:08');
INSERT INTO `mcp_sys_permission` VALUES (22, 'account-query', '账户管理-查', '', '2024-06-04 17:18:29', '', '2024-06-04 17:28:10');
INSERT INTO `mcp_sys_permission` VALUES (23, 'wallet-add', '钱包管理-增', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (24, 'wallet-update', '钱包管理-改', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (25, 'wallet-query', '钱包管理-查', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (30, 'user-add', '系统账号-增', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (31, 'user-update', '系统账号-改', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (32, 'user-query', '系统账号-查', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (33, 'ipWhitelist-add', 'ip白名单-增', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (34, 'ipWhitelist-update', 'ip白名单-改', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (35, 'ipWhitelist-query', 'ip白名单-查', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');
INSERT INTO `mcp_sys_permission` VALUES (36, 'channelAsset-remove', '通道资产-删', '', '2024-06-04 17:18:29', '', '2024-06-04 17:18:29');

CREATE TABLE `mcp_sys_role`  (
                                 `id` bigint(0) NOT NULL,
                                 `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色码',
                                 `role_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
                                 `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                 `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                 `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `uk_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mcp_sys_role
-- ----------------------------
INSERT INTO `mcp_sys_role` VALUES (1797872137417121794, 'admin', '管理员', 'admin', '2024-06-04 14:04:54', 'admin', '2024-06-04 14:10:10');
INSERT INTO `mcp_sys_role` VALUES (1797872390669197313, 'salesman', '业务员', 'admin', '2024-06-04 14:05:55', 'admin', '2024-06-04 14:12:01');
INSERT INTO `mcp_sys_role` VALUES (1797872456297472001, 'merchant', '商户', 'admin', '2024-06-04 14:06:10', 'admin', '2024-06-04 14:12:11');


CREATE TABLE `mcp_sys_role_permission_relation`  (
                                                     `id` bigint(0) NOT NULL,
                                                     `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '角色码',
                                                     `permission_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '权限码',
                                                     `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
                                                     `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                     `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
                                                     `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     INDEX `uk_role_permission_code`(`role_code`, `permission_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mcp_sys_role_permission_relation
-- ----------------------------
INSERT INTO `mcp_sys_role_permission_relation` VALUES (1, 'admin', '*', '', '2024-06-04 16:24:23', '', '2024-06-04 16:24:23');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (2, 'salesman', 'asset-update', '', '2024-06-04 17:21:03', '', '2024-06-04 17:21:03');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (3, 'salesman', 'asset-query', '', '2024-06-04 17:21:17', '', '2024-06-04 17:21:19');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (4, 'salesman', 'channelAsset-update', '', '2024-06-04 17:21:43', '', '2024-06-04 17:21:43');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (5, 'salesman', 'channelAsset-query', '', '2024-06-04 17:24:33', '', '2024-06-04 17:24:40');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (6, 'salesman', 'channel-add', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (7, 'salesman', 'channel-update', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (8, 'salesman', 'channel-query', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (9, 'salesman', 'channelCost-add', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (10, 'salesman', 'channelCost-update', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (11, 'salesman', 'channelCost-query', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (12, 'salesman', 'merchant-add', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (13, 'salesman', 'merchant-update', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (14, 'salesman', 'merchant-query', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (15, 'salesman', 'account-add', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (16, 'salesman', 'account-update', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (17, 'salesman', 'account-query', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (18, 'salesman', 'wallet-add', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (19, 'salesman', 'wallet-update', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (20, 'salesman', 'wallet-query', '', '2024-06-04 17:31:59', '', '2024-06-04 17:31:59');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (21, 'merchant', 'asset-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (22, 'merchant', 'channelAsset-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (23, 'merchant', 'channel-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (24, 'merchant', 'channelCost-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (25, 'merchant', 'merchant-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (26, 'merchant', 'account-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');
INSERT INTO `mcp_sys_role_permission_relation` VALUES (27, 'merchant', 'wallet-query', '', '2024-06-04 17:37:39', '', '2024-06-04 17:37:39');

ALTER TABLE `mc_payment`.`mcp_ip_whitelist` DROP COLUMN `user_status`;

ALTER TABLE `mc_payment`.`mcp_ip_whitelist` ADD COLUMN `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT 'IP状态,[0:禁用,1:激活]' AFTER `remark`;
### 以上已经执行test 2024年6月5日 18点11分


ALTER TABLE `mc_payment`.`mcp_ip_whitelist`
    MODIFY COLUMN `ip_addr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'IP地址' AFTER `id`;
ALTER TABLE `mc_payment`.`mcp_ip_whitelist`
    ADD UNIQUE INDEX `uk_ip_addr`(`ip_addr`);
## 给admin设置全部商户
update mcp_user SET merchant_rel_type=0 where user_account='admin';
### 以上已经执行test 2024年6月6日 11点37分

