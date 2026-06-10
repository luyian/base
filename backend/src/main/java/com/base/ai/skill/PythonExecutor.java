package com.base.ai.skill;

import com.base.ai.config.AiSkillConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Python 脚本执行器 — 只允许执行 SkillScriptEnum 中枚举的脚本，
 * 参数通过白名单校验，禁止任意代码执行。
 *
 * @author base
 * @since 2026-06-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonExecutor {

    private final AiSkillConfig skillConfig;

    /** 允许执行的脚本文件名白名单 */
    private Set<String> allowedScripts;

    /** 并发控制：最多同时执行 3 个 Python 进程 */
    private static final int MAX_CONCURRENT = 3;
    private final Semaphore concurrencySemaphore = new Semaphore(MAX_CONCURRENT);

    /** 脚本内容缓存（从 classpath:scripts/ 加载） */
    private final Map<String, String> scriptCache = new ConcurrentHashMap<>();

    /** 脚本临时目录（所有脚本提取到同一目录，保持 import 关系） */
    private volatile Path scriptTempDir;

    /** 输出最大字节数（防止巨大输出撑爆内存） */
    private static final int MAX_OUTPUT_BYTES = 64 * 1024;

    @PostConstruct
    public void init() {
        allowedScripts = Arrays.stream(SkillScriptEnum.values())
                .map(SkillScriptEnum::getFileName)
                .collect(Collectors.toSet());
        // 启动时将所有脚本提取到临时目录
        extractScriptsToTempDir();
        log.info("AI 技能执行器初始化完成，已注册 {} 个脚本，临时目录: {}", allowedScripts.size(), scriptTempDir);
    }

    /**
     * 执行指定枚举脚本，传入参数列表
     *
     * @param script 脚本枚举
     * @param args   参数列表（已经过白名单校验）
     * @return JSON 格式输出
     */
    public String execute(SkillScriptEnum script, List<String> args) {
        if (script == null) {
            return "{\"error\": \"脚本不存在\"}";
        }
        if (!allowedScripts.contains(script.getFileName())) {
            return "{\"error\": \"脚本不在白名单中\"}";
        }

        // 并发控制
        boolean acquired = false;
        try {
            acquired = concurrencySemaphore.tryAcquire(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "{\"error\": \"系统繁忙，请稍后重试\"}";
        }
        if (!acquired) {
            return "{\"error\": \"当前查询较多，请稍后重试\"}";
        }

        Path tempScript = null;
        try {
            // 确保脚本目录已就绪
            if (scriptTempDir == null) {
                extractScriptsToTempDir();
            }

            // 直接使用临时目录中的脚本文件（保持 import 关系）
            Path scriptFile = scriptTempDir.resolve(script.getFileName());
            if (!Files.exists(scriptFile)) {
                return "{\"error\": \"脚本文件不存在\"}";
            }

            // 构建命令
            String pythonPath = skillConfig.getPythonPath();
            List<String> command = new java.util.ArrayList<>();
            command.add(pythonPath);
            command.add("-X");
            command.add("utf8");
            command.add(scriptFile.toAbsolutePath().toString());
            if (args != null) {
                command.addAll(args);
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.environment().put("PYTHONIOENCODING", "utf-8");
            pb.environment().put("PYTHONUTF8", "1");
            pb.redirectErrorStream(false);

            Process process = pb.start();

            // 读取 stdout（限制大小）
            String stdout = readStream(process.getInputStream(), MAX_OUTPUT_BYTES);

            // 读取 stderr（仅用于内部日志，不暴露给用户）
            String stderr = readStream(process.getErrorStream(), 4096);

            int timeoutSeconds = skillConfig.getTimeout() != null ? skillConfig.getTimeout() : 30;
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.warn("AI 技能脚本执行超时: script={}, timeout={}s", script.getFileName(), timeoutSeconds);
                return "{\"error\": \"数据查询超时，请稍后重试\"}";
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.warn("AI 技能脚本执行失败: script={}, exitCode={}, stderr={}", script.getFileName(), exitCode, stderr);
                return "{\"error\": \"数据获取失败，请稍后重试\"}";
            }

            String result = stdout.trim();
            if (result.isEmpty()) {
                return "{\"error\": \"数据查询无结果\"}";
            }
            return result;

        } catch (Exception e) {
            log.error("AI 技能脚本执行异常: script={}, error={}", script.getFileName(), e.getMessage());
            return "{\"error\": \"数据查询异常，请稍后重试\"}";
        } finally {
            concurrencySemaphore.release();
        }
    }

    /**
     * 启动时将所有脚本从 classpath 提取到临时目录（保持 import 关系）
     */
    private void extractScriptsToTempDir() {
        try {
            scriptTempDir = Files.createTempDirectory("ai_skills_");
            // 提取公共模块
            extractResource("scripts/em_helper.py", scriptTempDir.resolve("em_helper.py"));
            // 提取所有技能脚本
            for (SkillScriptEnum s : SkillScriptEnum.values()) {
                extractResource("scripts/" + s.getFileName(), scriptTempDir.resolve(s.getFileName()));
            }
        } catch (IOException e) {
            log.error("提取脚本到临时目录失败: {}", e.getMessage());
        }
    }

    private void extractResource(String resourcePath, Path targetFile) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.warn("资源文件不存在: {}", resourcePath);
                return;
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            Files.write(targetFile, sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 读取流内容，限制最大字节数
     */
    private String readStream(InputStream is, int maxBytes) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            char[] buf = new char[1024];
            int total = 0;
            int read;
            while ((read = reader.read(buf)) != -1) {
                total += read;
                if (total > maxBytes) {
                    sb.append(buf, 0, read - (total - maxBytes));
                    break;
                }
                sb.append(buf, 0, read);
            }
        }
        return sb.toString();
    }
}
