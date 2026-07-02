package com.base.gaokao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高考专业录取分数线实体
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_major_admission_score")
public class MajorAdmissionScore extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 选科要求
     */
    private String selectionRequirement;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 专业代码
     */
    private String majorCode;

    /**
     * 专业组代码
     */
    private String majorGroupCode;

    /**
     * 专业备注
     */
    private String majorRemark;

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
    @TableField("is_985")
    private Integer is985;

    /**
     * 是否211
     */
    @TableField("is_211")
    private Integer is211;

    /**
     * 备注
     */
    private String remark;
}
