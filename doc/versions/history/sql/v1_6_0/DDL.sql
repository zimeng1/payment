CREATE TABLE `mcp_merchant_channel_asset`
(
    `id`                  bigint                                                       NOT NULL,
    `merchant_id`         bigint                                                       NOT NULL COMMENT '商户id',
    `channel_sub_type`    tinyint                                                      NOT NULL DEFAULT '0' COMMENT '通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal]',
    `asset_name`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产名称,[如:BTC]',
    `net_protocol`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议',
    `alarm_status`        tinyint                                                      NOT NULL COMMENT '是否启用告警,[0:否,1:是]',
    `reserve_alarm_value` decimal(32, 20)                                              NOT NULL COMMENT '备付金告警值',
    `create_by`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`         timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `update_by`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`         timestamp(3)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_channel_asset` (`merchant_id`, `channel_sub_type`, `asset_name`, `net_protocol`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='商户支付通道资产配置';

