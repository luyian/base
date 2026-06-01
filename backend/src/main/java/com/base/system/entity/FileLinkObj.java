package com.base.system.entity;

import com.base.system.enums.FileAreaTypeEnum;
import com.base.system.enums.FileLinkTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件业务关联实体（通用）
 *
 * <p>记录文件（sys_file）与各业务对象的关联关系，通过 areaType + linkType 区分用途，
 * linkId 指向具体业务对象主键，可被多个业务模块复用。</p>
 */
@Data
@TableName("file_link_obj")
public class FileLinkObj implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID（sys_file.id）
     */
    private Long fileId;

    /**
     * 业务领域类型
     */
    private FileAreaTypeEnum areaType;

    /**
     * 关联类型
     */
    private FileLinkTypeEnum linkType;

    /**
     * 关联业务对象ID
     */
    private Long linkId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标志（0：未删除，1：已删除）
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
