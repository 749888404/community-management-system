import request from '../utils/request'
import { download, upload } from '../utils/request'

export function pagePerson(params) {
  return request.get('/person/page', { params })
}
export function savePerson(data) {
  return request.post('/person', data)
}
export function updatePerson(data) {
  return request.put('/person', data)
}
export function deletePerson(id) {
  return request.delete('/person/' + id)
}
export function batchDeletePersons(ids) {
  return request.post('/person/batch-delete', ids)
}
export function exportPersons(params) {
  return download('/person/export', params)
}
export function importPersons(file) {
  return upload('/person/import', file)
}
