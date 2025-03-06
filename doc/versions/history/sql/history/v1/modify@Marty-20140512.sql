-- 线上环境初始化脚本0512

-- 配置表新增'fee_asset_name',手续费资产名称字段
ALTER TABLE mcp_asset_config ADD COLUMN fee_asset_name varchar(20) default ''  not null comment '手续费资产名称,[如:BTC]' after asset_name;


-- 出金记录表新增'冻结的预估手续费','冻结的钱包id'字段
ALTER TABLE mcp_withdrawal_record 
ADD COLUMN freeze_es_fee decimal(32,20) default 0 not null comment '冻结的预估手续费', 
ADD COLUMN freeze_wallet_id bigint default 0 not null comment '冻结的钱包id';


-- 初始化配置表 的 手续费资产名称字段
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'USDC' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'TRX' where asset_name = 'USDC' and net_protocol = 'Tron' and asset_net = 'TRON';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'USDC' and net_protocol = 'BSC' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = 'USDC' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'ARB' where asset_name = 'USDC' and net_protocol = 'Arbitrum' and asset_net = 'Arbitrum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'USDT' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'TRX' where asset_name = 'USDT' and net_protocol = 'TRC20' and asset_net = 'TRON';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'USDT' and net_protocol = 'BSC' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = 'USDT' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'ARB' where asset_name = 'USDT' and net_protocol = 'Arbitrum' and asset_net = 'Arbitrum';
update mcp_asset_config set fee_asset_name = 'BTC' where asset_name = 'BTC' and net_protocol = 'Bitcoin' and asset_net = 'Bitcoin';
update mcp_asset_config set fee_asset_name = 'BTC' where asset_name = 'BTC' and net_protocol = 'LIGHTNING' and asset_net = 'Lightning Network';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'ETH' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = 'ETH' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'XTZ' where asset_name = 'ETH' and net_protocol = 'Tezos' and asset_net = 'Tezos';
update mcp_asset_config set fee_asset_name = 'STRK' where asset_name = 'ETH' and net_protocol = 'Starknet' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'BNB' and net_protocol = 'Binance Smart Chain' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = 'SOL' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'SAND' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'MANA' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'BASED' where asset_name = 'EOS' and net_protocol = 'BASE_ASSET' and asset_net = 'BASE';
update mcp_asset_config set fee_asset_name = 'BASED' where asset_name = 'RONIN' and net_protocol = 'BASE_ASSET' and asset_net = 'BASE';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'DAI' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'XRP' and net_protocol = 'Binance Smart Chain' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'CRDN' where asset_name = 'ADA' and net_protocol = 'Cardano' and asset_net = 'Cardano';
update mcp_asset_config set fee_asset_name = 'DOGE' where asset_name = 'DOGE' and net_protocol = 'Dogecoin' and asset_net = 'Dogecoin';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'SHIB' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'AVAX' where asset_name = 'AVAX' and net_protocol = 'Avalanche' and asset_net = 'Avalanche';
update mcp_asset_config set fee_asset_name = 'DOT' where asset_name = 'DOT' and net_protocol = 'Polkadot' and asset_net = 'Polkadot';
update mcp_asset_config set fee_asset_name = 'TRX' where asset_name = 'TRX' and net_protocol = 'TRON' and asset_net = 'TRON';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'LINK' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'MATIC' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'MATIC' and net_protocol = 'Binance Smart Chain' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'MATIC' where asset_name = 'MATIC' and net_protocol = 'Polygon' and asset_net = 'Polygon';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'WBTC' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'HOT' where asset_name = 'BCH' and net_protocol = 'HECO' and asset_net = 'Huobi Eco Chain';
update mcp_asset_config set fee_asset_name = 'BNB' where asset_name = 'BCH' and net_protocol = 'Binance Smart Chain' and asset_net = 'Binance Smart Chain';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'UNI' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ICP' where asset_name = 'ICP' and net_protocol = 'Internet Computer' and asset_net = 'Internet Computer';
update mcp_asset_config set fee_asset_name = 'LTC' where asset_name = 'LTC' and net_protocol = 'Litecoin' and asset_net = 'Litecoin';
update mcp_asset_config set fee_asset_name = 'NEAR' where asset_name = 'NEAR' and net_protocol = 'NEAR Protocol' and asset_net = 'NEAR Protocol';
update mcp_asset_config set fee_asset_name = 'FIL' where asset_name = 'FIL' and net_protocol = 'Filecoin' and asset_net = 'Filecoin';
update mcp_asset_config set fee_asset_name = 'ETC' where asset_name = 'ETC' and net_protocol = 'Ethereum Classic' and asset_net = 'Ethereum Classic';
update mcp_asset_config set fee_asset_name = 'ATOM' where asset_name = 'ATOM' and net_protocol = 'Cosmos' and asset_net = 'Cosmos';
update mcp_asset_config set fee_asset_name = 'APT' where asset_name = 'APT' and net_protocol = 'Aptos' and asset_net = 'APT';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'IMX' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'OP' where asset_name = 'OP' and net_protocol = 'Optimism' and asset_net = 'Optimism';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'RNDR' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'GRT' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'HBAR' where asset_name = 'HBAR' and net_protocol = 'Hedera Hashgraph' and asset_net = 'Hedera Hashgraph';
update mcp_asset_config set fee_asset_name = 'STX' where asset_name = 'STX' and net_protocol = 'Stacks' and asset_net = 'Stacks';
update mcp_asset_config set fee_asset_name = 'XLM' where asset_name = 'XLM' and net_protocol = 'Stellar' and asset_net = 'Stellar';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = '1000PEPE' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'INJ' where asset_name = 'INJ' and net_protocol = 'Injective Protocol' and asset_net = 'Injective Protocol';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'WBETH' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'VET' where asset_name = 'VET' and net_protocol = 'VeChain' and asset_net = 'VeChain';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'FDUSD' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'THETA' where asset_name = 'THETA' and net_protocol = 'Theta Network' and asset_net = 'Theta Network';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'LDO' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'RUNE' where asset_name = 'RUNE' and net_protocol = 'THORChain' and asset_net = 'THORChain';
update mcp_asset_config set fee_asset_name = 'OP' where asset_name = 'TIA' and net_protocol = 'Optimism' and asset_net = 'Optimism';
update mcp_asset_config set fee_asset_name = 'AR' where asset_name = 'AR' and net_protocol = 'Arweave' and asset_net = 'Arweave';
update mcp_asset_config set fee_asset_name = 'ARB' where asset_name = 'ARB' and net_protocol = 'Arbitrum' and asset_net = 'Arbitrum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'FET' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'MKR' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = '1000FLOKI' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'SEI' where asset_name = 'SEI' and net_protocol = 'SEI' and asset_net = 'SEI';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'BEAMX' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'FTM' where asset_name = 'FTM' and net_protocol = 'Fantom' and asset_net = 'Fantom';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = 'WIF' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'FLOW' where asset_name = 'FLOW' and net_protocol = 'FLOW' and asset_net = 'FLOW';
update mcp_asset_config set fee_asset_name = 'ALGO' where asset_name = 'ALGO' and net_protocol = 'Algorand' and asset_net = 'Algorand';
update mcp_asset_config set fee_asset_name = 'SOL' where asset_name = '1000BONK' and net_protocol = 'Solana' and asset_net = 'Solana';
update mcp_asset_config set fee_asset_name = 'SUI' where asset_name = 'SUI' and net_protocol = 'SUI' and asset_net = 'SUI';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'AAVE' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'EGLD' where asset_name = 'EGLD' and net_protocol = 'MultiversX' and asset_net = 'MultiversX';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'GALA' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'AXS' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'STRK' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'JUP' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'DYDX' and net_protocol = 'ERC20' and asset_net = 'Ethereum';
update mcp_asset_config set fee_asset_name = 'ETH' where asset_name = 'AGIX' and net_protocol = 'ERC20' and asset_net = 'Ethereum';

