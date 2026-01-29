package com.base.system.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门树节点
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class DeptTreeNode {

    private Long id;

    private Long parentId;

    private String deptName;

    private String deptCode;

    private Integer sort;

    private String leader;

    private String phone;

    private String email;

    private Integer status;

    private List<DeptTreeNode> children = new ArrayList<>();
}
