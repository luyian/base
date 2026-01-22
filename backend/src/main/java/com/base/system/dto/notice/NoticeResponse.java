package com.base.system.dto.notice;

import lombok.Data;

/**
 * 通知公告响应结果
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class NoticeResponse {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（1-通知 2-公告）
     */
    private Integer type;

    /**
     * 通知类型名称
     */
    private String typeName;

    /**
     * 通知级别（1-普通 2-重要 3-紧急）
     */
    private Integer level;

    /**
     * 通知级别名称
     */
    private String levelName;

    /**
     * 状态（0-草稿 1-已发布）
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
