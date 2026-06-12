package com.base.dev.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.system.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 代码分支管理实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_branch")
public class DevBranch extends BaseEntity {

    /**
     * 编号
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * PRD链接
     */
    private String prdLink;

    /**
     * 生产分支
     */
    private String prodBranch;

    /**
     * 开发分支
     */
    private String devBranch;

    /**
     * 上线时间
     */
    private LocalDate onlineTime;

    /**
     * 紧急程度（0-普通 1-紧急 2-特急）
     */
    private Integer priority;

    /**
     * 状态（0-进行中 1-已完成）
     */
    private Integer status;
}
