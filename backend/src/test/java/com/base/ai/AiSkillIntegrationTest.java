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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * AI 技能集成测试 - 使用商汤 deepseek-v4-flash 验证 Function Calling 完整流程
 * 前置条件：python-tools 服务已启动（端口 8100）
 */
public class AiSkillIntegrationTest {

    static final String BASE_URL = "https://token.sensenova.cn/v1";
    static final String API_KEY = "sk-2RKTWVGRQw3wiibIx9Ucj5aZ52jYUk6q";
    static final String MODEL = "deepseek-v4-flash";

    static final String PYTHON_TOOLS_URL = "http://localhost:8100";

    interface StockAssistant {
        @SystemMessage("你是A股数据分析助手。当用户询问股票相关信息时，请调用工具获取实时数据并基于数据给出分析。回答用中文。数据仅供参考，不构成投资建议。")
        String chat(@UserMessage String message);
    }

    public static class TestStockTools {

        @Tool("查询股票实时行情，包括价格、涨跌幅、PE、PB、市值等")
        public String getStockQuote(@P("股票代码，多只用逗号分隔") String codes) {
            System.out.println("  [Tool Called] getStockQuote(" + codes + ")");
            return callPythonTools("/api/stock/quote?codes=" + codes);
        }

        @Tool("查询北向资金当日实时流向")
        public String getNorthboundFlow() {
            System.out.println("  [Tool Called] getNorthboundFlow()");
            return callPythonTools("/api/stock/northbound-flow");
        }

        @Tool("查询行业板块涨跌排名")
        public String getIndustryRank() {
            System.out.println("  [Tool Called] getIndustryRank()");
            return callPythonTools("/api/stock/industry-rank");
        }

        /**
         * 调用 python-tools HTTP 服务
         */
        private String callPythonTools(String path) {
            try {
                URL url = new URL(PYTHON_TOOLS_URL + path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);

                int status = conn.getResponseCode();
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                status == 200 ? conn.getInputStream() : conn.getErrorStream(),
                                StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }

                String body = sb.toString();
                System.out.println("  [HTTP " + status + "] " + body.substring(0, Math.min(body.length(), 200)) + "...");

                // 提取 data 字段
                if (body.contains("\"code\":200")) {
                    int dataIdx = body.indexOf("\"data\":");
                    if (dataIdx > 0) {
                        // 简单截取 data 内容返回给 LLM
                        String data = body.substring(dataIdx + 7, body.length() - 1);
                        return data;
                    }
                }
                return body;

            } catch (Exception e) {
                System.out.println("  [Exception] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== AI 技能集成测试 (商汤 deepseek-v4-flash) ===");
        System.out.println("Python Tools 服务地址: " + PYTHON_TOOLS_URL);
        System.out.println();

        // 先检查 python-tools 服务是否可用
        try {
            URL healthUrl = new URL(PYTHON_TOOLS_URL + "/health");
            HttpURLConnection conn = (HttpURLConnection) healthUrl.openConnection();
            conn.setConnectTimeout(3000);
            if (conn.getResponseCode() != 200) {
                System.out.println("❌ python-tools 服务未启动，请先运行: python-tools/run.bat");
                return;
            }
            System.out.println("✓ python-tools 服务正常");
        } catch (Exception e) {
            System.out.println("❌ python-tools 服务未启动，请先运行: python-tools/run.bat");
            System.out.println("   错误: " + e.getMessage());
            return;
        }

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
