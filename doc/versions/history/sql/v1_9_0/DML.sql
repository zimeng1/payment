# 币种数据初始化
SET @mcp_platform_asset_id := 0;
INSERT INTO `mc_payment`.`mcp_platform_asset` (`id`, `asset_type`, `asset_name`)
SELECT (@mcp_platform_asset_id := @mcp_platform_asset_id + 1),
       t.asset_type,
       t.asset_name
FROM (SELECT DISTINCT asset_type, asset_name FROM mcp_asset_config WHERE deleted = 0) t;

# 支付方式网络协议数据初始化
SET @mcp_pay_protocol_id := 0;
INSERT INTO `mc_payment`.`mcp_pay_protocol` (`id`, `asset_type`, `net_protocol`, `asset_net`)
SELECT (@mcp_pay_protocol_id := @mcp_pay_protocol_id + 1),
       t.asset_type,
       t.net_protocol,
       t.asset_net
FROM (SELECT DISTINCT asset_type, net_protocol, asset_net FROM mcp_asset_config WHERE deleted = 0 AND asset_type = 0) t;

INSERT INTO `mc_payment`.`mcp_pay_protocol` (`id`, `asset_type`, `net_protocol`)
SELECT (@mcp_pay_protocol_id := @mcp_pay_protocol_id + 1),
       t.asset_type,
       t.net_protocol
FROM (SELECT DISTINCT asset_type, net_protocol FROM mcp_asset_config WHERE deleted = 0 AND asset_type = 1) t;

## 通道资产数据初始化
SET @mcp_config_id := 0;
INSERT INTO `mc_payment`.`mcp_channel_asset_config` (`id`,
                                                     `channel_sub_type`,
                                                     `asset_type`,
                                                     `channel_asset_name`,
                                                     `channel_net_protocol`,
                                                     `asset_name`,
                                                     `net_protocol`,
                                                     `asset_net`,
                                                     `min_deposit_amount`,
                                                     `min_withdrawal_amount`,
                                                     `max_deposit_amount`,
                                                     `max_withdrawal_amount`,
                                                     `token_address`,
                                                     `test_hash_url`,
                                                     `main_hash_url`,
                                                     `fee_asset_name`,
                                                     `estimate_fee`,
                                                     `un_estimate_fee`,
                                                     `default_estimate_fee`,
                                                     `channel_credential`,
                                                     `status`)
SELECT (@mcp_config_id := @mcp_config_id + 1),
       t1.channel_sub_type,
       t2.asset_type,
       t1.channel_asset_name,
       '/',
       t1.asset_name,
       t1.net_protocol,
       t1.asset_net,
       t2.min_deposit_amount,
       t2.min_withdrawal_amount,
       t2.max_deposit_amount,
       t2.max_withdrawal_amount,
       t2.token_address,
       t2.hash_url,
       t2.hash_url,
       t2.fee_asset_name,
       t2.estimate_fee,
       t2.un_estimate_fee,
       t2.default_estimate_fee,
       t1.channel_credential,
       t2.`status`
FROM mcp_channel_asset t1
         LEFT JOIN mcp_asset_config t2 ON t1.asset_name = t2.asset_name
    AND t1.net_protocol = t2.net_protocol
    AND t2.deleted = 0;


## 商户资产数据增加字段并初始化数据
ALTER TABLE `mc_payment`.`mcp_merchant_channel_asset`
    ADD COLUMN `channel_asset_id` bigint(0)           NOT NULL DEFAULT 0 COMMENT '通道资产id' AFTER `merchant_id`,
    ADD COLUMN `asset_type`       tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '资产类型,[0:加密货币,1:法定货币]' AFTER `channel_asset_id`;


UPDATE mcp_merchant_channel_asset t1
    INNER JOIN mcp_channel_asset_config t2 ON t1.asset_name = t2.asset_name
        AND t1.net_protocol = t2.net_protocol
SET t1.channel_asset_id=t2.id,
    t1.asset_type      = t2.asset_type;

## 商户通道资产数据增加字段并初始化数据
ALTER TABLE `mc_payment`.`mcp_merchant_channel_asset`
    MODIFY COLUMN `alarm_status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否启用告警,[0:否,1:是]' AFTER `net_protocol`,
    ADD COLUMN `deposit_status`    tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否入金可用,[0:否,1:是]' AFTER `reserve_alarm_value`,
    ADD COLUMN `withdrawal_status` tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否出金可用,[0:否,1:是]' AFTER `deposit_status`;


## 权限数据初始化
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (49, 'platformAsset-query', '平台资产-查', '', '2024-11-15 17:12:30', '', '2024-11-15 17:12:30');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (50, 'platformAsset-add', '平台资产-增', '', '2024-11-15 17:17:41', '', '2024-11-15 17:17:41');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (51, 'platformAsset-update', '平台资产-改', '', '2024-11-15 17:17:56', '', '2024-11-15 17:17:56');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (52, 'payProtocol-fiat-query', '法币支付类型-查', '', '2024-11-15 17:18:26', '', '2024-11-15 17:18:26');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (53, 'payProtocol-fiat-add', '法币支付类型-增', '', '2024-11-15 17:18:47', '', '2024-11-15 17:18:47');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (54, 'payProtocol-fiat-update', '法币支付类型-改', '', '2024-11-15 17:19:04', '', '2024-11-15 17:19:04');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (55, 'payProtocol-crypto-query', '加密货币网络协议-查', '', '2024-11-15 17:19:22', '', '2024-11-15 17:19:22');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (56, 'payProtocol-crypto-add', '加密货币网络协议-增', '', '2024-11-15 17:19:43', '', '2024-11-15 17:19:43');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (57, 'payProtocol-crypto-update', '加密货币网络协议-改', '', '2024-11-15 17:19:59', '', '2024-11-15 17:19:59');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (58, 'merchantConfig-query', '商户配置-查', '', '2024-11-15 17:22:08', '', '2024-11-15 17:36:32');
INSERT INTO `mc_payment`.`mcp_sys_permission`(`id`, `permission_code`, `permission_name`, `create_by`, `create_time`,
                                              `update_by`, `update_time`)
VALUES (59, 'merchantConfig-update', '商户配置-改', '', '2024-11-15 17:22:27', '', '2024-11-15 17:36:37');


INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (33, 'salesman', 'platformAsset-update', '', '2024-11-15 17:32:05', '', '2024-11-15 17:32:05');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (34, 'salesman', 'platformAsset-query', '', '2024-11-15 17:32:29', '', '2024-11-15 17:32:29');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (35, 'salesman', 'payProtocol-fiat-query', '', '2024-11-15 17:32:38', '', '2024-11-15 17:32:50');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (36, 'salesman', 'payProtocol-fiat-update', '', '2024-11-15 17:32:52', '', '2024-11-15 17:33:01');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (37, 'salesman', 'payProtocol-crypto-query', '', '2024-11-15 17:33:08', '', '2024-11-15 17:33:18');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (38, 'salesman', 'payProtocol-crypto-update', '', '2024-11-15 17:33:27', '', '2024-11-15 17:33:29');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (39, 'salesman', 'merchantConfig-query', '', '2024-11-15 17:43:13', '', '2024-11-15 17:43:13');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (40, 'salesman', 'merchantConfig-update', '', '2024-11-15 17:43:19', '', '2024-11-15 17:43:33');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (41, 'merchant', 'merchantConfig-query', '', '2024-11-15 17:48:25', '', '2024-11-15 17:48:25');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (42, 'merchant', 'merchantConfig-update', '', '2024-11-15 17:48:31', '', '2024-11-15 17:48:45');

## 银行代码

INSERT INTO mc_payment.mcp_asset_bank (payment_type, asset_name, net_protocol, bank_code, bank_name, create_by,
                                       create_time, update_by, update_time)
VALUES ('1', 'IDR', 'BANK_ID', '116', 'Bank Aceh Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1160', 'Bank Agris UUS', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1161', 'BPD ISTIMEWA ACEH SYARIAH', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '945', 'Bank IBK Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1162', 'Bank Amar Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '494', 'Bank Agroniaga', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '466', 'Bank Andara', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '531', 'Anglomas International Bank', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1163', 'Bank Antar Daerah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '061', 'Bank ANZ Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0610', 'Bank ANZ PANIN', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '987', 'Artajasa Pembayaran Elektronik', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '020', 'Bank Arta Niaga Kencana', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '037', 'Bank Artha Graha Internasional', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '542', 'Bank Artos / Bank Jago', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '129', 'BPD Bali', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '459', 'Bank Bisnis Internasional', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '040', 'Bangkok Bank', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '558', 'BPD Banten', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '525', 'Bank Barclays Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '014', 'Bank Central Asia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '536', 'Bank Central Asia (BCA) Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '133', 'Bank Bengkulu', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '110', 'Bank Jawa Barat (BJB)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '425', 'Bank BJB Syariah', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '009', 'Bank Negara Indonesia (BNI)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '427', 'Bank BNI Syariah', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '069', 'Bank of China Limited', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '002', 'Bank Rakyat Indonesia (BRI)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '422', 'Bank BRI Syariah', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1450', 'Bank BNP Paribas', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '033', 'Bank of America NA', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '688', 'BPR KS', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '4510', 'Bank Syariah Indonesia (BSI)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '200', 'Bank Tabungan Negara (BTN)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '2000', 'Bank Tabungan Negara (BTN) UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '213', 'Bank BTPN', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5470', 'BTPN Syariah', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '547', 'Bank BTPN Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '441', 'Wokee/Bukopin', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '521', 'Bank Bukopin Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '076', 'Bank Bumi Arta', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '4850', 'Bank Bumiputera', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '054', 'Bank Capital Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5590', 'Bank Centratama', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '9490', 'Bank China Construction', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '949', 'CTBC Indonesia', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '559', 'Centratama Nasional Bank (CNB)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '022', 'Bank CIMB Niaga', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0220', 'Bank CIMB Niaga UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0221', 'Bank CIMB Niaga REKENING PONSEL', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '031', 'Citibank', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '950', 'Bank Commonwealth', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '112', 'BPD DIY', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1121', 'Bank Pembangunan Daerah DIY Unit Usaha Syariah', 'pine', '2024-11-20 17:39:47',
        'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '011', 'Bank Danamon', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0110', 'Bank Danamon UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '046', 'Bank DBS Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '067', 'Deutsche Bank', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '526', 'Bank Dinar Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5230', 'Bank Dipo International', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '111', 'Bank DKI', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '778', 'Bank DKI UUS', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '699', 'Bank Eka', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '087', 'Bank Ekonomi Raharja', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '562', 'Bank Fama International', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '161', 'Bank Ganesha', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '484', 'Line Bank / KEB Hana', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '567', 'Allo Bank / Bank Harda Internasional', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '2120', 'Bank Himpunan Saudara 1906', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '041', 'HSBC', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '164', 'Bank ICBC Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '513', 'Bank Ina Perdana', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '555', 'Bank Index Selindo', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '146', 'Bank of India Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5421', 'Bank Jago Tbk', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '115', 'Bank Jambi', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '472', 'Bank Jasa Jakarta', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '113', 'Bank Jateng', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1130', 'BPD JAWA TENGAH UNIT USAHA SYARIAH', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '114', 'Bank Jatim', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1140', 'BPD Jawa Timur', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1141', 'Bank Jatim UUS', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '032', 'JPMORGAN CHASE BANK', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '095', 'Bank JTrust Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '123', 'BPD Kalimantan Barat / Kalbar', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1230', 'BPD Kalimantan Barat UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '122', 'BPD Kalimantan Selatan / Kalsel', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1220', 'BPD Kalimantan Selatan UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '125', 'BPD Kalimantan Tengah (Kalteng)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '124', 'BPD Kalimantan Timur', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1240', 'BPD Kalimantan Timur UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '535', 'Seabank / Bank Kesejahteraan Ekonomi (BKE)', 'pine', '2024-11-20 17:39:47',
        'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '121', 'BPD Lampung', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '131', 'Bank Maluku', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '008', 'Bank Mandiri', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '451', 'Bank Syariah Mandiri', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5640', 'Bank Mandiri Taspen Pos', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '564', 'Bank MANTAP', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '548', 'Bank Multi Arta Sentosa (MAS)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '157', 'Bank Maspion Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '097', 'Bank Mayapada', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '016', 'Bank Maybank', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '947', 'Bank Maybank Syariah Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0160', 'Bank Maybank Syariah Indonesia UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '553', 'Bank Mayora Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '426', 'Bank Mega', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '506', 'Bank Mega Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '151', 'Bank Mestika Dharma', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1520', 'BANK METRO EXPRESS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '485', 'Motion / Bank MNC Internasional', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '147', 'Bank Muamalat Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '491', 'Bank Mitra Niaga', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '048', 'Bank Mizuho Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10010', 'Bank MUTIARA', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10006', 'Bank MULTICOR', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '503', 'Bank National Nobu', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '583', 'BANK NIAGA TBK. SYARIAH', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '128', 'BPD Nusa Tenggara Barat (NTB)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1280', 'BPD Nusa Tenggara Barat (NTB) UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '130', 'BPD Nusa Tenggara Timur (NTT)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '145', 'Bank Nusantara Parahyangan', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '028', 'Bank OCBC NISP', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0280', 'Bank OCBC NISP UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '019', 'Bank Panin', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '517', 'Panin Dubai Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '132', 'Bank Papua', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '013', 'Bank Permata', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '0130', 'Bank Permata UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '520', 'Bank Prima Master', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '584', 'Bank Pundi Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '167', 'QNB Kesawan', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1670', 'QNB Indonesia', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5260', 'Bank Oke Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '089', 'Rabobank International Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '047', 'Bank Resona Perdania', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '119', 'BPD Riau Dan Kepri', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1190', 'BPD Riau Dan Kepri UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5010', 'Blu / BCA Digital', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '5471', 'Bank Purba Danarta', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '523', 'Bank Sahabat Sampoerna', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '498', 'Bank SBI Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '152', 'Bank Shinhan Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '153', 'Bank Sinarmas', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1530', 'Bank Sinarmas UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '126', 'Bank Sulselbar', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1260', 'Bank Sulselbar UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '127', 'BPD Sulawesi Utara', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '118', 'BPD Sumatera Barat', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1180', 'BPD Sumatera Barat UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1181', 'Bank Nagari', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '120', 'BPD Sumsel Babel', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1200', 'Bank Sumsel Dan Babel', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1201', 'Bank Sumsel Dan Babel UUS', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '117', 'Bank Sumut', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1170', 'Bank Sumut UUS', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '045', 'Bank Sumitomo Mitsui Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '042', 'Bank of Tokyo', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '023', 'Bank UOB Indonesia', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '566', 'Bank Victoria International', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '405', 'Bank Victoria Syariah', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '212', 'Bank Yudha Bhakti', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '490', 'Neo Commerce/Bank Yudha Bhakti(BNC)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '1120', 'BPD_Daerah_Istimewa_Yogyakarta_(DIY)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '088', 'CCB Indonesia', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '501', 'Royal Bank of Scotland (RBS)', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10001', 'OVO', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10002', 'DANA', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10003', 'GOPAY', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10008', 'SHOPEEPAY', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'IDR', 'BANK_ID', '10009', 'LINKAJA', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47');



INSERT INTO mc_payment.mcp_asset_bank (payment_type, asset_name, net_protocol, bank_code, bank_name, create_by,
                                       create_time, update_by, update_time)
VALUES ('1', 'THB', 'BANK_TH', '001', 'Kasikorn Bank Plc.', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '003', 'Bangkok Bank Plc.', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '004', 'Krung Thai Bank', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '005', 'ABN Amro Bank N.V.', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '007', 'TMBThanachart', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '010', 'Siam Commercial Bank', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '016', 'UOB Bank Plc.', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '017', 'Bank of Ayudhya / Krungsri', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '018', 'CIMB Thai Bank Public Company Limited', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '020', 'Land and Houses Bank Public Company Limited', 'pine', '2024-11-20 17:39:47',
        'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '022', 'Government Savings Bank', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '023', 'Kiatnakin Phatra Bank Public Company Limited', 'pine', '2024-11-20 17:39:47',
        'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '024', 'Citibank N.A.', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '025', 'Government Housing Bank', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '026', 'Bank for Agriculture and Agricultural Cooperatives', 'pine',
        '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '027', 'Mizuho Corporate Bank Limited', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '028', 'Islamic Bank of Thailand', 'pine', '2024-11-20 17:39:47', 'pine',
        '2024-11-20 17:39:47'),
       ('1', 'THB', 'BANK_TH', '029', 'TISCO Bank Plc.', 'pine', '2024-11-20 17:39:47', 'pine', '2024-11-20 17:39:47');


## 删除业务员角色的编辑和新增权限
DELETE
FROM mcp_sys_role_permission_relation
WHERE role_code = 'salesman'
  AND (permission_code LIKE "%update%" or permission_code LIKE "%add%");
## 新增商户角色的权限
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (43, 'merchant', 'payProtocol-fiat-query', '', '2024-11-28 15:14:27', '', '2024-11-28 15:14:34');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (44, 'merchant', 'platformAsset-query', '', '2024-11-28 15:14:37', '', '2024-11-28 15:14:57');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (45, 'merchant', 'channelAsset-query', '', '2024-11-28 15:15:34', '', '2024-11-28 15:15:36');

INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (46, 'merchant', 'payProtocol-crypto-query', '', '2024-11-28 16:46:38', '', '2024-11-28 16:46:46');

INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (47, 'merchant', 'registerAsset-query', '', '2024-11-29 10:56:11', '', '2024-11-29 10:56:16');
INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (48, 'salesman', 'merchantConfig-update', '', '2024-11-29 10:56:39', '', '2024-11-29 10:56:44');

INSERT INTO `mc_payment`.`mcp_sys_role_permission_relation`(`id`, `role_code`, `permission_code`, `create_by`,
                                                            `create_time`, `update_by`, `update_time`)
VALUES (49, 'merchant', 'channelCost-query', '', '2024-11-29 14:35:11', '', '2024-11-29 14:35:23');
