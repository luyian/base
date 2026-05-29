-- 基金估值记录表新增持仓计算涨跌幅字段
ALTER TABLE stk_fund_valuation_record
    ADD COLUMN raw_weighted_change DECIMAL(10, 6) DEFAULT NULL COMMENT '持仓计算涨跌幅(%)' AFTER estimated_change_percent;
