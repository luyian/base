package com.base.gaokao.dto;

import com.base.common.dto.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高考数据分页查询请求基类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GaokaoBaseQueryRequest extends BasePageRequest {

    /**
     * 年份
     */
    @ApiModelProperty("年份")
    private Integer admissionYear;

    /**
     * 考生省份编码
     */
    @ApiModelProperty("考生省份编码")
    private String candidateProvinceCode;

    /**
     * 考生省份名称
     */
    @ApiModelProperty("考生省份名称")
    private String candidateProvinceName;

    /**
     * 批次
     */
    @ApiModelProperty("批次")
    private String batchName;

    /**
     * 科类
     */
    @ApiModelProperty("科类")
    private String subjectCategory;

    /**
     * 院校代码
     */
    @ApiModelProperty("院校代码")
    private String collegeCode;

    /**
     * 院校名称
     */
    @ApiModelProperty("院校名称")
    private String collegeName;

    /**
     * 专业组代码
     */
    @ApiModelProperty("专业组代码")
    private String majorGroupCode;

    /**
     * 选科要求
     */
    @ApiModelProperty("选科要求")
    private String selectionRequirement;

    /**
     * 关键词，匹配院校或专业名称/代码
     */
    @ApiModelProperty("关键词，匹配院校或专业名称/代码")
    private String keyword;

    /**
     * 排序字段，支持 camelCase 或 snake_case
     */
    @ApiModelProperty("排序字段，支持 camelCase 或 snake_case")
    private String sortField;

    /**
     * 排序方向：asc 或 desc
     */
    @ApiModelProperty(value = "排序方向：asc 或 desc", example = "desc")
    private String sortOrder;
}
