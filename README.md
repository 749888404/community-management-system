# 社区管理系统

基于 **Vue 3 + Spring Boot 3 + MySQL** 的社区管理后台，包含用户管理、小区管理、残疾人管理三大模块。

## 技术栈

| 层 | 技术 |
|----|------|
| 前端 | Vue 3 + Vite + Element Plus + Vue Router + Axios |
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5 + Java 17 |
| 数据库 | MySQL 8.x |

## 目录结构

```
community-management-system/
├── database/                 # 数据库初始化脚本
│   └── init.sql
├── backend/                 # Java 后端（Spring Boot）
│   ├── pom.xml
│   └── src/main/java/com/community/
│       ├── config/          # MyBatis-Plus 分页、跨域、自动填充
│       ├── common/          # 统一返回 Result / 分页 PageResult
│       ├── entity/          # 实体类 + DisabledVO
│       ├── mapper/          # Mapper 接口
│       ├── service/         # 服务层接口与实现
│       └── controller/      # 接口控制器
└── frontend/                # Vue 前端
    ├── package.json
    ├── vite.config.js       # 已配置 /api 代理到 8080
    └── src/
        ├── api/             # 接口封装
        ├── router/          # 路由
        ├── utils/request.js # Axios 封装
        └── views/           # 登录、布局、三个模块页面
```

## 一、初始化数据库

```bash
# 进入 MySQL（务必用 utf8mb4 连接！Windows 下 MySQL 客户端默认 gbk，
# 直接导入 UTF-8 脚本会因中文被误读而报 "Data too long for column 'real_name'"）
mysql -u root -p --default-character-set=utf8mb4

# 执行建库建表脚本（已含示例数据）
source database/init.sql
```

默认连接：`jdbc:mysql://localhost:3306/community_db`，账号 `root` / `root`。
若你的数据库账号密码不同，请修改 `backend/src/main/resources/application.yml`。

## 二、启动后端

需本地安装 JDK 17 与 Maven。Maven 二进制若需下载，可用华为云镜像
`https://mirrors.huaweicloud.com/apache/maven/`（apache.org 直连常不可达）。

```bash
cd backend
mvn spring-boot:run
```

后端端口已在 `pom.xml` 的 `spring-boot-maven-plugin` 中固化为 `8080`
（用于覆盖本机 `SERVER_PORT=0` 环境变量导致的随机端口问题）。
接口前缀 `/api`，例如：
- `GET  /api/user/page`    用户分页
- `GET  /api/community/page` 小区分页
- `GET  /api/disabled/page`  残疾人分页（返回含小区名称）
- `POST /api/user`  `PUT /api/user`  `DELETE /api/user/{id}` 等增删改接口

## 三、启动前端

### 方式 A（推荐，稳定）：构建产物 + 纯 Node 静态托管

`npm run dev` 在本机会话后台易被回收导致 5173 掉线。改用构建静态产物、由纯 `node` 进程托管（与后端 java 同构，持久稳定）：

```bash
cd frontend
npm run build            # 生成 dist/（仅需执行一次，改源码后需重新 build）
cd ..
node serve-frontend.js   # 托管 dist/，并将 /api 反向代理到后端 8080，端口 5173
```

### 方式 B：Vite 开发服务器

需本地安装 Node.js 16+。

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173`，默认跳转登录页。
演示账号：`admin` / `123456`（前端演示版直接放行，未接入真实鉴权）。

## 四、功能说明

1. **用户管理**：账号、姓名、手机、邮箱、角色（管理员/普通用户）、状态（启用/禁用）的增删改查与分页检索。
2. **小区管理**：小区名称、地址、负责人、电话、户数、人口的维护，并提供下拉数据源供残疾人模块选择。
3. **残疾人管理**：姓名、性别、身份证、残疾类型（视力/听力/言语/肢体/智力/精神/多重）、等级、所属小区、监护人、联系方式的维护，按小区联动查询。

## 五、接口约定

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { "total": 3, "records": [ ... ] }
}
```

分页请求：`/page?page=1&size=10&name=关键字`（page 从 1 开始）。

## 备注

- 演示版密码统一以 MD5（`123456` = `e10adc3949ba59abbe56e057f20f883e`）存储，未做加密加盐。
- 前端登录已接入真实后端校验（`POST /api/auth/login`，明文密码经 MD5 与库中密文比对，并校验账号是否存在、是否禁用），路由守卫拦截未登录访问；演示 token 为随机 UUID（未落库），生产需替换为 JWT 并加 Spring Security 鉴权。

## 六、登录鉴权（已接入）

- 入口：访问前端根路径自动跳转登录页（演示账号 `admin / 123456`，数据在 `sys_user` 表）。
- 校验流程：前端提交账号密码 → 后端 `/api/auth/login` 校验 → 成功返回 `{ token, username, realName, role }`，前端写入 `localStorage`。
- 路由守卫：`router.beforeEach` 拦截未携带 `localStorage.token` 的访问，强制跳登录页；`request.js` 请求拦截器自动在 `Authorization` 头携带 token。
- 退出：顶栏"退出"按钮清除 `localStorage` 中的 token/用户信息并跳回登录页。
