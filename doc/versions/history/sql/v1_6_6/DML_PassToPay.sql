# 新增支付通道
INSERT INTO `mc_payment`.`mcp_dict`(`id`, `parent_code`, `dict_code`, `dict_desc`, `category_code`, `category_desc`,
                                    `sort_no`)
VALUES (20, 'CHANNEL_TYPE:1', '4', 'PassToPay', 'CHANNEL_SUB_TYPE', '通道子类型', 4);

# 新增平台资产
INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (66, 1, 'CNY', 'Alipay QR', 'Alipay QR', 'CNY', 0.01, 0.01, 0, 0, 0, 0, 0);

INSERT INTO `mc_payment`.`mcp_channel_asset`(`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`, `asset_net`,
                                             `channel_credential`, `net_protocol`)
VALUES (82, 4, 'CNY_ALI_QR', 'CNY', 'Alipay QR', '{
  \"wayCode\": \"ALI_QR\"
}', 'Alipay QR');


INSERT INTO `mc_payment`.`mcp_asset_config`(`id`, `asset_type`, `asset_name`, `asset_net`, `net_protocol`,
                                            `fee_asset_name`, `min_deposit_amount`, `min_withdrawal_amount`,
                                            `estimate_fee`, `un_estimate_fee`, `default_estimate_fee`,
                                            `max_deposit_amount`, `max_withdrawal_amount`)
VALUES (118, 1, 'CNY', 'Alipay WAP', 'Alipay WAP', 'CNY', 0.01, 0.01, 0, 0, 0, 0, 0);

INSERT INTO `mc_payment`.`mcp_channel_asset`(`id`, `channel_sub_type`, `channel_asset_name`, `asset_name`,
                                             `asset_net`, `channel_credential`, `net_protocol`)
VALUES (92, 4, 'CNY_ALI_WAP', 'CNY', 'Alipay WAP', '{
  \"wayCode\": \"ALI_WAP\"
}', 'Alipay WAP');