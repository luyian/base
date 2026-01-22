package com.base.system.controller;

import com.base.system.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 *
 * @author base
 * @since 2026-01-12
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "系统运行正常");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    @ApiOperation("Hello World")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, Base System!");
    }

}
