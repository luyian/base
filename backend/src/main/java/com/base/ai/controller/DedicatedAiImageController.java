package com.base.ai.controller;

import com.base.ai.dto.image.DedicatedAiImageGenerateRequest;
import com.base.ai.dto.image.DedicatedAiImageGenerateResponse;
import com.base.ai.dto.image.DedicatedAiImagePolishRequest;
import com.base.ai.dto.image.DedicatedAiImagePolishResponse;
import com.base.ai.service.DedicatedAiImageService;
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
 * AI 作图专用接口。
 *
 * @author base
 */
@Tag(name = "AI 作图")
@RestController
@RequestMapping("/ai/image")
@RequiredArgsConstructor
public class DedicatedAiImageController {

    private final DedicatedAiImageService dedicatedAiImageService;

    /**
     * 生成多张图片。
     *
     * @param request 生成请求
     * @return 生成结果
     */
    @Operation(summary = "AI 作图专用多图生成")
    @PostMapping("/generate")
    @Log(title = "AI 作图", content = "AI 作图专用多图生成", type = "OTHER")
    public Result<DedicatedAiImageGenerateResponse> generate(
            @Validated @RequestBody DedicatedAiImageGenerateRequest request) {
        return Result.success(dedicatedAiImageService.generate(request));
    }

    /**
     * 润色图片提示词。
     *
     * @param request 润色请求
     * @return 润色结果
     */
    @Operation(summary = "AI 作图提示词润色")
    @PostMapping("/polish")
    @Log(title = "AI 作图", content = "AI 作图提示词润色", type = "OTHER")
    public Result<DedicatedAiImagePolishResponse> polish(
            @Validated @RequestBody DedicatedAiImagePolishRequest request) {
        return Result.success(dedicatedAiImageService.polish(request));
    }
}
