<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">居民管理</span>
      <el-button type="success" @click="openAdd">新增居民</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="姓名" clearable />
      </el-form-item>
      <el-form-item label="所属小区">
        <el-select v-model="query.communityId" placeholder="全部" clearable style="width:150px">
          <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
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
      <el-table-column prop="name" label="姓名" width="90" />
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="idCard" label="身份证号" width="200" show-overflow-tooltip />
      <el-table-column prop="phone" label="联系电话" width="140" show-overflow-tooltip />
      <el-table-column prop="communityName" label="所属小区" min-width="140" show-overflow-tooltip />
      <el-table-column prop="building" label="楼栋" width="80" />
      <el-table-column prop="unit" label="单元" width="70" />
      <el-table-column prop="room" label="房号" width="80" />
      <el-table-column prop="householdType" label="户籍类型" width="90" />
      <el-table-column prop="nativePlace" label="籍贯" width="120" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑居民信息' : '新增居民信息'">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" maxlength="18" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="所属小区">
          <el-select v-model="form.communityId" placeholder="请选择小区" clearable style="width:100%">
            <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋"><el-input v-model="form.building" /></el-form-item>
        <el-form-item label="单元"><el-input v-model="form.unit" /></el-form-item>
        <el-form-item label="房号"><el-input v-model="form.room" /></el-form-item>
        <el-form-item label="户籍类型">
          <el-select v-model="form.householdType" placeholder="请选择" clearable style="width:100%">
            <el-option label="常住" value="常住" />
            <el-option label="暂住" value="暂住" />
          </el-select>
        </el-form-item>
        <el-form-item label="籍贯"><el-input v-model="form.nativePlace" /></el-form-item>
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
import { pagePerson, savePerson, updatePerson, deletePerson, batchDeletePersons, exportPersons, importPersons } from '../../api/person'
import { listCommunities } from '../../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'
import { validateIdCard } from '../../utils/validators'

const list = ref([])
const selectedIds = ref([])
const total = ref(0)
const communities = ref([])
const query = reactive({ page: 1, size: 10, name: '', communityId: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const empty = { id: null, name: '', gender: '男', idCard: '', phone: '', communityId: null, building: '', unit: '', room: '', householdType: '', nativePlace: '', remark: '' }
const form = reactive({ ...empty })
const formRef = ref()
const rules = {
  idCard: [{ validator: validateIdCard, trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function load() {
  pagePerson(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}
function loadCommunities() {
  listCommunities().then(res => { communities.value = res.data })
}
function reset() {
  query.name = ''
  query.communityId = null
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
    if (isEdit.value) updatePerson(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
    else savePerson(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
  })
}
function remove(row) {
  ElMessageBox.confirm('确认删除该居民？', '提示', { type: 'warning' })
    .then(() => deletePerson(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}

function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}

function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的居民')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 位居民？`, '提示', { type: 'warning' })
    .then(() => batchDeletePersons(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}
function onExport() {
  exportPersons({ name: query.name, communityId: query.communityId })
    .then(() => ElMessage.success('导出成功'))
    .catch(() => {})
}
function onImport(file) {
  importPersons(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}
onMounted(() => { load(); loadCommunities() })
</script>
