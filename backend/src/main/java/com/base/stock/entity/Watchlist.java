package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自选股票实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_watchlist")
@ApiModel(value = "Watchlist", description = "自选股票实体")
public class Watchlist extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 股票代码
     */
    @ApiModelProperty("股票代码")
    private String stockCode;

    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer sortOrder;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 股票名称（关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty("股票名称")
    private String stockName;

    /**
     * 市场（关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty("市场")
    private String market;
}
