package com.base.system.dto.knowledge;

import lombok.Data;

/**
 * 标签响应
 */
@Data
public class TagResponse {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;
}
