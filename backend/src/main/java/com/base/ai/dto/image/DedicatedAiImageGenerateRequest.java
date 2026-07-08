package com.base.ai.dto.image;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * AI 作图专用生成请求。
 *
 * @author base
 */
@Data
public class DedicatedAiImageGenerateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 正向提示词。
     */
    @NotBlank(message = "图片提示词不能为空")
    @Size(max = 4000, message = "图片提示词长度不能超过 4000 字符")
    private String prompt;

    /**
     * 负向提示词。
     */
    @Size(max = 2000, message = "负向提示词长度不能超过 2000 字符")
    private String negativePrompt;

    /**
     * 图片尺寸。
     */
    @Pattern(regexp = "^\\d{3,4}x\\d{3,4}$", message = "图片尺寸格式必须为 宽x高，例如 1024x1024")
    private String size;

    /**
     * 生成数量。
     */
    @Min(value = 1, message = "图片数量最少为 1 张")
    @Max(value = 8, message = "图片数量最多为 8 张")
    private Integer count;

    /**
     * 图片模型。
     */
    @Size(max = 100, message = "模型名称长度不能超过 100 字符")
    private String model;

    /**
     * 图片适配器。
     */
    @Size(max = 50, message = "适配器名称长度不能超过 50 字符")
    private String adapter;

    /**
     * 风格要求。
     */
    @Size(max = 100, message = "风格长度不能超过 100 字符")
    private String style;

    /**
     * 随机种子。
     */
    @Min(value = 0, message = "seed 最小值为 0")
    @Max(value = Integer.MAX_VALUE, message = "seed 超出允许范围")
    private Integer seed;
}
