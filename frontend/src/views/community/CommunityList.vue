<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">小区管理</span>
      <el-button type="success" @click="openAdd">新增小区</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="小区名称">
        <el-input v-model="query.name" placeholder="小区名称" clearable />
      </el-form-item>
      <el-form-item label="物业负责人">
        <el-input v-model="query.manager" placeholder="物业负责人" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="warning" @click="onExport">导出</el-button>
        <el-upload :show-file-list="false" :before-upload="onImport" accept=".xlsx,.xls">
          <el-button type="success" plain>导入</el-button>
        </el-upload>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="batchRemove">
          {{ selectedIds.length ? '批量删除 (' + selectedIds.length + ')' : '批量删除' }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border stripe row-key="id" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="小区名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="manager" label="物业负责人" width="110" show-overflow-tooltip />
      <el-table-column prop="company" label="物业公司" min-width="160" show-overflow-tooltip />
      <el-table-column prop="phone" label="联系电话" width="140" show-overflow-tooltip />
      <el-table-column prop="buildingHouseholds" label="建筑户数" width="90" />
      <el-table-column prop="residentHouseholds" label="常住户数" width="90" />
      <el-table-column prop="residentPopulation" label="常住人口" width="90" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top:15px;justify-content:flex-end"
      background layout="total, sizes, prev, pager, next"
      :total="total" :current-page="query.page" :page-size="query.size"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="onPage" @size-change="onSize" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑小区' : '新增小区'">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="小区名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="物业负责人"><el-input v-model="form.manager" /></el-form-item>
        <el-form-item label="物业公司"><el-input v-model="form.company" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="建筑户数"><el-input-number v-model="form.buildingHouseholds" :min="0" /></el-form-item>
        <el-form-item label="常住户数"><el-input-number v-model="form.residentHouseholds" :min="0" /></el-form-item>
        <el-form-item label="常住人口"><el-input-number v-model="form.residentPopulation" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { pageCommunities, saveCommunity, updateCommunity, deleteCommunity, batchDeleteCommunities, exportCommunities, importCommunities } from '../../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const selectedIds = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, name: '', manager: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const empty = { id: null, name: '', address: '', manager: '', company: '', phone: '', buildingHouseholds: 0, residentHouseholds: 0, residentPopulation: 0, remark: '' }
const form = reactive({ ...empty })
const formRef = ref()
const rules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function load() {
  pageCommunities(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}
function reset() {
  query.name = ''
  query.manager = ''
  query.page = 1
  load()
}
function onPage(p) { query.page = p; nextTick(() => load()) }
function onSize(s) { query.size = s; query.page = 1; nextTick(() => load()) }
function openAdd() { isEdit.value = false; Object.assign(form, empty); dialogVisible.value = true }
function openEdit(row) { isEdit.value = true; Object.assign(form, { ...row }); dialogVisible.value = true }
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) return
    if (isEdit.value) updateCommunity(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
    else saveCommunity(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
  })
}
function remove(row) {
  ElMessageBox.confirm('确认删除该小区？', '提示', { type: 'warning' })
    .then(() => deleteCommunity(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}

function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}

function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的小区')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个小区？`, '提示', { type: 'warning' })
    .then(() => batchDeleteCommunities(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}
function onExport() {
  exportCommunities({ name: query.name, manager: query.manager })
    .then(() => ElMessage.success('导出成功'))
    .catch(() => {})
}
function onImport(file) {
  importCommunities(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}
onMounted(load)
</script>

<style scoped>
.search-bar { margin-bottom: 15px; }
</style>
