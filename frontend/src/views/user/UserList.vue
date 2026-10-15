<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">用户管理</span>
      <el-button type="success" @click="openAdd">新增用户</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="账号">
        <el-input v-model="query.username" placeholder="账号" clearable />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="query.realName" placeholder="真实姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
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
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="账号" min-width="120" show-overflow-tooltip />
      <el-table-column prop="realName" label="真实姓名" min-width="100" />
      <el-table-column prop="phone" label="手机号" width="140" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top:15px;justify-content:flex-end"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      :current-page="query.page"
      :page-size="query.size"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="onPage"
      @size-change="onSize" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="账号">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
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
import { pageUsers, saveUser, updateUser, deleteUser, batchDeleteUsers, exportUsers, importUsers } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const selectedIds = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, username: '', realName: '', status: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, username: '', realName: '', phone: '', email: '', role: 'USER', status: 1 })
const formRef = ref()
const rules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function load() {
  pageUsers(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}

function reset() {
  query.username = ''
  query.realName = ''
  query.status = null
  query.page = 1
  load()
}

function onPage(p) { query.page = p; nextTick(() => load()) }
function onSize(s) { query.size = s; query.page = 1; nextTick(() => load()) }

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', realName: '', phone: '', email: '', role: 'USER', status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

function submit() {
  formRef.value.validate((valid) => {
    if (!valid) return
    if (isEdit.value) {
      updateUser(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
    } else {
      saveUser(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
    }
  })
}

function remove(row) {
  ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
    .then(() => deleteUser(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}

function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}

function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的用户')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个用户？`, '提示', { type: 'warning' })
    .then(() => batchDeleteUsers(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}

function onExport() {
  exportUsers({ username: query.username, realName: query.realName, status: query.status })
    .then(() => ElMessage.success('导出成功'))
    .catch(() => {})
}

function onImport(file) {
  importUsers(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}

onMounted(load)
</script>

<style scoped>
.search-bar { margin-bottom: 15px; }
</style>
