-- 基金配置表新增基准指数字段
ALTER TABLE stk_fund_config
    ADD COLUMN benchmark_code VARCHAR(20) DEFAULT NULL COMMENT '基准指数代码' AFTER description;

-- 基金估值记录表新增基准指数相关字段
ALTER TABLE stk_fund_valuation_record
    ADD COLUMN benchmark_code VARCHAR(20) DEFAULT NULL COMMENT '基准指数代码' AFTER raw_weighted_change,
    ADD COLUMN benchmark_change_percent DECIMAL(10,6) DEFAULT NULL COMMENT '基准指数涨跌幅(%)' AFTER benchmark_code;

-- 常用指数基础数据
INSERT INTO stk_stock_info (stock_code, stock_name, market, stock_type, status) VALUES
    ('000300', '沪深300', 'SH', 'index', 1),
    ('000905', '中证500', 'SH', 'index', 1),
    ('000016', '上证50', 'SH', 'index', 1),
    ('399006', '创业板指', 'SZ', 'index', 1),
    ('399997', '中证白酒', 'SZ', 'index', 1),
    ('000001', '上证指数', 'SH', 'index', 1),
    ('399001', '深证成指', 'SZ', 'index', 1)
ON DUPLICATE KEY UPDATE stock_type = 'index';
