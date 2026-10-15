import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pageDisabled(params) {
  return request.get('/disabled/page', { params })
}
export function saveDisabled(data) {
  return request.post('/disabled', data)
}
export function updateDisabled(data) {
  return request.put('/disabled', data)
}
export function deleteDisabled(id) {
  return request.delete('/disabled/' + id)
}
export function batchDeleteDisabled(ids) {
  return request.post('/disabled/batch-delete', ids)
}
export function exportDisabled(params) {
  return download('/disabled/export', params)
}
export function importDisabled(file) {
  return upload('/disabled/import', file)
}
