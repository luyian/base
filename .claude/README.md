# .claude 目录结构说明

本目录存放 Claude Code 的项目级配置、文档和工具定义。

```
.claude/
├── CLAUDE.md              # 项目指令（Claude Code 每次对话自动加载）
├── README.md              # 本文件
├── settings.local.json    # 本地权限配置
├── docs/
│   ├── TEMP.md            # 代码变更记录（每次修改业务代码后更新）
│   ├── feishu.md          # 飞书集成文档（OAuth + 消息）
│   ├── oauth.md           # 第三方登录配置
│   ├── open-api.md        # 开放接口文档
│   ├── 使用手册.md         # 系统使用手册
│   ├── workflow-flowable-guide.md  # Flowable 工作流使用手册
│   ├── workflow-development.md     # 工作流开发指南
│   ├── workflow-requirements.md    # 工作流需求文档
│   └── archive/           # 已完成的设计文档归档
│       ├── FEISHU_APPROVAL_DESIGN.md
│       └── weather-multi-source.md
├── plans/
│   └── package-restructure.md     # 待实施：包结构重构方案
└── skills/                # OpenSpec 规格驱动开发 Skill
    ├── openspec-propose.md        # /opsx:propose 生成提案/设计/规格/任务
    ├── openspec-explore.md        # /opsx:explore 探索模式
    ├── openspec-apply-change.md   # /opsx:apply 逐任务实现
    └── openspec-archive-change.md # /opsx:archive 归档
```

## 各目录用途

| 目录 | 用途 | 更新时机 |
|------|------|----------|
| `docs/` | 项目文档和变更记录 | 每次修改业务代码后 |
| `docs/archive/` | 已完成的方案/设计文档 | 功能上线后归档 |
| `plans/` | 待实施或进行中的方案 | 规划阶段创建，完成后归档 |
| `skills/` | Claude Code Skill 定义 | 新增/修改开发流程时 |
