package com.base.ai.dto.image;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * AI 作图专用提示词润色请求。
 *
 * @author base
 */
@Data
public class DedicatedAiImagePolishRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原始图片描述。
     */
    @NotBlank(message = "图片描述不能为空")
    @Size(max = 4000, message = "图片描述长度不能超过 4000 字符")
    private String prompt;

    /**
     * 当前风格要求。
     */
    @Size(max = 100, message = "风格长度不能超过 100 字符")
    private String style;

    /**
     * 当前图片尺寸。
     */
    @Pattern(regexp = "^\\d{3,4}x\\d{3,4}$", message = "图片尺寸格式必须为 宽x高，例如 1024x1024")
    private String size;
}
