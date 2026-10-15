<template>
  <CrudList :api="api" :columns="columns" :fields="fields" :searches="searches" />
</template>

<script setup>
import CrudList from '@/components/CrudList.vue'
import * as base from '@/api/user.js'

// 自定义 api：编辑时若密码为空则不提交密码字段
const api = {
  page: base.pageUser,
  create: base.createUser,
  remove: base.removeUser,
  batchRemove: base.batchRemoveUser,
  update: (d) => {
    const c = { ...d }
    if (!c.password) delete c.password
    return base.updateUser(c)
  }
}

const columns = [
  { key: 'username', label: '账号' },
  { key: 'realName', label: '姓名' },
  { key: 'phone', label: '电话' },
  { key: 'role', label: '角色', format: (v) => (v === 'ADMIN' ? '管理员' : '普通用户') },
  { key: 'status', label: '状态', format: (v) => (v === 1 ? '启用' : '禁用') }
]
const fields = [
  { key: 'username', label: '登录账号', type: 'text', required: true },
  { key: 'realName', label: '真实姓名', type: 'text' },
  { key: 'phone', label: '手机号', type: 'text' },
  { key: 'email', label: '邮箱', type: 'text' },
  {
    key: 'role',
    label: '角色',
    type: 'select',
    options: [
      { label: '普通用户', value: 'USER' },
      { label: '管理员', value: 'ADMIN' }
    ]
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  },
  { key: 'password', label: '密码(新增必填/编辑留空不改)', type: 'text' }
]
const searches = [
  { key: 'username', label: '账号' },
  { key: 'realName', label: '姓名' },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  }
]
</script>
