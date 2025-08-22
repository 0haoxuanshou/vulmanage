# Makefile for VulManage Project

# 变量定义
FRONTEND_IMAGE = registry.cn-shanghai.aliyuncs.com/bountyteam/vul_fe:1.1
BACKEND_IMAGE = registry.cn-shanghai.aliyuncs.com/bountyteam/vul_be:1.1

# 默认目标
.PHONY: all build-frontend build-backend build push-frontend push-backend push clean up local-up down

all: build

# 构建所有镜像
build: build-frontend build-backend

# 构建前端镜像
build-frontend:
	@echo "Building frontend image..."
	cd frontend && npm run build && docker buildx build --platform linux/amd64,linux/arm64 -t $(FRONTEND_IMAGE) . --push
	@echo "Frontend image built successfully: $(FRONTEND_IMAGE)"

# 构建后端镜像
build-backend:
	@echo "Building backend image..."
	mvn clean package -DskipTests && docker buildx build --platform linux/amd64,linux/arm64 -t $(BACKEND_IMAGE) . --push
	@echo "Backend image built successfully: $(BACKEND_IMAGE)"

# 清理本地镜像
clean:
	@echo "Cleaning up local images..."
	docker rmi $(FRONTEND_IMAGE) $(BACKEND_IMAGE) || true
	@echo "Cleanup completed"

up: 
	@echo "up local containers..."
	cd docker && docker-compose pull && docker-compose up -d
	@echo "local containers up successfully"

local-up:
	@echo "up local containers with local ports..."
	cd docker && docker-compose pull && docker-compose -f docker-compose.yml -f docker-compose-local-ports.yml up -d
	@echo "local containers up successfully with local ports"

down:
	@echo "down local containers..."
	cd docker && docker-compose down
	@echo "local containers down successfully"

# 帮助信息
help:
	@echo "Available targets:"
	@echo "  build          - Build both frontend and backend images"
	@echo "  build-frontend - Build frontend image only"
	@echo "  build-backend  - Build backend image only"
	@echo "  clean          - Remove local images"
	@echo "  help           - Show this help message"
	@echo "  up             - Up local containers"
	@echo "  local-up       - Up local containers with local ports"
	@echo "  down           - Down local containers"
