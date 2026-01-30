package com.base.stock.controller;

import com.base.common.result.Result;
import com.base.stock.entity.DataMapping;
import com.base.stock.service.DataMappingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据映射配置控制器
 *
 * @author base
 */
@Api(tags = "数据映射配置")
@RestController
@RequestMapping("/stock/mapping")
@RequiredArgsConstructor
public class DataMappingController {

    private final DataMappingService dataMappingService;

    /**
     * 查询映射配置列表
     */
    @ApiOperation("查询映射配置列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:mapping:list')")
    public Result<List<DataMapping>> list() {
        List<DataMapping> list = dataMappingService.listMappings();
        return Result.success(list);
    }

    /**
     * 根据编码获取映射配置
     */
    @ApiOperation("根据编码获取映射配置")
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('stock:mapping:query')")
    public Result<DataMapping> getByCode(@PathVariable String code) {
        DataMapping mapping = dataMappingService.getByCode(code);
        return Result.success(mapping);
    }

    /**
     * 根据 ID 获取映射配置
     */
    @ApiOperation("根据ID获取映射配置")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:mapping:query')")
    public Result<DataMapping> getById(@PathVariable Long id) {
        DataMapping mapping = dataMappingService.getById(id);
        return Result.success(mapping);
    }

    /**
     * 保存映射配置
     */
    @ApiOperation("保存映射配置")
    @PostMapping
    @PreAuthorize("hasAuthority('stock:mapping:add')")
    public Result<Long> save(@RequestBody DataMapping dataMapping) {
        Long id = dataMappingService.saveMapping(dataMapping);
        return Result.success(id);
    }

    /**
     * 更新映射配置
     */
    @ApiOperation("更新映射配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:mapping:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody DataMapping dataMapping) {
        dataMapping.setId(id);
        dataMappingService.updateMapping(dataMapping);
        return Result.success();
    }

    /**
     * 删除映射配置
     */
    @ApiOperation("删除映射配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:mapping:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        dataMappingService.deleteMapping(id);
        return Result.success();
    }
}
