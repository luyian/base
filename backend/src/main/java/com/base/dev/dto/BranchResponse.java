package com.base.dev.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分支管理响应结果
 */
@Data
public class BranchResponse {

    /**
     * ID
     */
    private Long id;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate onlineTime;

    /**
     * 紧急程度（0-普通 1-紧急 2-特急）
     */
    private Integer priority;

    /**
     * 紧急程度描述
     */
    private String priorityDesc;

    /**
     * 状态（0-进行中 1-已完成）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
