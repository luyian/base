package com.base.workflow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程节点关系实体
 */
@Data
@TableName("sys_process_node_relation")
public class ProcessNodeRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long processId;

    private String sourceNodeKey;

    private String targetNodeKey;

    private String conditionExpr;

    private String relationName;

    private Integer positionX;

    private Integer positionY;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
