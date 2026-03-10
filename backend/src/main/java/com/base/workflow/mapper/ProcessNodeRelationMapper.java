package com.base.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.workflow.entity.ProcessNodeRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程节点关系 Mapper
 */
@Mapper
public interface ProcessNodeRelationMapper extends BaseMapper<ProcessNodeRelation> {

    List<ProcessNodeRelation> selectByProcessId(Long processId);
}
