package com.base.system.dto.notice;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 通知公告保存请求参数
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class NoticeSaveRequest {

    /**
     * 通知ID（编辑时必填）
     */
    private Long id;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;

    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;

    /**
     * 通知类型（1-通知 2-公告）
     */
    @NotNull(message = "通知类型不能为空")
    private Integer type;

    /**
     * 通知级别（1-普通 2-重要 3-紧急）
     */
    @NotNull(message = "通知级别不能为空")
    private Integer level;

    /**
     * 状态（0-草稿 1-已发布）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
