# Makefile for VulManage Project

# 变量定义
FRONTEND_IMAGE = registry.cn-shanghai.aliyuncs.com/bountyteam/vul_fe:1.0
BACKEND_IMAGE = registry.cn-shanghai.aliyuncs.com/bountyteam/vul_be:1.0

# 默认目标
.PHONY: all build-frontend build-backend build push-frontend push-backend push clean up

all: build

# 构建所有镜像
build: build-frontend build-backend

# 构建前端镜像
build-frontend:
	@echo "Building frontend image..."
	cd frontend && npm run build && docker build -t $(FRONTEND_IMAGE) .
	@echo "Frontend image built successfully: $(FRONTEND_IMAGE)"

# 构建后端镜像
build-backend:
	@echo "Building backend image..."
	mvn clean package -DskipTests && docker build -t $(BACKEND_IMAGE) .
	@echo "Backend image built successfully: $(BACKEND_IMAGE)"

# 推送所有镜像
push: push-frontend push-backend

# 推送前端镜像
push-frontend:
	@echo "Pushing frontend image..."
	docker push $(FRONTEND_IMAGE)
	@echo "Frontend image pushed successfully"

# 推送后端镜像
push-backend:
	@echo "Pushing backend image..."
	docker push $(BACKEND_IMAGE)
	@echo "Backend image pushed successfully"

# 清理本地镜像
clean:
	@echo "Cleaning up local images..."
	docker rmi $(FRONTEND_IMAGE) $(BACKEND_IMAGE) || true
	@echo "Cleanup completed"

up: 
	@echo "up local containers..."
	cd docker && docker-compose up -d
	@echo "local containers up successfully"
	
# 帮助信息
help:
	@echo "Available targets:"
	@echo "  build          - Build both frontend and backend images"
	@echo "  build-frontend - Build frontend image only"
	@echo "  build-backend  - Build backend image only"
	@echo "  push           - Push both images to registry"
	@echo "  push-frontend  - Push frontend image to registry"
	@echo "  push-backend   - Push backend image to registry"
	@echo "  clean          - Remove local images"
	@echo "  help           - Show this help message"
	@echo "  up             - Up local containers"