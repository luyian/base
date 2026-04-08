package com.base.open.controller;

import com.base.common.result.Result;
import com.base.open.config.OpenApiConfig;
import com.base.open.dto.OpenApiTokenRequest;
import com.base.open.dto.OpenApiTokenResponse;
import com.base.open.service.OpenApiTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 开放接口认证控制器
 *
 * @author base
 */
@Slf4j
@Api(tags = "开放接口-认证")
@RestController
@RequestMapping("/open/auth")
public class OpenApiAuthController {

    @Autowired
    private OpenApiTokenService openApiTokenService;

    @Autowired
    private OpenApiConfig openApiConfig;

    /**
     * 获取访问令牌
     */
    @ApiOperation("获取访问令牌")
    @PostMapping("/token")
    public Result<OpenApiTokenResponse> getToken(@Valid @RequestBody OpenApiTokenRequest request) {
        if (!Boolean.TRUE.equals(openApiConfig.getEnabled())) {
            return Result.error("开放接口未启用");
        }

        String accessToken = openApiTokenService.createToken(request.getAppId(), request.getAppSecret());

        OpenApiTokenResponse response = OpenApiTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(openApiConfig.getTokenExpire())
                .tokenType("Bearer")
                .build();

        return Result.success(response);
    }
}
