package com.base.stock.factory;

import lombok.Data;

import java.util.List;

/**
 * 映射配置
 *
 * @author base
 */
@Data
public class MappingConfig {

    /**
     * 字段映射列表
     */
    private List<FieldMapping> mappings;
}
