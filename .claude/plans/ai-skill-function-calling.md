# AI 技能扩展 — Function Calling 实现方案

## 概述

在现有 AI 模块基础上，通过 LangChain4j 的 `@Tool` + `AiServices` 机制，让大模型在对话中自动识别用户意图并调用 Python 脚本获取 A 股实时数据，实现智能问答与数据技能的融合。

## 技术选型

| 项目 | 选择 | 原因 |
|------|------|------|
| Function Calling | LangChain4j `@Tool` + `AiServices` | 项目已有 langchain4j 0.31.0 依赖，原生支持 |
| 数据获取 | ProcessBuilder 调 Python 独立脚本 | 复用 a-stock-data skill 的成熟代码 |
| 模型 | 商汤 deepseek-v4-flash（已验证） | 完美支持 function calling，走 OpenAI 兼容接口 |
| 脚本加载 | ClassLoader 从 classpath 提取到临时目录 | 本地/远程部署通用，无硬编码路径 |

## 架构设计

```
用户提问（前端 Dashboard）
    ↓
POST /ai/chat { message, enableSkills: true }
    ↓
AiController → AiService.chatWithSkills(request)
    ↓
LangChain4j AiServices 代理（自动判断是否需要调用工具）
    ├── 不需要工具 → 直接生成回答
    └── 需要工具 → 调用 @Tool 标注的方法
                      ↓
               StockDataTools.getXxx(参数白名单校验)
                      ↓
               PythonExecutor.execute(SkillScriptEnum, args)
                      ↓
               从 classpath 提取脚本到临时目录 → ProcessBuilder 执行
                      ↓
               JSON stdout 返回给模型
                      ↓
               模型生成自然语言回答（含数据免责声明）
```

## 安全边界

| 层级 | 措施 |
|------|------|
| 工具选择 | LLM 只能调用 @Tool 标注的固定方法，不能生成任意脚本 |
| 参数校验 | StockDataTools 中所有入参仅允许数字+逗号（正则白名单），最多10只 |
| 脚本白名单 | PythonExecutor 只执行 SkillScriptEnum 枚举范围内的文件 |
| 并发控制 | Semaphore 最多 3 个 Python 进程同时执行 |
| 输出限制 | stdout 最大 64KB，超出截断 |
| 超时控制 | 单脚本 30s 超时强制终止 |
| 错误隔离 | stderr 仅写日志，不暴露给用户，统一返回友好提示 |
| 降级兜底 | 技能对话失败自动回退到普通对话模式 |
| 数据免责 | System Prompt 注入"数据仅供参考，不构成投资建议" |

## 模块结构

```
backend/src/main/java/com/base/ai/
├── skill/
│   ├── SkillScriptEnum.java        # 脚本枚举白名单（10个）
│   ├── StockDataTools.java         # @Tool 标注的技能方法（参数白名单校验）
│   └── PythonExecutor.java         # 脚本执行器（ClassLoader加载 + 临时目录 + 并发控制）
├── config/
│   └── AiSkillConfig.java          # 【新增】技能配置（pythonPath, timeout, enabled）
├── service/
│   ├── AiService.java              # 【修改】新增 chatWithSkills 方法
│   └── impl/AiServiceImpl.java     # 【修改】集成 AiServices + Tools + 降级
├── dto/
│   └── ChatRequest.java            # 【修改】新增 enableSkills 字段
└── controller/
    └── AiController.java           # 【修改】按 enableSkills 分流

backend/src/main/resources/scripts/  # Python 脚本（打包进 jar）
├── em_helper.py                     # 东财限流公共模块
├── stock_quote.py                   # 实时行情（腾讯财经，不封IP）
├── stock_fund_flow.py               # 资金流向（东财 push2）
├── stock_news.py                    # 个股新闻（东财 search-api）
├── industry_rank.py                 # 行业涨跌排名（东财 push2）
├── northbound_flow.py               # 北向资金（同花顺 hsgtApi）
├── dragon_tiger.py                  # 龙虎榜（东财 datacenter）
├── hot_stocks.py                    # 当日强势股（同花顺）
├── concept_blocks.py                # 板块归属（东财 slist）
├── margin_trading.py                # 融资融券（东财 datacenter）
└── stock_valuation.py               # 综合估值（腾讯+同花顺一致预期）

frontend/src/views/Dashboard.vue     # 【修改】双模式切换（A股助手/系统助手）
```

## 脚本加载机制

```
应用启动
    ↓
PythonExecutor.init() (@PostConstruct)
    ↓
ClassLoader.getResourceAsStream("scripts/xxx.py")
    ↓
Files.createTempDirectory("ai_skills_")
    ↓
所有脚本 + em_helper.py 提取到同一临时目录
    ↓
脚本间 from em_helper import * 正常工作
```

- 本地 IDEA 开发：从 target/classes/scripts/ 加载
- 远程 jar 部署：从 jar 内 BOOT-INF/classes/scripts/ 加载
- 无硬编码路径依赖

## 数据源与稳定性

| 脚本 | 数据源 | 封IP风险 | 限流措施 |
|------|--------|---------|---------|
| stock_quote | 腾讯财经 | 不封IP | 无需 |
| stock_fund_flow | 东财 push2 | 有风控 | em_get() 内置1s间隔 |
| stock_news | 东财 search-api | 有风控 | em_get() |
| industry_rank | 东财 push2 | 有风控 | em_get() |
| northbound_flow | 同花顺 hsgtApi | 极低 | 无需 |
| dragon_tiger | 东财 datacenter | 有风控 | em_get() |
| hot_stocks | 同花顺 | 极低 | 无需 |
| concept_blocks | 东财 slist | 有风控 | em_get() |
| margin_trading | 东财 datacenter | 有风控 | em_get() |
| stock_valuation | 腾讯+同花顺 | 低 | 无需 |

**降级策略**：脚本执行失败（超时/网络异常/接口变更）时返回 `{"error": "..."}`，模型收到后会告知用户数据暂不可用。整个技能对话异常时自动降级到普通对话模式。

**数据免责**：所有接口为第三方公开数据，无 SLA 保证，可能存在延迟或间歇不可用。System Prompt 已注入免责声明。

## 前端产品设计

Dashboard AI 助手提供双模式切换：
- **A股助手**：`enableSkills=true`，快捷问题为股票数据相关
- **系统助手**：`enableSkills=false`，快捷问题为服务器/系统相关

通过 `el-segmented` 组件切换，互不干扰。

## 依赖变更

**pom.xml**：
- 腾讯云 COS SDK 排除旧版 okhttp/okio（解决与 LangChain4j 的 okhttp3 冲突）
- 无需新增依赖

**服务器 Python 环境要求**：
```bash
pip install requests pandas
```

## 已验证结论（2026-06-10）

| 验证项 | 结果 |
|--------|------|
| 商汤 deepseek-v4-flash Function Calling | ✅ 正确选择工具、解析参数 |
| sensenova-6.7-flash-lite Function Calling | ✅ 正确 |
| Python 脚本独立执行 | ✅ stock_quote/northbound_flow 实测正常 |
| 模型多工具自动合并 | ✅ "对比600519和000858" → getStockQuote(codes="600519,000858") |
| 工具失败降级回答 | ✅ 收到error后给出友好提示 |
| OkHttp 冲突修复 | ✅ 排除 COS SDK 旧版后 NoSuchFieldError 解决 |
| 后端编译 | ✅ mvn compile 通过 |
| 代码格式归一化 | ✅ 支持 600519.SH / SH600519 / 600519 三种入参 |

## 验收用例

| # | 用例 | 预期 |
|---|------|------|
| 1 | "查询贵州茅台行情" | 调用 getStockQuote，返回实时价格/PE/市值 |
| 2 | "今日北向资金" | 调用 getNorthboundFlow，返回沪/深股通数据 |
| 3 | "行业涨幅排名" | 调用 getIndustryRank，返回 TOP10 行业 |
| 4 | "今天天气怎么样" | 不调用任何工具，直接回答 |
| 5 | 工具超时/网络异常 | 返回友好错误提示，不暴露 stderr |
| 6 | 代码格式 "600519.SH" | 正确归一化为 600519 并查询成功 |
| 7 | 切换到"系统助手"模式 | enableSkills=false，走普通对话 |
| 8 | 后端 mvn compile | 零错误 |
| 9 | 前端 npm run build | 零错误 |
