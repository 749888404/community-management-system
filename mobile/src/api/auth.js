import { post, get } from '@/utils/request'

export const login = (data) => post('/auth/login', data)
export const listCommunity = () => get('/community/list')
