package com.base.barcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成条码结果 DTO（生成即落库：写 COS + 记 sys_file + 记 t_barcode，未绑定态）
 *
 * @author base
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "BarcodeGenerateResult", description = "生成条码结果")
public class BarcodeGenerateResult {

    /**
     * 条码内容（编号，如 BR202609210001 / BAR202609210001）
     */
    @ApiModelProperty("条码内容")
    private String code;

    /**
     * 条码记录ID（t_barcode.id）
     */
    @ApiModelProperty("条码记录ID")
    private Long id;

    /**
     * 归档文件ID（sys_file.id）
     */
    @ApiModelProperty("归档文件ID")
    private Long fileId;

    /**
     * 文件存储路径（COS Key）
     */
    @ApiModelProperty("文件存储路径")
    private String filePath;

    /**
     * 文件访问 URL（COS 预签名地址）
     */
    @ApiModelProperty("文件访问URL")
    private String fileUrl;

    /**
     * 是否已绑定（t_barcode.biz_id 非空）
     */
    @ApiModelProperty("是否已绑定")
    private Boolean bound;

    /**
     * 本次是否命中已有记录（复用，而非新建）
     */
    @ApiModelProperty("是否复用已有记录")
    private Boolean exists;
}