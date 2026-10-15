import { get, post, put, del } from '@/utils/request'

export const pageUser = (p) => get('/user/page', p)
export const createUser = (d) => post('/user', d)
export const updateUser = (d) => put('/user', d)
export const removeUser = (id) => del('/user/' + id)
export const batchRemoveUser = (ids) => post('/user/batch-delete', ids)

export default {
  page: pageUser,
  create: createUser,
  update: updateUser,
  remove: removeUser,
  batchRemove: batchRemoveUser
}
