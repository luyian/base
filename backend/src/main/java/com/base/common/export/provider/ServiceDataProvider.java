package com.base.common.export.provider;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.export.constant.DataSourceTypeEnum;
import com.base.common.export.engine.ExportContext;
import com.base.system.export.entity.ExportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 服务方法数据提供者
 * 通过反射调用 Service 方法获取数据
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class ServiceDataProvider implements ExportDataProvider {

    private static final Logger log = LoggerFactory.getLogger(ServiceDataProvider.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public long count(ExportContext context) {
        ExportConfig config = context.getConfig();
        try {
            Object service = applicationContext.getBean(config.getDataSourceBean());
            String countMethodName = config.getDataSourceMethod() + "Count";

            // 尝试调用 xxxCount 方法
            Method countMethod = findMethod(service.getClass(), countMethodName, context.getQueryParam());
            if (countMethod != null) {
                Object result = invokeMethod(service, countMethod, context.getQueryParam());
                if (result instanceof Number) {
                    return ((Number) result).longValue();
                }
            }

            // 如果没有 count 方法，调用分页方法获取总数
            Method pageMethod = findMethod(service.getClass(), config.getDataSourceMethod(), context.getQueryParam());
            if (pageMethod != null) {
                Object result = invokeMethod(service, pageMethod, context.getQueryParam());
                if (result instanceof IPage) {
                    return ((IPage<?>) result).getTotal();
                }
            }

            return 0;
        } catch (Exception e) {
            log.error("获取数据总数失败", e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchData(ExportContext context, long offset, int limit) {
        ExportConfig config = context.getConfig();
        try {
            Object service = applicationContext.getBean(config.getDataSourceBean());
            Method method = findMethod(service.getClass(), config.getDataSourceMethod(), context.getQueryParam());

            if (method == null) {
                log.error("未找到方法: {}.{}", config.getDataSourceBean(), config.getDataSourceMethod());
                return Collections.emptyList();
            }

            // 设置分页参数
            Object queryParam = context.getQueryParam();
            setPageParam(queryParam, offset, limit);

            Object result = invokeMethod(service, method, queryParam);

            // 处理返回结果
            List<?> dataList;
            if (result instanceof IPage) {
                dataList = ((IPage<?>) result).getRecords();
            } else if (result instanceof List) {
                dataList = (List<?>) result;
            } else {
                return Collections.emptyList();
            }

            // 转换为 Map 列表
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (Object item : dataList) {
                if (item instanceof Map) {
                    mapList.add((Map<String, Object>) item);
                } else {
                    // 将对象转换为 Map
                    String json = JSON.toJSONString(item);
                    Map<String, Object> map = JSON.parseObject(json, Map.class);
                    mapList.add(map);
                }
            }
            return mapList;
        } catch (Exception e) {
            log.error("获取数据失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean supports(String dataSourceType) {
        return DataSourceTypeEnum.SERVICE.getCode().equals(dataSourceType);
    }

    /**
     * 查找方法
     */
    private Method findMethod(Class<?> clazz, String methodName, Object param) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0 && param == null) {
                    return method;
                }
                if (paramTypes.length == 1 && param != null) {
                    if (paramTypes[0].isAssignableFrom(param.getClass())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 调用方法
     */
    private Object invokeMethod(Object service, Method method, Object param) throws Exception {
        if (method.getParameterCount() == 0) {
            return method.invoke(service);
        }
        return method.invoke(service, param);
    }

    /**
     * 设置分页参数
     */
    private void setPageParam(Object queryParam, long offset, int limit) {
        if (queryParam == null) {
            return;
        }
        try {
            // 计算页码
            int pageNum = (int) (offset / limit) + 1;

            // 尝试设置 pageNum
            Method setPageNum = findSetter(queryParam.getClass(), "setPageNum", Integer.class);
            if (setPageNum != null) {
                setPageNum.invoke(queryParam, pageNum);
            }

            // 尝试设置 pageSize
            Method setPageSize = findSetter(queryParam.getClass(), "setPageSize", Integer.class);
            if (setPageSize != null) {
                setPageSize.invoke(queryParam, limit);
            }
        } catch (Exception e) {
            log.warn("设置分页参数失败", e);
        }
    }

    /**
     * 查找 setter 方法
     */
    private Method findSetter(Class<?> clazz, String methodName, Class<?> paramType) {
        try {
            return clazz.getMethod(methodName, paramType);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(methodName, int.class);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }
}
