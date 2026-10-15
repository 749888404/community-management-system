import { BASE_URL } from '@/config'

// uni.request 封装：统一返回 body.data，统一错误提示与登录失效处理
function request(options) {
  const { url, method = 'GET', data = {}, header = {} } = options
  const token = uni.getStorageSync('token') || ''
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        Authorization: token,
        'Content-Type':
          method === 'GET'
            ? 'application/x-www-form-urlencoded'
            : 'application/json',
        ...header
      },
      success: (res) => {
        const body = res.data
        // HTTP 层错误
        if (res.statusCode !== 200) {
          uni.showToast({ title: '服务异常 ' + res.statusCode, icon: 'none' })
          return reject(new Error('HTTP ' + res.statusCode))
        }
        // 业务层
        if (body && body.code === 200) {
          return resolve(body.data)
        }
        if (body && body.code === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.showToast({ title: '登录已失效，请重新登录', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/login' })
          }, 800)
          return reject(new Error(body.message || '未登录'))
        }
        const msg = (body && body.message) || '请求失败'
        uni.showToast({ title: msg, icon: 'none' })
        return reject(new Error(msg))
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误，请检查后端是否启动', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put = (url, data) => request({ url, method: 'PUT', data })
export const del = (url, data) => request({ url, method: 'DELETE', data })

// 文件上传：基于 uni.uploadFile，返回 body.data
export function uploadFile(url, filePath, formKey = 'files', extra = {}, name = 'files') {
  const token = uni.getStorageSync('token') || ''
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name,
      formData: extra,
      header: { Authorization: token },
      success: (res) => {
        let body = res.data
        try {
          body = typeof body === 'string' ? JSON.parse(body) : body
        } catch (e) {
          body = {}
        }
        if (res.statusCode === 200 && body && body.code === 200) {
          resolve(body.data)
        } else {
          const msg = (body && body.message) || '上传失败'
          uni.showToast({ title: msg, icon: 'none' })
          reject(new Error(msg))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败，请检查网络', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default request
