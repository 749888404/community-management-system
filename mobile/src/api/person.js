import { get, post, put, del } from '@/utils/request'

export const pagePerson = (p) => get('/person/page', p)
export const createPerson = (d) => post('/person', d)
export const updatePerson = (d) => put('/person', d)
export const removePerson = (id) => del('/person/' + id)
export const batchRemovePerson = (ids) => post('/person/batch-delete', ids)

export default {
  page: pagePerson,
  create: createPerson,
  update: updatePerson,
  remove: removePerson,
  batchRemove: batchRemovePerson
}
