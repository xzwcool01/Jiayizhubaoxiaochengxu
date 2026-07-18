# Phase 0：环境搭建 — 详细任务拆解

> 更新日期：2026-07-17
> 前置条件：JDK 17 + Maven + Node.js 24 + Git + HBuilderX 已安装

---

## 一、准备工作（你需提供）

开始前请提供以下信息，填入后即可启动：

| # | 项目 | 你需要提供 | 用途 |
|---|------|-----------|------|
| 1 | 微信小程序 AppID | 微信公众平台 → 开发管理 → 复制 AppID | uni-app manifest.json 配置 |
| 2 | MySQL root 密码 | 你本地 MySQL 的 root 密码（或新建用户） | application.yml 数据源配置 |
| 3 | MySQL 端口 | 默认 3306，如不同请告知 | 同上 |
| 4 | 数据库名 | 默认 `jiayi_db`，如需改名请告知 | 建库语句 |

如信息已就绪，直接告诉我"开始"即可。

---

## 二、项目结构

```
E:\AiProject\Jiayizhubaoxiaochengxu\
├── docs\                        ← 需求文档
├── jiayi-api\                   ← 后端 API（Spring Boot 3.2 + JDK 17）
├── jiayi-miniapp\               ← 小程序前端（uni-app Vue3）
└── jiayi-admin\                 ← 后台管理（Vue3 + Element Plus）
```

---

## 三、任务清单（共16项）

### 3.1 jiayi-api（Spring Boot 后端）— 6项

| 序号 | 任务 | 产出 | 可验证 |
|------|------|------|--------|
| 0.1.1 | Maven 初始化项目 | pom.xml（Spring Boot 3.2, MyBatis-Plus, Spring Security, JWT, MySQL, Lombok, Swagger） | 文件存在 |
| 0.1.2 | 配置 application.yml | 数据源、端口8080、MyBatis-Plus、日志 | 文件存在 |
| 0.1.3 | 创建包结构 | com.jiayi.{config,controller,service,mapper,entity,dto,common} | 目录完整 |
| 0.1.4 | 统一响应 R<T> | R.java（code/message/data）+ 成功/失败静态方法 | 编译通过 |
| 0.1.5 | 全局异常处理器 | GlobalExceptionHandler.java | 编译通过 |
| 0.1.6 | 启动类 + 测试接口 | JiayiApplication.java + GET /api/health → {"code":200,"message":"Hello 嘉怡珠宝"} | ✅ 启动后 curl 验证 |

### 3.2 jiayi-miniapp（uni-app 小程序）— 6项

| 序号 | 任务 | 产出 | 可验证 |
|------|------|------|--------|
| 0.2.1 | 创建 uni-app 项目 | jiayi-miniapp 目录（vue3 + vite 模板） | 目录存在 |
| 0.2.2 | 配置 manifest.json | 标题"嘉怡珠宝"、AppID、微信配置 | 文件存在 |
| 0.2.3 | 配置全局主题色 | uni.scss：$primary: #C8A27A, $bg: #F8F6F3 | 文件存在 |
| 0.2.4 | 配置底部 Tab 导航 | pages.json 中 5 个 tab（首页/分类/发现/会员/我的） | 文件存在 |
| 0.2.5 | 创建5个空壳页面 | pages/index/category/discovery/member/my 各含占位内容 | ✅ 微信开发者工具看到5个Tab可切换 |
| 0.2.6 | 封装请求工具 | api/request.ts（baseURL: http://localhost:8080/api） | 文件存在 |

### 3.3 jiayi-admin（Vue3 后台管理）— 6项

| 序号 | 任务 | 产出 | 可验证 |
|------|------|------|--------|
| 0.3.1 | Vite 创建 Vue3 + TS 项目 | jiayi-admin 目录（vue-router, pinia, element-plus, axios, echarts） | 目录存在，npm install 无报错 |
| 0.3.2 | 配置项目基础文件 | vite.config.ts（代理/api→8080）、tsconfig.json、.env.development | 文件存在 |
| 0.3.3 | 搭建 Layout 框架 | 左侧菜单 + 顶部Header + 内容区（Element Plus Container） | ✅ 页面显示布局 |
| 0.3.4 | 配置 Vue Router | /login, /（仪表盘）, /product, /order, /user, /content, /settings | 文件存在 |
| 0.3.5 | 配置 Pinia + axios 封装 | store/user.ts、utils/axios.ts（拦截器 + JWT注入） | 文件存在 |
| 0.3.6 | 登录页 UI 占位 | src/views/login/index.vue（账号密码表单） | ✅ 访问 http://localhost:5173 显示登录页 |

### 3.4 数据库 — 2项

| 序号 | 任务 | 产出 | 可验证 |
|------|------|------|--------|
| 0.4.1 | 创建数据库 jiayi_db | MySQL 数据库 | ✅ SHOW DATABASES 可见 |
| 0.4.2 | 建表 SQL + MyBatis-Plus 实体 | 6表：sys_user, ums_user, ums_member_level, pms_category, pms_product, ums_member_signin | ✅ 启动项目后表自动生成 |

### 3.5 总体验收 — 3项

| 序号 | 验证项 | 命令/操作 | 预期结果 |
|------|--------|----------|---------|
| 0.5.1 | API 启动 | http://localhost:8080/api/health | 200 JSON |
| 0.5.2 | 小程序预览 | 微信开发者工具导入 jiayi-miniapp | 5个Tab可切换 |
| 0.5.3 | 后台访问 | http://localhost:5173 | 显示登录页 |

---

## 四、断点恢复指引

如果中途中断，重新启动时：

### 检查当前进度
查看 docs\Phase0-进度.md（第一次开始后自动创建），或检查目录：

```powershell
# 检查哪些项目已创建
Test-Path E:\AiProject\Jiayizhubaoxiaochengxu\jiayi-api
Test-Path E:\AiProject\Jiayizhubaoxiaochengxu\jiayi-miniapp
Test-Path E:\AiProject\Jiayizhubaoxiaochengxu\jiayi-admin
```

### 从断点继续
根据检查结果告知我，我会从对应序号继续执行。

---

## 五、Phase 0 完成后进入 Phase 1

Phase 1 开始前会生成 Phase1-详细任务.md，延续同样格式。
