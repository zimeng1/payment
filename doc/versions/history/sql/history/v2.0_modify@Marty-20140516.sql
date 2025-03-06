-- mc_payment二期脚本

-- 通道表新增[通道有效期类型]字段
ALTER TABLE mcp_channel  ADD COLUMN expiration_date_type tinyint unsigned default 1 not null comment '通道有效期类型,0:一直有效,1:取通道有效期具体时间段';

-- 入金记录明细表新增[当时的钱包余额],[fireblocks回调的交易更新时间],[当时币种转换为USDT的汇率],[当时手续费币种转换为USDT的汇率]字段
ALTER TABLE mcp_deposit_record_detail
    ADD COLUMN addr_balance decimal(32,20) default 0 not null comment '当时的(目标)钱包地址余额',
    ADD COLUMN last_updated bigint default 0 not null comment 'fireblocks回调的交易更新时间-精确毫秒',
    ADD COLUMN rate decimal(32,20) default 0 not null comment '当时币种转换为USDT的汇率',
    ADD COLUMN fee_rate decimal(32,20) default 0 not null comment '当时手续费币种转换为USDT的汇率';

-- 入金记录表新增[当时的钱包余额],[手续费资产名称],[当时币种转换为USDT的汇率],[当时手续费币种转换为USDT的汇率]字段
ALTER TABLE mcp_deposit_record
    ADD COLUMN addr_balance decimal(32,20) default 0 not null comment '当时的(目标)钱包地址余额',
    ADD COLUMN fee_asset_name varchar(20) default '' not null comment '手续费资产名称',
    ADD COLUMN rate decimal(32,20) default 0 not null comment '当时币种转换为USDT的汇率',
    ADD COLUMN fee_rate decimal(32,20) default 0 not null comment '当时手续费币种转换为USDT的汇率';

-- 出金记录表新增[当时的钱包余额],[通道资产名称],[手续费的通道资产名称],[手续费资产名称],[当时币种转换为USDT的汇率],[当时手续费币种转换为USDT的汇率]字段
ALTER TABLE mcp_withdrawal_record
    ADD COLUMN addr_balance decimal(32,20) default 0 not null comment '当时的(来源)钱包地址余额',
    ADD COLUMN channel_asset_name varchar(20) default '' not null comment '通道资产名称',
    ADD COLUMN fee_channel_asset_name varchar(20) default '' not null comment '手续费的通道资产名称',
    ADD COLUMN fee_asset_name varchar(20) default '' not null comment '手续费资产名称',
    ADD COLUMN rate decimal(32,20) default 0 not null comment '当时币种转换为USDT的汇率',
    ADD COLUMN fee_rate decimal(32,20) default 0 not null comment '当时手续费币种转换为USDT的汇率';

-- 通道(平台)成本表-最低成本允许未空
alter table mcp_channel_cost modify min_cost_limit decimal(32, 20) null comment '最低成本';

-- 通道(平台)成本表-最高成本允许未空
alter table mcp_channel_cost modify max_cost_limit decimal(32, 20) null comment '最高成本';




-- 需要初始化脚本 -----------------------------------------------------------------------------------------------------------------
-- 初始化出金记录表的商户名称
update mcp_withdrawal_record wr set wr.merchant_name = (select name from mcp_merchant m where m.id = wr.merchant_id)
where wr.merchant_name = '' or wr.merchant_name =  CONVERT(CAST(wr.merchant_id AS CHAR) USING utf8mb4) COLLATE utf8mb4_general_ci and (select count(*) from mcp_merchant m where m.id = wr.merchant_id) > 0;

-- 初始化通道资产名称
update mcp_withdrawal_record wr
set channel_asset_name = (select ca.channel_asset_name from mcp_channel_asset ca where ca.asset_name = wr.asset_name and ca.net_protocol = wr.net_protocol  limit 1)
where wr.net_protocol != '' and  (select count(*) from mcp_channel_asset ca where ca.asset_name = wr.asset_name and ca.net_protocol = wr.net_protocol  limit 1) > 0;

-- 初始化异种币通道资产名称
UPDATE mcp_withdrawal_record wr
SET wr.fee_channel_asset_name =
        CASE
            WHEN ( SELECT fee_asset_name FROM mcp_asset_config ac
                   WHERE ac.asset_name = wr.asset_name AND ac.net_protocol = wr.net_protocol limit 1
    ) = wr.asset_name THEN wr.channel_asset_name
            ELSE (SELECT channel_asset_name FROM mcp_channel_asset ca
                  WHERE ca.asset_name = ( SELECT fee_asset_name FROM mcp_asset_config ac1 WHERE ac1.asset_name = wr.asset_name AND ac1.net_protocol = wr.net_protocol limit 1)
                    AND ca.net_protocol = wr.net_protocol limit 1)
END
WHERE wr.net_protocol != '' and  (select count(*) from mcp_channel_asset ca where ca.asset_name = wr.asset_name and ca.net_protocol = wr.net_protocol  limit 1) > 0;

-- 初始化 手续费资产名称(入金记录表)
update mcp_deposit_record dr
set fee_asset_name = (select ac.fee_asset_name from mcp_asset_config ac where ac.asset_name = dr.asset_name and ac.net_protocol = dr.net_protocol  limit 1)
where dr.net_protocol != '' and dr.fee_asset_name = '' and  (select count(fee_asset_name) from mcp_asset_config ac where ac.asset_name = dr.asset_name and ac.net_protocol = dr.net_protocol  limit 1) > 0;

-- 初始化 手续费资产名称(出金记录表)
update mcp_withdrawal_record wr
set fee_asset_name = (select ac.fee_asset_name from mcp_asset_config ac where ac.asset_name = wr.asset_name and ac.net_protocol = wr.net_protocol  limit 1)
where wr.net_protocol != '' and wr.fee_asset_name = '' and  (select count(fee_asset_name) from mcp_asset_config ac where ac.asset_name = wr.asset_name and ac.net_protocol = wr.net_protocol  limit 1) > 0;

