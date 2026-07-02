package com.base.gaokao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业录取分数分页查询请求
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "专业录取分数分页查询请求")
public class MajorScoreQueryRequest extends CollegeScoreQueryRequest {

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
}
