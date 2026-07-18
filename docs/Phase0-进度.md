# Phase 0 进度记录

> 更新：2026-07-17 15:00

## 完成状态

| 模块 | 任务 | 状态 | 备注 |
|------|------|------|------|
| 数据库 | 0.4.1 创建 jiayi_db | ✅ | 6表已建：sys_user, ums_user, ums_member_level, pms_category, pms_product, ums_member_signin |
| 数据库 | 0.4.2 建表 SQL + 实体 | ✅ | 6个实体类 + MyMetaObjectHandler |
| jiayi-api | 0.1.1 pom.xml | ✅ | Spring Boot 3.2.5, MyBatis-Plus, JJWT, SpringDoc |
| jiayi-api | 0.1.2 application.yml | ✅ | 数据源 localhost:3306/jiayi_db |
| jiayi-api | 0.1.3 包结构 | ✅ | config/controller/service/mapper/entity/dto/common |
| jiayi-api | 0.1.4 R.java | ✅ | 统一响应类 |
| jiayi-api | 0.1.5 全局异常处理器 | ✅ | BusinessException + 参数校验 + 系统异常 |
| jiayi-api | 0.1.6 启动类 + /api/health | ✅ | 编译通过，启动成功（3.59秒） |
| jiayi-api | 0.1.6 SecurityConfig | ✅ | Spring Security 基础配置 |
| jiayi-miniapp | 0.2.1 创建项目 | ✅ | vue3 + vite 模板 |
| jiayi-miniapp | 0.2.2 manifest.json | ✅ | AppID: wxff03445f8c883241 |
| jiayi-miniapp | 0.2.3 uni.scss 主题色 | ✅ | $primary: #C8A27A |
| jiayi-miniapp | 0.2.4 底部Tab导航 | ✅ | 5个Tab |
| jiayi-miniapp | 0.2.5 5个空壳页面 | ✅ | 首页/分类/发现/会员/我的 |
| jiayi-miniapp | 0.2.6 request.ts 请求工具 | ✅ | baseURL: localhost:8080/api |
| jiayi-miniapp | Tab 图标占位 | ✅ | 金色/灰色圆形占位 |
| jiayi-miniapp | 首页 (index) - Stitch设计 | ✅ | banner轮播, 图标网格, 限时抢购, 瀑布流, AI浮窗 |
| jiayi-miniapp | 分类 (category) - Stitch设计 | ✅ | 左侧分类栏, 右侧商品网格, 搜索框, FAB筛选 |
| jiayi-miniapp | 发现 (discovery) - Stitch设计 | ✅ | 3Tab (AI穿搭秀/珠宝指南/达人晒单), 瀑布流 |
| jiayi-miniapp | 购物车 (cart) - Stitch设计 | ✅ | 购物车列表, 推荐单品, 粘性结算栏 |
| jiayi-miniapp | 我的 (my) - Stitch设计 | ✅ | 个人资料, 积分卡片, 2x4菜单网格 |
| jiayi-miniapp | 限时活动 (activity/list) - Stitch设计 | ✅ | 活动卡片, 进行中/即将开始/长期状态 |
| jiayi-miniapp | 新品上市 (activity/new) - Stitch设计 | ✅ | 全屏hero, 2列商品网格, AI穿戴卡片 |
| jiayi-miniapp | 限时拍卖 (auction) - Stitch设计 | ✅ | 拍品卡片, 实时出价, 倒计时 |
| jiayi-miniapp | AI穿戴 (ai-wear) - Stitch设计 | ✅ | 上传照片/预览效果, 风格选择 |
| jiayi-miniapp | 积分商城 (points) - Stitch设计 | ✅ | 积分展示, 会员等级, 积分动态 |
| jiayi-miniapp | 商品详情 (product/detail) - Stitch设计 | ✅ | 图库, 尺码选择, 规格, 评价, 底部操作栏 |
| jiayi-miniapp | 会员中心 (member) - Stitch设计 | ✅ | 等级卡, 成长进度, 权益网格, 最近订单 |
| jiayi-admin | 0.3.1 Vite + Vue3 项目 | ✅ | element-plus, pinia, axios, echarts |
| jiayi-admin | 0.3.2 vite.config.ts + env | ✅ | 代理 /api → 8080 |
| jiayi-admin | 0.3.3 Layout 框架 | ✅ | 左侧菜单 + 顶栏 + 内容区 |
| jiayi-admin | 0.3.4 Vue Router | ✅ | /login + 6个模块路由 |
| jiayi-admin | 0.3.5 Pinia + axios 封装 | ✅ | JWT拦截器 + 401处理 |
| jiayi-admin | 0.3.6 登录页 | ✅ | 表单 + 模拟登录跳转 |

## 已知问题

1. **`mvn package` 打包失败** — 系统虚拟内存不足（页面文件太小）。解决方案：增大虚拟内存 或 用 `mvn spring-boot:run` 直接运行。
2. **Tab 图标为占位圆形** — 后续需要替换为正式设计图标。
3. **微信开发者工具未测试** — 用户需要打开验证所有 11 个页面的渲染效果。
4. **Stitch设计中的字体** — 使用了 Google Fonts (Noto Serif SC, Inter) 和 Material Symbols Outlined，微信小程序环境需验证兼容性。

## 启动方式

```powershell
# 启动后端 API（在 jiayi-api 目录）
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.1"
$env:MAVEN_HOME = "D:\ruanjian\apache-maven-3.9.9"
& mvn spring-boot:run

# 启动后台管理（在 jiayi-admin 目录）
npm run dev
# 访问 http://localhost:5173

# 启动小程序（用 HBuilderX 打开 jiayi-miniapp 目录）
# 运行 → 运行到小程序模拟器 → 微信开发者工具
```
