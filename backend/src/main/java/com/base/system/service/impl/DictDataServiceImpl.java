package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.dict.DictDataQueryRequest;
import com.base.system.dto.dict.DictDataResponse;
import com.base.system.dto.dict.DictDataSaveRequest;
import com.base.system.entity.DictData;
import com.base.system.mapper.DictDataMapper;
import com.base.system.service.DictDataService;
import com.base.system.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现类
 *
 * @author base
 * @since 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final DictDataMapper dictDataMapper;
    private final RedisUtil redisUtil;

    private static final String DICT_CACHE_PREFIX = "dict:type:";

    @Override
    public Page<DictDataResponse> pageDictData(DictDataQueryRequest request) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(request.getDictType()), DictData::getDictType, request.getDictType())
                .like(StringUtils.hasText(request.getDictLabel()), DictData::getDictLabel, request.getDictLabel())
                .eq(request.getStatus() != null, DictData::getStatus, request.getStatus())
                .orderByAsc(DictData::getSort)
                .orderByDesc(DictData::getCreateTime);

        Page<DictData> page = request.buildPage();
        dictDataMapper.selectPage(page, wrapper);

        Page<DictDataResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<DictDataResponse> responseList = page.getRecords().stream().map(entity -> {
            DictDataResponse response = new DictDataResponse();
            BeanUtils.copyProperties(entity, response);
            return response;
        }).collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public DictDataResponse getDictDataById(Long id) {
        DictData entity = dictDataMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        DictDataResponse response = new DictDataResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public List<DictDataResponse> listByDictType(String dictType) {
        // 先从缓存获取
        String cacheKey = DICT_CACHE_PREFIX + dictType;
        @SuppressWarnings("unchecked")
        List<DictDataResponse> cacheList = (List<DictDataResponse>) redisUtil.get(cacheKey);
        if (cacheList != null) {
            return cacheList;
        }

        // 从数据库查询
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getSort);
        List<DictData> dataList = dictDataMapper.selectList(wrapper);

        List<DictDataResponse> responseList = dataList.stream().map(entity -> {
            DictDataResponse response = new DictDataResponse();
            BeanUtils.copyProperties(entity, response);
            return response;
        }).collect(Collectors.toList());

        // 存入缓存（24小时）
        redisUtil.set(cacheKey, responseList, 86400);

        return responseList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictData(DictDataSaveRequest request) {
        // 校验同类型下 dictValue 唯一
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, request.getDictType())
                .eq(DictData::getDictValue, request.getDictValue());
        Long count = dictDataMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该字典类型下的键值已存在");
        }

        DictData entity = new DictData();
        BeanUtils.copyProperties(request, entity);
        dictDataMapper.insert(entity);

        // 清除缓存
        redisUtil.delete(DICT_CACHE_PREFIX + request.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(DictDataSaveRequest request) {
        DictData entity = dictDataMapper.selectById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }

        // 校验同类型下 dictValue 唯一（排除自己）
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, request.getDictType())
                .eq(DictData::getDictValue, request.getDictValue())
                .ne(DictData::getId, request.getId());
        Long count = dictDataMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该字典类型下的键值已存在");
        }

        String oldDictType = entity.getDictType();
        BeanUtils.copyProperties(request, entity);
        dictDataMapper.updateById(entity);

        // 清除缓存（如果修改了 dictType，需要清除新旧两个缓存）
        redisUtil.delete(DICT_CACHE_PREFIX + request.getDictType());
        if (!oldDictType.equals(request.getDictType())) {
            redisUtil.delete(DICT_CACHE_PREFIX + oldDictType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long id) {
        DictData entity = dictDataMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "字典数据不存在");
        }

        dictDataMapper.deleteById(id);
        redisUtil.delete(DICT_CACHE_PREFIX + entity.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictData(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择要删除的字典数据");
        }

        List<DictData> dataList = dictDataMapper.selectBatchIds(ids);
        List<String> dictTypes = dataList.stream()
                .map(DictData::getDictType)
                .distinct()
                .collect(Collectors.toList());

        dictDataMapper.deleteBatchIds(ids);
        dictTypes.forEach(type -> redisUtil.delete(DICT_CACHE_PREFIX + type));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByDictType(String dictType) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType);
        dictDataMapper.delete(wrapper);
        redisUtil.delete(DICT_CACHE_PREFIX + dictType);
    }
}
