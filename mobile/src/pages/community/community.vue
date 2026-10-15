<template>
  <CrudList :api="api" :columns="columns" :fields="fields" :searches="searches" />
</template>

<script setup>
import CrudList from '@/components/CrudList.vue'
import api from '@/api/community.js'

const columns = [
  { key: 'name', label: '小区名称' },
  { key: 'address', label: '地址' },
  { key: 'manager', label: '负责人' },
  { key: 'phone', label: '电话' },
  { key: 'buildingHouseholds', label: '建筑户数' },
  { key: 'residentHouseholds', label: '常住户数' },
  { key: 'residentPopulation', label: '常住人口' }
]
const fields = [
  { key: 'name', label: '小区名称', type: 'text', required: true },
  { key: 'address', label: '详细地址', type: 'text' },
  { key: 'manager', label: '物业负责人', type: 'text' },
  { key: 'company', label: '物业公司', type: 'text' },
  { key: 'phone', label: '联系电话', type: 'text' },
  { key: 'buildingHouseholds', label: '建筑户数', type: 'number' },
  { key: 'residentHouseholds', label: '常住户数', type: 'number' },
  { key: 'residentPopulation', label: '常住人口', type: 'number' },
  { key: 'remark', label: '备注', type: 'textarea' }
]
const searches = [
  { key: 'name', label: '小区名称' },
  { key: 'manager', label: '负责人' }
]
</script>
