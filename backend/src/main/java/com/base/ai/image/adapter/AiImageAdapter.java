package com.base.ai.image.adapter;

import com.base.ai.image.AiImageContext;
import com.base.ai.image.AiImageResult;

/**
 * AI 图片生成适配器。
 *
 * @author base
 */
public interface AiImageAdapter {

    /**
     * 判断当前适配器是否支持该请求。
     *
     * @param context 图片生成上下文
     * @return true 表示支持
     */
    boolean supports(AiImageContext context);

    /**
     * 生成图片。
     *
     * @param context 图片生成上下文
     * @return 图片生成结果
     */
    AiImageResult generate(AiImageContext context);

    /**
     * 适配器名称。
     *
     * @return 适配器名称
     */
    String getName();
}
