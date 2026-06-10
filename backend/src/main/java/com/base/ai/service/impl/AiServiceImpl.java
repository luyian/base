package com.base.ai.service.impl;

import com.base.ai.config.AiChatModelHolder;
import com.base.ai.config.AiSkillConfig;
import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.AiService;
import com.base.ai.skill.StockDataTools;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 对话服务实现（基于 LangChain4j ChatLanguageModel）
 * 支持普通对话和带技能的对话（Function Calling）
 *
 * @author base
 * @since 2026-03-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiConfigProvider aiConfigProvider;
    private final AiChatModelHolder chatModelHolder;
    private final AiSkillConfig skillConfig;
    private final StockDataTools stockDataTools;

    @Override
    public ChatResponse chat(ChatRequest request) {
        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        ChatLanguageModel model = chatModelHolder.getModel(aiConfigProvider);
        if (model == null) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        int maxMsg = aiConfigProvider.getMaxMessageLength() != null ? aiConfigProvider.getMaxMessageLength() : 2000;
        int maxCtx = aiConfigProvider.getMaxContextLength() != null ? aiConfigProvider.getMaxContextLength() : 5000;
        if (request.getMessage() != null && request.getMessage().length() > maxMsg) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "问题长度不能超过 " + maxMsg + " 字符");
        }
        if (request.getContext() != null && request.getContext().length() > maxCtx) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "上下文长度不能超过 " + maxCtx + " 字符");
        }

        List<ChatMessage> messages = buildMessages(request);
        long start = System.currentTimeMillis();
        try {
            Response<AiMessage> response = model.generate(messages);
            long cost = System.currentTimeMillis() - start;
            String text = response != null && response.content() != null ? response.content().text() : null;
            String answer = text != null ? text : "";
            log.info("AI 对话成功，耗时 {} ms", cost);
            return new ChatResponse(answer);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 对话失败，耗时 {} ms，原因: {}", cost, e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "AI 服务暂时不可用: " + (e.getMessage() != null ? e.getMessage() : "请求失败"));
        }
    }

    @Override
    public ChatResponse chatWithSkills(ChatRequest request) {
        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        if (!Boolean.TRUE.equals(skillConfig.getEnabled())) {
            // 技能未启用，走普通对话
            return chat(request);
        }

        ChatLanguageModel model = chatModelHolder.getModel(aiConfigProvider);
        if (model == null) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }

        int maxMsg = aiConfigProvider.getMaxMessageLength() != null ? aiConfigProvider.getMaxMessageLength() : 2000;
        if (request.getMessage() != null && request.getMessage().length() > maxMsg) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "问题长度不能超过 " + maxMsg + " 字符");
        }

        long start = System.currentTimeMillis();
        try {
            // 构建带 Tools 的 AI 代理
            SkillAssistant assistant = AiServices.builder(SkillAssistant.class)
                    .chatLanguageModel(model)
                    .tools(stockDataTools)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();

            // 构造提问（含系统提示和上下文）
            String fullMessage = buildSkillMessage(request);
            String answer = assistant.chat(fullMessage);

            long cost = System.currentTimeMillis() - start;
            log.info("AI 技能对话成功，耗时 {} ms", cost);
            return new ChatResponse(answer != null ? answer : "");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 技能对话失败，耗时 {} ms，原因: {}", cost, e.getMessage());
            // 降级到普通对话
            log.info("技能对话失败，降级到普通对话模式");
            try {
                return chat(request);
            } catch (Exception fallbackEx) {
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                        "AI 服务暂时不可用: " + (e.getMessage() != null ? e.getMessage() : "请求失败"));
            }
        }
    }

    /**
     * LangChain4j AiServices 代理接口
     */
    interface SkillAssistant {
        @dev.langchain4j.service.SystemMessage("你是一个专业的 A 股数据分析助手。你可以查询实时行情、资金流向、龙虎榜、北向资金、行业排名等数据，并基于数据为用户提供分析。" +
                "回答时请使用中文，数据展示清晰有条理。当用户询问股票相关信息时，请主动调用对应的工具获取最新数据。" +
                "如果工具返回了 error 字段，请告知用户数据获取暂时失败并给出可能的原因。" +
                "重要提示：数据来源为第三方公开接口，仅供参考，不构成投资建议。数据可能存在延迟或误差，投资决策请以官方披露为准。")
        String chat(@dev.langchain4j.service.UserMessage String message);
    }

    private String buildSkillMessage(ChatRequest request) {
        StringBuilder sb = new StringBuilder();
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            sb.append("【当前上下文】\n").append(request.getContext()).append("\n\n");
        }
        sb.append(request.getMessage());
        return sb.toString();
    }

    private List<ChatMessage> buildMessages(ChatRequest request) {
        List<ChatMessage> messages = new ArrayList<>();
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            String systemContent = "以下为当前页面/系统提供的上下文，请结合上下文回答用户问题。\n\n" + request.getContext();
            messages.add(new SystemMessage(systemContent));
        }
        messages.add(new UserMessage(request.getMessage()));
        return messages;
    }
}
