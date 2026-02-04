package com.base.common.export.engine;

import com.base.system.export.entity.ExportConfig;
import com.base.system.export.entity.ExportField;
import com.base.system.export.entity.ExportTask;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导出上下文
 *
 * @author base
 * @since 2026-02-04
 */
@Data
public class ExportContext {

    /**
     * 导出配置
     */
    private ExportConfig config;

    /**
     * 字段配置列表
     */
    private List<ExportField> fields;

    /**
     * 导出任务
     */
    private ExportTask task;

    /**
     * 查询参数对象
     */
    private Object queryParam;

    /**
     * 查询参数 Map
     */
    private Map<String, Object> queryParamMap;

    /**
     * 数据总数
     */
    private long totalCount;

    /**
     * 已处理数量
     */
    private long processedCount;

    /**
     * 当前 Sheet 索引
     */
    private int currentSheetIndex;

    /**
     * 当前 Sheet 行数
     */
    private int currentSheetRows;

    /**
     * 导出文件路径
     */
    private String filePath;

    /**
     * 导出文件名
     */
    private String fileName;

    /**
     * 是否已取消
     */
    private volatile boolean cancelled;

    /**
     * 更新进度
     *
     * @param processed 已处理数量
     */
    public void updateProgress(long processed) {
        this.processedCount = processed;
        if (task != null && totalCount > 0) {
            int progress = (int) (processed * 100 / totalCount);
            task.setProcessedCount((int) processed);
            task.setProgress(Math.min(progress, 100));
        }
    }

    /**
     * 获取进度百分比
     *
     * @return 进度百分比
     */
    public int getProgress() {
        if (totalCount <= 0) {
            return 0;
        }
        return (int) (processedCount * 100 / totalCount);
    }
}
