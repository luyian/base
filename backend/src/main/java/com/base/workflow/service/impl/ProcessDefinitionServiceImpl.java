package com.base.workflow.service.impl;

import cn.hutool.json.JSONUtil;
import com.base.workflow.dto.ProcessDefinitionSaveRequest;
import com.base.workflow.entity.ProcessDefinition;
import com.base.workflow.entity.ProcessNode;
import com.base.workflow.entity.ProcessNodeRelation;
import com.base.workflow.enums.DefinitionStatus;
import com.base.workflow.mapper.ProcessDefinitionMapper;
import com.base.workflow.mapper.ProcessNodeMapper;
import com.base.workflow.mapper.ProcessNodeRelationMapper;
import com.base.workflow.service.ProcessDefinitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程定义服务实现
 */
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Autowired
    private ProcessNodeRelationMapper processNodeRelationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinition save(ProcessDefinitionSaveRequest request, String operator) {
        // 检查流程标识是否已存在
        ProcessDefinition exist = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .eq(ProcessDefinition::getProcessKey, request.getProcessKey())
        );

        if (exist != null) {
            throw new RuntimeException("流程标识已存在");
        }

        ProcessDefinition definition = new ProcessDefinition();
        definition.setProcessKey(request.getProcessKey());
        definition.setProcessName(request.getProcessName());
        definition.setCategory(request.getCategory());
        definition.setDescription(request.getDescription());
        definition.setVersion(1);
        definition.setStatus(DefinitionStatus.DRAFT.getCode());
        definition.setCreateBy(operator);
        definition.setCreateTime(LocalDateTime.now());

        processDefinitionMapper.insert(definition);

        // 保存节点配置
        saveNodes(definition.getId(), request.getNodes());

        // 保存节点关系
        saveRelations(definition.getId(), request.getNodeRelations());

        return definition;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinition update(Long id, ProcessDefinitionSaveRequest request, String operator) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }

        if (DefinitionStatus.PUBLISHED.getCode().equals(definition.getStatus())) {
            throw new RuntimeException("已发布的流程不能修改");
        }

        definition.setProcessName(request.getProcessName());
        definition.setCategory(request.getCategory());
        definition.setDescription(request.getDescription());
        definition.setUpdateBy(operator);
        definition.setUpdateTime(LocalDateTime.now());

        processDefinitionMapper.updateById(definition);

        // 删除旧的节点配置
        processNodeMapper.delete(new LambdaQueryWrapper<ProcessNode>()
                .eq(ProcessNode::getProcessId, id));
        processNodeRelationMapper.delete(new LambdaQueryWrapper<ProcessNodeRelation>()
                .eq(ProcessNodeRelation::getProcessId, id));

        // 重新保存节点配置
        saveNodes(id, request.getNodes());
        saveRelations(id, request.getNodeRelations());

        return definition;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }

        if (DefinitionStatus.PUBLISHED.getCode().equals(definition.getStatus())) {
            throw new RuntimeException("已发布的流程不能删除");
        }

        processDefinitionMapper.deleteById(id);
        processNodeMapper.delete(new LambdaQueryWrapper<ProcessNode>()
                .eq(ProcessNode::getProcessId, id));
        processNodeRelationMapper.delete(new LambdaQueryWrapper<ProcessNodeRelation>()
                .eq(ProcessNodeRelation::getProcessId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }

        if (DefinitionStatus.PUBLISHED.getCode().equals(definition.getStatus())) {
            throw new RuntimeException("流程已发布");
        }

        definition.setStatus(DefinitionStatus.PUBLISHED.getCode());
        definition.setUpdateTime(LocalDateTime.now());
        processDefinitionMapper.updateById(definition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }

        definition.setStatus(DefinitionStatus.DISABLED.getCode());
        definition.setUpdateTime(LocalDateTime.now());
        processDefinitionMapper.updateById(definition);
    }

    @Override
    public List<ProcessDefinition> list(String category, String keyword, Integer status) {
        LambdaQueryWrapper<ProcessDefinition> wrapper = new LambdaQueryWrapper<>();

        if (category != null && !category.isEmpty()) {
            wrapper.eq(ProcessDefinition::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(ProcessDefinition::getProcessName, keyword)
                    .or().like(ProcessDefinition::getProcessKey, keyword));
        }
        if (status != null) {
            wrapper.eq(ProcessDefinition::getStatus, status);
        }

        wrapper.orderByDesc(ProcessDefinition::getCreateTime);
        return processDefinitionMapper.selectList(wrapper);
    }

    @Override
    public ProcessDefinition getById(Long id) {
        return processDefinitionMapper.selectById(id);
    }

    @Override
    public ProcessDefinition getLatestPublished(String processKey) {
        return processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .eq(ProcessDefinition::getProcessKey, processKey)
                        .eq(ProcessDefinition::getStatus, DefinitionStatus.PUBLISHED.getCode())
                        .orderByDesc(ProcessDefinition::getVersion)
                        .last("LIMIT 1")
        );
    }

    private void saveNodes(Long processId, List<ProcessDefinitionSaveRequest.NodeConfig> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        for (ProcessDefinitionSaveRequest.NodeConfig config : nodes) {
            ProcessNode node = new ProcessNode();
            node.setProcessId(processId);
            node.setNodeKey(config.getNodeKey());
            node.setNodeName(config.getNodeName());
            node.setNodeType(config.getNodeType());
            node.setEventHandler(config.getEventHandler());
            node.setCandidateType(config.getCandidateType());
            node.setCandidateConfig(config.getCandidateConfig());
            node.setApproveType(config.getApproveType());
            node.setApproveRatio(config.getApproveRatio());
            node.setCanRollback(config.getCanRollback());
            node.setRollbackNodes(config.getRollbackNodes());
            node.setPositionX(config.getPositionX());
            node.setPositionY(config.getPositionY());
            node.setCreateTime(LocalDateTime.now());
            node.setUpdateTime(LocalDateTime.now());
            processNodeMapper.insert(node);
        }
    }

    private void saveRelations(Long processId, List<ProcessDefinitionSaveRequest.NodeRelationConfig> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }

        for (ProcessDefinitionSaveRequest.NodeRelationConfig config : relations) {
            ProcessNodeRelation relation = new ProcessNodeRelation();
            relation.setProcessId(processId);
            relation.setSourceNodeKey(config.getSourceNodeKey());
            relation.setTargetNodeKey(config.getTargetNodeKey());
            relation.setConditionExpr(config.getConditionExpr());
            relation.setRelationName(config.getRelationName());
            relation.setCreateTime(LocalDateTime.now());
            processNodeRelationMapper.insert(relation);
        }
    }
}
