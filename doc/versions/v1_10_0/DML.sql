INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`)
VALUES (62, 'merchantConfig-generateWallet', '商户配置-生成钱包');


INSERT INTO `mc_crm`.`t_crm_merchant`(`id`, `name`, `token`, `status`)
VALUES (6, 'Payment', '577235a75c8e44ad8706f2f23ab3975e', 1);
INSERT INTO `mc_crm`.`t_sub_system`(`id`, `sub_system_code`, `sub_system_name`, `icon`, `bg_img`, `url`,
                                    `sub_system_sort`, `sub_system_display`, `sub_system_secret`, `remark`, `deleted`,
                                    `creator`, `updater`, `create_time`, `update_time`, `sub_system_public_key`)
VALUES (5, '000005', 'Payment', 'icon', 'bgimg', '#', 5, 1,
        '', '', 0, 1, 1, NOW(), NOW(), '');