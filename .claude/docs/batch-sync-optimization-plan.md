# 批量拉取数据多线程优化 - 开发计划

## 一、开发阶段划分

### 阶段一：数据库设计与实体层（1天）
- 创建失败记录表 `stk_sync_failure`
- 创建实体类、Mapper、Service

### 阶段二：Token 管理增强（0.5天）
- 添加批量获取可用 Token 方法
- 添加 Token 分配工具方法

### 阶段三：多线程同步核心逻辑（2天）
- 重构 `StockSyncServiceImpl.batchSyncAllKlineData` 方法
- 实现线程池创建和任务分配
- 实现失败记录机制

### 阶段四：补拉功能（1天）
- 实现补拉服务方法
- 添加补拉 Controller 接口
- 添加配置参数

### 阶段五：测试与优化（1天）
- 单元测试
- 集成测试
- 性能测试

**总计**：5.5 天

---

## 二、详细开发任务

### 阶段一：数据库设计与实体层

#### 任务 1.1：创建数据库表
**文件**：`backend/src/main/resources/db/sync_failure_schema.sql`

```sql
CREATE TABLE `stk_sync_failure` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stock_code` varchar(20) NOT NULL COMMENT '股票代码',
    `start_date` date NOT NULL COMMENT '开始日期',
    `end_date` date NOT NULL COMMENT '结束日期',
    `failure_reason` varchar(500) COMMENT '失败原因',
    `retry_count` int DEFAULT 0 COMMENT '重试次数',
    `status` tinyint DEFAULT 0 COMMENT '状态：0-待重试，1-重试成功，2-放弃重试',
    `last_retry_time` datetime COMMENT '最后重试时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_stock_code` (`stock_code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票同步失败记录表';
```

#### 任务 1.2：创建实体类
**文件**：`backend/src/main/java/com/base/stock/entity/SyncFailure.java`

**内容**：
- 继承 `BaseEntity`
- 包含所有字段的 getter/setter
- 使用 `@TableName("stk_sync_failure")`

#### 任务 1.3：创建 Mapper
**文件**：`backend/src/main/java/com/base/stock/mapper/SyncFailureMapper.java`

**方法**：
```java
public interface SyncFailureMapper extends BaseMapper<SyncFailure> {
    // 继承 MyBatis Plus 基础方法即可
}
```

#### 任务 1.4：创建 Service
**文件**：
- `backend/src/main/java/com/base/stock/service/SyncFailureService.java`
- `backend/src/main/java/com/base/stock/service/impl/SyncFailureServiceImpl.java`

**方法**：
```java
// 记录失败
void recordFailure(String stockCode, LocalDate startDate, LocalDate endDate, String reason);

// 查询待重试的失败记录
List<SyncFailure> listPendingRetry(String stockCode, int maxRetryCount);

// 更新失败记录状态
void updateStatus(Long id, int status, int retryCount);

// 批量更新状态
void batchUpdateStatus(List<Long> ids, int status);
```

---

### 阶段二：Token 管理增强

#### 任务 2.1：添加批量获取可用 Token 方法
**文件**：`backend/src/main/java/com/base/stock/service/TokenManagerService.java`

**新增方法**：
```java
/**
 * 批量获取可用 Token
 * @param provider 服务商
 * @return 可用 Token 列表
 */
List<ApiToken> getAvailableTokens(String provider);
```

**实现逻辑**（`TokenManagerServiceImpl.java`）：
```java
@Override
public List<ApiToken> getAvailableTokens(String provider) {
    LambdaQueryWrapper<ApiToken> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ApiToken::getProvider, provider)
           .eq(ApiToken::getStatus, 1)
           .eq(ApiToken::getDeleted, 0)
           .and(w -> w.isNull(ApiToken::getExpireTime)
                   .or()
                   .gt(ApiToken::getExpireTime, LocalDateTime.now()));

    List<ApiToken> tokens = apiTokenMapper.selectList(wrapper);

    // 过滤掉已达限额的 Token
    return tokens.stream()
            .filter(token -> token.getDailyLimit() == 0 ||
                           token.getDailyUsed() < token.getDailyLimit())
            .collect(Collectors.toList());
}
```

#### 任务 2.2：添加 Token 分配工具方法
**文件**：`backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`

**新增方法**：
```java
/**
 * 将 Token 均匀分配给各个线程
 * 规则：
 * 1. 每个线程至少分配 baseTokens = tokenCount / threadCount 个 Token
 * 2. 多余的 Token (tokenCount % threadCount) 均匀分配给前面的线程
 *
 * 示例：14 个 Token 分配给 2 个线程
 * - baseTokens = 14 / 2 = 7
 * - extraTokens = 14 % 2 = 0
 * - 结果：[7, 7]
 *
 * 示例：20 个 Token 分配给 3 个线程
 * s = 20 / 3 = 6
 * - extraTokens = 20 % 3 = 2
 * - 结果：[7, 7, 6]（前2个线程多分配1个）
 */
private List<Queue<ApiToken>> distributeTokens(List<ApiToken> tokens, int threadCount) {
    List<Queue<ApiToken>> result = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
        result.add(new LinkedList<>());
    }

    int baseTokens = tokens.size() / threadCount;
    int extraTokens = tokens.size() % threadCount;

    int tokenIndex = 0;
    for (int i = 0; i < threadCount; i++) {
        // 前 extraTokens 个线程多分配 1 个 Token
        int tokensForThisThread = baseTokens + (i < extraTokens ? 1 : 0);

        for (int j = 0; j < tokensForThisThread; j+         result.get(i).offer(tokens.get(tokenIndex++));
        }

        log.info("线程 {} 分配到 {} 个 Token", i, tokensForThisThread);
    }

    return result;
}
```

---

### 阶段三：多线程同步核心逻辑

#### 任务 3.1：添加配置参数
**文件**：`backend/src/main/resources/application-dev.yml`

```yaml
stock:
  sync:
    # 每个线程需要的 Token 数量
    tokens-per-thread: 6
    # 最大线程数
    max-threads: 10
    # 失败最大重试次数
    max-retry-count: 3
    # 线程池队列大小
    queue-size: 1000
```

**配置类**：`backend/src/main/java/com/base/stock/config/StockSyncConfig.java`

```java
@Configuration
@ConfigurationProperties(prefix = "stock.sync")
@Data
public class StockSyncConfig {
    private int tokensPerThread = 6;
    private int maxThreads = 10;
    private int maxRetryCount = 3;
    private int queueSize = 1000;
}
```

#### 任务 3.2：创建同步结果类
**文件**：`backend/src/main/java/com/base/stock/dto/SyncResult.java`

```java
@Data
public class SyncResult {
    private int successCount;
    private int failCount;
    private List<String> failedStocks;
}
```

#### 任务 3.3：创建同步任务类
**文件**：`backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`（内部类）

```java
private class SyncTask implements Callable<SyncResult> {
    private final Queue<ApiToken> tokenQueue;
    private final List<StockInfo> stocks;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String region;

    public SyncTask(Queue<ApiToken> tokenQueue, List<StockInfo> stocks,
                   LocalDate startDate, LocalDate endDate, String region) {
        this.tokenQueue = tokenQueue;
        this.stocks = stocks;
        this.startDate = startDate;
        this.endDate = endDate;
        this.region = region;
    }

    @Override
    public SyncResult call() {
        SyncResult result = new SyncResult();
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setFailedStocks(new ArrayList<>());

        log.info("线程 {} 开始处理 {} 只股票，持有 {} 个 Token",
                Thread.currentThread().getName(), stocks.size(), tokenQueue.size());

        for (StockInfo stock : stocks) {
            try {
                // 获取 Token
                ApiToken token = tokenQueue.poll();
                if (token == null) {
                    log.warn("线程 {} Token 已耗尽，跳过股票 {}",
                            Thread.currentThread().getName(), stock.getStockCode());
                    recordFailure(stock.getStockCode(), startDate, endDate, "Token不可用");
                    result.setFailCount(result.getFailCount() + 1);
                    result.getFailedStocks().add(stock.getStockCode());
                    continue;
                }

                // 同步数据
                syncSingleStock(stock, token, startDate, endDate, region);

                // 成功后放回队列
                tokenQueue.offer(token);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                log.error("同步股票失败: {}", stock.getStockCode(), e);
                recordFailure(stock.getStockCode(), startDate, endDate, e.getMessage());
                result.setFailCount(result.getFailCount() + 1);
                result.getFailedStocks().add(stock.getStockCode());
                // Token 失败不放回队列
            }
        }

        log.info("线程 {} 处理完成，成功：{}，失败：{}",
                Thread.currentThread().getName(), result.getSuccessCount(), result.getFailCount());

        return result;
    }
}
```

#### 任务 3.4：重构批量同步方法
**文件**：`backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`

**重构 `batchSyncAllKlineData` 方法**：

```java
@Override
public int batchSyncAllKlineData(String market, LocalDate startDate, LocalDate endDate) {
    log.info("开始批量同步K线数据，市场：{}，日期范围：{}rket, startDate, endDate);

    // 1. 查询股票列表
    List<StockInfo> stockList = queryStockList(market);
    if (stockList.isEmpty()) {
        log.warn("没有需要同步的股票");
        return 0;
    }

    // 2. 查询可用 Token
    List<ApiToken> availableTokens = tokenManagerService.getAvailableTokens("itick");
    if (availableTokens.isEmpty()) {
        log.error("没有可用的 Token");
        throw new RuntimeException("没有可用的 Token");
    }

    // 3. 计算线程数
    int threadCount = calculateThreadCount(availableTokens.size());
    log.info("可用 Token 数量：{}，创建线程数：{}", availableTokens.size(), threadCount);

    // 4. 创建线程池
    ExecutorService executor = createThreadPool(threadCount);

    try {
        // 5. 按市场分组
        Map<String, List<StockInfo>> marketGroups = stockList.stream()
                .collect(Collectors.groupingBy(StockInfo::getMarket));

        int totalCount = 0;

        // 6. 对每个市场执行多线程同步
        for (Map.Entry<String, List<StockInfo>> entry : marketGroups.entrySet()) {
            String region = entry.getKey();
            List<StockInfo> stocks = entry.getValue();

            log.info("开始同步市场 {}，股票数量：{}", region, stocks.size());

            // 分配 Token（均匀分配，确保每个线程至少6个）
            List<Queue<ApiToken>> tokenQueues = distributeTokens(availableTokens, threadCount);

            // 分配股票
            List<List<StockInfo>> stockGroups = distributeStocks(stocks, threadCount);

            // 提交任务
            List<Future<SyncResult>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                SyncTask task = new SyncTask(tokenQueues.get(i), stockGroups.get(i),
                                            startDate, endDate, region);
                futures.add(executor.submit(task));
            }

            // 等待完成并汇总结果
            for (Future<SyncResult> future : futures) {
                try {
                    SyncResult result = future.get();
                    totalCount += result.getSuccessCount();
                    log.info("线程完成，成功：{}，失败：{}",
                            result.getSuccessCount(), result.getFailCount());
                } catch (Exception e) {
                    log.error("线程执行异常", e);
                }
            }
        }

        log.info("批量同步完成，总计同步：{} 条", totalCount);
        return totalCount;

    } finally {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

#### 任务 3.5：添加辅助方法

```java
/**
 * 计算线程数
 * 规则：线程数 = 可用Token数 / 6，最小1，最大10
 */
private int calculateThreadCount(int availableTokenCount) {
    int threadCount = availableTokenCount / stockSyncConfig.getTokensPerThread();
    return Math.max(1, Math.min(threadCount, stockSyncConfig.getMaxThreads()));
}

/**
 * 创建线程池
 */
private ExecutorService createThreadPool(int threadCount) {
    ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("stock-sync-pool-%d")
            .build();

    return new ThreadPoolExecutor(
            threadCount,
            threadCount,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(stockSyncConfig.getQueueSize()),
            threadFactory,
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
}

/**
 * 分配股票到线程
 * 采用轮询方式均匀分配
 */
private List<List<StockInfo>> distributeStocks(List<StockInfo> stocks, int threadCount) {
    List<List<StockInfo>> result = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
        result.add(new ArrayList<>());
    }

    for (int i = 0; i < stocks.size(); i++) {
        result.get(i % threadCount).add(stocks.get(i));
    }

    return result;
}

/**
 * 同步单只股票
 */
private void syncSingleStock(StockInfo stock, ApiToken token,
                             LocalDate startDate, LocalDate endDate, String region) {
    // 调用现有的同步逻辑
    // ...
}

/**
 * 记录失败
 */
private void recordFailure(String stockCode, LocalDate startDate,
                          LocalDate endDate, String reason) {
    try {
        syncFailureService.recordFailure(stockCode, startDate, endDate, reason);
    } catch (Exception e) {
        log.error("记录失败信息异常", e);
    }
}
```

---

### 阶段四：补拉功能

#### 任务 4.1：实现补拉服务方法
**文件**：`backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`

```java
@Override
public int retryFailedSync(String stockCode, int maxRetryCount) {
    log.info("开始补拉失败数据，stockCode：{}，maxRetryCount：{}", stockCode, maxRetryCount);

    // 1. 查询待重试的失败记录
    List<SyncFailure> failures = syncFailureService.listPendingRetry(stockCode, maxRetryCount);
    if (failures.isEmpty()) {
        log.info("没有需要补拉的失败记录");
        return 0;
    }

    // 2. 按股票代码去重
    Map<String, SyncFailure> stockMap = new HashMap<>();
    for (SyncFailure failure : failures) {
        stockMap.putIfAbsent(failure.getStockCode(), failure);
    }

    // 3. 查询股票信息
    List<String> stockCodes = new ArrayList<>(stockMap.keySet());
    List<StockInfo> stocks = stockInfoMapper.selectList(
            new LambdaQueryWrapper<StockInfo>()
                    .in(StockInfo::getStockCode, stockCodes)
                    .eq(StockInfo::getDeleted, 0)
    );

    // 4. 执行多线程同步（复用批量同步逻辑）
    List<ApiToken> availableTokens = tokenManagerService.getAvailableTokens("itick");
    int threadCount = calculateThreadCount(availableTokens.size());
    ExecutorService executor = createThreadPool(threadCount);

    try {
        List<Queue<ApiToken>> tokenQueues = distributeTokens(availableTokens, threadCount);
        List<List<StockInfo>> stockGroups = distributeStocks(stocks, threadCount);

        List<Future<SyncResult>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            SyncFailure failure = stockMap.get(stockGroups.get(i).get(0).getStockCode());
            SyncTask task = new SyncTask(tokenQu stockGroups.get(i),
                    failure.getStartDate(), failure.getEndDate(),
                    stockGroups.get(i).get(0).getMarket());
            futures.add(executor.submit(task));
        }

        int successCount = 0;
        for (Future<SyncResult> future : futures) {
            SyncResult result = future.get();
            successCount += result.getSuccessCount();

            // 更新失败记录状态
            for (String code : result.getFailedStocks()) {
                SyncFailure failure = stockMap.get(code);
                ryCount = failure.getRetryCount() + 1;
                int newStatus = newt >= maxRetryCount ? 2 : 0;
                syncFailureService.updateStatus(failure.getId(), newStatus, newRetryCount);
            }
        }

        log.info("补拉完成，成功：{}", successCount);
        return successCount;

    } catch (Exception e) {
        log.error("补拉失败", e);
        throw new RuntimeException("补拉失败", e);
    } finally {
        executor.shutdown();
    }
}
```

#### 任务 4.2：添加 Controller 接口
**文件**：`backend/src/main/java/com/base/stock/controller/StockSyncController.java`

```java
/**
 * 补拉失败数据
 */
@ApiOperation("补拉失败数据")
@PostMapping("/retry-failed")
thorize("hasAuthority('stock:sync:retry')")
public Result<Integer> retryFailedSync(
        @RequestParam(required = false) String stockCode,
        @RequestParam(defaultValue = "3") int maxRetryCount) {

    int count = stockSyncService.retryFailedSync(stockCode, maxRetryCount);
    return Result.success(count);
}

/**
 * 查询失败记录列表
 */
@ApiOperation("查询失败记录列表")
@GetMapping("/failure/list")
@PreAuthorize("hasAuthority('stock:sync:list')")
public Result<Page<SyncFailure>> listFailures(
        @RequestParam(required false) String stockCode,
        @RequestParam(required = false) Integer status,
        @RequestParam(defaultValue = "1"age,
        @RequestParam(defaultValue = "20") int size) {

    Page<SyncFailure> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<SyncFailure> wrapper = new LambdaQueryWrapper<>();

    if (stockCode != null && !stockCode.isEmpty()) {
        wrapper.eq(SyncFailure::getStockCode, stockCode);
    }
    if (status != null) {
        wrapper.eq(SyncFailure::getStatus, status);
    }

    wrapper.orderByDesc(SyncFailure::getCreateTime);
    Page<SyncFailure> result = syncFailureService.page(pageParam, wrapper);

    return Result.success(result);
}
```

---

### 阶段五：测试与优化

#### 任务 5.1：单元测试

**测试类**：`backendva/com/base/stock/service/StockSyncServiceTest.java`

**测试用例**：
1. `testCalculateThreadCount()` - 测试线程数计算
   - 5个Token → 1线程
   - 12个Token → 2线程
   - 100个Token → 10线程

2. `testDistributeTokens()` - 测试 Token 分配
   - 12个Token分配给2个线程 → [6, 6]
   - 14个Token分配给2个线程 → [7, 7]
   - 20个Token分配给3个线程 → [7, 7, 6]

3. `testDistributeStocks()` - 测试股票分配
   - 10只股票分配给3个线程 → [4, 3, 3]

4. `testRecordFailure()` - 测试失败记录

5. `testRetryFailedSync()` - 测试补拉逻辑

#### 任务 5.2：集成测试

**测试场景**：
1. 准备 300 只股票，12 个 Token
2. 执行批量同步，验证创建 2 个线程，每个线程持有 6 个 Token
3. 检查同步结果和失败记录
4. 手动作废部分 Token，验证失败记录
5. 执行补拉，验证状态更新

#### 任务 5.3：性能测试

**对比测试**：
- 单线程同步 1000 只股票的耗时
- 多线程（2线程，12个Token）同步 1000 只股票的耗时
- 多线程（5线程，30个Token）同步 1000 只股票的耗时

**预期结果**：
- 2 线程提升 50%+
- 5 线程提升 100%+

---

## 三、开发注意事项

### 3.1 代码规范
- 遵循阿里巴巴编码规范
- 所有方法添加中文注释
- 异常处理要完善

### 3.2 日志规范
- 关键节点记录 INFO 日志（线程启动、任务完成、Token分配）
- 异常记录 ERROR 日志
- 调试信息记录 DEBUG 日志

### 3.3 事务处理
- 失败记录插入使用独立事务（避免影响主流程）
- 补拉时状态更新使用事务

### 3.4 资源释放
- 线程池必须正确关闭
- Token 使用后必须放回队列或移除

### 3.5 Token 分配验证
- 确保每个线程至少持有 6 个 Token（除非总Token数不足6）
- 多余 Token 必须均匀分配
- 添加日志记录每个线程分配到的 Token 数量

---

## 四、部署清单

### 4.1 数据库变更
- [ ] 执行 `sync_failure_sche` 创建表

### 4.2 配置变更
- [ ] 添加 `stock.sync` 配置项到 `application-dev.yml`

### 4.3 代码变更
- [ ] 新增文件：SyncFailure.java、SyncFailureMapper.java、SyncFailureService.java 等
- [ ] 修改文件：StockSyncServiceImpl.java、TokenManagerService.java、StockSyncController.java

### 4.4 权限配置
- [ ] 添加 `stock:sync:retry` 权限
- [ ] 添加 `stock:sync:list` 权限

---

## 五、回归测试清单

- [ ] 原有单个同步功能正常
- [ ] 原有批量同步功能正常（单线程模式）
- [ ] Token  ] 定时任务正常执行

---

## 六、上线计划

### 6.1 灰度发布
1. 先在测试环境验证
2. 生产环境先使用单线程模式（配置 `tokens-per-thread: 100`）
3. 观察 1 天无异常后，调整为多线程模式

### 6.2 监控指标
- 同步耗时
- 失败率
- Token 使用率
- 线程池队列长度
- 每个线程分配的 Token 数量

### 6.3 回滚方案
- 如果出现严重问题，调整配置 `tokens-per-thread: 100` 回退到单线程模式
- 不需要回滚代码
