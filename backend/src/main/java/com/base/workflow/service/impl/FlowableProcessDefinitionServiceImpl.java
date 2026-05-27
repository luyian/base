package com.base.workflow.service.impl;

import com.base.workflow.dto.ProcessDefinitionResponse;
import com.base.workflow.dto.ProcessDefinitionSaveRequest;
import com.base.workflow.entity.FlowableDefinitionExt;
import com.base.workflow.mapper.FlowableDefinitionExtMapper;
import com.base.workflow.service.ProcessDefinitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于 Flowable 的流程定义服务实现
 */
@Service
public class FlowableProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private FlowableDefinitionExtMapper extMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinitionResponse save(ProcessDefinitionSaveRequest request, String operator) {
        FlowableDefinitionExt exist = extMapper.selectOne(
                new LambdaQueryWrapper<FlowableDefinitionExt>()
                        .eq(FlowableDefinitionExt::getProcessKey, request.getProcessKey())
                        .eq(FlowableDefinitionExt::getDeleted, 0)
        );
        if (exist != null) {
            throw new RuntimeException("流程标识已存在");
        }

        FlowableDefinitionExt ext = new FlowableDefinitionExt();
        ext.setProcessKey(request.getProcessKey());
        ext.setProcessName(request.getProcessName());
        ext.setCategory(request.getCategory());
        ext.setDescription(request.getDescription());
        ext.setBpmnXml(request.getBpmnXml());
        ext.setVersion(1);
        ext.setStatus(0);
        ext.setCreateBy(operator);
        extMapper.insert(ext);

        return ProcessDefinitionResponse.fromEntity(ext);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinitionResponse update(Long id, ProcessDefinitionSaveRequest request, String operator) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            throw new RuntimeException("流程定义未找到");
        }
        if (ext.getStatus() == 1) {
            throw new RuntimeException("已发布的流程不能修改，请创建新版本");
        }

        ext.setProcessName(request.getProcessName());
        ext.setCategory(request.getCategory());
        ext.setDescription(request.getDescription());
        ext.setBpmnXml(request.getBpmnXml());
        ext.setUpdateBy(operator);
        extMapper.updateById(ext);

        return ProcessDefinitionResponse.fromEntity(ext);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            throw new RuntimeException("流程定义未找到");
        }

        if (ext.getDeploymentId() != null) {
            repositoryService.deleteDeployment(ext.getDeploymentId(), true);
        }
        extMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            throw new RuntimeException("流程定义未找到");
        }
        if (ext.getBpmnXml() == null || ext.getBpmnXml().isEmpty()) {
            throw new RuntimeException("BPMN XML 为空，无法发布");
        }

        String resourceName = ext.getProcessKey() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(ext.getProcessName())
                .addString(resourceName, ext.getBpmnXml())
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        ext.setDeploymentId(deployment.getId());
        ext.setProcessDefinitionId(processDefinition.getId());
        ext.setVersion(processDefinition.getVersion());
        ext.setStatus(1);
        extMapper.updateById(ext);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            throw new RuntimeException("流程定义未找到");
        }

        if (ext.getProcessDefinitionId() != null) {
            repositoryService.suspendProcessDefinitionById(ext.getProcessDefinitionId(), true, null);
        }

        ext.setStatus(2);
        extMapper.updateById(ext);
    }

    @Override
    public List<ProcessDefinitionResponse> list(String category, String keyword, Integer status) {
        LambdaQueryWrapper<FlowableDefinitionExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowableDefinitionExt::getDeleted, 0);

        if (category != null && !category.isEmpty()) {
            wrapper.eq(FlowableDefinitionExt::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(FlowableDefinitionExt::getProcessName, keyword)
                    .or().like(FlowableDefinitionExt::getProcessKey, keyword));
        }
        if (status != null) {
            wrapper.eq(FlowableDefinitionExt::getStatus, status);
        }

        wrapper.orderByDesc(FlowableDefinitionExt::getCreateTime);
        List<FlowableDefinitionExt> list = extMapper.selectList(wrapper);
        return list.stream()
                .map(ProcessDefinitionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessDefinitionResponse getById(Long id) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            return null;
        }
        return ProcessDefinitionResponse.fromEntity(ext);
    }

    @Override
    public String getBpmnXml(Long id) {
        FlowableDefinitionExt ext = extMapper.selectById(id);
        if (ext == null) {
            throw new RuntimeException("流程定义未找到");
        }
        return ext.getBpmnXml();
    }
}
