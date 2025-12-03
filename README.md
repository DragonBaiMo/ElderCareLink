# 基于 SpringBoot 的关爱空巢老人管理系统

## 项目简介
本系统按照 PDR/PRD/IPO 要求实现，提供老人信息、健康记录、志愿者探访、公告与统计等核心功能，采用 SpringBoot + MySQL + Vue3 + Element Plus 的单体架构，适合本科毕业设计演示。

## 运行环境
- JDK 17
- Maven 3.8+
- Node.js 18+
- MySQL 5.7/8.0（数据库名 eldercare，账号/密码 root/root 可按需调整）

## 快速启动
1. 创建数据库并导入建表脚本：
   ```sql
   source schema.sql;
   ```
2. 启动后端：
   ```bash
   cd backend
   mvn spring-boot:run
   ```
3. 启动前端：
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
4. 浏览器访问 `http://localhost:5173`，使用演示账号登录：
   - 管理员：admin / 123456
   - 志愿者：vol01 / 123456

## 主要功能
- 登录认证与角色控制
- 老人信息的新增、查询、修改、注销
- 健康记录录入与按老人查询
- 志愿者探访记录的录入与查询
- 公告发布与状态管理
- 数据字典维护（居住类型、健康等级等）
- 仪表盘统计：老人数量、健康记录数、探访记录数

## 目录结构
- backend：SpringBoot 后端源码
- frontend：Vue3 前端项目
- schema.sql：数据库建表脚本
- docs：答辩材料与架构说明

## 接口说明
后端接口统一以 `/api` 开头，登录接口 `/api/auth/login` 返回 Bearer Token，后续请求需在 `Authorization` 头携带 `Bearer <token>`。

## 部署建议
- 正式演示前先准备好样例数据，可通过 DataInitializer 自动生成或在前端表单录入。
- 如需修改数据库连接，更新 `backend/src/main/resources/application.yml`。
- 若需要将前端打包为静态资源，可执行 `npm run build` 并将 `frontend/dist` 发布至任意静态服务器或整合到 SpringBoot 静态目录。
