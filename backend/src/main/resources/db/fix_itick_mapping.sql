-- =============================================
-- 修复 iTick API 字段映射配置
-- iTick API 返回的字段是简写格式
-- =============================================

-- 更新股票列表映射
-- iTick 返回: c=code, n=name, e=exchange
UPDATE stk_data_mapping
SET field_mapping = '{"mappings":[{"source":"c","target":"stockCode","type":"string"},{"source":"n","target":"stockName","type":"string"},{"source":"e","target":"exchange","type":"string"}]}'
WHERE mapping_code = 'itick_stock_list';

-- 更新K线数据映射
-- iTick 返回: t=timestamp(毫秒), o=open, h=high, l=low, c=close, v=volume, tu=turnover
UPDATE stk_data_mapping
SET field_mapping = '{"mappings":[{"source":"t","target":"tradeDate","type":"timestamp"},{"source":"o","target":"openPrice","type":"decimal","scale":4},{"source":"h","target":"highPrice","type":"decimal","scale":4},{"source":"l","target":"lowPrice","type":"decimal","scale":4},{"source":"c","target":"closePrice","type":"decimal","scale":4},{"source":"v","target":"volume","type":"long"},{"source":"tu","target":"amount","type":"decimal","scale":4}]}'
WHERE mapping_code = 'itick_kline_daily';
