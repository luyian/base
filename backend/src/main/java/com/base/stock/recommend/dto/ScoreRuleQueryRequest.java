package com.base.stock.recommend.dto;

import com.base.common.dto.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打分规则查询请求参数
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "打分规则查询请求参数")
public class ScoreRuleQueryRequest extends BasePageRequest {

}
