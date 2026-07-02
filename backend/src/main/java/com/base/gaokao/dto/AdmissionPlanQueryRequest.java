package com.base.gaokao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业招生计划分页查询请求
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "专业招生计划分页查询请求")
public class AdmissionPlanQueryRequest extends GaokaoBaseQueryRequest {

    /**
     * 专业代码
     */
    @ApiModelProperty("专业代码")
    private String majorCode;

    /**
     * 专业名称
     */
    @ApiModelProperty("专业名称")
    private String majorName;

    /**
     * 专业备注
     */
    @ApiModelProperty("专业备注")
    private String majorRemark;

    /**
     * 学制起始值
     */
    @ApiModelProperty("学制起始值")
    private Integer studyYearsStart;

    /**
     * 学制结束值
     */
    @ApiModelProperty("学制结束值")
    private Integer studyYearsEnd;

    /**
     * 学费
     */
    @ApiModelProperty("学费")
    private String tuitionFee;

    /**
     * 计划人数起始值
     */
    @ApiModelProperty("计划人数起始值")
    private Integer planCountStart;

    /**
     * 计划人数结束值
     */
    @ApiModelProperty("计划人数结束值")
    private Integer planCountEnd;
}
