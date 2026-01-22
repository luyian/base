package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告实体类
 *
 * @author base
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class Notice extends BaseEntity {

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
     * 通知级别（1-普通 2-重要 3-紧急）
     */
    private Integer level;

    /**
     * 状态（0-草稿 1-已发布）
     */
    private Integer status;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 备注
     */
    private String remark;
}
