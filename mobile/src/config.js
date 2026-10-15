// ============================================================
// 后端地址配置
// 开发默认：本地 8080；云端部署时通过 VITE_API_BASE 环境变量注入公网地址
// 例如（构建时）：
//   VITE_API_BASE=https://your-backend.onrender.com/api npm run build:h5
//
// 注意：
//  - 手机真机本地调试时把 localhost 换成电脑局域网 IP，例如 http://192.168.1.10:8080/api
//  - 微信小程序：必须是 https 域名，且要在微信公众平台「开发管理-服务器域名」中配置 request 合法域名
//  - App 打包：可填具体域名或 http(s) 地址
// ============================================================
export const BASE_URL = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

// H5 预览/下载文件用的后端根地址（不含 /api）
export const FILE_BASE_URL = BASE_URL.replace(/\/api\/?$/, '')
