package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("验证码响应")
public class CaptchaResponse {

    /**
     * 验证码唯一标识
     */
    @ApiModelProperty("验证码唯一标识")
    private String captchaKey;

    /**
     * 验证码图片（Base64 格式）
     */
    @ApiModelProperty("验证码图片（Base64 格式）")
    private String captchaImage;

    /**
     * 过期时间（秒）
     */
    @ApiModelProperty("过期时间（秒）")
    private Long expiresIn;
}
