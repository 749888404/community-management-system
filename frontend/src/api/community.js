import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pageCommunities(params) {
  return request.get('/community/page', { params })
}
export function listCommunities() {
  return request.get('/community/list')
}
export function statsCommunities() {
  return request.get('/community/stats')
}
export function saveCommunity(data) {
  return request.post('/community', data)
}
export function updateCommunity(data) {
  return request.put('/community', data)
}
export function deleteCommunity(id) {
  return request.delete('/community/' + id)
}
export function batchDeleteCommunities(ids) {
  return request.post('/community/batch-delete', ids)
}
export function exportCommunities(params) {
  return download('/community/export', params)
}
export function importCommunities(file) {
  return upload('/community/import', file)
}
