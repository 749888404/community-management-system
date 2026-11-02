# 仓库根目录 Dockerfile —— 专供 Render（构建上下文 = 仓库根，这是 Render 的默认行为）
# 与 backend/Dockerfile 的区别仅在于 COPY 路径多了一层 backend/ 前缀。
# 多阶段构建：Maven 打包 → JRE 运行（适配 Render 免费层 512MB 内存）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
# 先拉依赖（利用缓存层）；个别工程 go-offline 会误报解析失败，用 || true 兜底不影响后续 package
RUN mvn -B -ntp dependency:go-offline || true
COPY backend/src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 上传目录（注意：容器文件系统是临时的，重启会丢，见 DEPLOY.md）
RUN mkdir -p /app/uploads
# 适配 Railway Free 档 0.5GB 内存上限：堆固定 280m，元空间/代码缓存封顶，避免容器被 OOM Kill
# 开 App Sleeping 后无流量时容器停机不计费，这套参数只在唤醒期间消耗额度
ENV JAVA_OPTS="-Xms96m -Xmx280m -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=48m -XX:+UseSerialGC -Xss384k -Dfile.encoding=UTF-8 -XX:+ExitOnOutOfMemoryError"
# 端口：优先跟随平台注入的 PORT（Zeabur/Railway 等），未注入时回落 8080（Render）。
# --server.port 是 Spring Boot 最高优先级参数，可压过 SERVER_PORT 等同名环境变量，保证路由稳定。
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]
