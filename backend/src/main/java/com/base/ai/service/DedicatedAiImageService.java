package com.base.ai.service;

import com.base.ai.dto.image.DedicatedAiImageGenerateRequest;
import com.base.ai.dto.image.DedicatedAiImageGenerateResponse;
import com.base.ai.dto.image.DedicatedAiImagePolishRequest;
import com.base.ai.dto.image.DedicatedAiImagePolishResponse;

/**
 * AI 作图专用服务。
 *
 * @author base
 */
public interface DedicatedAiImageService {

    /**
     * 生成图片。
     *
     * @param request 生成请求
     * @return 生成响应
     */
    DedicatedAiImageGenerateResponse generate(DedicatedAiImageGenerateRequest request);

    /**
     * 润色图片提示词。
     *
     * @param request 润色请求
     * @return 润色响应
     */
    DedicatedAiImagePolishResponse polish(DedicatedAiImagePolishRequest request);
}
