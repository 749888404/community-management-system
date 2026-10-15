import request from '../utils/request'
import { download, raw } from '../utils/request'

export function pageFile(params) {
  return request.get('/file/page', { params })
}
export function listUploaders() {
  return request.get('/file/uploaders')
}
export function uploadFiles(formData) {
  return raw.post('/file/upload', formData, { timeout: 120000 }).then(res => res.data)
}
export function downloadFile(id) {
  return download('/file/download/' + id, {})
}
export function updateFile(data) {
  return request.put('/file', data)
}
export function deleteFile(id) {
  return request.delete('/file/' + id)
}
export function batchDeleteFiles(ids) {
  return request.post('/file/batch-delete', ids)
}
