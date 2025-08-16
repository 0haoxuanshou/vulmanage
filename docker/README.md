# VulManage Docker 部署指南

## 概述

本目录包含了 VulManage 应用的 Docker 容器化部署配置。

## 目录结构

```
docker/
├── .env                    # 环境变量配置文件
├── docker-compose.yml      # Docker Compose 配置文件
├── config/                 # 应用配置文件目录
│   └── application.yml     # 后端应用配置
└── README.md              # 本文件
```

## 快速开始

### 1. 构建镜像

在项目根目录执行以下命令构建前后端镜像：

```bash
# 构建所有镜像
make build

# 或者分别构建
make build-frontend
make build-backend
```

### 2. 配置环境变量

编辑 `.env` 文件，配置数据库和 MinIO 的相关参数：

```bash
# 数据库配置
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=vulmanage

# MinIO 配置
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
```

### 3. 启动服务

```bash
cd docker
docker-compose up -d
```

### 4. 访问应用

- 前端应用：http://localhost
- 后端 API：http://localhost:8080
- MinIO 管理界面：http://localhost:9001

## 服务说明

### 前端服务 (frontend)
- **镜像**：registry.cn-shanghai.aliyuncs.com/bountyteam/vul_fe:1.0
- **端口**：80
- **功能**：提供 Web 界面，代理 API 请求到后端服务

### 后端服务 (backend)
- **镜像**：registry.cn-shanghai.aliyuncs.com/bountyteam/vul_be:1.0
- **端口**：8080
- **配置**：使用 `config/application.yml` 进行配置
- **依赖**：PostgreSQL 数据库、MinIO 对象存储

### 数据库服务 (postgres)
- **镜像**：registry.cn-shanghai.aliyuncs.com/bountyteam/postgres:17
- **端口**：5432
- **数据持久化**：使用 Docker volume `postgres_data`

### 对象存储服务 (minio)
- **镜像**：registry.cn-shanghai.aliyuncs.com/bountyteam/minio:RELEASE.2024-01-18T22-51-28Z
- **端口**：9000 (API), 9001 (管理界面)
- **数据持久化**：使用 Docker volume `minio_data`

## 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [service_name]

# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 重启特定服务
docker-compose restart [service_name]
```

## 配置说明

### 后端配置 (config/application.yml)

后端应用支持通过环境变量进行配置，主要配置项包括：

- `DB_HOST`：数据库主机地址
- `DB_PORT`：数据库端口
- `DB_NAME`：数据库名称
- `DB_USERNAME`：数据库用户名
- `DB_PASSWORD`：数据库密码
- `MINIO_HOST`：MinIO 主机地址
- `MINIO_PORT`：MinIO 端口
- `MINIO_ACCESS_KEY`：MinIO 访问密钥
- `MINIO_SECRET_KEY`：MinIO 秘密密钥
- `MINIO_BUCKET`：MinIO 存储桶名称

### 网络配置

所有服务都连接到 `vulmanage-network` 网络，确保服务间可以通过服务名进行通信。

## 健康检查

- **后端服务**：通过 `/actuator/health` 端点进行健康检查
- **前端服务**：通过根路径进行健康检查

## 故障排除

1. **服务启动失败**：检查 `.env` 文件配置是否正确
2. **数据库连接失败**：确保 PostgreSQL 服务已启动且配置正确
3. **MinIO 连接失败**：确保 MinIO 服务已启动且访问密钥配置正确
4. **前端无法访问后端**：检查网络配置和服务依赖关系

## 镜像推送

构建完成后，可以将镜像推送到镜像仓库：

```bash
# 推送所有镜像
make push

# 或者分别推送
make push-frontend
make push-backend
```