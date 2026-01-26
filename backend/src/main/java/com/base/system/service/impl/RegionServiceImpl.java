package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.RegionCascadeNode;
import com.base.system.dto.RegionQueryRequest;
import com.base.system.dto.RegionTreeNode;
import com.base.system.entity.Region;
import com.base.system.mapper.RegionMapper;
import com.base.system.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 行政区划服务实现类
 */
@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionMapper regionMapper;

    @Override
    public List<RegionTreeNode> getRegionTree(RegionQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0);

        if (request != null) {
            if (request.getRegionName() != null && !request.getRegionName().isEmpty()) {
                wrapper.like(Region::getRegionName, request.getRegionName());
            }
            if (request.getRegionCode() != null && !request.getRegionCode().isEmpty()) {
                wrapper.eq(Region::getRegionCode, request.getRegionCode());
            }
            if (request.getLevel() != null) {
                wrapper.eq(Region::getLevel, request.getLevel());
            }
            if (request.getStatus() != null) {
                wrapper.eq(Region::getStatus, request.getStatus());
            }
            if (request.getPinyinPrefix() != null && !request.getPinyinPrefix().isEmpty()) {
                wrapper.like(Region::getPinyinPrefix, request.getPinyinPrefix());
            }
        }

        wrapper.orderByAsc(Region::getSort, Region::getCreateTime);

        List<Region> regions = regionMapper.selectList(wrapper);

        // 构建树形结构
        return buildRegionTree(regions, 0L);
    }

    @Override
    public List<Region> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0)
                .eq(Region::getParentId, parentId)
                .eq(Region::getStatus, 1)
                .orderByAsc(Region::getSort);
        return regionMapper.selectList(wrapper);
    }

    @Override
    public List<Region> getRegionsByLevel(Integer level) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0)
                .eq(Region::getLevel, level)
                .eq(Region::getStatus, 1)
                .orderByAsc(Region::getSort);
        return regionMapper.selectList(wrapper);
    }

    @Override
    public List<RegionCascadeNode> getCascadeNodes(Long parentId) {
        Long pid = parentId == null ? 0L : parentId;
        List<Region> regions = getChildrenByParentId(pid);

        return regions.stream().map(region -> {
            RegionCascadeNode node = new RegionCascadeNode();
            node.setValue(region.getRegionCode());
            node.setLabel(region.getRegionName());
            node.setLevel(region.getLevel());
            // 判断是否为叶子节点（街道层级为叶子节点）
            node.setLeaf(region.getLevel() == 4);
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public String getFullPath(String regionCode) {
        Region region = getByRegionCode(regionCode);
        if (region == null) {
            return "";
        }

        List<String> pathList = new ArrayList<>();
        buildFullPath(region, pathList);

        // 反转列表，使其从省到当前区划
        List<String> reversedPath = new ArrayList<>();
        for (int i = pathList.size() - 1; i >= 0; i--) {
            reversedPath.add(pathList.get(i));
        }

        return String.join("/", reversedPath);
    }

    @Override
    public Region getByRegionCode(String regionCode) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0)
                .eq(Region::getRegionCode, regionCode);
        return regionMapper.selectOne(wrapper);
    }

    @Override
    public Region getById(Long id) {
        Region region = regionMapper.selectById(id);
        if (region == null || region.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区划不存在");
        }
        return region;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRegion(Region region) {
        // 验证区划代码唯一性
        Region existing = getByRegionCode(region.getRegionCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "区划代码已存在");
        }

        // 设置默认值
        if (region.getParentId() == null) {
            region.setParentId(0L);
        }
        if (region.getSort() == null) {
            region.setSort(0);
        }
        if (region.getStatus() == null) {
            region.setStatus(1);
        }

        regionMapper.insert(region);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegion(Region region) {
        // 验证区划是否存在
        Region existing = getById(region.getId());

        // 如果修改了区划代码，验证唯一性
        if (!existing.getRegionCode().equals(region.getRegionCode())) {
            Region codeCheck = getByRegionCode(region.getRegionCode());
            if (codeCheck != null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "区划代码已存在");
            }
        }

        regionMapper.updateById(region);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRegion(Long id) {
        // 验证区划是否存在
        Region region = getById(id);

        // 验证是否有子区划
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0)
                .eq(Region::getParentId, id);
        Long count = regionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, "该区划下存在子区划，无法删除");
        }

        // 逻辑删除
        region.setDeleted(1);
        regionMapper.updateById(region);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(List<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return 0;
        }

        // 分批插入，每批1000条
        int batchSize = 1000;
        int total = 0;
        for (int i = 0; i < regions.size(); i += batchSize) {
            int end = Math.min(i + batchSize, regions.size());
            List<Region> batch = regions.subList(i, end);

            for (Region region : batch) {
                try {
                    // 设置默认值
                    if (region.getParentId() == null) {
                        region.setParentId(0L);
                    }
                    if (region.getSort() == null) {
                        region.setSort(0);
                    }
                    if (region.getStatus() == null) {
                        region.setStatus(1);
                    }

                    regionMapper.insert(region);
                    total++;
                } catch (Exception e) {
                    // 忽略重复数据等错误，继续导入
                    System.err.println("导入区划失败: " + region.getRegionCode() + " - " + e.getMessage());
                }
            }
        }

        return total;
    }

    @Override
    public List<Region> searchRegions(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getDeleted, 0)
                .eq(Region::getStatus, 1)
                .and(w -> w.like(Region::getRegionName, keyword)
                        .or().like(Region::getPinyin, keyword)
                        .or().like(Region::getPinyinPrefix, keyword))
                .orderByAsc(Region::getLevel, Region::getSort)
                .last("LIMIT 50"); // 限制返回数量

        return regionMapper.selectList(wrapper);
    }

    /**
     * 构建区划树
     *
     * @param regions  区划列表
     * @param parentId 父级ID
     * @return 树节点列表
     */
    private List<RegionTreeNode> buildRegionTree(List<Region> regions, Long parentId) {
        List<RegionTreeNode> treeNodes = new ArrayList<>();

        // 按父级ID分组
        Map<Long, List<Region>> groupMap = regions.stream()
                .collect(Collectors.groupingBy(Region::getParentId));

        // 获取当前层级的区划
        List<Region> currentLevel = groupMap.get(parentId);
        if (currentLevel == null || currentLevel.isEmpty()) {
            return treeNodes;
        }

        for (Region region : currentLevel) {
            RegionTreeNode node = new RegionTreeNode();
            BeanUtils.copyProperties(region, node);

            // 递归构建子节点
            List<RegionTreeNode> children = buildRegionTree(regions, region.getId());
            node.setChildren(children);
            node.setHasChildren(!children.isEmpty());

            treeNodes.add(node);
        }

        return treeNodes;
    }

    /**
     * 构建完整路径
     *
     * @param region   当前区划
     * @param pathList 路径列表
     */
    private void buildFullPath(Region region, List<String> pathList) {
        if (region == null) {
            return;
        }

        pathList.add(region.getRegionName());

        if (region.getParentId() != null && region.getParentId() > 0) {
            Region parent = regionMapper.selectById(region.getParentId());
            buildFullPath(parent, pathList);
        }
    }
}
