package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.enums.EnumItemSaveRequest;
import com.base.system.dto.enums.EnumQueryRequest;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.dto.enums.EnumSaveRequest;
import com.base.system.dto.enums.EnumTypeResponse;
import com.base.system.entity.Enum;
import com.base.system.mapper.EnumMapper;
import com.base.system.service.EnumService;
import com.base.system.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 枚举服务实现类
 */
@Service
@RequiredArgsConstructor
public class EnumServiceImpl implements EnumService {

    private final EnumMapper enumMapper;
    private final RedisUtil redisUtil;

    private static final String ENUM_CACHE_PREFIX = "enum:type:";

    @Override
    public Page<EnumResponse> pageEnums(EnumQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getEnumType()), Enum::getEnumType, request.getEnumType())
                .like(StringUtils.hasText(request.getEnumCode()), Enum::getEnumCode, request.getEnumCode())
                .eq(request.getStatus() != null, Enum::getStatus, request.getStatus())
                .orderByAsc(Enum::getSort)
                .orderByDesc(Enum::getCreateTime);

        // 分页查询
        Page<Enum> page = new Page<>(request.getCurrent(), request.getSize());
        enumMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<EnumResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<EnumResponse> responseList = page.getRecords().stream().map(enumEntity -> {
            EnumResponse response = new EnumResponse();
            BeanUtils.copyProperties(enumEntity, response);
            return response;
        }).collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public EnumResponse getEnumById(Long id) {
   Enum enumEntity = enumMapper.selectById(id);
        if (enumEntity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "枚举不存在");
        }

        EnumResponse response = new EnumResponse();
        BeanUtils.copyProperties(enumEntity, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEnum(EnumSaveRequest request) {
        // 检查枚举类型和枚举值是否已存在
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enum::getEnumType, request.getEnumType())
                .eq(Enum::getEnumValue, request.getEnumValue());
        Long count = enumMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该枚举类型下的枚举值已存在");
        }

        // 保存枚举
        Enum enumEntity = new Enum();
        BeanUtils.copyProperties(request, enumEntity);
        enumMapper.insert(enumEntity);

        // 删除缓存
        redisUtil.delete(ENUM_CACHE_PREFIX + request.getEnumType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnum(EnumSaveRequest request) {
        // 检查枚举是否存在
        Enum enumEntity = enumMapper.selectById(request.getId());
        if (enumEntity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "枚举不存在");
        }

        // 检查枚举类型和枚举值是否已存在（排除自己）
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enum::getEnumType, request.getEnumType())
                .eq(Enum::getEnumValue, request.getEnumValue())
                .ne(Enum::getId, request.getId());
        Long count = enumMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该枚举类型下的枚举值已存在");
        }

        // 更新枚举
        BeanUtils.copyProperties(request, enumEntity);
        enumMapper.updateById(enumEntity);

        // 删除缓存
        redisUtil.delete(ENUM_CACHE_PREFIX + request.getEnumType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnum(Long id) {
        // 检查枚举是否存在
        Enum enumEntity = enumMapper.selectById(id);
        if (enumEntity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "枚举不存在");
        }

        // 删除枚举
        enumMapper.deleteById(id);

        // 删除缓存
        redisUtil.delete(ENUM_CACHE_PREFIX + enumEntity.getEnumType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteEnums(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择要删除的枚举");
        }

        // 查询所有枚举类型
        List<Enum> enumList = enumMapper.selectBatchIds(ids);
        List<String> enumTypes = enumList.stream()
                .map(Enum::getEnumType)
                .distinct()
                .collect(Collectors.toList());

        // 批量删除
        enumMapper.deleteBatchIds(ids);

        // 删除缓存
        enumTypes.forEach(type -> redisUtil.delete(ENUM_CACHE_PREFIX + type));
    }

    @Override
    public List<EnumResponse> listByType(String enumType) {
        // 先从缓存中获取
        String cacheKey = ENUM_CACHE_PREFIX + enumType;
        List<EnumResponse> cacheList = (List<EnumResponse>) redisUtil.get(cacheKey);
        if (cacheList != null) {
            return cacheList;
        }

        // 从数据库查询
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enum::getEnumType, enumType)
                .eq(Enum::getStatus, 1)
                .orderByAsc(Enum::getSort);
        List<Enum> enumList = enumMapper.selectList(wrapper);

        // 转换为响应对象
        List<EnumResponse> responseList = enumList.stream().map(enumEntity -> {
            EnumResponse response = new EnumResponse();
            BeanUtils.copyProperties(enumEntity, response);
            return response;
        }).collect(Collectors.toList());

        // 存入缓存（24小时）
        redisUtil.set(cacheKey, responseList, 86400);

        return responseList;
    }

    @Override
    public void refreshCache() {
        // 查询所有枚举类型
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Enum::getEnumType)
                .groupBy(Enum::getEnumType);
        List<Enum> enumList = enumMapper.selectList(wrapper);

        // 删除所有枚举缓存
        enumList.forEach(enumEntity -> {
            redisUtil.delete(ENUM_CACHE_PREFIX + enumEntity.getEnumType());
        });
    }

    @Override
    public List<EnumTypeResponse> listEnumTypes() {
        // 查询所有枚举，按类型分组统计
        List<Enum> allEnums = enumMapper.selectList(null);

        // 按类型分组
        return allEnums.stream()
                .collect(Collectors.groupingBy(Enum::getEnumType))
                .entrySet().stream()
                .map(entry -> {
                    EnumTypeResponse response = new EnumTypeResponse();
                    response.setEnumType(entry.getKey());
                    response.setItemCount(entry.getValue().size());
                    // 取该类型下最早的创建时间
                    response.setCreateTime(entry.getValue().stream()
                            .map(Enum::getCreateTime)
                            .filter(time -> time != null)
                            .min((a, b) -> a.compareTo(b))
                            .orElse(null));
                    return response;
                })
                .sorted((a, b) -> {
                    if (a.getCreateTime() == null) {
                        return 1;
                    }
                    if (b.getCreateTime() == null) {
                        return -1;
                    }
                    return b.getCreateTime().compareTo(a.getCreateTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveByType(String enumType, List<EnumItemSaveRequest> items) {
        // 查询该类型下现有的枚举项
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enum::getEnumType, enumType);
        List<Enum> existingEnums = enumMapper.selectList(wrapper);

        // 收集请求中的ID（用于判断哪些需要删除）
        Set<Long> requestIds = items.stream()
                .map(EnumItemSaveRequest::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 删除不在请求中的枚举项
        List<Long> toDeleteIds = existingEnums.stream()
                .map(Enum::getId)
                .filter(id -> !requestIds.contains(id))
                .collect(Collectors.toList());
        if (!toDeleteIds.isEmpty()) {
            enumMapper.deleteBatchIds(toDeleteIds);
        }

        // 检查枚举编码是否重复
        Set<String> enumCodes = new HashSet<>();
        for (EnumItemSaveRequest item : items) {
            if (!enumCodes.add(item.getEnumCode())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "枚举编码重复：" + item.getEnumCode());
            }
        }

        // 新增或更新枚举项
        for (EnumItemSaveRequest item : items) {
            Enum enumEntity = new Enum();
            enumEntity.setEnumType(enumType);
            enumEntity.setEnumCode(item.getEnumCode());
            enumEntity.setEnumValue(item.getEnumValue());
            enumEntity.setSort(item.getSort());
            enumEntity.setStatus(item.getStatus());

            if (item.getId() != null) {
                // 更新
                enumEntity.setId(item.getId());
                enumMapper.updateById(enumEntity);
            } else {
                // 新增
                enumMapper.insert(enumEntity);
            }
        }

        // 删除缓存
        redisUtil.delete(ENUM_CACHE_PREFIX + enumType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByType(String enumType) {
        // 删除该类型下所有枚举项
        LambdaQueryWrapper<Enum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enum::getEnumType, enumType);
        int count = enumMapper.delete(wrapper);

        if (count == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "枚举类型不存在");
        }

        // 删除缓存
        redisUtil.delete(ENUM_CACHE_PREFIX + enumType);
    }
}
