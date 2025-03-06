-- ----------------------------
-- 移除钱包表重复数据
-- ----------------------------
WITH cte AS (
    SELECT *,
           ROW_NUMBER() OVER(PARTITION BY account_id, asset_name, net_protocol, wallet_address
               ORDER BY CASE WHEN external_id = '' THEN 0 ELSE 1 END, update_time desc ) AS rn
    FROM mcp_wallet
)
DELETE FROM mcp_wallet WHERE id in(select id FROM cte WHERE rn > 1);
-- ----------------------------
-- 更新钱包表external_id为空的数据
-- ----------------------------
update mcp_wallet set external_id = (select external_id from mcp_account where id = account_id) where external_id = ''
### 以上已经执行test环境 2024年6月13日 14点10分

