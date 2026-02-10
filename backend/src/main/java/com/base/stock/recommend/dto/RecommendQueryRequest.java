package com.base.stock.recommend.dto;

import com.base.common.dto.BasePageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 推荐股票查询请求参数
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "推荐股票查询请求参数")
public class RecommendQueryRequest extends BasePageRequest {

    /**
     * 推荐日期
     */
    @ApiModelProperty(value = "推荐日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recommendDate;
}
