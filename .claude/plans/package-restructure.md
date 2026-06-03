# 包结构重构方案

## 目标结构

从 `com.base.{module}.{layer}` 改为 `com.base.{layer}.{module}`

## 映射规则

### 标准层级（controller/service/mapper/entity/dto）

| 旧包 | 新包 |
|------|------|
| com.base.ai.controller | com.base.controller.ai |
| com.base.ai.service(.impl) | com.base.service.ai(.impl) |
| com.base.ai.mapper | com.base.mapper.ai |
| com.base.ai.entity | com.base.entity.ai |
| com.base.ai.dto | com.base.dto.ai |
| com.base.ai.config | com.base.config.ai |
| com.base.approval.controller | com.base.controller.approval |
| com.base.approval.service(.impl) | com.base.service.approval(.impl) |
| com.base.approval.mapper | com.base.mapper.approval |
| com.base.approval.entity | com.base.entity.approval |
| com.base.approval.dto | com.base.dto.approval |
| com.base.approval.enums | com.base.enums.approval |
| com.base.approval.task | com.base.task.approval |
| com.base.message.controller | com.base.controller.message |
| com.base.message.service(.impl) | com.base.service.message(.impl) |
| com.base.message.mapper | com.base.mapper.message |
| com.base.message.entity | com.base.entity.message |
| com.base.message.dto | com.base.dto.message |
| com.base.message.channel | com.base.channel.message |
| com.base.message.content | com.base.content.message |
| com.base.message.constant | com.base.constant.message |
| com.base.message.task | com.base.task.message |
| com.base.stock.controller | com.base.controller.stock |
| com.base.stock.service(.impl) | com.base.service.stock(.impl) |
| com.base.stock.mapper | com.base.mapper.stock |
| com.base.stock.entity | com.base.entity.stock |
| com.base.stock.dto | com.base.dto.stock |
| com.base.stock.config | com.base.config.stock |
| com.base.stock.enums | com.base.enums.stock |
| com.base.stock.client(.impl) | com.base.client.stock(.impl) |
| com.base.stock.factory(.impl) | com.base.factory.stock(.impl) |
| com.base.stock.engine | com.base.engine.stock |
| com.base.stock.http | com.base.http.stock |
| com.base.stock.strategy(.impl) | com.base.strategy.stock(.impl) |
| com.base.stock.task | com.base.task.stock |
| com.base.stock.util | com.base.util.stock |
| com.base.system.controller | com.base.controller.system |
| com.base.system.service(.impl) | com.base.service.system(.impl) |
| com.base.system.mapper | com.base.mapper.system |
| com.base.system.entity | com.base.entity.system |
| com.base.system.dto.* | com.base.dto.system.* |
| com.base.system.common | com.base.common.result (Result/ResultCode) |
| com.base.system.annotation | com.base.annotation.system |
| com.base.system.aspect | com.base.aspect.system |
| com.base.system.constant | com.base.constant.system |
| com.base.system.enums | com.base.enums.system |
| com.base.system.exception | com.base.exception.system |
| com.base.system.handler | com.base.handler.system |
| com.base.system.util | com.base.util.system |
| com.base.system.export.controller | com.base.controller.export |
| com.base.system.export.service(.impl) | com.base.service.export(.impl) |
| com.base.system.export.entity | com.base.entity.export |
| com.base.system.export.dto.* | com.base.dto.export.* |
| com.base.weather.controller | com.base.controller.weather |
| com.base.weather.service(.impl) | com.base.service.weather(.impl) |
| com.base.weather.dto | com.base.dto.weather |
| com.base.weather.config | com.base.config.weather |
| com.base.weather.factory | com.base.factory.weather |
| com.base.weather.provider(.impl) | com.base.provider.weather(.impl) |
| com.base.workflow.controller | com.base.controller.workflow |
| com.base.workflow.service(.impl) | com.base.service.workflow(.impl) |
| com.base.workflow.mapper | com.base.mapper.workflow |
| com.base.workflow.entity | com.base.entity.workflow |
| com.base.workflow.dto | com.base.dto.workflow |
| com.base.workflow.enums | com.base.enums.workflow |
| com.base.workflow.handler | com.base.handler.workflow |
| com.base.workflow.listener | com.base.listener.workflow |
| com.base.open.controller | com.base.controller.open |
| com.base.open.service(.impl) | com.base.service.open(.impl) |
| com.base.open.config | com.base.config.open |
| com.base.open.dto | com.base.dto.open |
| com.base.open.context | com.base.context.open |
| com.base.open.interceptor | com.base.interceptor.open |

### 保持不动的包

| 包 | 原因 |
|---|------|
| com.base.common.* | 已经是公共基础设施，结构合理 |
| com.base.config | 全局配置，不属于任何业务模块 |
| com.base.security | 全局安全，不属于任何业务模块 |
| com.base.entity (顶层) | 已有文件保留 |
| com.base.handler (顶层) | 全局异常处理器 |
| com.base.util (顶层) | 全局工具类 |

## 实施步骤

1. 写 bash 脚本批量移动文件并修改 package 声明
2. 批量替换所有 Java 文件中的 import 语句
3. 更新 Mapper XML 中的 namespace
4. 更新 MybatisPlusConfig 中的 @MapperScan
5. 更新 BaseSystemApplication 中的 @ComponentScan
6. 编译验证
