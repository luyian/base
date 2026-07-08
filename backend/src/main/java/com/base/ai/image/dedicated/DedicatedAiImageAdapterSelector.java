package com.base.ai.image.dedicated;

import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AI 作图专用适配器选择器。
 *
 * @author base
 */
@Component
@RequiredArgsConstructor
public class DedicatedAiImageAdapterSelector {

    private static final Set<String> SUPPORTED_ADAPTERS = new HashSet<>(Arrays.asList(
            "openai-images", "images", "chat-completions", "chat", "sensenova", "sense-nova"
    ));

    private final List<DedicatedAiImageAdapter> adapters;

    /**
     * 选择可用适配器。
     *
     * @param context 作图上下文
     * @return 适配器
     */
    public DedicatedAiImageAdapter select(DedicatedAiImageContext context) {
        validateAdapterConfig(context);
        for (DedicatedAiImageAdapter adapter : adapters) {
            if (adapter.supports(context)) {
                return adapter;
            }
        }
        throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "当前 AI 配置不支持图片生成");
    }

    private void validateAdapterConfig(DedicatedAiImageContext context) {
        if (context == null || !StringUtils.hasText(context.getAdapter())) {
            return;
        }
        String adapter = context.getAdapter().trim().toLowerCase();
        if (!SUPPORTED_ADAPTERS.contains(adapter)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "不支持的图片适配器：" + context.getAdapter());
        }
    }
}
