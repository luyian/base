package com.base.system.dto.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 枚举类型响应结果（按类型分组）
 */
@Data
public class EnumTypeResponse {

    /**
     * 枚举类型
     */
    private String enumType;

    /**
     * 枚举项数量
     */
    private Integer itemCount;

    /**
     * 创建时间（取该类型下最早的创建时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
