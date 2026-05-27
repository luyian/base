package com.base.workflow.service;

import com.base.workflow.dto.ProcessDefinitionResponse;
import com.base.workflow.dto.ProcessDefinitionSaveRequest;

import java.util.List;

/**
 * 流程定义服务接口
 */
public interface ProcessDefinitionService {

    /**
     * 保存流程定义（草稿）
     */
    ProcessDefinitionResponse save(ProcessDefinitionSaveRequest request, String operator);

    /**
     * 更新流程定义
     */
    ProcessDefinitionResponse update(Long id, ProcessDefinitionSaveRequest request, String operator);

    /**
     * 删除流程定义
     */
    void delete(Long id);

    /**
     * 发布流程定义（部署到 Flowable 引擎）
     */
    void publish(Long id);

    /**
     * 禁用流程定义（挂起 Flowable 流程定义）
     */
    void disable(Long id);

    /**
     * 获取流程定义列表
     */
    List<ProcessDefinitionResponse> list(String category, String keyword, Integer status);

    /**
     * 获取流程定义详情（含 BPMN XML）
     */
    ProcessDefinitionResponse getById(Long id);

    /**
     * 获取 BPMN XML 内容
     */
    String getBpmnXml(Long id);
}
