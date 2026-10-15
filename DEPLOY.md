# 云端免费部署指南（TiDB + Render + Cloudflare Pages）

> 目标：项目 24 小时可公网访问，完全不依赖本地电脑，全程免费。
>
> 免费额度限制（够个人/演示使用）：
> - **Render 免费层**：512MB 内存；**15 分钟无访问会休眠**，再次访问需等待约 30-60 秒冷启动
> - **TiDB Serverless**：5GB 存储 + 每月一定请求额度
> - **Cloudflare Pages**：不限请求数，单文件 25MB 以内

---

## 部署架构

```
用户浏览器/手机
   │
   ├── PC 管理端  → Cloudflare Pages 站点 A（frontend/dist）
   ├── 手机端 H5  → Cloudflare Pages 站点 B（mobile/dist/build/h5）
   │
   └── 两者的 API 请求 → Render 后端（backend，Docker 部署）
                              │
                              └── TiDB Cloud Serverless（MySQL 兼容）
```

---

## 第 0 步：准备账号（全部免费注册）

| 平台 | 用途 | 注册地址 |
|---|---|---|
| GitHub | 存放代码，Render/CF 从这里拉取 | https://github.com |
| TiDB Cloud | 免费 MySQL 数据库 | https://tidbcloud.com |
| Render | 免费后端容器 | https://render.com |
| Cloudflare | 免费静态托管 | https://dash.cloudflare.com |

---

## 第 1 步：推送代码到 GitHub

1. GitHub 上新建仓库（例如 `community-management-system`，**Private 私有即可**）
2. 本项目已完成 git 初始化和首次提交，执行：

```bash
cd community-management-system
git remote add origin https://github.com/<你的用户名>/community-management-system.git
git branch -M main
git push -u origin main
```

---

## 第 2 步：创建 TiDB 免费数据库

1. 注册/登录 https://tidbcloud.com ，选择 **Serverless（免费）** 计划
2. 创建集群（区域选最近的，如 Singapore / Tokyo）
3. 进入集群 → **SQL Editor**（或 Connect 页面的连接串信息）
4. 在 SQL Editor 中执行本项目 `database/init.sql` 的全部内容
   - TiDB 完全兼容 MySQL 语法，可直接粘贴执行
5. 在 **Connect** 页面获取连接信息（选择语言 `General` / 连接串），记下：
   - Host（形如 `xxx.xxx.aws.tidbcloud.com`）
   - Port（4000）
   - User（形如 `<前缀>.root`）
   - Password（创建集群时设置的 root 密码）

**组装 JDBC URL（下一步要用）：**

```
jdbc:mysql://<HOST>:4000/community_db?sslMode=REQUIRED
```

> TiDB Serverless 强制 TLS 加密。`sslMode=REQUIRED` 即可正常连接；如需严格证书校验可改用
> `sslMode=VERIFY_IDENTITY&sslBundledCertPath=/etc/ssl/certs/ca-certificates.crt`（Render 容器自带系统 CA）。

---

## 第 3 步：Render 部署后端

1. 登录 https://render.com → **New +** → **Web Service**
2. 连接 GitHub 账号，选择仓库 `community-management-system`
3. 配置：
   - **Root Directory**：`backend`
   - **Runtime**：`Docker`（识别到 backend/Dockerfile 会自动使用）
   - **Instance Type**：`Free`
   - **环境变量（Environment Variables）**：

     | Key | Value |
     |---|---|
     | `DB_URL` | 第 2 步组装的 JDBC URL |
     | `DB_USERNAME` | `<前缀>.root` |
     | `DB_PASSWORD` | TiDB root 密码 |

4. 点击 **Create Web Service**，等待构建（首次约 5-10 分钟）
5. 部署完成后得到公网地址，形如：`https://community-management-system-xxxx.onrender.com`
6. **验证**：浏览器访问 `https://xxxx.onrender.com/api/auth/login`（POST 或看到非 404 响应即成功），也可以先访问 `https://xxxx.onrender.com/api/community/list` 试试

> 提示：如果构建超时/失败，可在 Render 重新触发 Deploy（Render 会缓存依赖层，第二次会快很多）。

---

## 第 4 步：Cloudflare Pages 部署前端（两个站点）

### 4A. PC 管理端

本地构建（把 `<RENDER后端地址>` 换成第 3 步得到的地址）：

```bash
cd frontend
VITE_API_BASE=https://xxxx.onrender.com/api npm run build
```

> 也可以不本地构建，在 Cloudflare Pages 里配置 Git 构建：
> - Root directory: `frontend`
> - Build command: `npm run build`
> - Build output: `dist`
> - 环境变量：`VITE_API_BASE = https://xxxx.onrender.com/api`

**Direct Upload 方式（推荐，最简单）：**
1. Cloudflare Dashboard → **Workers & Pages** → **Create** → **Pages** → **Upload assets（直接上传）**
2. 项目名如 `community-admin`，把 `frontend/dist` 文件夹拖进去
3. 部署完成后得到 `https://community-admin.pages.dev`

### 4B. 手机端 H5

```bash
cd mobile
VITE_API_BASE=https://xxxx.onrender.com/api npm run build:h5
```

产物在 `mobile/dist/build/h5`，同样方式上传到另一个 Pages 项目（如 `community-mobile`），得到 `https://community-mobile.pages.dev`

---

## 第 5 步：验收

1. 打开 `https://community-admin.pages.dev` → 用 admin / 123456 登录
2. 打开 `https://community-mobile.pages.dev`（手机浏览器效果更佳）→ 同账号登录
3. 检查各模块数据是否正常（TiDB 里的 init.sql 示例数据）

---

## 已知限制与注意事项

| 事项 | 说明 |
|---|---|
| **冷启动慢** | Render 免费层 15 分钟无访问休眠，下次首访需等 30-60 秒。可定时用监控服务（如 UptimeRobot 免费版）每 10 分钟 ping 一次后端，缓解休眠 |
| **上传文件会丢** | 后端文件存储在容器本地磁盘（`uploads/`），Render 重启/重新部署后清空。如需持久化，后续可改造为存云存储或数据库 BLOB |
| **安全性** | 当前鉴权为演示级 token（后端不校验签名）。公网长期开放前建议：修改默认 admin 密码；后续升级 JWT |
| **小程序/App** | 手机端要发微信小程序，必须 HTTPS + 备案域名，Cloudflare Pages 的 pages.dev 域名不能配置为小程序服务器域名，需购买域名后再配置 |

---

## 常见问题

**Q：登录后一直"网络错误"？**
A：检查前端是否用旧构建（没带 VITE_API_BASE）；检查 Render 后端是否在休眠（先手动访问一次后端地址）。

**Q：TiDB 连接报 SSL 错误？**
A：确认 JDBC URL 带了 `sslMode=REQUIRED` 参数（TiDB Serverless 强制 TLS）。

**Q：文件上传成功但预览/下载失败？**
A：Render 重新部署过容器导致文件丢失（见上表"上传文件会丢"），重新上传即可。
