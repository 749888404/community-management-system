import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pageStations(params) {
  return request.get('/station/page', { params })
}
export function saveStation(data) {
  return request.post('/station', data)
}
export function updateStation(data) {
  return request.put('/station', data)
}
export function deleteStation(id) {
  return request.delete('/station/' + id)
}
export function batchDeleteStations(ids) {
  return request.post('/station/batch-delete', ids)
}
export function exportStations(params) {
  return download('/station/export', params)
}
export function importStations(file) {
  return upload('/station/import', file)
}
