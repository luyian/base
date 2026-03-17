package com.base.ai.controller;

import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;
import com.base.ai.service.AiService;
import com.base.common.annotation.Log;
import com.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 助手接口（Dashboard 对话）
 *
 * @author base
 * @since 2026-03-16
 */
@Tag(name = "AI 助手")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "对话")
    @PostMapping("/chat")
    @Log(title = "AI 助手", content = "Dashboard AI 对话", type = "OTHER")
    public Result<ChatResponse> chat(@Validated @RequestBody ChatRequest request) {
        ChatResponse response = aiService.chat(request);
        return Result.success(response);
    }
}
