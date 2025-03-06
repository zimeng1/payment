-- 纠正表名
ALTER TABLE mcp_receive_wehbook_log RENAME TO mcp_receive_webhook_log;

-- 新建索引idx_unique_net_protocol
CREATE UNIQUE INDEX idx_unique_net_protocol ON mcp_protocol_config (net_protocol);
