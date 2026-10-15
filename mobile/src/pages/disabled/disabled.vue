<template>
  <CrudList :api="api" :columns="columns" :fields="fields" :searches="searches" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CrudList from '@/components/CrudList.vue'
import api from '@/api/disabled.js'
import { listCommunity } from '@/api/community'

const columns = [
  { key: 'name', label: '姓名' },
  { key: 'gender', label: '性别' },
  { key: 'disabilityType', label: '残疾类型' },
  { key: 'communityName', label: '所属小区' }
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
  {
    key: 'disabilityType',
    label: '残疾类型',
    type: 'select',
    options: [
      { label: '视力', value: '视力' },
      { label: '听力', value: '听力' },
      { label: '言语', value: '言语' },
      { label: '肢体', value: '肢体' },
      { label: '智力', value: '智力' },
      { label: '精神', value: '精神' },
      { label: '多重', value: '多重' }
    ],
    required: true
  },
  {
    key: 'disabilityLevel',
    label: '残疾等级',
    type: 'select',
    options: [
      { label: '一级', value: '一级' },
      { label: '二级', value: '二级' },
      { label: '三级', value: '三级' },
      { label: '四级', value: '四级' }
    ]
  },
  {
    key: 'communityId',
    label: '所属小区',
    type: 'select',
    options: [],
    required: true
  },
  { key: 'phone', label: '本人电话', type: 'text' },
  { key: 'guardian', label: '监护人姓名', type: 'text' },
  { key: 'guardianPhone', label: '监护人电话', type: 'text' },
  { key: 'guardianRelation', label: '监护关系', type: 'text' },
  { key: 'disabilityCertNo', label: '残疾证号', type: 'text' },
  { key: 'ethnicity', label: '民族', type: 'text' },
  { key: 'education', label: '文化程度', type: 'text' },
  {
    key: 'maritalStatus',
    label: '婚姻状况',
    type: 'select',
    options: [
      { label: '已婚', value: '已婚' },
      { label: '未婚', value: '未婚' },
      { label: '离异', value: '离异' },
      { label: '丧偶', value: '丧偶' }
    ]
  },
  {
    key: 'householdType',
    label: '户口类型',
    type: 'select',
    options: [
      { label: '农业户口', value: '农业户口' },
      { label: '非农业户口', value: '非农业户口' }
    ]
  },
  { key: 'address', label: '家庭住址', type: 'text' },
  { key: 'remark', label: '备注', type: 'textarea' }
])
const searches = [
  { key: 'name', label: '姓名' },
  {
    key: 'disabilityType',
    label: '残疾类型',
    type: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '视力', value: '视力' },
      { label: '听力', value: '听力' },
      { label: '言语', value: '言语' },
      { label: '肢体', value: '肢体' },
      { label: '智力', value: '智力' },
      { label: '精神', value: '精神' },
      { label: '多重', value: '多重' }
    ]
  },
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
