package com.base.system.aspect;

import com.base.system.annotation.OperationLog;
import com.base.system.service.OperationLogService;
import com.base.system.util.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 自动记录标注了 @OperationLog 注解的方法的操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    /**
     * 定义切点：所有标注了 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.base.system.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 创建日志对象
        com.base.system.entity.OperationLog operationLog = new com.base.system.entity.OperationLog();
        operationLog.setModule(annotation.module());
        operationLog.setOperation(annotation.operation());
        operationLog.setMethod(method.getDeclaringClass().getName() + "." + method.getName());

        // 获取请求参数
        if (annotation.saveParams()) {
            try {
                Object[] args = joinPoint.getArgs();
                String params = objectMapper.writeValueAsString(args);
           // 限制参数长度
                if (params.length() > 2000) {
                    params = params.substring(0, 2000) + "...";
                }
                operationLog.setParams(params);
            } catch (Exception e) {
                log.warn("记录请求参数失败", e);
            }
        }

        // 获取请求信息
        if (request != null) {
            operationLog.setIp(IpUtils.getIpAddress(request));
            operationLog.setUserAgent(request.getHeader("User-Agent"));
        }

        // 获取当前用户
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                operationLog.setOperatorName(username);
            }
        } catch (Exception e) {
            log.warn("获取当前用户失败", e);
        }

        // 执行方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            operationLog.setStatus(1); // 成功

            // 保存返回结果
            if (annotation.saveResult() && result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    // 限制结果长度
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    operationLog.setResult(resultStr);
                } catch (Exception e) {
                    log.warn("记录返回结果失败", e);
                }
            }
        } catch (Exception e) {
            operationLog.setStatus(0); // 失败
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时长
            long executeTime = System.currentTimeMillis() - startTime;
            operationLog.setExecuteTime((int) executeTime);
            operationLog.setCreateTime(LocalDateTime.now());

            // 异步保存日志
            try {
                operationLogService.save(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }

        return result;
    }
}
