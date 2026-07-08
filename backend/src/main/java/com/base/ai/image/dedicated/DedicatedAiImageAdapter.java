package com.base.ai.image.dedicated;

/**
 * AI 作图专用适配器。
 *
 * @author base
 */
public interface DedicatedAiImageAdapter {

    /**
     * 当前适配器是否支持上下文配置。
     *
     * @param context 作图上下文
     * @return 是否支持
     */
    boolean supports(DedicatedAiImageContext context);

    /**
     * 生成图片。
     *
     * @param context 作图上下文
     * @return 多图结果
     */
    DedicatedAiImageResult generate(DedicatedAiImageContext context);

    /**
     * 适配器名称。
     *
     * @return 适配器名称
     */
    String getName();
}
