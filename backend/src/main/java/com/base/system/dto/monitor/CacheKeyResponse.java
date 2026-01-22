package com.base.system.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 缓存键响应DTO
 *
 * @author base
 * @date 2026-01-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "缓存键响应")
public class CacheKeyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "缓存键")
    private String key;

    @ApiModelProperty(value = "缓存值")
    private String value;

    @ApiModelProperty(value = "过期时间（秒）")
    private Long ttl;
}
