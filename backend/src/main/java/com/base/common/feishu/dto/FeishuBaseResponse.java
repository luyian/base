package com.base.common.feishu.dto;

import lombok.Data;

/**
 * 飞书 API 通用响应基类
 *
 * @author base
 * @since 2026-02-11
 */
@Data
public class FeishuBaseResponse {

    /**
     * 错误码，0 表示成功
     */
    private Integer code;

    /**
     * 错误描述
     */
    private String msg;

    /**
     * 判断请求是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
}
