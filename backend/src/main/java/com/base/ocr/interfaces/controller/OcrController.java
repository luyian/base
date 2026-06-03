package com.base.ocr.interfaces.controller;

import com.base.ocr.application.OcrApplicationService;
import com.base.ocr.domain.model.BankCardResult;
import com.base.ocr.domain.model.IdCardResult;
import com.base.ocr.domain.model.InvoiceResult;
import com.base.system.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OCR 识别控制器
 *
 * @author base
 */
@Slf4j
@RestController
@RequestMapping("/ocr")
@Api(tags = "OCR识别")
@RequiredArgsConstructor
public class OcrController {

    private final OcrApplicationService ocrApplicationService;

    /**
     * 身份证识别
     */
    @PostMapping("/id-card")
    @ApiOperation("身份证识别")
    public Result<IdCardResult> recognizeIdCard(
            @RequestParam("file") MultipartFile file,
            @ApiParam("识别面：front-正面 back-背面") @RequestParam(defaultValue = "front") String side,
            @ApiParam("指定供应商（可选）") @RequestParam(required = false) String provider) {
        byte[] imageData = getImageData(file);
        if (imageData == null) {
            return Result.error("图片文件为空或读取失败");
        }

        IdCardResult result = ocrApplicationService.recognizeIdCard(imageData, side, provider);
        if (result == null) {
            return Result.error("身份证识别失败，请检查图片质量或稍后重试");
        }
        return Result.success(result);
    }

    /**
     * 发票识别
     */
    @PostMapping("/invoice")
    @ApiOperation("发票识别")
    public Result<InvoiceResult> recognizeInvoice(
            @RequestParam("file") MultipartFile file,
            @ApiParam("指定供应商（可选）") @RequestParam(required = false) String provider) {
        byte[] imageData = getImageData(file);
        if (imageData == null) {
            return Result.error("图片文件为空或读取失败");
        }

        InvoiceResult result = ocrApplicationService.recognizeInvoice(imageData, provider);
        if (result == null) {
            return Result.error("发票识别失败，请检查图片质量或稍后重试");
        }
        return Result.success(result);
    }

    /**
     * 银行卡识别
     */
    @PostMapping("/bank-card")
    @ApiOperation("银行卡识别")
    public Result<BankCardResult> recognizeBankCard(
            @RequestParam("file") MultipartFile file,
            @ApiParam("指定供应商（可选）") @RequestParam(required = false) String provider) {
        byte[] imageData = getImageData(file);
        if (imageData == null) {
            return Result.error("图片文件为空或读取失败");
        }

        BankCardResult result = ocrApplicationService.recognizeBankCard(imageData, provider);
        if (result == null) {
            return Result.error("银行卡识别失败，请检查图片质量或稍后重试");
        }
        return Result.success(result);
    }

    /**
     * 查询可用供应商列表
     */
    @GetMapping("/providers")
    @ApiOperation("查询可用OCR供应商列表")
    public Result<List<OcrApplicationService.ProviderInfo>> getProviders() {
        return Result.success(ocrApplicationService.getProviders());
    }

    /**
     * 读取上传文件的字节数据
     */
    private byte[] getImageData(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            return file.getBytes();
        } catch (Exception e) {
            log.error("读取上传文件失败: {}", e.getMessage());
            return null;
        }
    }
}
