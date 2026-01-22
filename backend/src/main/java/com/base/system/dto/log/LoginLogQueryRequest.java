package com.base.system.dto.log;

import lombok.Data;

/**
 * 登录日志查询请求参数
 */
@Data
public class LoginLogQueryRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示数量
     */
    private Long size = 10L;
}
