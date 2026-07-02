package com.base.gaokao.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业招生计划响应
 *
 * @author base
 */
@Data
public class MajorAdmissionPlanResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 招生年份
     */
    private Integer admissionYear;

    /**
     * 考生省份编码
     */
    private String candidateProvinceCode;

    /**
     * 考生省份名称
     */
    private String candidateProvinceName;

    /**
     * 批次
     */
    private String batchName;

    /**
     * 科类
     */
    private String subjectCategory;

    /**
     * 院校代码
     */
    private String collegeCode;

    /**
     * 院校名称
     */
    private String collegeName;

    /**
     * 专业组代码
     */
    private String majorGroupCode;

    /**
     * 专业代码
     */
    private String majorCode;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 专业备注
     */
    private String majorRemark;

    /**
     * 选科要求
     */
    private String selectionRequirement;

    /**
     * 学制
     */
    private Integer studyYears;

    /**
     * 学费
     */
    private String tuitionFee;

    /**
     * 计划人数
     */
    private Integer planCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
