package com.base.stock.dto;

import com.base.common.dto.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 股票查询请求参数
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "股票查询请求参数")
public class StockQueryRequest extends BasePageRequest {

    /**
     * 市场（HK/SH/SZ）
     */
    @ApiModelProperty(value = "市场（HK/SH/SZ）")
    private String market;

    /**
     * 所属行业
     */
    @ApiModelProperty(value = "所属行业")
    private String industry;

    /**
     * 关键词（匹配代码或名称）
     */
    @ApiModelProperty(value = "关键词（匹配代码或名称）")
    private String keyword;
}
