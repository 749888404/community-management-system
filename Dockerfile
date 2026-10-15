# 仓库根目录 Dockerfile —— 专供 Render（构建上下文 = 仓库根，这是 Render 的默认行为）
# 与 backend/Dockerfile 的区别仅在于 COPY 路径多了一层 backend/ 前缀。
# 多阶段构建：Maven 打包 → JRE 运行（适配 Render 免费层 512MB 内存）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
# 先拉依赖（利用缓存层，后续改动源码时不必重下依赖）
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 上传目录（注意：容器文件系统是临时的，重启会丢，见 DEPLOY.md）
RUN mkdir -p /app/uploads
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC"
# 锁死端口 8080：--server.port 是最高优先级参数，
# 可压过平台注入的 SERVER_PORT / PORT 等环境变量，保证 Render 路由稳定
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8080"]
