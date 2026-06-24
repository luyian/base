package com.base.ai.skill;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A 股数据技能 — 通过 @Tool 注解暴露给 LangChain4j AiServices，
 * 大模型根据用户问题自动选择调用。每个方法内部验证参数后通过 HTTP 调用 python-tools 服务。
 * <p>
 * 安全边界：
 * - LLM 只能选择已定义的 Tool 方法，不能生成任意请求
 * - 所有参数经过正则白名单校验（仅允许数字和逗号）
 * - HTTP 调用仅限内部 python-tools 服务
 *
 * @author base
 * @since 2026-06-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataTools {

    private final PythonToolsClient pythonToolsClient;

    /** 股票代码参数格式：6位数字，多个用逗号分隔 */
    private static final Pattern CODE_PATTERN = Pattern.compile("^\\d{6}(,\\d{6})*$");

    @Tool("查询股票实时行情，包括当前价格、涨跌幅、PE(TTM)、PB、总市值、流通市值、换手率、涨停价、跌停价等。支持同时查询多只股票。")
    public String getStockQuote(
            @P("股票代码，多只用逗号分隔，如 600519,000858,300750") String codes) {
        log.info("AI 技能调用: getStockQuote({})", codes);
        String sanitized = validateCodes(codes);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/quote?codes=" + sanitized);
    }

    @Tool("A股资金流向综合分析（优先使用此工具）：当用户问【A股资金流向】、【今天资金面怎么样】、【市场资金分析】等宽泛问题时，调用此工具。"
            + "它一次性返回近5日大盘主力资金流向、行业板块资金净流入/净流出前10、个股主力资金净流入TOP20。"
            + "仅当用户明确指定单个维度（如【大盘主力流入多少】或【半导体行业资金】）时，才用对应的单独工具。\n"
            + "拿到数据后请按以下思路分析并输出：\n"
            + "1. 大盘资金流向：近几日主力净流入/流出趋势，判断市场整体资金态度（流入=积极进攻，流出=谨慎防守）\n"
            + "2. 行业板块资金流向：主力净流入前10行业（资金主攻方向）和净流出前10行业（资金撤退方向），归纳资金主线逻辑\n"
            + "3. 个股主力资金TOP20：主力重点加仓标的，结合行业分析判断资金集中方向\n"
            + "4. 综合判断：总结当前市场风格（进攻/防御/分化）、资金主线方向、风险提示")
    public String getAstockFundFlowSummary() {
        log.info("AI 技能调用: getAstockFundFlowSummary()");
        return pythonToolsClient.call("/api/stock/astock-fund-flow-summary");
    }

    @Tool("查询沪深两市大盘资金流向（日K级别），包括近N日主力净流入、超大单、大单、中单、小单净流入金额及占比。仅当用户明确只问大盘主力资金趋势时使用，宽泛的【资金流向】问题请用getAstockFundFlowSummary。")
    public String getMarketFundFlow(
            @P("查询天数，默认5天，最大30天") String days) {
        log.info("AI 技能调用: getMarketFundFlow({})", days);
        int d = 5;
        if (days != null && !days.trim().isEmpty()) {
            try {
                d = Integer.parseInt(days.trim());
                if (d < 1) {
                    d = 1;
                }
                if (d > 30) {
                    d = 30;
                }
            } catch (NumberFormatException e) {
                d = 5;
            }
        }
        return pythonToolsClient.call("/api/stock/market-fund-flow?days=" + d);
    }

    @Tool("查询个股当日资金流向（分钟级），包括主力净流入、超大单、大单、中单、小单净流入金额，判断主力资金动向。")
    public String getStockFundFlow(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getStockFundFlow({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/fund-flow?code=" + sanitized);
    }

    @Tool("查询个股相关新闻资讯，了解最新动态和市场情绪。")
    public String getStockNews(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getStockNews({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/news?code=" + sanitized);
    }

    @Tool("查询全市场行业板块涨跌幅排名，了解当日行业轮动和资金偏好方向。")
    public String getIndustryRank() {
        log.info("AI 技能调用: getIndustryRank()");
        return pythonToolsClient.call("/api/stock/industry-rank");
    }

    @Tool("查询行业板块主力资金流向排名，返回净流入前10和净流出前10。仅当用户明确问某个行业或行业间资金对比时使用，宽泛的【资金流向】问题请用getAstockFundFlowSummary。")
    public String getIndustryFundFlow() {
        log.info("AI 技能调用: getIndustryFundFlow()");
        return pythonToolsClient.call("/api/stock/industry-fund-flow");
    }

    @Tool("查询全市场个股主力资金净流入排名TOP20。仅当用户明确问【哪些个股主力加仓】时使用，宽泛的【资金流向】问题请用getAstockFundFlowSummary。")
    public String getStockFundFlowRank() {
        log.info("AI 技能调用: getStockFundFlowRank()");
        return pythonToolsClient.call("/api/stock/stock-fund-flow-rank");
    }

    @Tool("查询北向资金（沪股通/深股通）当日实时分钟流向，判断外资态度。")
    public String getNorthboundFlow() {
        log.info("AI 技能调用: getNorthboundFlow()");
        return pythonToolsClient.call("/api/stock/northbound-flow");
    }

    @Tool("查询龙虎榜数据，包括个股上榜记录、买卖营业部席位TOP5、机构净买入。用于跟踪游资和机构动向。")
    public String getDragonTiger(
            @P("6位股票代码，如 002475") String code) {
        log.info("AI 技能调用: getDragonTiger({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/dragon-tiger?code=" + sanitized);
    }

    @Tool("查询当日涨停/强势股及题材归因，了解市场热点主线和资金聚焦方向。")
    public String getHotStocks() {
        log.info("AI 技能调用: getHotStocks()");
        return pythonToolsClient.call("/api/stock/hot-stocks");
    }

    @Tool("查询个股所属概念板块/行业板块归属，了解股票的题材属性。")
    public String getConceptBlocks(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getConceptBlocks({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/concept-blocks?code=" + sanitized);
    }

    @Tool("查询个股融资融券数据，包括融资余额、融资买入额、融券余额等，判断杠杆资金动向。")
    public String getMarginTrading(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getMarginTrading({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/margin-trading?code=" + sanitized);
    }

    @Tool("个股综合估值分析，包括实时PE/PB/市值、机构一致预期EPS、前向PE、PEG、PE消化年限，用于判断估值高低。")
    public String getStockValuation(
            @P("6位股票代码，如 688017") String code) {
        log.info("AI 技能调用: getStockValuation({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonToolsClient.call("/api/stock/valuation?code=" + sanitized);
    }

    /**
     * 校验单个股票代码：必须为6位数字
     */
    private String validateSingleCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        String trimmed = code.trim().replaceAll("[^0-9]", "");
        if (trimmed.length() != 6) {
            return null;
        }
        return trimmed;
    }

    /**
     * 校验多个股票代码：每个必须为6位数字，逗号分隔
     */
    private String validateCodes(String codes) {
        if (codes == null || codes.trim().isEmpty()) {
            return null;
        }
        // 去除非数字非逗号字符
        String cleaned = codes.trim().replaceAll("[^0-9,]", "");
        if (!CODE_PATTERN.matcher(cleaned).matches()) {
            return null;
        }
        // 限制最多查询 10 只
        String[] parts = cleaned.split(",");
        if (parts.length > 10) {
            cleaned = String.join(",", Arrays.copyOf(parts, 10));
        }
        return cleaned;
    }
}
