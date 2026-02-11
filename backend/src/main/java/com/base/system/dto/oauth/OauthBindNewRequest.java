package com.base.system.dto.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建新账号并绑定请求 DTO
 */
@Data
@ApiModel("创建新账号绑定请求")
public class OauthBindNewRequest {

    /**
     * 临时绑定凭证
     */
    @NotBlank(message = "绑定凭证不能为空")
    @ApiModelProperty("临时绑定凭证")
    private String oauthToken;
}
