package com.base.system.export.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.export.constant.DataSourceTypeEnum;
import com.base.common.export.constant.FieldTypeEnum;
import com.base.common.export.constant.MaskTypeEnum;
import com.base.common.util.SecurityUtils;
import com.base.system.export.dto.config.ExportConfigQueryRequest;
import com.base.system.export.dto.config.ExportConfigResponse;
import com.base.system.export.dto.config.ExportConfigSaveRequest;
import com.base.system.export.dto.field.ExportFieldResponse;
import com.base.system.export.dto.field.ExportFieldSaveRequest;
import com.base.system.export.entity.ExportConfig;
import com.base.system.export.entity.ExportField;
import com.base.system.mapper.ExportConfigMapper;
import com.base.system.mapper.ExportFieldMapper;
import com.base.system.export.service.ExportConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出配置服务实现
 *
 * @author base
 * @since 2026-02-04
 */
@Service
public class ExportConfigServiceImpl implements ExportConfigService {

    @Autowired
    private ExportConfigMapper exportConfigMapper;

    @Autowired
    private ExportFieldMapper exportFieldMapper;

    @Override
    public Page<ExportConfigResponse> pageConfigs(ExportConfigQueryRequest request) {
        Page<ExportConfig> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<ExportConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getConfigCode()), ExportConfig::getConfigCode, request.getConfigCode())
                .like(StringUtils.hasText(request.getConfigName()), ExportConfig::getConfigName, request.getConfigName())
                .eq(StringUtils.hasText(request.getDataSourceType()), ExportConfig::getDataSourceType, request.getDataSourceType())
                .eq(request.getStatus() != null, ExportConfig::getStatus, request.getStatus())
                .orderByAsc(ExportConfig::getSort)
                .orderByDesc(ExportConfig::getCreateTime);

        Page<ExportConfig> resultPage = exportConfigMapper.selectPage(page, wrapper);

        Page<ExportConfigResponse> responsePage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        responsePage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        return responsePage;
    }

    @Override
    public ExportConfigResponse getConfigById(Long id) {
        ExportConfig config = exportConfigMapper.selectById(id);
        if (config == null) {
            return null;
        }
        ExportConfigResponse response = convertToResponse(config);
        response.setFields(getFieldsByConfigId(id));
        return response;
    }

    @Override
    public ExportConfig getConfigByCode(String configCode) {
        LambdaQueryWrapper<ExportConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportConfig::getConfigCode, configCode)
                .eq(ExportConfig::getStatus, 1);
        return exportConfigMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addConfig(ExportConfigSaveRequest request) {
        // 检查编码是否重复
        LambdaQueryWrapper<ExportConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportConfig::getConfigCode, request.getConfigCode());
        if (exportConfigMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("配置编码已存在");
        }

        ExportConfig config = new ExportConfig();
        BeanUtils.copyProperties(request, config);

        exportConfigMapper.insert(config);

        // 保存字段配置
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            saveFields(config.getId(), request.getFields());
        }

        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ExportConfigSaveRequest request) {
        if (request.getId() == null) {
            throw new RuntimeException("配置ID不能为空");
        }

        ExportConfig existConfig = exportConfigMapper.selectById(request.getId());
        if (existConfig == null) {
            throw new RuntimeException("配置不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<ExportConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportConfig::getConfigCode, request.getConfigCode())
                .ne(ExportConfig::getId, request.getId());
        if (exportConfigMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("配置编码已存在");
        }

        ExportConfig config = new ExportConfig();
        BeanUtils.copyProperties(request, config);

        exportConfigMapper.updateById(config);

        // 更新字段配置
        if (request.getFields() != null) {
            saveFields(config.getId(), request.getFields());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        // 删除字段配置
        LambdaQueryWrapper<ExportField> fieldWrapper = new LambdaQueryWrapper<>();
        fieldWrapper.eq(ExportField::getConfigId, id);
        exportFieldMapper.delete(fieldWrapper);

        // 删除配置
        exportConfigMapper.deleteById(id);
    }

    @Override
    public List<ExportFieldResponse> getFieldsByConfigId(Long configId) {
        LambdaQueryWrapper<ExportField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportField::getConfigId, configId)
                .orderByAsc(ExportField::getSort);

        List<ExportField> fields = exportFieldMapper.selectList(wrapper);
        return fields.stream()
                .map(this::convertFieldToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFields(Long configId, List<ExportFieldSaveRequest> fields) {
        // 删除原有字段配置
        LambdaQueryWrapper<ExportField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportField::getConfigId, configId);
        exportFieldMapper.delete(wrapper);

        // 批量新增字段配置
        if (fields != null && !fields.isEmpty()) {
            List<ExportField> fieldList = new ArrayList<>();
            int sort = 0;
            for (ExportFieldSaveRequest fieldRequest : fields) {
                ExportField field = new ExportField();
                BeanUtils.copyProperties(fieldRequest, field);
                field.setId(null);
                field.setConfigId(configId);
                field.setSort(sort++);
                fieldList.add(field);
            }
            exportFieldMapper.batchInsert(fieldList);
        }
    }

    @Override
    public List<ExportConfigResponse> listEnabledConfigs() {
        LambdaQueryWrapper<ExportConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportConfig::getStatus, 1)
                .orderByAsc(ExportConfig::getSort);

        List<ExportConfig> configs = exportConfigMapper.selectList(wrapper);
        return configs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应对象
     */
    private ExportConfigResponse convertToResponse(ExportConfig config) {
        ExportConfigResponse response = new ExportConfigResponse();
        BeanUtils.copyProperties(config, response);

        // 设置数据源类型描述
        DataSourceTypeEnum dataSourceType = DataSourceTypeEnum.getByCode(config.getDataSourceType());
        if (dataSourceType != null) {
            response.setDataSourceTypeDesc(dataSourceType.getDesc());
        }

        // 设置状态描述
        response.setStatusDesc(config.getStatus() != null && config.getStatus() == 1 ? "启用" : "禁用");

        return response;
    }

    /**
     * 转换字段为响应对象
     */
    private ExportFieldResponse convertFieldToResponse(ExportField field) {
        ExportFieldResponse response = new ExportFieldResponse();
        BeanUtils.copyProperties(field, response);

        // 设置字段类型描述
        FieldTypeEnum fieldType = FieldTypeEnum.getByCode(field.getFieldType());
        if (fieldType != null) {
            response.setFieldTypeDesc(fieldType.getDesc());
        }

        // 设置脱敏类型描述
        MaskTypeEnum maskType = MaskTypeEnum.getByCode(field.getMaskType());
        if (maskType != null) {
            response.setMaskTypeDesc(maskType.getDesc());
        }

        return response;
    }
}
