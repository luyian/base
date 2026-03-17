package com.base.ai.service.impl;

import com.base.ai.config.AiChatModelHolder;
import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.AiService;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 对话服务实现（基于 LangChain4j ChatLanguageModel，模仿 langchain4j-atguiguV5）
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
            Response<dev.langchain4j.data.message.AiMessage> response = model.generate(messages);
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
