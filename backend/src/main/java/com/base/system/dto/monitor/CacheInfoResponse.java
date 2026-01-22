package com.base.system.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 缓存信息响应DTO
 *
 * @author base
 * @date 2026-01-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "缓存信息响应")
public class CacheInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Redis版本")
    private String version;

    @ApiModelProperty(value = "运行模式")
    private String mode;

    @ApiModelProperty(value = "端口")
    private String port;

    @ApiModelProperty(value = "客户端连接数")
    private Integer clients;

    @ApiModelProperty(value = "已用内存（MB）")
    private Double usedMemory;

    @ApiModelProperty(value = "最大内存（MB）")
    private Double maxMemory;

    @ApiModelProperty(value = "内存使用率（%）")
    private Double memoryUsedPercent;

    @ApiModelProperty(value = "键总数")
    private Long keyCount;

    @ApiModelProperty(value = "命中次数")
    private Long hits;

    @ApiModelProperty(value = "未命中次数")
    private Long misses;

    @ApiModelProperty(value = "命中率（%）")
    private Double hitRate;

    @ApiModelProperty(value = "运行时长（秒）")
    private Long uptime;

    @ApiModelProperty(value = "运行时长（格式化）")
    private String uptimeFormatted;
}
