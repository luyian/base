# OCR 模块集成方案（DDD V1.5 架构）

## 一、需求概述

集成 OCR 能力，支持特定场景识别（身份证、发票、银行卡），供应商可配置多个（腾讯云 + 百度云 + 阿里云），支持主备切换和降级。提供前端上传识别页面 + REST API。

## 二、DDD 四层分包结构

```
com.base.ocr/
├── interfaces/                    # 接口层：对外暴露的 API
│   ├── controller/
│   │   └── OcrController.java
│   └── dto/
│       ├── OcrRecognizeRequest.java   # 接口请求参数
│       └── OcrRecognizeResponse.java  # 接口统一响应
├── application/                   # 应用层：编排领域逻辑
│   └── OcrApplicationService.java     # 应用服务（编排 Provider 调用 + 降级）
├── domain/                        # 领域层：核心业务模型
│   ├── model/                     # 值对象（贫血模型）
│   │   ├── IdCardResult.java
│   │   ├── InvoiceResult.java
│   │   └── BankCardResult.java
│   ├── enums/
│   │   └── OcrSceneEnum.java
│   └── service/
│       └── OcrProvider.java       # 领域服务接口（由基础设施层实现）
└── infrastructure/                # 基础设施层：外部依赖实现
    ├── config/
    │   └── OcrSourceConfig.java   # 配置（yml + sys_config）
    ├── factory/
    │   └── OcrProviderFactory.java # 供应商工厂（主/备/降级）
    └── provider/
        ├── TencentOcrProvider.java
        ├── BaiduOcrProvider.java
        └── AliyunOcrProvider.java
```

## 三、各层职责

| 层 | 职责 | 依赖方向 |
|----|------|----------|
| interfaces | 接收 HTTP 请求，参数校验，调用应用层，返回响应 | → application |
| application | 编排业务流程：选择供应商、调用识别、降级处理 | → domain |
| domain | 定义领域模型（值对象）和领域服务接口 | 无外部依赖 |
| infrastructure | 实现领域服务接口，对接外部云厂商 API | → domain |

## 四、核心接口设计

### 4.1 OcrProvider（领域服务接口，定义在 domain 层）

```java
public interface OcrProvider {
    String getName();
    String getDisplayName();
    IdCardResult recognizeIdCard(byte[] imageData, String side);
    InvoiceResult recognizeInvoice(byte[] imageData);
    BankCardResult recognizeBankCard(byte[] imageData);
}
```

### 4.2 OcrApplicationService（应用层）

```java
public class OcrApplicationService {
    // 统一入口：按场景分发 + 主备降级
    Object recognize(OcrSceneEnum scene, byte[] imageData, Map<String, String> params);
}
```

### 4.3 REST API（接口层）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/ocr/id-card` | 身份证识别（参数 side=front/back） |
| POST | `/ocr/invoice` | 发票识别 |
| POST | `/ocr/bank-card` | 银行卡识别 |
| GET | `/ocr/providers` | 查询可用供应商列表 |

### 4.4 配置方案

application-dev.yml:
```yaml
ocr:
  source: tencent
  fallback-enabled: true
  fallback-source: baidu
```

sys_config 表存储 API Key（运行时可改）。

## 五、前端

- `api/ocr.js` — 接口封装
- `views/ocr/index.vue` — OCR 识别页面（Tab 切换场景 + 图片上传 + 结构化结果展示）

## 六、实施任务

- [ ] 1. 创建 DDD 四层包结构 + domain 层（枚举、值对象、OcrProvider 接口）
- [ ] 2. 实现 infrastructure 配置类 OcrSourceConfig
- [ ] 3. 实现 TencentOcrProvider（腾讯云 TC3 签名）
- [ ] 4. 实现 BaiduOcrProvider（百度云 access_token + Redis 缓存）
- [ ] 5. 实现 AliyunOcrProvider（阿里云 V3 签名）
- [ ] 6. 实现 OcrProviderFactory 工厂
- [ ] 7. 实现 application 层 OcrApplicationService
- [ ] 8. 实现 interfaces 层 OcrController + DTO
- [ ] 9. application-dev.yml 添加 ocr 配置
- [ ] 10. 前端 api/ocr.js + views/ocr/index.vue + 菜单权限
