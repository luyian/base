package com.base.system.dto.notice;

import lombok.Data;

/**
 * 通知公告查询请求参数
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class NoticeQueryRequest {

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知类型（1-通知 2-公告）
     */
    private Integer type;

    /**
     * 通知级别（1-普通 2-重要 3-紧急）
     */
    private Integer level;

    /**
     * 状态（0-草稿 1-已发布）
     */
    private Integer status;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页显示数量
     */
    private Integer size = 10;
}
