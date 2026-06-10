package com.base.system.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据响应结果
 *
 * @author base
 * @since 2026-06-09
 */
@Data
public class DictDataResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 字典标签（显示值）
     */
    private String dictLabel;

    /**
     * 字典键值（编码）
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
