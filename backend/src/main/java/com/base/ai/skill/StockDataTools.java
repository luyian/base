package com.base.ai.skill;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A 股数据技能 — 通过 @Tool 注解暴露给 LangChain4j AiServices，
 * 大模型根据用户问题自动选择调用。每个方法内部验证参数后调用 PythonExecutor。
 * <p>
 * 安全边界：
 * - LLM 只能选择已定义的 Tool 方法，不能生成任意脚本
 * - 所有参数经过正则白名单校验（仅允许数字和逗号）
 * - PythonExecutor 只执行枚举范围内的脚本
 *
 * @author base
 * @since 2026-06-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataTools {

    private final PythonExecutor pythonExecutor;

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
        return pythonExecutor.execute(SkillScriptEnum.STOCK_QUOTE, Collections.singletonList(sanitized));
    }

    @Tool("查询个股当日资金流向（分钟级），包括主力净流入、超大单、大单、中单、小单净流入金额，判断主力资金动向。")
    public String getStockFundFlow(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getStockFundFlow({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.STOCK_FUND_FLOW, Collections.singletonList(sanitized));
    }

    @Tool("查询个股相关新闻资讯，了解最新动态和市场情绪。")
    public String getStockNews(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getStockNews({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.STOCK_NEWS, Collections.singletonList(sanitized));
    }

    @Tool("查询全市场行业板块涨跌幅排名，了解当日行业轮动和资金偏好方向。")
    public String getIndustryRank() {
        log.info("AI 技能调用: getIndustryRank()");
        return pythonExecutor.execute(SkillScriptEnum.INDUSTRY_RANK, Collections.emptyList());
    }

    @Tool("查询北向资金（沪股通/深股通）当日实时分钟流向，判断外资态度。")
    public String getNorthboundFlow() {
        log.info("AI 技能调用: getNorthboundFlow()");
        return pythonExecutor.execute(SkillScriptEnum.NORTHBOUND_FLOW, Collections.emptyList());
    }

    @Tool("查询龙虎榜数据，包括个股上榜记录、买卖营业部席位TOP5、机构净买入。用于跟踪游资和机构动向。")
    public String getDragonTiger(
            @P("6位股票代码，如 002475") String code) {
        log.info("AI 技能调用: getDragonTiger({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.DRAGON_TIGER, Collections.singletonList(sanitized));
    }

    @Tool("查询当日涨停/强势股及题材归因，了解市场热点主线和资金聚焦方向。")
    public String getHotStocks() {
        log.info("AI 技能调用: getHotStocks()");
        return pythonExecutor.execute(SkillScriptEnum.HOT_STOCKS, Collections.emptyList());
    }

    @Tool("查询个股所属概念板块/行业板块归属，了解股票的题材属性。")
    public String getConceptBlocks(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getConceptBlocks({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.CONCEPT_BLOCKS, Collections.singletonList(sanitized));
    }

    @Tool("查询个股融资融券数据，包括融资余额、融资买入额、融券余额等，判断杠杆资金动向。")
    public String getMarginTrading(
            @P("6位股票代码，如 600519") String code) {
        log.info("AI 技能调用: getMarginTrading({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.MARGIN_TRADING, Collections.singletonList(sanitized));
    }

    @Tool("个股综合估值分析，包括实时PE/PB/市值、机构一致预期EPS、前向PE、PEG、PE消化年限，用于判断估值高低。")
    public String getStockValuation(
            @P("6位股票代码，如 688017") String code) {
        log.info("AI 技能调用: getStockValuation({})", code);
        String sanitized = validateSingleCode(code);
        if (sanitized == null) {
            return "{\"error\": \"股票代码格式错误，请输入6位数字代码\"}";
        }
        return pythonExecutor.execute(SkillScriptEnum.STOCK_VALUATION, Collections.singletonList(sanitized));
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
