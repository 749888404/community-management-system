import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pageParkings(params) {
  return request.get('/parking/page', { params })
}
export function statsParkings(params) {
  return request.get('/parking/stats', { params })
}
export function saveParking(data) {
  return request.post('/parking', data)
}
export function updateParking(data) {
  return request.put('/parking', data)
}
export function deleteParking(id) {
  return request.delete('/parking/' + id)
}
export function batchDeleteParkings(ids) {
  return request.post('/parking/batch-delete', ids)
}
export function exportParkings(params) {
  return download('/parking/export', params)
}
export function importParkings(file) {
  return upload('/parking/import', file)
}
