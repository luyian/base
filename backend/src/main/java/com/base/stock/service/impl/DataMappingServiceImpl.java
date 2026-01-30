package com.base.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.entity.DataMapping;
import com.base.stock.mapper.DataMappingMapper;
import com.base.stock.service.DataMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据映射配置服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataMappingServiceImpl implements DataMappingService {

    private final DataMappingMapper dataMappingMapper;

    @Override
    public List<DataMapping> listMappings() {
        LambdaQueryWrapper<DataMapping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataMapping::getDeleted, 0)
                .orderByDesc(DataMapping::getCreateTime);
        return dataMappingMapper.selectList(wrapper);
    }

    @Override
    public DataMapping getByCode(String mappingCode) {
        LambdaQueryWrapper<DataMapping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataMapping::getMappingCode, mappingCode)
                .eq(DataMapping::getDeleted, 0);
        return dataMappingMapper.selectOne(wrapper);
    }

    @Override
    public DataMapping getById(Long id) {
        return dataMappingMapper.selectById(id);
    }

    @Override
    public Long saveMapping(DataMapping dataMapping) {
        // 检查编码是否已存在
        DataMapping existing = getByCode(dataMapping.getMappingCode());
        if (existing != null) {
            throw new RuntimeException("映射编码已存在: " + dataMapping.getMappingCode());
        }
        if (dataMapping.getStatus() == null) {
            dataMapping.setStatus(1);
        }
        dataMappingMapper.insert(dataMapping);
        log.info("映射配置保存成功，id: {}, code: {}", dataMapping.getId(), dataMapping.getMappingCode());
        return dataMapping.getId();
    }

    @Override
    public void updateMapping(DataMapping dataMapping) {
        // 检查编码是否被其他记录使用
        DataMapping existing = getByCode(dataMapping.getMappingCode());
        if (existing != null && !existing.getId().equals(dataMapping.getId())) {
            throw new RuntimeException("映射编码已存在: " + dataMapping.getMappingCode());
        }
        dataMappingMapper.updateById(dataMapping);
        log.info("映射配置更新成功，id: {}", dataMapping.getId());
    }

    @Override
    public void deleteMapping(Long id) {
        dataMappingMapper.deleteById(id);
        log.info("映射配置删除成功，id: {}", id);
    }
}
