package com.base.dev.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 分支管理保存请求参数
 */
@Data
public class BranchSaveRequest {

    /**
     * ID（编辑时必填）
     */
    private Long id;

    /**
     * 编号
     */
    @NotBlank(message = "编号不能为空")
    @Size(max = 64, message = "编号长度不能超过64个字符")
    private String code;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    /**
     * PRD链接
     */
    @Size(max = 512, message = "PRD链接长度不能超过512个字符")
    private String prdLink;

    /**
     * 上线时间
     */
    @NotNull(message = "上线时间不能为空")
    private LocalDate onlineTime;

    /**
     * 紧急程度（0-普通 1-紧急 2-特急）
     */
    private Integer priority;

    /**
     * 开发分支（可选，为空时自动生成）
     */
    @Size(max = 255, message = "开发分支长度不能超过255个字符")
    private String devBranch;
}
