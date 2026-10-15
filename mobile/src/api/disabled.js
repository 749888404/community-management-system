import { get, post, put, del } from '@/utils/request'

export const pageDisabled = (p) => get('/disabled/page', p)
export const createDisabled = (d) => post('/disabled', d)
export const updateDisabled = (d) => put('/disabled', d)
export const removeDisabled = (id) => del('/disabled/' + id)
export const batchRemoveDisabled = (ids) => post('/disabled/batch-delete', ids)

export default {
  page: pageDisabled,
  create: createDisabled,
  update: updateDisabled,
  remove: removeDisabled,
  batchRemove: batchRemoveDisabled
}
