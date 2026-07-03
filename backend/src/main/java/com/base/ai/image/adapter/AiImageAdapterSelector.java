package com.base.ai.image.adapter;

import com.base.ai.image.AiImageContext;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * AI 图片生成适配器选择器。
 *
 * @author base
 */
@Component
@RequiredArgsConstructor
public class AiImageAdapterSelector {

    private static final String OPENAI_IMAGES_ADAPTER = "openai-images";

    private static final String IMAGES_ADAPTER = "images";

    private static final String CHAT_COMPLETIONS_ADAPTER = "chat-completions";

    private static final String CHAT_ADAPTER = "chat";

    private static final String SENSENOVA_ADAPTER = "sensenova";

    private static final String SENSE_NOVA_ADAPTER = "sense-nova";

    private static final String IMAGES_GENERATIONS_PATH = "/images/generations";

    private final List<AiImageAdapter> adapters;

    /**
     * 根据请求上下文选择适配器。
     *
     * @param context 图片生成上下文
     * @return 图片生成适配器
     */
    public AiImageAdapter select(AiImageContext context) {
        validateAdapterConfig(context);
        for (AiImageAdapter adapter : adapters) {
            if (adapter.supports(context)) {
                return adapter;
            }
        }
        throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "当前 AI 配置不支持图片生成");
    }

    private void validateAdapterConfig(AiImageContext context) {
        if (!StringUtils.hasText(context.getAdapter())) {
            return;
        }
        String adapter = context.getAdapter().trim().toLowerCase();
        if (!isKnownAdapter(adapter)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的图片适配器：" + context.getAdapter());
        }
        String baseUrl = context.getBaseUrl() == null ? "" : context.getBaseUrl().trim().toLowerCase();
        if (isChatAdapter(adapter) && baseUrl.contains(IMAGES_GENERATIONS_PATH)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "图片 API 地址是 /images/generations，但图片适配器选择了 Chat Completions，"
                            + "请改为 OpenAI 图片接口或商汤日日新");
        }
        if (isSenseNovaAdapter(adapter) && !baseUrl.contains("sensenova") && StringUtils.hasText(baseUrl)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "图片适配器选择了商汤日日新，但 API 地址不是 token.sensenova.cn，"
                            + "请填写 https://token.sensenova.cn/v1");
        }
    }

    private boolean isKnownAdapter(String adapter) {
        return isOpenAiImagesAdapter(adapter) || isChatAdapter(adapter) || isSenseNovaAdapter(adapter);
    }

    private boolean isOpenAiImagesAdapter(String adapter) {
        return OPENAI_IMAGES_ADAPTER.equals(adapter) || IMAGES_ADAPTER.equals(adapter);
    }

    private boolean isChatAdapter(String adapter) {
        return CHAT_COMPLETIONS_ADAPTER.equals(adapter) || CHAT_ADAPTER.equals(adapter);
    }

    private boolean isSenseNovaAdapter(String adapter) {
        return SENSENOVA_ADAPTER.equals(adapter) || SENSE_NOVA_ADAPTER.equals(adapter);
    }
}
