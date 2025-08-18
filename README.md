# VulManage - 安全漏洞管理系统

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.2.13-brightgreen.svg)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)

一个功能完善的安全漏洞管理平台，帮助安全团队有效地跟踪、管理和分析安全漏洞。

## ✨ 功能特性

### 🔐 用户管理
- **安全登录**: 支持基于JWT的用户认证
- **密码管理**: 支持密码修改和安全策略
- **会话管理**: 自动会话保持和超时处理

### 🛡️ 漏洞管理
- **漏洞录入**: 支持完整的漏洞信息录入，包括：
  - 自动生成漏洞编号（格式：VUL_YYYY_XXXX）
  - 漏洞名称、类型、严重程度
  - 影响范围、发现日期
  - 漏洞概述、复现步骤、修复建议
  - 多URL关联、附件上传
  - 状态跟踪（未修复、修复中、已修复、已验证）

- **漏洞编辑**: 全字段编辑支持，自动记录修改历史
- **高级查询**: 支持多条件组合查询和筛选：
  - 按漏洞名称、类型、严重程度筛选
  - 按发现日期范围查询
  - 按状态筛选
  - 支持模糊查询和精确匹配

- **详情查看**: 完整的漏洞信息展示和修改历史追踪
- **附件管理**: 支持文件上传、下载和预览

### 📊 数据管理
- **历史记录**: 完整的字段修改历史追踪
- **数据导出**: 支持漏洞数据导出
- **分页展示**: 高效的大数据量分页处理

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.5.4
- **安全**: Spring Security + JWT
- **数据库**: PostgreSQL 17
- **ORM**: MyBatis-Plus 3.5.7
- **构建工具**: Maven
- **Java版本**: OpenJDK 21

### 前端技术栈
- **框架**: Vue.js 3.2.13
- **UI组件**: Element Plus 2.10.7
- **HTTP客户端**: Axios 1.11.0
- **富文本编辑**: CKEditor 5
- **路由**: Vue Router 4.5.1

### 基础设施
- **容器化**: Docker + Docker Compose
- **文件存储**: MinIO
- **数据库迁移**: Flyway
- **反向代理**: Nginx

## 🚀 快速开始

### 环境要求
- Docker 20.0+
- Docker Compose 2.0+
- Git

### 一键部署

1. **克隆项目**
```bash
git clone https://github.com/0haoxuanshou/vulmanage.git
cd vulmanage
```

2. **配置环境变量**
```bash
cp docker/.env.example docker/.env
# 编辑 docker/.env 文件，修改相关配置（推荐账号密码都改成强密码）
```

3. **启动服务**
```bash
cd docker
docker-compose up -d
```

4. **访问系统**
- 前端地址: http://localhost

### 开发环境部署

#### 后端开发

1. **环境要求**
   - JDK 21
   - Maven 3.6+
   - PostgreSQL 17

2. **数据库准备**
```bash
# 启动PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=vulmanage \
  -p 5432:5432 postgres:17
```

3. **启动后端服务**
```bash
# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```

#### 前端开发

1. **环境要求**
   - Node.js 16+
   - npm 8+

2. **启动前端服务**
```bash
cd frontend
npm install
npm run serve
```

## 📦 构建部署

### 使用Makefile构建

```bash
# 构建所有镜像
make build

# 单独构建前端
make build-frontend

# 单独构建后端
make build-backend

# 推送镜像到仓库
make push
```

### 手动构建

```bash
# 构建后端
mvn clean package -DskipTests
docker build -t vulmanage-backend .

# 构建前端
cd frontend
npm run build
docker build -t vulmanage-frontend .
```

## ⚙️ 配置说明

### 环境变量配置

| 变量名 | 描述 | 默认值 |
|--------|------|--------|
| `DB_URL` | 数据库连接URL | `jdbc:postgresql://postgres:5432/vulmanage` |
| `DB_USERNAME` | 数据库用户名 | `postgres` |
| `DB_PASSWORD` | 数据库密码 | `postgres` |
| `MINIO_ENDPOINT` | MinIO服务地址 | `http://minio:9000` |
| `MINIO_ACCESS_KEY` | MinIO访问密钥 | `minioadmin` |
| `MINIO_SECRET_KEY` | MinIO秘密密钥 | `minioadmin` |
| `VULN_PREFIX` | 漏洞编号前缀 | `VUL` |
| `LOG_LEVEL` | 日志级别 | `DEBUG` |

### 数据库配置

系统使用PostgreSQL作为主数据库，支持以下特性：
- 自动数据库迁移（Flyway）
- 连接池管理
- 事务支持
- 索引优化

## 📖 API文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `PUT /api/auth/password` - 修改密码

### 漏洞管理接口
- `GET /api/vulnerabilities` - 获取漏洞列表（支持筛选）
- `GET /api/vulnerabilities/{id}` - 获取漏洞详情
- `POST /api/vulnerabilities` - 创建新漏洞
- `PUT /api/vulnerabilities/{id}` - 更新漏洞信息
- `DELETE /api/vulnerabilities/{id}` - 删除漏洞
- `GET /api/vulnerabilities/{id}/history` - 获取漏洞修改历史

### 附件管理接口
- `POST /api/vulnerabilities/{id}/attachments` - 上传附件
- `GET /api/vulnerabilities/{id}/attachments` - 获取附件列表
- `GET /api/attachments/{id}` - 下载附件
- `DELETE /api/attachments/{id}` - 删除附件

## 🔒 安全特性

- **认证授权**: 基于JWT的无状态认证
- **密码安全**: 强哈希算法加密存储
- **输入验证**: 防止SQL注入、XSS攻击
- **CORS配置**: 跨域请求安全控制
- **操作审计**: 完整的操作日志记录
- **文件安全**: 安全的文件上传和访问控制

## 🤝 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 开发规范

- 遵循现有代码风格
- 添加适当的测试用例
- 更新相关文档
- 确保所有测试通过

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和用户。

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系我们：

- 提交 [Issue](https://github.com/0haoxuanshou/vulmanage/issues)
- 发送邮件至: 455674848@qq.com

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！