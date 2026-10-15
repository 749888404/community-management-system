import { get, post, put, del, uploadFile } from '@/utils/request'
import { BASE_URL, FILE_BASE_URL } from '@/config'

export const pageFile = (p) => get('/file/page', p)
export const removeFile = (id) => del('/file/' + id)
export const batchRemoveFile = (ids) => post('/file/batch-delete', ids)
export const updateFile = (d) => put('/file', d)
export const uploadFiles = (filePaths, extra = {}) => {
  // 逐个上传（后端接收 files 数组，循环传也可）
  const tasks = filePaths.map((p) => uploadFile('/file/upload', p, 'files', extra))
  return Promise.all(tasks)
}

// 预览/下载地址
export const previewUrl = (id) => BASE_URL + '/file/preview/' + id
export const downloadUrl = (id) => BASE_URL + '/file/download/' + id
export const rawFileUrl = (id) => FILE_BASE_URL + '/api/file/preview/' + id

export default {
  page: pageFile,
  remove: removeFile,
  batchRemove: batchRemoveFile,
  update: updateFile,
  uploadFiles
}
