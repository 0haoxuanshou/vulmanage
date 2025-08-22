# 生产阶段
FROM registry.cn-shanghai.aliyuncs.com/bountyteam/java:21

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY ./target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]