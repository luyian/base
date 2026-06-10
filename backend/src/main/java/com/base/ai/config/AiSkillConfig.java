package com.base.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI 技能配置（Python 执行环境）
 *
 * @author base
 * @since 2026-06-10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.skill")
public class AiSkillConfig {

    /**
     * 是否启用 AI 技能（Function Calling）
     */
    private Boolean enabled = true;

    /**
     * Python 解释器路径（默认使用系统 PATH 中的 python）
     */
    private String pythonPath = "python";

    /**
     * 脚本执行超时时间（秒）
     */
    private Integer timeout = 30;

    /**
     * 单次对话最大工具调用次数（防止死循环）
     */
    private Integer maxToolCalls = 5;

    /**
     * AI 助手系统提示词
     */
    private String systemPrompt = "你是一个专业的 A 股数据分析助手。你可以查询实时行情、资金流向、龙虎榜、北向资金、行业排名等数据，并基于数据为用户提供分析。回答时请使用中文，数据展示清晰有条理。";
}
