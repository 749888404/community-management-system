# 多阶段构建：Maven 打包 → JRE 运行
# 适配 Koyeb / Render 免费层（512MB 内存）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# 先拉依赖（利用缓存层）
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 上传目录（注意：容器文件系统是临时的，重启会丢，见 DEPLOY.md）
RUN mkdir -p /app/uploads
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC"
# 锁死端口 8080：--server.port 为最高优先级参数，
# 可压过宿主机/平台注入的 SERVER_PORT、PORT 等环境变量，保证路由稳定
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8080"]
