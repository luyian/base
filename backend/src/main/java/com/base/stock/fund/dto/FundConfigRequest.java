package com.base.stock.fund.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 基金配置请求 DTO
 *
 * @author base
 */
@Data
public class FundConfigRequest {

    /**
     * 基金名称
     */
    @NotBlank(message = "基金名称不能为空")
    @Size(max = 100, message = "基金名称长度不能超过100个字符")
    private String fundName;

    /**
     * 基金代码（可选）
     */
    @Size(max = 50, message = "基金代码长度不能超过50个字符")
    private String fundCode;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = 1;

    /**
     * 持仓列表
     */
    @NotEmpty(message = "持仓列表不能为空")
    @Valid
    private List<HoldingItem> holdings;

    /**
     * 持仓项
     */
    @Data
    public static class HoldingItem {

        /**
         * 股票代码
         */
        @NotBlank(message = "股票代码不能为空")
        private String stockCode;

        /**
         * 权重占比(%)
         */
        @NotNull(message = "权重不能为空")
        private BigDecimal weight;
    }
}
