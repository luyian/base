package com.base.common.interceptor;

import com.base.common.aspect.DataScopeContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * 数据权限拦截器
 * 在SQL执行前自动添加数据权限过滤条件
 *
 * @author base
 * @since 2026-01-13
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataScopeInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            // 获取数据权限SQL
            String dataScopeSql = DataScopeContextHolder.getDataScope();
            if (dataScopeSql != null && !dataScopeSql.isEmpty()) {
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
                MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

                // 获取BoundSql
                BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
                String originalSql = boundSql.getSql();

                // 只对SELECT语句进行处理
                if (originalSql.trim().toUpperCase().startsWith("SELECT")) {
                    // 在WHERE子句后添加数据权限条件
                    String newSql = addDataScopeCondition(originalSql, dataScopeSql);

                    // 更新SQL
                    metaObject.setValue("delegate.boundSql.sql", newSql);

                    log.debug("原始SQL: {}", originalSql);
                    log.debug("数据权限SQL: {}", newSql);
                }
            }
        } finally {
            // 清除ThreadLocal，防止内存泄漏
            DataScopeContextHolder.clearDataScope();
        }

        return invocation.proceed();
    }

    /**
     * 添加数据权限条件到SQL
     */
    private String addDataScopeCondition(String originalSql, String dataScopeSql) {
        // 简化处理：在WHERE子句后添加条件
        // 实际项目中可能需要使用SQL解析器（如JSqlParser）来更准确地处理
        String upperSql = originalSql.toUpperCase();

        if (upperSql.contains("WHERE")) {
            // 已有WHERE子句，直接添加AND条件
            int whereIndex = upperSql.indexOf("WHERE");
            int whereEndIndex = whereIndex + 5; // "WHERE"的长度

            return originalSql.substring(0, whereEndIndex) +
                   " 1=1 " + dataScopeSql + " AND " +
                   originalSql.substring(whereEndIndex);
        } else {
            // 没有WHERE子句，需要添加WHERE
            // 查找ORDER BY、GROUP BY、LIMIT等子句的位置
            int insertIndex = originalSql.length();

            String[] keywords = {"ORDER BY", "GROUP BY", "LIMIT", "UNION"};
            for (String keyword : keywords) {
                int index = upperSql.indexOf(keyword);
                if (index > 0 && index < insertIndex) {
                    insertIndex = index;
                }
            }

            return originalSql.substring(0, insertIndex) +
                   " WHERE 1=1 " + dataScopeSql + " " +
                   originalSql.substring(insertIndex);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以从配置文件中读取属性
    }
}
