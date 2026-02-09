-- 新增5条打分规则配置
-- 执行时间：2026-02-09

INSERT INTO `stk_score_rule` (`rule_code`, `rule_name`, `rule_desc`, `category`, `score_type`, `base_score`, `max_score`, `weight`, `config_json`, `status`, `sort_order`, `remark`) VALUES
('RSI_OVERSOLD', 'RSI超卖反弹', 'RSI指标低于超卖线（30）后回升，表明股价可能超跌反弹，是潜在的买入时机', 'TECHNICAL', 'DYNAMIC', 12, 18, 1.20, '{"period":14,"oversoldLine":30,"recoveryLine":40}', 1, 6, 'RSI是经典的超买超卖指标'),
('KDJ_GOLDEN_CROSS', 'KDJ金叉', 'KDJ指标K线上穿D线形成金叉，且J值从低位回升，是短期买入信号', 'TECHNICAL', 'DYNAMIC', 12, 18, 1.15, '{"period":9}', 1, 7, 'KDJ金叉是常用的短线买入信号'),
('BOLLINGER_BREAK', '布林带突破', '价格突破布林带上轨，表明强势上涨，可能开启新一轮行情', 'TECHNICAL', 'DYNAMIC', 10, 16, 1.10, '{"period":20,"multiplier":2.0}', 1, 8, '布林带突破是趋势启动信号'),
('VOLUME_RISE', '放量上涨', '成交量显著放大（超过近期平均1.5倍）且价格上涨，表明资金积极入场', 'TECHNICAL', 'DYNAMIC', 10, 18, 1.25, '{"period":5,"volumeRatio":1.5,"priceRatio":0.02}', 1, 9, '量价配合是技术分析的核心'),
('MA_SUPPORT', '均线支撑', '价格回踩均线后获得支撑并反弹，表明均线支撑有效，趋势延续', 'TECHNICAL', 'DYNAMIC', 10, 15, 1.00, '{"maPeriod":20,"touchRatio":0.02}', 1, 10, '均线支撑是趋势延续的重要信号');
