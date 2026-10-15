import { get, post, put, del } from '@/utils/request'

export const pageCommunity = (p) => get('/community/page', p)
export const createCommunity = (d) => post('/community', d)
export const updateCommunity = (d) => put('/community', d)
export const removeCommunity = (id) => del('/community/' + id)
export const batchRemoveCommunity = (ids) => post('/community/batch-delete', ids)
export const listCommunity = () => get('/community/list')

export default {
  page: pageCommunity,
  create: createCommunity,
  update: updateCommunity,
  remove: removeCommunity,
  batchRemove: batchRemoveCommunity
}
