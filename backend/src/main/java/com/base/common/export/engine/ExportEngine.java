package com.base.common.export.engine;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.base.common.export.config.ExportFileConfig;
import com.base.common.export.constant.ExportStatusEnum;
import com.base.common.export.constant.FieldTypeEnum;
import com.base.common.export.converter.DataConverter;
import com.base.common.export.mask.DataMasker;
import com.base.common.export.provider.ExportDataProvider;
import com.base.common.export.registry.ConverterRegistry;
import com.base.common.export.registry.DataSourceRegistry;
import com.base.common.export.registry.MaskerRegistry;
import com.base.system.export.entity.ExportConfig;
import com.base.system.export.entity.ExportField;
import com.base.system.export.entity.ExportTask;
import com.base.system.util.RedisUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 导出引擎核心
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class ExportEngine {

    private static final Logger log = LoggerFactory.getLogger(ExportEngine.class);

    private static final String PROGRESS_KEY_PREFIX = "export:progress:";

    @Autowired
    private DataSourceRegistry dataSourceRegistry;

    @Autowired
    private ConverterRegistry converterRegistry;

    @Autowired
    private MaskerRegistry maskerRegistry;

    @Autowired
    private ExportFileConfig fileConfig;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 执行导出
     *
     * @param context 导出上下文
     */
    public void execute(ExportContext context) {
        ExportTask task = context.getTask();
        ExportConfig config = context.getConfig();

        try {
            // 更新任务状态为处理中
            task.setStatus(ExportStatusEnum.PROCESSING.getCode());
            task.setStartTime(LocalDateTime.now());

            // 获取数据提供者
            ExportDataProvider provider = dataSourceRegistry.getProvider(config.getDataSourceType());
            if (provider == null) {
                throw new RuntimeException("不支持的数据源类型: " + config.getDataSourceType());
            }

            // 获取数据总数
            long totalCount = provider.count(context);
            if (totalCount == 0) {
                throw new RuntimeException("没有可导出的数据");
            }

            // 限制最大导出数量
            if (config.getMaxExportCount() != null && totalCount > config.getMaxExportCount()) {
                totalCount = config.getMaxExportCount();
            }

            context.setTotalCount(totalCount);
            task.setTotalCount((int) totalCount);

            // 生成文件路径
            String fileName = generateFileName(config);
            String filePath = generateFilePath(fileName);
            context.setFileName(fileName);
            context.setFilePath(filePath);

            // 确保目录存在
            File dir = new File(fileConfig.getPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 执行导出
            doExport(context, provider);

            // 更新任务状态为完成
            File file = new File(filePath);
            task.setStatus(ExportStatusEnum.COMPLETED.getCode());
            task.setFileName(fileName);
            task.setFilePath(filePath);
            task.setFileSize(file.length());
            task.setFileUrl(fileConfig.getUrlPrefix() + task.getTaskNo());
            task.setEndTime(LocalDateTime.now());
            task.setExpireTime(LocalDateTime.now().plusDays(fileConfig.getExpireDays()));
            task.setProgress(100);
            task.setProcessedCount((int) totalCount);

            // 更新 Redis 进度
            updateProgress(task.getTaskNo(), 100);

        } catch (Exception e) {
            log.error("导出失败", e);
            task.setStatus(ExportStatusEnum.FAILED.getCode());
            task.setErrorMessage(e.getMessage());
            task.setEndTime(LocalDateTime.now());
        }
    }

    /**
     * 执行导出逻辑
     */
    private void doExport(ExportContext context, ExportDataProvider provider) {
        ExportConfig config = context.getConfig();
        List<ExportField> fields = context.getFields();
        int batchSize = config.getBatchSize() != null ? config.getBatchSize() : 5000;
        long totalCount = context.getTotalCount();

        // 构建表头
        List<List<String>> headers = buildHeaders(fields);

        // 构建列宽度
        List<Integer> columnWidths = buildColumnWidths(fields);

        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(context.getFilePath())
                    .registerWriteHandler(new CustomColumnWidthStrategy(columnWidths))
                    .build();

            int sheetIndex = 0;
            int sheetRows = 0;
            int sheetMaxRows = config.getSheetMaxRows() != null ? config.getSheetMaxRows() : 100000;
            boolean enableMultiSheet = Boolean.TRUE.equals(config.getEnableMultiSheet());

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex, "Sheet" + (sheetIndex + 1))
                    .head(headers).build();

            long offset = 0;
            long processedCount = 0;

            while (offset < totalCount && !context.isCancelled()) {
                // 分批获取数据
                List<Map<String, Object>> dataList = provider.fetchData(context, offset, batchSize);
                if (dataList == null || dataList.isEmpty()) {
                    break;
                }

                // 处理数据
                for (Map<String, Object> row : dataList) {
                    if (context.isCancelled()) {
                        break;
                    }

                    // 检查是否需要新建 Sheet
                    if (enableMultiSheet && sheetRows >= sheetMaxRows) {
                        sheetIndex++;
                        sheetRows = 0;
                        writeSheet = EasyExcel.writerSheet(sheetIndex, "Sheet" + (sheetIndex + 1))
                                .head(headers).build();
                    }

                    // 转换数据行（传入序号，从1开始）
                    List<Object> rowData = convertRow(row, fields, context, processedCount + 1);
                    excelWriter.write(Collections.singletonList(rowData), writeSheet);

                    sheetRows++;
                    processedCount++;

                    // 每 1000 条更新一次进度
                    if (processedCount % 1000 == 0) {
                        context.updateProgress(processedCount);
                        updateProgress(context.getTask().getTaskNo(), context.getProgress());
                    }
                }

                offset += dataList.size();
            }

            // 最终更新进度
            context.updateProgress(processedCount);

        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 构建表头
     */
    private List<List<String>> buildHeaders(List<ExportField> fields) {
        List<List<String>> headers = new ArrayList<>();

        // 添加序号列
        List<String> indexHeader = new ArrayList<>();
        indexHeader.add("序号");
        headers.add(indexHeader);

        for (ExportField field : fields) {
            if (field.getStatus() != null && field.getStatus() == 1) {
                List<String> header = new ArrayList<>();
                header.add(field.getFieldLabel());
                headers.add(header);
            }
        }
        return headers;
    }

    /**
     * 构建列宽度列表
     */
    private List<Integer> buildColumnWidths(List<ExportField> fields) {
        List<Integer> widths = new ArrayList<>();

        // 序号列宽度
        widths.add(8);

        for (ExportField field : fields) {
            if (field.getStatus() != null && field.getStatus() == 1) {
                Integer width = field.getFieldWidth();
                widths.add(width != null ? width : 20);
            }
        }
        return widths;
    }

    /**
     * 转换数据行
     *
     * @param row 数据行
     * @param fields 字段配置
     * @param context 导出上下文
     * @param rowNum 行号（序号）
     */
    private List<Object> convertRow(Map<String, Object> row, List<ExportField> fields, ExportContext context, long rowNum) {
        List<Object> rowData = new ArrayList<>();

        // 添加序号列
        rowData.add(rowNum);

        for (ExportField field : fields) {
            if (field.getStatus() == null || field.getStatus() != 1) {
                continue;
            }

            Object value = row.get(field.getFieldName());

            // 默认值处理
            if (value == null && StringUtils.hasText(field.getDefaultValue())) {
                value = field.getDefaultValue();
            }

            // 字典转换
            if (StringUtils.hasText(field.getDictType())) {
                DataConverter dictConverter = converterRegistry.getConverter("dictConverter");
                if (dictConverter != null) {
                    value = dictConverter.convert(value, field, context);
                }
            }

            // 自定义转换器
            if (StringUtils.hasText(field.getConverterBean())) {
                DataConverter converter = converterRegistry.getConverter(field.getConverterBean());
                if (converter != null) {
                    value = converter.convert(value, field, context);
                }
            }

            // 格式化处理
            value = formatValue(value, field);

            // 脱敏处理
            if (StringUtils.hasText(field.getMaskType()) && value != null) {
                DataMasker masker = maskerRegistry.getMasker(field.getMaskType());
                if (masker != null) {
                    value = masker.mask(String.valueOf(value));
                }
            }

            rowData.add(value);
        }
        return rowData;
    }

    /**
     * 格式化值
     */
    private Object formatValue(Object value, ExportField field) {
        if (value == null) {
            return "";
        }

        String fieldType = field.getFieldType();
        String format = field.getFieldFormat();

        if (FieldTypeEnum.DATE.getCode().equals(fieldType) || FieldTypeEnum.DATETIME.getCode().equals(fieldType)) {
            String pattern = StringUtils.hasText(format) ? format : "yyyy-MM-dd HH:mm:ss";
            if (value instanceof LocalDateTime) {
                return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(pattern));
            } else if (value instanceof LocalDate) {
                return ((LocalDate) value).format(DateTimeFormatter.ofPattern(pattern));
            } else if (value instanceof Date) {
                return new SimpleDateFormat(pattern).format((Date) value);
            }
        }

        return value;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(ExportConfig config) {
        String pattern = config.getFileNamePattern();
        if (!StringUtils.hasText(pattern)) {
            pattern = config.getConfigName();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return pattern + "_" + timestamp + ".xlsx";
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = fileConfig.getPath() + datePath + "/";

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dirPath + fileName;
    }

    /**
     * 更新进度到 Redis
     */
    private void updateProgress(String taskNo, int progress) {
        try {
            redisUtil.set(PROGRESS_KEY_PREFIX + taskNo, progress, 3600);
        } catch (Exception e) {
            log.warn("更新进度到 Redis 失败", e);
        }
    }

    /**
     * 从 Redis 获取进度
     */
    public Integer getProgress(String taskNo) {
        try {
            Object progress = redisUtil.get(PROGRESS_KEY_PREFIX + taskNo);
            if (progress instanceof Integer) {
                return (Integer) progress;
            }
            if (progress instanceof Number) {
                return ((Number) progress).intValue();
            }
        } catch (Exception e) {
            log.warn("从 Redis 获取进度失败", e);
        }
        return null;
    }

    /**
     * 自定义列宽策略
     */
    private static class CustomColumnWidthStrategy extends AbstractColumnWidthStyleStrategy {

        private final List<Integer> columnWidths;
        private boolean hasSetWidth = false;

        public CustomColumnWidthStrategy(List<Integer> columnWidths) {
            this.columnWidths = columnWidths;
        }

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder,
                                      List<com.alibaba.excel.metadata.data.WriteCellData<?>> cellDataList,
                                      Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            // 只在第一行设置列宽
            if (!hasSetWidth && isHead != null && isHead) {
                Sheet sheet = writeSheetHolder.getSheet();
                for (int i = 0; i < columnWidths.size(); i++) {
                    // EasyExcel 列宽单位是字符宽度的 256 分之一
                    sheet.setColumnWidth(i, columnWidths.get(i) * 256);
                }
                hasSetWidth = true;
            }
        }
    }
}
