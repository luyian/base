package com.base.stock.dto;

import com.base.common.dto.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步失败记录查询请求参数
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "同步失败记录查询请求参数")
public class SyncFailureQueryRequest extends BasePageRequest {

    /**
     * 股票代码
     */
    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    /**
     * 状态：0-待重试，1-成功，2-放弃
     */
    @ApiModelProperty(value = "状态：0-待重试，1-成功，2-放弃")
    private Integer status;
}
