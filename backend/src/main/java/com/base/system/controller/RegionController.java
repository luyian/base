package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.RegionCascadeNode;
import com.base.system.dto.RegionQueryRequest;
import com.base.system.dto.RegionTreeNode;
import com.base.system.entity.Region;
import com.base.system.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 行政区划管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/region")
@Api(tags = "行政区划管理")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 获取行政区划树
     */
    @GetMapping("/tree")
    @ApiOperation("获取行政区划树")
    public Result<List<RegionTreeNode>> getRegionTree(RegionQueryRequest request) {
        List<RegionTreeNode> tree = regionService.getRegionTree(request);
        return Result.success(tree);
    }

    /**
     * 根据父级ID查询子区划
     */
    @GetMapping("/children/{parentId}")
    @ApiOperation("根据父级ID查询子区划")
    public Result<List<Region>> getChildrenByParentId(@PathVariable Long parentId) {
        List<Region> children = regionService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 根据层级查询区划
     */
    @GetMapping("/level/{level}")
    @ApiOperation("根据层级查询区划")
    public Result<List<Region>> getRegionsByLevel(@PathVariable Integer level) {
        List<Region> regions = regionService.getRegionsByLevel(level);
        return Result.success(regions);
    }

    /**
     * 获取级联选择器数据（懒加载）
     */
    @GetMapping("/cascade")
    @ApiOperation("获取级联选择器数据")
    public Result<List<RegionCascadeNode>> getCascadeNodes(@RequestParam(required = false) Long parentId) {
        List<RegionCascadeNode> nodes = regionService.getCascadeNodes(parentId);
        return Result.success(nodes);
    }

    /**
     * 根据区划代码获取完整路径
     */
    @GetMapping("/fullPath/{regionCode}")
    @ApiOperation("根据区划代码获取完整路径")
    public Result<String> getFullPath(@PathVariable String regionCode) {
        String fullPath = regionService.getFullPath(regionCode);
        return Result.success(fullPath);
    }

    /**
     * 根据区划代码查询
     */
    @GetMapping("/code/{regionCode}")
    @ApiOperation("根据区划代码查询")
    public Result<Region> getByRegionCode(@PathVariable String regionCode) {
        Region region = regionService.getByRegionCode(regionCode);
        return Result.success(region);
    }

    /**
     * 根据ID查询区划详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询区划详情")
    public Result<Region> getById(@PathVariable Long id) {
        Region region = regionService.getById(id);
        return Result.success(region);
    }

    /**
     * 搜索区划
     */
    @GetMapping("/search")
    @ApiOperation("搜索区划")
    public Result<List<Region>> searchRegions(@RequestParam String keyword) {
        List<Region> regions = regionService.searchRegions(keyword);
        return Result.success(regions);
    }

    /**
     * 创建区划
     */
    @PostMapping
    @ApiOperation("创建区划")
    @PreAuthorize("hasAuthority('system:region:add')")
    @OperationLog(module = "行政区划", operation = "新增")
    public Result<Void> createRegion(@Validated @RequestBody Region region) {
        regionService.createRegion(region);
        return Result.success();
    }

    /**
     * 更新区划
     */
    @PutMapping
    @ApiOperation("更新区划")
    @PreAuthorize("hasAuthority('system:region:edit')")
    @OperationLog(module = "行政区划", operation = "编辑")
    public Result<Void> updateRegion(@Validated @RequestBody Region region) {
        regionService.updateRegion(region);
        return Result.success();
    }

    /**
     * 删除区划
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除区划")
    @PreAuthorize("hasAuthority('system:region:delete')")
    @OperationLog(module = "行政区划", operation = "删除")
    public Result<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return Result.success();
    }

    /**
     * 批量导入区划数据
     */
    @PostMapping("/import")
    @ApiOperation("批量导入区划数据")
    @PreAuthorize("hasAuthority('system:region:import')")
    @OperationLog(module = "行政区划", operation = "导入", saveResult = false)
    public Result<Integer> importRegions(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件解析和导入逻辑
        return Result.success(0);
    }
}
