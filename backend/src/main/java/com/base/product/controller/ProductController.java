package com.base.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.common.result.Result;
import com.base.product.dto.ProductBarcodeResponse;
import com.base.product.dto.ProductRequest;
import com.base.product.dto.ProductResponse;
import com.base.product.service.ProductService;
import com.base.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 *
 * @author base
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("/prod")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询我的商品
     */
    @ApiOperation("分页查询我的商品")
    @GetMapping("/list")
    public Result<IPage<ProductResponse>> list(
            @ApiParam("商品名称（模糊）") @RequestParam(required = false) String name,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long page,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") long size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.pageProducts(userId, name, page, size));
    }

    /**
     * 新增商品
     */
    @ApiOperation("新增商品")
    @PostMapping
    public Result<Long> create(@Validated @RequestBody ProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.createProduct(userId, request));
    }

    /**
     * 编辑商品
     */
    @ApiOperation("编辑商品")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id,
                                  @Validated @RequestBody ProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.updateProduct(userId, id, request);
        return Result.success(true);
    }

    /**
     * 删除商品
     */
    @ApiOperation("删除商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.deleteProduct(userId, id);
        return Result.success(true);
    }

    /**
     * 查询商品详情
     */
    @ApiOperation("查询商品详情")
    @GetMapping("/{id}")
    public Result<ProductResponse> get(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.getProduct(userId, id));
    }

    /**
     * 查询商品关联条码列表
     */
    @ApiOperation("查询商品关联条码列表")
    @GetMapping("/codes/{id}")
    public Result<List<ProductBarcodeResponse>> codes(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.getProduct(userId, id);
        return Result.success(productService.listBarcodes(id));
    }

    /**
     * 扫码命中：编码 → 商品
     */
    @ApiOperation("扫码命中商品")
    @GetMapping("/scan/{code}")
    public Result<ProductResponse> scan(@PathVariable String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.scanByCode(userId, code));
    }

    /**
     * 生成新的业务唯一编码（未落库）
     */
    @ApiOperation("生成业务唯一编码")
    @GetMapping("/generate-code")
    public Result<String> generateCode() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.generateProductCode(userId));
    }

    /**
     * 绑定条码到商品
     */
    @ApiOperation("绑定条码到商品")
    @PostMapping("/{id}/bind")
    public Result<Long> bind(@PathVariable Long id,
                             @ApiParam("条码内容") @RequestParam String code,
                             @ApiParam("类型(1条码 2二维码)") @RequestParam(required = false) Integer type,
                             @ApiParam("来源(1自生成 2图片识别录入)") @RequestParam(required = false) Integer source) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.bindCodeToProduct(userId, id, code, type, source));
    }

    /**
     * 解绑商品条码
     */
    @ApiOperation("解绑商品条码")
    @DeleteMapping("/{id}/bind/{codeId}")
    public Result<Boolean> unbind(@PathVariable Long id, @PathVariable Long codeId) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.unbindCode(userId, id, codeId);
        return Result.success(true);
    }
}