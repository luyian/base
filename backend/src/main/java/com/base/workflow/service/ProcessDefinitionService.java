package com.base.workflow.service;

import com.base.workflow.dto.ProcessDefinitionSaveRequest;
import com.base.workflow.entity.ProcessDefinition;

import java.util.List;

/**
 * 流程定义服务接口
 */
public interface ProcessDefinitionService {

    /**
     * 创建流程定义
     */
    ProcessDefinition save(ProcessDefinitionSaveRequest request, String operator);

    /**
     * 更新流程定义
     */
    ProcessDefinition update(Long id, ProcessDefinitionSaveRequest request, String operator);

    /**
     * 删除流程定义
     */
    void delete(Long id);

    /**
     * 发布流程定义
     */
    void publish(Long id);

    /**
     * 禁用流程定义
     */
    void disable(Long id);

    /**
     * 获取流程定义列表
     */
    List<ProcessDefinition> list(String category, String keyword, Integer status);

    /**
     * 获取流程定义详情
     */
    ProcessDefinition getById(Long id);

    /**
     * 根据流程Key获取最新发布的流程定义
     */
    ProcessDefinition getLatestPublished(String processKey);
}
