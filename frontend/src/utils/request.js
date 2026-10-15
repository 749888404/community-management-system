import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 开发环境走 vite 代理（默认 '/api'）；生产部署时通过 VITE_API_BASE 注入后端公网地址
export const API_BASE = import.meta.env.VITE_API_BASE || '/api'

const request = axios.create({
  baseURL: API_BASE,
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    ElMessage.error('网络错误，请检查后端服务')
    return Promise.reject(error)
  }
)

// 不经过 JSON 拦截器的原始实例（用于文件下载/上传）
export const raw = axios.create({ baseURL: API_BASE, timeout: 60000 })

/** 导出文件：触发浏览器下载 */
export function download(url, params) {
  return raw.get(url, {
    params,
    responseType: 'blob',
    headers: { Authorization: localStorage.getItem('token') || '' }
  }).then(res => {
    const blob = res.data
    const cd = res.headers['content-disposition'] || ''
    let filename = 'export.xlsx'
    const m = cd.match(/filename\*=utf-8''([^;]+)/)
    if (m) filename = decodeURIComponent(m[1])
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(a.href)
  })
}

/** 导入文件：以 multipart/form-data 上传 */
export function upload(url, file) {
  const form = new FormData()
  form.append('file', file)
  return raw.post(url, form, {
    headers: { Authorization: localStorage.getItem('token') || '' }
  }).then(res => res.data)
}

export default request
