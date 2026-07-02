package com.base.gaokao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 院校录取分数分页查询请求
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "院校录取分数分页查询请求")
public class CollegeScoreQueryRequest extends GaokaoBaseQueryRequest {

    /**
     * 录取类型
     */
    @ApiModelProperty("录取类型")
    private String admissionType;

    /**
     * 最低分数起始值
     */
    @ApiModelProperty("最低分数起始值")
    private Integer minScoreStart;

    /**
     * 最低分数结束值
     */
    @ApiModelProperty("最低分数结束值")
    private Integer minScoreEnd;

    /**
     * 最低位次起始值
     */
    @ApiModelProperty("最低位次起始值")
    private Integer minRankStart;

    /**
     * 最低位次结束值
     */
    @ApiModelProperty("最低位次结束值")
    private Integer minRankEnd;

    /**
     * 学校所在省份
     */
    @ApiModelProperty("学校所在省份")
    private String collegeProvinceName;

    /**
     * 学校性质
     */
    @ApiModelProperty("学校性质")
    private String collegeNature;

    /**
     * 是否985：0否，1是
     */
    @ApiModelProperty("是否985：0否，1是")
    private Integer is985;

    /**
     * 是否211：0否，1是
     */
    @ApiModelProperty("是否211：0否，1是")
    private Integer is211;
}
