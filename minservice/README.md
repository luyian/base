# 微信小程序 - 基金估值系统

基于 Base 系统后端 API 开发的微信小程序，提供基金估值、自选股管理等功能。

## 功能列表

- [x] 微信授权登录
- [x] 基金列表展示
- [x] 基金估值详情
- [x] 持仓股票展示
- [x] 自选股管理
- [x] 用户 profile

## 项目结构

```
minservice/
├── api/                    # API 接口
│   ├── auth.js            # 认证相关
│   ├── fund.js            # 基金相关
│   └── watchlist.js       # 自选股相关
├── pages/                  # 页面
│   ├── login/             # 登录页
│   ├── index/             # 首页
│   ├── fund/              # 基金页
│   ├── watchlist/         # 自选股页
│   └── profile/           # 我的页
├── utils/                  # 工具
│   └── request.js         # HTTP 请求封装
├── assets/                 # 静态资源
├── app.js                  # 应用入口
├── app.json                # 应用配置
└── project.config.json     # 项目配置
```

## 使用说明

1. 修改 `app.js` 中的 `baseUrl` 为实际后端地址
2. 在微信公众平台注册小程序，获取 AppID
3. 修改 `project.config.json` 中的 `appid`
4. 使用微信开发者工具打开项目

## API 对接

小程序需要后端提供以下接口：

- `POST /api/auth/wx-login` - 微信登录
- `GET /api/auth/info` - 获取用户信息
- `GET /api/stock/fund/list` - 获取基金列表
- `GET /api/stock/fund/{id}` - 获取基金详情
- `POST /api/stock/fund/refresh-all` - 刷新所有估值

## 开发

```bash
# 安装依赖（如需）
npm install

# 微信开发者工具打开 minservice 目录
```

## 注意事项

- 使用前请确保后端 API 已配置跨域支持
- 小程序需要在微信公众平台配置服务器域名