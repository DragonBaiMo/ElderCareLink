# 系统架构说明

## 技术栈
- 后端：Java 17 + SpringBoot 3.2 + Spring Data JPA + Validation + JWT
- 前端：Vue3 + Vite + Element Plus + Axios
- 数据库：MySQL 5.7/8.0，字符集 utf8mb4

## 分层设计
- **Controller 层**：接收请求、做基础校验、调用业务逻辑，统一返回 `ApiResponse`。
- **Service 层**：封装业务处理，如登录校验、密码修改等（示例：`UserService`）。
- **Repository 层**：基于 Spring Data JPA 的数据访问层，负责实体 CRUD。
- **Entity 层**：与数据表一一对应的持久化实体，如 `User`、`Elder`、`HealthRecord`。
- **Config 层**：安全配置、公共 Bean、数据初始化等。

## 核心流程
1. 用户登录：`/api/auth/login` 校验账号密码，生成 JWT 返回前端。
2. 前端持有 token 访问业务接口，`TokenFilter` 负责解析并注入用户信息。
3. 业务接口根据角色/输入完成增删改查，统一通过 `GlobalExceptionHandler` 捕获异常并返回中文提示。
4. 统计接口直接汇总数据库数据，供仪表盘展示。

## 部署关系
- 单体部署：后端 jar 运行在 8080 端口，前端 Vite 开发端口 5173，生产可将前端静态资源交由任意 Web 服务器托管。
- 数据库独立 MySQL 实例，连接信息在 `application.yml` 中配置。

## 安全与校验
- 使用 BCrypt 加密存储密码。
- 使用 JWT 维护登录态，Header 传递 `Authorization: Bearer <token>`。
- 启用 Bean Validation 对输入参数进行中文提示的校验。
