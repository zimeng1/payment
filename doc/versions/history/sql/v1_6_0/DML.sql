INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (44, 1, 'USD', 'PayPal', 'PayPal',
        'USD', 0, 0, 0,
        0, 0, 0, 0);
INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (45, 1, 'JPY', 'PayPal', 'PayPal',
        'JPY', 0, 0, 0,
        0, 0, 0, 0);
INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (46, 1, 'EUR', 'PayPal', 'PayPal', 'EUR', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (47, 1, 'CAD', 'PayPal', 'PayPal', 'CAD', 0, 0, 0, 0, 0, 0, 0),
       (48, 1, 'CHF', 'PayPal', 'PayPal', 'CHF', 0, 0, 0, 0, 0, 0, 0),
       (49, 1, 'CZK', 'PayPal', 'PayPal', 'CZK', 0, 0, 0, 0, 0, 0, 0),
       (50, 1, 'DKK', 'PayPal', 'PayPal', 'DKK', 0, 0, 0, 0, 0, 0, 0),
       (51, 1, 'MXN', 'PayPal', 'PayPal', 'MXN', 0, 0, 0, 0, 0, 0, 0),
       (52, 1, 'GBP', 'PayPal', 'PayPal', 'GBP', 0, 0, 0, 0, 0, 0, 0),
       (53, 1, 'HKD', 'PayPal', 'PayPal', 'HKD', 0, 0, 0, 0, 0, 0, 0),
       (54, 1, 'HUF', 'PayPal', 'PayPal', 'HUF', 0, 0, 0, 0, 0, 0, 0),
       (55, 1, 'ILS', 'PayPal', 'PayPal', 'ILS', 0, 0, 0, 0, 0, 0, 0),
       (56, 1, 'NOK', 'PayPal', 'PayPal', 'NOK', 0, 0, 0, 0, 0, 0, 0),
       (57, 1, 'NZD', 'PayPal', 'PayPal', 'NZD', 0, 0, 0, 0, 0, 0, 0),
       (58, 1, 'PHP', 'PayPal', 'PayPal', 'PHP', 0, 0, 0, 0, 0, 0, 0),
       (59, 1, 'PLN', 'PayPal', 'PayPal', 'PLN', 0, 0, 0, 0, 0, 0, 0),
       (60, 1, 'RUB', 'PayPal', 'PayPal', 'RUB', 0, 0, 0, 0, 0, 0, 0),
       (61, 1, 'SEK', 'PayPal', 'PayPal', 'SEK', 0, 0, 0, 0, 0, 0, 0),
       (62, 1, 'SGD', 'PayPal', 'PayPal', 'SGD', 0, 0, 0, 0, 0, 0, 0),
       (63, 1, 'THB', 'PayPal', 'PayPal', 'THB', 0, 0, 0, 0, 0, 0, 0),
       (64, 1, 'AUD', 'PayPal', 'PayPal', 'AUD', 0, 0, 0, 0, 0, 0, 0),
       (65, 1, 'TWD', 'PayPal', 'PayPal', 'TWD', 0, 0, 0, 0, 0, 0, 0);



# 测试环境sql 增加paypal配置
INSERT INTO `mc_payment`.`mcp_channel_asset`(`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                             `channel_credential`, `net_protocol`)
VALUES (60, 3, 'USD', 'USD', 'PayPal', '{
  \"accountName\": \"sb-pxm5632684614@business.example.com\"
}', 'PayPal');

INSERT INTO `mc_payment`.`mcp_channel_asset`(`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                             `channel_credential`, `net_protocol`)
VALUES (61, 3, 'JPY', 'JPY', 'PayPal', '{
  \"accountName\": \"sb-zbab732658182@business.example.com\"
}', 'PayPal');
INSERT INTO `mc_payment`.`mcp_channel_asset`(`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                             `channel_credential`, `net_protocol`)
VALUES (81, 3, 'CAD', 'CAD', 'PayPal', '{
  "accountName": "sb-muac032771589@business.example.com"
}', 'PayPal'),
       (63, 3, 'CHF', 'CHF', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (64, 3, 'CZK', 'CZK', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (65, 3, 'DKK', 'DKK', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (66, 3, 'MXN', 'MXN', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (67, 3, 'GBP', 'GBP', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (68, 3, 'HKD', 'HKD', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (69, 3, 'HUF', 'HUF', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (70, 3, 'ILS', 'ILS', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (71, 3, 'NOK', 'NOK', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (72, 3, 'NZD', 'NZD', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (73, 3, 'PHP', 'PHP', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (74, 3, 'PLN', 'PLN', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (75, 3, 'RUB', 'RUB', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (76, 3, 'SEK', 'SEK', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (77, 3, 'SGD', 'SGD', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (78, 3, 'THB', 'THB', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (79, 3, 'AUD', 'AUD', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal'),
       (80, 3, 'TWD', 'TWD', 'PayPal', '{
         "accountName": "sb-muac032771589@business.example.com"
       }', 'PayPal');

ALTER TABLE `mc_payment`.`mcp_deposit_record`
    ADD COLUMN `channel_transaction_id` varchar(255) NOT NULL DEFAULT '' COMMENT '第三方通道交易id' AFTER `user_ip`;
