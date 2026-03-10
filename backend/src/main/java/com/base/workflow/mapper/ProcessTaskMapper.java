package com.base.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.workflow.entity.ProcessTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程任务 Mapper
 */
@Mapper
public interface ProcessTaskMapper extends BaseMapper<ProcessTask> {

    List<ProcessTask> selectPendingTasks(String assignee);

    List<ProcessTask> selectByInstanceId(Long instanceId);

    List<ProcessTask> selectByInstanceIdAndNodeKey(Long instanceId, String nodeKey);
}
