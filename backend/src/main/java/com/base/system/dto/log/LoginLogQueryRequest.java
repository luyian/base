package com.base.system.dto.log;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogQueryRequest extends BasePageRequest {

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
}
