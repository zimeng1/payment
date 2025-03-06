ALTER TABLE mcp_asset_config
    ADD hash_url varchar(128) not null default '' comment 'Hash跳转url';

update mcp_asset_config
set hash_url = 'https://bscscan.com/tx/'
where net_protocol = 'BEP20';

update mcp_asset_config
set hash_url = 'https://www.blockchain.com/explorer/transactions/btc/'
where net_protocol = 'Bitcoin';

update mcp_asset_config
set hash_url = 'https://etherscan.io/tx/'
where net_protocol = 'ERC20';

update mcp_asset_config
set hash_url = 'https://solscan.io/tx/'
where net_protocol = 'Solana';

update mcp_asset_config
set hash_url = 'https://arbiscan.io/tx/'
where net_protocol = 'Arbitrum';

update mcp_asset_config
set hash_url = 'https://cexplorer.io/address/'
where net_protocol = 'Cardano';

update mcp_asset_config
set hash_url = 'https://allo.info/tx/'
where net_protocol = 'Algorand';

update mcp_asset_config
set hash_url = 'https://www.atomscan.com/transactions/'
where net_protocol = 'Cosmos';

update mcp_asset_config
set hash_url = 'https://www.blockchain.com/explorer/transactions/bch/'
where net_protocol = 'Bitcoin';

update mcp_asset_config
set hash_url = 'https://www.oklink.com/zh-hans/doge/tx/'
where net_protocol = 'DogeCoin';

update mcp_asset_config
set hash_url = 'https://polkadot.subscan.io/extrinsic'
where net_protocol = 'Polkadot';

update mcp_asset_config
set hash_url = 'https://eosflare.io/tx/'
where net_protocol = 'EOS';

update mcp_asset_config
set hash_url = 'https://www.oklink.com/zh-hans/etc'
where net_protocol = 'Ethereum Classic';

update mcp_asset_config
set hash_url = 'https://hederaexplorer.io/'
where net_protocol = 'Hedera Hashgraph';

update mcp_asset_config
set hash_url = 'https://litecoinblockexplorer.net/'
where net_protocol = 'Litecoin';

update mcp_asset_config
set hash_url = 'https://near.tokenview.io/'
where net_protocol = 'NEAR Protocol';

update mcp_asset_config
set hash_url = 'https://app.roninchain.com/'
where net_protocol = 'Ronin';

update mcp_asset_config
set hash_url = 'https://nile.tronscan.org/#/'
where net_protocol = 'TRC20';

update mcp_asset_config
set hash_url = 'https://stellarchain.io/transactions/'
where net_protocol = 'Stellar';

update mcp_asset_config
set hash_url = 'https://blockchair.com/xrp-ledger/transaction/'
where net_protocol = 'Ripple';

ALTER TABLE mcp_deposit_record
    ADD stay_reason varchar(128) not null default '' comment '停留原因';

ALTER TABLE mcp_withdrawal_record
    ADD stay_reason varchar(128) not null default '' comment '停留原因';

ALTER TABLE mcp_deposit_record_detail
    add status TINYINT not null default '0' COMMENT '入金明细状态';

CREATE TABLE `mcp_withdrawal_record_detail`
(
    `id`                  bigint                                                        NOT NULL,
    `record_id`           varchar(128) COLLATE utf8mb4_general_ci                       NOT NULL DEFAULT '' COMMENT '记录id',
    `tx_hash`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'TxHash',
    `channel_sub_type`    tinyint                                                       NOT NULL DEFAULT '0' COMMENT '通道子类型 1 FireBlocks,2 OFAPay',
    `asset_name`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '资产名称,[如:BTC]',
    `net_protocol`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '网络协议',
    `source_address`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '来源地址',
    `destination_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目标地址',
    `merchant_id`         bigint                                                        NOT NULL COMMENT '商户id',
    `merchant_name`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '商户名称',
    `amount`              decimal(32, 20)                                               NOT NULL COMMENT '金额',
    `network_fee`         decimal(32, 20)                                               NOT NULL DEFAULT '0.00000000000000000000' COMMENT '网络费',
    `service_fee`         decimal(32, 20)                                               NOT NULL DEFAULT '0.00000000000000000000' COMMENT '服务费',
    `rate`                decimal(32, 20)                                               NOT NULL DEFAULT '0.00000000000000000000' COMMENT '当时币种转换为USDT的汇率',
    `fee_rate`            decimal(32, 20)                                               NOT NULL DEFAULT '0.00000000000000000000' COMMENT '当时手续费币种转换为USDT的汇率',
    `addr_balance`        decimal(32, 20)                                               NOT NULL COMMENT '当时的钱包地址余额',
    `status`              tinyint                                                       not null default '0' COMMENT '出金明细明细状态 1未确认,2确认中,3已确认,4已取消,5已出金,6出金失败',
    `deleted`             tinyint unsigned                                              NOT NULL DEFAULT '0' COMMENT '逻辑删除,[0:未删除,1已删除]',
    `create_by`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`         timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`         timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_record_id` (`record_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

CREATE TABLE `mcp_merchant_wallet_snapshot`
(
    `id`           bigint                                                       NOT NULL COMMENT '主键id',
    `merchant_id`  bigint                                                       NOT NULL COMMENT '账户签约的商户的id',
    `asset_type`   tinyint unsigned                                             NOT NULL DEFAULT '0' COMMENT '资产类型,[0:加密货币,1:法定货币]',
    `asset_name`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '资产类型/币种',
    `net_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '网络协议/支付网络',
    `purpose_type` tinyint                                                      NOT NULL COMMENT '用途类型,[0:入金,1:出金]',
    `wallet_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账户地址',
    `balance`      decimal(32, 20)                                              NOT NULL COMMENT '余额',
    `create_by`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`  timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最后修改人',
    `update_time`  timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='商户钱包快照';

alter table mcp_deposit_record
    add audit_status tinyint NOT NULL DEFAULT '0' comment '审核状态 1审核通过,2审核不通过';

alter table mcp_withdrawal_record
    add audit_status tinyint NOT NULL DEFAULT '0' comment '审核状态 1审核通过,2审核不通过,3终止执行';

ALTER TABLE mcp_merchant
    MODIFY COLUMN alarm_email text COMMENT '告警邮箱';

ALTER TABLE mcp_merchant
    MODIFY COLUMN settlement_email text COMMENT '结算对接人邮箱';

alter table mcp_deposit_record_detail
    add channel_sub_type tinyint NOT NULL DEFAULT '0' COMMENT '通道子类型 1 FireBlocks,2 OFAPay';

CREATE UNIQUE INDEX uk_tx_hash ON mcp_withdrawal_record_detail (tx_hash);


