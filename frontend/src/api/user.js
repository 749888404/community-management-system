import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pageUsers(params) {
  return request.get('/user/page', { params })
}
export function saveUser(data) {
  return request.post('/user', data)
}
export function updateUser(data) {
  return request.put('/user', data)
}
export function deleteUser(id) {
  return request.delete('/user/' + id)
}
export function batchDeleteUsers(ids) {
  return request.post('/user/batch-delete', ids)
}
export function exportUsers(params) {
  return download('/user/export', params)
}
export function importUsers(file) {
  return upload('/user/import', file)
}
