[Uploading README.md…]()
# TaskHub AI 后端服务

## 项目简介

TaskHub AI 后端是一个基于 Spring Boot 3 + Spring Security + JWT 的企业级项目管理系统后端服务，提供项目、任务、缺陷、迭代的完整 RESTful API，支持基于角色的访问控制（RBAC）、JWT 认证与刷新、Redis Token 黑名单、雪花算法分布式 ID 等功能。

## 技术栈

- Java 21
- Spring Boot 4.0.6
- Spring Security 6
- Spring Data JPA (Hibernate)
- MySQL 8.0
- Redis 7.x
- JWT (JJWT)
- Lombok
- Maven

## 核心功能

- 🔐 **用户认证**：注册、登录、JWT 颁发与刷新、登出（Token 黑名单）
- 📁 **项目管理**：创建、编辑、删除、查询，支持项目成员管理（管理员/成员/只读）
- ✅ **任务管理**：任务看板（待办/进行中/已完成），支持状态流转权限控制
- 🐛 **缺陷跟踪**：缺陷创建、编辑、删除，严重程度分级
- 🔄 **迭代管理**：迭代周期管理
- 👤 **个人中心**：用户资料修改、密码变更
- 👑 **超级管理员**：`is_admin` 字段，可查看/操作所有项目
- 🆔 **雪花算法 ID**：19 位分布式唯一 ID，后端自动转换为字符串返回，避免前端精度丢失

## 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x

## 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/your-username/taskhub-ai-backend.git
cd taskhub-ai-backend
```

### 2. 创建数据库

登录 MySQL，执行项目中的数据库脚本（位于 `src/main/resources/db/schema.sql`）：

```text
CREATE DATABASE IF NOT EXISTS `taskhub_ai` DEFAULT CHARACTER SET utf8mb4;
USE `taskhub_ai`;
-- 执行 schema.sql 中的建表语句
```

（可选）执行 `data.sql` 插入初始管理员账号。

### 3. 修改配置文件

复制 `src/main/resources/application-example.yml` 为 `application-dev.yml`，修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskhub_ai?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password:   # 若无密码则留空
jwt:
  secret: your_base64_secret_key_at_least_256bits   # 必须自己生成
  expiration: 3600000          # 1小时
  refresh-expiration: 604800000 # 7天
app:
  super-admin-invite-code: "TH_ADMIN_2026"   # 超级管理员注册邀请码
```

### 4. 编译运行

```bash
mvn clean package
java -jar target/taskhub-ai-1.0.0.jar --spring.profiles.active=dev
```

服务启动后访问：`http://localhost:8080`

## API 文档

（可选）集成 SpringDoc OpenAPI 后，访问 `http://localhost:8080/swagger-ui/index.html` 可查看接口文档。

## 项目结构

```
src/main/java/com/taskhub/ai/
├── common/                 # 通用组件（统一响应、实体基类）
├── config/                 # 配置类（CORS、Security、Jackson、Redis）
├── security/               # JWT 认证过滤器、Token 提供者
├── controller/             # REST 控制器
├── service/                # 业务逻辑接口与实现
├── repository/             # JPA 数据访问层
├── entity/                 # 数据库实体
├── dto/                    # 请求数据传输对象
├── vo/                     # 响应视图对象
├── exception/              # 全局异常处理
└── utils/                  # 工具类（雪花算法、SecurityUtils）
```

## 权限模型

| 角色 | 权限范围 |
|------|----------|
| 全局管理员 (`is_admin=1`) | 查看/编辑/删除所有项目，管理任意项目成员 |
| 项目所有者 (`owner_id`) | 完全控制该项目（删除、编辑、成员管理） |
| 项目管理员 (`role=ADMIN`) | 编辑项目内容、管理成员，不能删除项目 |
| 项目成员 (`role=MEMBER`) | 创建/编辑/删除项目内的任务、缺陷、迭代 |
| 只读成员 (`role=VIEWER`) | 仅查看项目内容 |

## 常见问题

### 1. 启动时提示 Redis 连接失败
- 确保 Redis 服务已启动：`redis-server`
- 检查 `application-dev.yml` 中的 Redis 配置是否正确

### 2. 登录后访问其他接口返回 403
- 检查 JWT 过滤器是否已授予 `ROLE_USER` 角色（已在 `JwtAuthenticationFilter` 中实现）
- 确保请求头 `Authorization: Bearer <token>` 正确携带

### 3. 前端删除/编辑时提示“项目不存在”
- 后端已将所有 Long 类型 ID 序列化为字符串，请确保前端未将 ID 转换为数字
- 检查 `JacksonConfig` 是否生效（返回 JSON 中 ID 带双引号）

### 4. 数据库表未自动创建
- 项目使用 `ddl-auto: validate`，请手动执行 `schema.sql` 建表
- 如需自动建表，可修改配置为 `ddl-auto: update`（仅开发环境）

