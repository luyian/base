package com.base.ai.skill;

/**
 * AI 技能脚本枚举 — 只有此处列出的脚本才可被执行，防止任意代码执行
 *
 * @author base
 * @since 2026-06-10
 */
public enum SkillScriptEnum {

    /** 实时行情（腾讯财经） */
    STOCK_QUOTE("stock_quote.py", "查询股票实时行情"),

    /** 资金流向（东财 push2） */
    STOCK_FUND_FLOW("stock_fund_flow.py", "查询个股资金流向"),

    /** 个股新闻（东财） */
    STOCK_NEWS("stock_news.py", "查询个股新闻"),

    /** 行业排名（东财） */
    INDUSTRY_RANK("industry_rank.py", "查询行业涨跌排名"),

    /** 北向资金（同花顺） */
    NORTHBOUND_FLOW("northbound_flow.py", "查询北向资金流向"),

    /** 龙虎榜（东财） */
    DRAGON_TIGER("dragon_tiger.py", "查询龙虎榜数据"),

    /** 强势股（同花顺） */
    HOT_STOCKS("hot_stocks.py", "查询当日强势股"),

    /** 板块归属（东财） */
    CONCEPT_BLOCKS("concept_blocks.py", "查询板块归属"),

    /** 融资融券（东财） */
    MARGIN_TRADING("margin_trading.py", "查询融资融券"),

    /** 综合估值（腾讯 + 同花顺） */
    STOCK_VALUATION("stock_valuation.py", "个股综合估值");

    private final String fileName;
    private final String description;

    SkillScriptEnum(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }
}
