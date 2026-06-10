package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.dict.DictTypeQueryRequest;
import com.base.system.dto.dict.DictTypeResponse;
import com.base.system.dto.dict.DictTypeSaveRequest;
import com.base.system.entity.DictData;
import com.base.system.entity.DictType;
import com.base.system.mapper.DictDataMapper;
import com.base.system.mapper.DictTypeMapper;
import com.base.system.service.DictTypeService;
import com.base.system.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现类
 *
 * @author base
 * @since 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;
    private final RedisUtil redisUtil;

    private static final String DICT_CACHE_PREFIX = "dict:type:";

    @Override
    public Page<DictTypeResponse> pageDictTypes(DictTypeQueryRequest request) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getDictType()), DictType::getDictType, request.getDictType())
                .like(StringUtils.hasText(request.getDictName()), DictType::getDictName, request.getDictName())
                .eq(request.getStatus() != null, DictType::getStatus, request.getStatus())
                .orderByDesc(DictType::getCreateTime);

        Page<DictType> page = request.buildPage();
        dictTypeMapper.selectPage(page, wrapper);

        Page<DictTypeResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<DictTypeResponse> responseList = page.getRecords().stream().map(entity -> {
            DictTypeResponse response = new DictTypeResponse();
            BeanUtils.copyProperties(entity, response);
            return response;
        }).collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public DictTypeResponse getDictTypeById(Long id) {
        DictType entity = dictTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }
        DictTypeResponse response = new DictTypeResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public List<DictTypeResponse> listAllDictTypes() {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getStatus, 1)
                .orderByDesc(DictType::getCreateTime);
        List<DictType> list = dictTypeMapper.selectList(wrapper);

        return list.stream().map(entity -> {
            DictTypeResponse response = new DictTypeResponse();
            BeanUtils.copyProperties(entity, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictType(DictTypeSaveRequest request) {
        // 校验字典类型编码唯一
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictType, request.getDictType());
        Long count = dictTypeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "字典类型编码已存在");
        }

        DictType entity = new DictType();
        BeanUtils.copyProperties(request, entity);
        dictTypeMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictTypeSaveRequest request) {
        DictType entity = dictTypeMapper.selectById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }

        // 如果修改了类型编码，需要同步更新字典数据表
        String oldDictType = entity.getDictType();
        String newDictType = request.getDictType();

        // 校验新编码唯一（排除自己）
        if (!oldDictType.equals(newDictType)) {
            LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictType::getDictType, newDictType)
                    .ne(DictType::getId, request.getId());
            Long count = dictTypeMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "字典类型编码已存在");
            }
        }

        BeanUtils.copyProperties(request, entity);
        dictTypeMapper.updateById(entity);

        // 同步更新字典数据的 dictType
        if (!oldDictType.equals(newDictType)) {
            DictData updateData = new DictData();
            updateData.setDictType(newDictType);
            LambdaQueryWrapper<DictData> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(DictData::getDictType, oldDictType);
            dictDataMapper.update(updateData, dataWrapper);

            // 清除旧类型缓存
            redisUtil.delete(DICT_CACHE_PREFIX + oldDictType);
        }

        // 清除缓存
        redisUtil.delete(DICT_CACHE_PREFIX + newDictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        DictType entity = dictTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典类型不存在");
        }

        // 检查是否有关联的字典数据
        LambdaQueryWrapper<DictData> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(DictData::getDictType, entity.getDictType());
        Long dataCount = dictDataMapper.selectCount(dataWrapper);
        if (dataCount > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该字典类型下存在数据项，请先删除数据项");
        }

        dictTypeMapper.deleteById(id);
        redisUtil.delete(DICT_CACHE_PREFIX + entity.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictTypes(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择要删除的字典类型");
        }

        List<DictType> typeList = dictTypeMapper.selectBatchIds(ids);
        for (DictType type : typeList) {
            LambdaQueryWrapper<DictData> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(DictData::getDictType, type.getDictType());
            Long dataCount = dictDataMapper.selectCount(dataWrapper);
            if (dataCount > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR,
                        "字典类型[" + type.getDictName() + "]下存在数据项，请先删除数据项");
            }
        }

        dictTypeMapper.deleteBatchIds(ids);
        typeList.forEach(type -> redisUtil.delete(DICT_CACHE_PREFIX + type.getDictType()));
    }

    @Override
    public void refreshCache() {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(DictType::getDictType);
        List<DictType> typeList = dictTypeMapper.selectList(wrapper);
        typeList.forEach(type -> redisUtil.delete(DICT_CACHE_PREFIX + type.getDictType()));
    }
}
