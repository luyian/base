package com.base.system.controller;

import com.base.common.exception.BusinessException;
import com.base.common.result.Result;
import com.base.system.service.ScanDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 扫描文档整理控制器
 * <p>
 * 提供「多张文档扫描图片 → 压缩合并成 PDF」的工作区式流程接口。
 * </p>
 *
 * @author base
 * @since 2026-08-25
 */
@Api(tags = "扫描文档整理")
@Slf4j
@RestController
@RequestMapping("/system/scan-doc")
public class ScanDocController {

    @Resource
    private ScanDocService scanDocService;

    /**
     * 开始整理：创建文档工作区
     */
    @ApiOperation("开始整理（创建扫描文档工作区）")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> create(@RequestParam(value = "docName", required = false) String docName) {
        try {
            return Result.success(scanDocService.create(docName));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 追加图片
     */
    @ApiOperation("追加图片到扫描文档")
    @PostMapping("/{docId}/images")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> appendImages(@PathVariable String docId,
                                                    @RequestParam("files") MultipartFile[] files) {
        try {
            return Result.success(scanDocService.appendImages(docId, files));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 调整图片顺序
     */
    @ApiOperation("调整扫描文档图片顺序")
    @PutMapping("/{docId}/order")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> reorderImages(@PathVariable String docId,
                                                     @RequestBody List<String> ids) {
        try {
            return Result.success(scanDocService.reorderImages(docId, ids));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除单张图片
     */
    @ApiOperation("删除扫描文档单张图片")
    @DeleteMapping("/{docId}/images/{imageId}")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> deleteImage(@PathVariable String docId,
                                                   @PathVariable String imageId) {
        try {
            return Result.success(scanDocService.deleteImage(docId, imageId));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 放弃整个工作区
     */
    @ApiOperation("放弃并清理扫描文档工作区")
    @DeleteMapping("/{docId}")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Void> deleteWorkArea(@PathVariable String docId) {
        try {
            scanDocService.deleteWorkArea(docId);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成归档：压缩合并成 PDF
     */
    @ApiOperation("完成归档（压缩合并生成 PDF）")
    @PostMapping("/{docId}/finalize")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> finalize(@PathVariable String docId,
                                                @RequestParam(value = "maxSide", defaultValue = "2000") int maxSide,
                                                @RequestParam(value = "quality", defaultValue = "70") int quality) {
        try {
            return Result.success(scanDocService.finalize(docId, maxSide, quality));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
}