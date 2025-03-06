# 商户通道关系表新增reserve_fund_type字段和asset_name字段, 在create_time字段之前
ALTER TABLE mcp_merchant_channel_relation ADD reserve_fund_type tinyint DEFAULT 0 COMMENT '备付金类型[0:全部币种, 1:部分币种]' after reserve_ratio;
ALTER TABLE mcp_merchant_channel_relation ADD asset_name VARCHAR(20) DEFAULT '' COMMENT '资产名称,[如:BTC]' after reserve_ratio;

# 商户通道关系表删除索引uni_merchant_channel, 新增组合索引merchant_id,channel_sub_type, reserve_fund_type
ALTER TABLE mcp_merchant_channel_relation DROP INDEX uni_merchant_channel;
ALTER TABLE mcp_merchant_channel_relation ADD INDEX idx_merchant_channel_reserve (merchant_id, channel_sub_type, reserve_fund_type);

# 添加业务员权限
INSERT INTO `mcp_sys_role_permission_relation` VALUES (30, 'salesman', 'registerAsset-query', '', '2024-06-25 18:00:00', '', '2024-06-25 18:00:00');


# 通道资产注册表(用于支持新币种)
create table mcp_channel_asset_register
(
    id                 bigint                                 not null primary key,
    channel_sub_type   tinyint      default 1                 not null comment '通道子类型,[0:BlockATM,1:FireBlocks]',
    channel_asset_name varchar(20)  default ''                not null comment '通道资产名称',
    asset_name         varchar(20)  default ''                not null comment '资产名称',
    net_protocol       varchar(20)  default ''                not null comment '网络协议',
    asset_net          varchar(50)  default ''                not null comment '资产网络',

    block_chain_id     varchar(20)  default ''                not null comment '原生资产ID',
    chain_address      varchar(255) default ''                not null comment '合约地址/资产地址',
    chain_symbol       varchar(64)  default ''                not null comment 'Asset symbol',
    chain_name       varchar(128) default ''                not null comment '第三方返回的名称',
    decimals           SMALLINT     default 18                not null comment '小数位数',
    asset_class         varchar(32)  default ''                not null comment '资产类别 (NATIVE FT NFT SFT)',
    scope              varchar(32)  default ''                not null comment '资产的范围(Global Local)',
    deleted       tinyint unsigned default '0'               not null comment '逻辑删除,[0:未删除,1已删除]',
    create_by          varchar(20)  default ''                not null comment '创建人',
    create_time        timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by          varchar(20)  default ''                not null comment '最后修改人',
    update_time        timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_sub_type_asset_name_protocol unique (channel_sub_type, asset_name, net_protocol),
    constraint uk_sub_type_channel_asset_name unique (channel_sub_type, channel_asset_name)
)
    comment '通道资产注册表(用于支持新币种)';

# 以上 test环境已执行 2024年6月25日 10点36分