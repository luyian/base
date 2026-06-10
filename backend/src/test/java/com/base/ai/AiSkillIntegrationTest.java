package com.base.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * AI 技能集成测试 - 使用商汤 deepseek-v4-flash 验证 Function Calling 完整流程
 */
public class AiSkillIntegrationTest {

    static final String BASE_URL = "https://token.sensenova.cn/v1";
    static final String API_KEY = "sk-2RKTWVGRQw3wiibIx9Ucj5aZ52jYUk6q";
    static final String MODEL = "deepseek-v4-flash";

    interface StockAssistant {
        @SystemMessage("你是A股数据分析助手。当用户询问股票相关信息时，请调用工具获取实时数据并基于数据给出分析。回答用中文。数据仅供参考，不构成投资建议。")
        String chat(@UserMessage String message);
    }

    public static class TestStockTools {

        @Tool("查询股票实时行情，包括价格、涨跌幅、PE、PB、市值等")
        public String getStockQuote(@P("股票代码，多只用逗号分隔") String codes) {
            System.out.println("  [Tool Called] getStockQuote(" + codes + ")");
            return executePython("stock_quote.py", codes);
        }

        @Tool("查询北向资金当日实时流向")
        public String getNorthboundFlow() {
            System.out.println("  [Tool Called] getNorthboundFlow()");
            return executePython("northbound_flow.py", null);
        }

        @Tool("查询行业板块涨跌排名")
        public String getIndustryRank() {
            System.out.println("  [Tool Called] getIndustryRank()");
            return executePython("industry_rank.py", null);
        }

        private String executePython(String script, String arg) {
            java.nio.file.Path tempDir = null;
            try {
                // 从 classpath 加载脚本及公共模块到同一临时目录（保持 import 关系）
                tempDir = java.nio.file.Files.createTempDirectory("ai_test_");

                // 提取公共模块 em_helper.py
                extractResource("scripts/em_helper.py", tempDir.resolve("em_helper.py"));
                // 提取目标脚本
                extractResource("scripts/" + script, tempDir.resolve(script));

                String scriptPath = tempDir.resolve(script).toAbsolutePath().toString();
                System.out.println("  [Script] " + scriptPath);

                ProcessBuilder pb;
                if (arg != null && !arg.isEmpty()) {
                    pb = new ProcessBuilder("python", "-X", "utf8", scriptPath, arg);
                } else {
                    pb = new ProcessBuilder("python", "-X", "utf8", scriptPath);
                }
                pb.environment().put("PYTHONIOENCODING", "utf-8");
                pb.environment().put("PYTHONUTF8", "1");
                pb.redirectErrorStream(false);

                Process p = pb.start();

                // 读取 stdout
                StringBuilder stdout = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stdout.append(line).append("\n");
                    }
                }

                // 读取 stderr
                StringBuilder stderr = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stderr.append(line).append("\n");
                    }
                }

                boolean finished = p.waitFor(30, TimeUnit.SECONDS);
                if (!finished) {
                    p.destroyForcibly();
                    return "{\"error\": \"脚本执行超时\"}";
                }

                int exitCode = p.exitValue();
                String output = stdout.toString().trim();
                String errOutput = stderr.toString().trim();

                System.out.println("  [Exit] " + exitCode);
                if (!errOutput.isEmpty()) {
                    System.out.println("  [Stderr] " + errOutput.substring(0, Math.min(errOutput.length(), 300)));
                }

                if (exitCode != 0 || output.isEmpty()) {
                    String errMsg = errOutput.isEmpty() ? "脚本无输出" : errOutput;
                    String[] lines = errMsg.split("\n");
                    String lastLine = lines[lines.length - 1];
                    return "{\"error\": \"" + lastLine.replace("\"", "'").substring(0, Math.min(lastLine.length(), 100)) + "\"}";
                }

                System.out.println("  [Output] " + output.substring(0, Math.min(output.length(), 150)) + "...");
                return output;

            } catch (Exception e) {
                System.out.println("  [Exception] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return "{\"error\": \"" + e.getMessage() + "\"}";
            } finally {
                // 清理临时目录
                if (tempDir != null) {
                    try {
                        java.io.File[] files = tempDir.toFile().listFiles();
                        if (files != null) {
                            for (java.io.File f : files) {
                                f.delete();
                            }
                        }
                        tempDir.toFile().delete();
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        private void extractResource(String resourcePath, java.nio.file.Path target) throws Exception {
            java.io.InputStream is = TestStockTools.class.getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                System.out.println("  [WARN] 资源不存在: " + resourcePath);
                return;
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            java.nio.file.Files.write(target, sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== AI 技能集成测试 (商汤 deepseek-v4-flash) ===");
        System.out.println("工作目录: " + System.getProperty("user.dir"));
        System.out.println();

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(MODEL)
                .baseUrl(BASE_URL)
                .build();

        StockAssistant assistant = AiServices.builder(StockAssistant.class)
                .chatLanguageModel(model)
                .tools(new TestStockTools())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String[] testCases = {
            "查询贵州茅台的实时行情",
            "今日北向资金流向如何？",
            "今天哪些行业涨幅居前？",
        };

        for (int i = 0; i < testCases.length; i++) {
            String question = testCases[i];
            System.out.println("\n--- 测试 " + (i + 1) + ": " + question + " ---");
            try {
                long start = System.currentTimeMillis();
                String answer = assistant.chat(question);
                long cost = System.currentTimeMillis() - start;
                System.out.println("  [Answer] " + answer.substring(0, Math.min(answer.length(), 600)));
                System.out.println("  [Cost] " + cost + " ms");
            } catch (Exception e) {
                System.out.println("  [Error] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        System.out.println("\n=== 测试完成 ===");
    }
}
