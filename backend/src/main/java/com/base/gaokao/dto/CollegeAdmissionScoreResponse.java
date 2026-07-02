package com.base.gaokao.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 院校录取分数响应
 *
 * @author base
 */
@Data
public class CollegeAdmissionScoreResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 录取年份
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
     * 院校名称
     */
    private String collegeName;

    /**
     * 院校代码
     */
    private String collegeCode;

    /**
     * 科类
     */
    private String subjectCategory;

    /**
     * 批次
     */
    private String batchName;

    /**
     * 专业组代码
     */
    private String majorGroupCode;

    /**
     * 选科要求
     */
    private String selectionRequirement;

    /**
     * 录取类型
     */
    private String admissionType;

    /**
     * 录取人数
     */
    private Integer admissionCount;

    /**
     * 最低分数
     */
    private Integer minScore;

    /**
     * 最低位次
     */
    private Integer minRank;

    /**
     * 批次线差
     */
    private Integer batchLineDiff;

    /**
     * 学校所在省份
     */
    private String collegeProvinceName;

    /**
     * 学校性质
     */
    private String collegeNature;

    /**
     * 是否985
     */
    private Integer is985;

    /**
     * 是否211
     */
    private Integer is211;

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
