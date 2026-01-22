package com.base.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 路由菜单VO
 */
@Data
public class RouterVO {

    /**
     * 路由ID
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由元信息
     */
    private RouterMeta meta;

    /**
     * 子路由
     */
    private List<RouterVO> children;

    @Data
    public static class RouterMeta {
        /**
         * 标题
         */
        private String title;

        /**
         * 图标
         */
        private String icon;

        /**
         * 是否隐藏
         */
        private Boolean hidden;

        /**
         * 权限标识
         */
        private String permission;
    }
}
