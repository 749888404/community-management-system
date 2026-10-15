<template>
  <CrudList :api="api" :columns="columns" :fields="fields" :searches="searches" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CrudList from '@/components/CrudList.vue'
import api from '@/api/person.js'
import { listCommunity } from '@/api/community'

const columns = [
  { key: 'name', label: '姓名' },
  { key: 'gender', label: '性别' },
  { key: 'communityName', label: '所属小区' },
  { key: 'phone', label: '电话' }
]
const fields = ref([
  { key: 'name', label: '姓名', type: 'text', required: true },
  {
    key: 'gender',
    label: '性别',
    type: 'select',
    options: [
      { label: '男', value: '男' },
      { label: '女', value: '女' }
    ]
  },
  { key: 'idCard', label: '身份证号', type: 'text' },
  { key: 'phone', label: '联系电话', type: 'text' },
  {
    key: 'communityId',
    label: '所属小区',
    type: 'select',
    options: [],
    required: true
  },
  { key: 'building', label: '楼栋', type: 'text' },
  { key: 'unit', label: '单元', type: 'text' },
  { key: 'room', label: '房号', type: 'text' },
  {
    key: 'householdType',
    label: '户籍类型',
    type: 'select',
    options: [
      { label: '常住', value: '常住' },
      { label: '暂住', value: '暂住' }
    ]
  },
  { key: 'nativePlace', label: '籍贯', type: 'text' },
  { key: 'remark', label: '备注', type: 'textarea' }
])
const searches = [
  { key: 'name', label: '姓名' },
  { key: 'communityId', label: '所属小区', type: 'select', options: [] }
]

onMounted(async () => {
  try {
    const list = await listCommunity()
    const opts = (list || []).map((c) => ({ label: c.name, value: c.id }))
    const f = fields.value.find((x) => x.key === 'communityId')
    if (f) f.options = opts
    const s = searches.find((x) => x.key === 'communityId')
    if (s) s.options = [{ label: '全部', value: '' }, ...opts]
  } catch (e) {
    // ignore
  }
})
</script>
