package com.base.system.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 服务器信息响应DTO
 *
 * @author base
 * @date 2026-01-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "服务器信息响应")
public class ServerInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "CPU信息")
    private CpuInfo cpu;

    @ApiModelProperty(value = "内存信息")
    private MemoryInfo memory;

    @ApiModelProperty(value = "JVM信息")
    private JvmInfo jvm;

    @ApiModelProperty(value = "系统信息")
    private SystemInfo system;

    @ApiModelProperty(value = "磁盘信息列表")
    private List<DiskInfo> disks;

    /**
     * CPU信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "CPU信息")
    public static class CpuInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "核心数")
        private Integer coreCount;

        @ApiModelProperty(value = "CPU使用率（%）")
        private Double usedPercent;

        @ApiModelProperty(value = "CPU型号")
        private String model;
    }

    /**
     * 内存信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "内存信息")
    public static class MemoryInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "总内存（GB）")
        private Double total;

        @ApiModelProperty(value = "已用内存（GB）")
        private Double used;

        @ApiModelProperty(value = "可用内存（GB）")
        private Double available;

        @ApiModelProperty(value = "使用率（%）")
        private Double usedPercent;
    }

    /**
     * JVM信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "JVM信息")
    public static class JvmInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "JVM总内存（MB）")
        private Double total;

        @ApiModelProperty(value = "JVM已用内存（MB）")
        private Double used;

        @ApiModelProperty(value = "JVM可用内存（MB）")
        private Double available;

        @ApiModelProperty(value = "JVM使用率（%）")
        private Double usedPercent;

        @ApiModelProperty(value = "JVM版本")
        private String version;

        @ApiModelProperty(value = "JVM启动时间")
        private String startTime;

        @ApiModelProperty(value = "JVM运行时长")
        private String runTime;
    }

    /**
     * 系统信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "系统信息")
    public static class SystemInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "操作系统")
        private String os;

        @ApiModelProperty(value = "系统架构")
        private String arch;

        @ApiModelProperty(value = "主机名")
        private String hostName;

        @ApiModelProperty(value = "IP地址")
        private String ipAddress;
    }

    /**
     * 磁盘信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "磁盘信息")
    public static class DiskInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "磁盘路径")
        private String path;

        @ApiModelProperty(value = "磁盘名称")
        private String name;

        @ApiModelProperty(value = "总空间（GB）")
        private Double total;

        @ApiModelProperty(value = "已用空间（GB）")
        private Double used;

        @ApiModelProperty(value = "可用空间（GB）")
        private Double available;

        @ApiModelProperty(value = "使用率（%）")
        private Double usedPercent;
    }
}
