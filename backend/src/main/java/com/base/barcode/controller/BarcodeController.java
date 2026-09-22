package com.base.barcode.controller;

import com.base.barcode.dto.BarcodeBatchRequest;
import com.base.barcode.dto.BarcodeGenerateRequest;
import com.base.barcode.dto.BarcodeGenerateResult;
import com.base.barcode.dto.BarcodeListResponse;
import com.base.barcode.dto.BarcodeRecordRequest;
import com.base.barcode.entity.Barcode;
import com.base.barcode.service.BarcodeService;
import com.base.common.result.Result;
import com.base.common.util.SecurityUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 通用条码/二维码控制器
 *
 * @author base
 */
@Api(tags = "通用条码/二维码")
@RestController
@RequestMapping("/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    /**
     * 生成条码/二维码（生成即落库：存 COS + 记 sys_file + 记 t_barcode，返回归档信息与绑定态）
     */
    @ApiOperation("生成条码/二维码")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<BarcodeGenerateResult> generate(@Valid @RequestBody BarcodeGenerateRequest request) {
        return Result.success(barcodeService.generate(request.getContent(), request.getType()));
    }

    /**
     * 批量生成条码/二维码（选择数量，内容由系统自动生成唯一编号，生成即入库）
     */
    @ApiOperation("批量生成条码/二维码")
    @PostMapping("/generate-batch")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<List<BarcodeGenerateResult>> generateBatch(@Validated @RequestBody BarcodeBatchRequest request) {
        return Result.success(barcodeService.generateBatch(request.getType(), request.getCount()));
    }

    /**
     * 分页查询当前用户的条码/二维码列表（可过滤类型与绑定态）
     */
    @ApiOperation("分页查询条码/二维码列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<Page<BarcodeListResponse>> list(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "bound", required = false) Boolean bound,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "12") long size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(barcodeService.listBarcodes(userId, type, bound, page, size));
    }

    /**
     * 登记通用条码记录
     */
    @ApiOperation("登记通用条码记录")
    @PostMapping("/code-record")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<Long> record(@Validated @RequestBody BarcodeRecordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(barcodeService.record(userId, request));
    }

    /**
     * 按编码查询通用条码记录
     */
    @ApiOperation("按编码查询通用条码记录")
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<Barcode> getByCode(@PathVariable String code) {
        Barcode barcode = barcodeService.getByCode(code);
        return Result.success(barcode);
    }

    /**
     * 删除条码记录
     */
    @ApiOperation("删除条码记录")
    @DeleteMapping("/code-record/{id}")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<Boolean> deleteRecord(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        barcodeService.deleteRecord(userId, id);
        return Result.success(true);
    }

    /**
     * 识别图片中的条码/二维码（返回原始内容）
     */
    @ApiOperation("识别图片条码/二维码")
    @PostMapping("/decode-image")
    @PreAuthorize("hasAuthority('common:barcode:use')")
    public Result<Map<String, Object>> decodeImage(@RequestParam("file") MultipartFile file) {
        return Result.success(barcodeService.decodeImage(file));
    }
}