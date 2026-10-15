<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">残疾人管理</span>
      <el-button type="success" @click="openAdd">新增残疾人</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="姓名" clearable />
      </el-form-item>
      <el-form-item label="残疾类型">
        <el-select v-model="query.disabilityType" placeholder="全部" clearable style="width:130px">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
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
      <el-table-column prop="disabilityType" label="残疾类型" width="90" />
      <el-table-column prop="disabilityLevel" label="等级" width="80" />
      <el-table-column prop="ethnicity" label="民族" width="80" />
      <el-table-column prop="education" label="文化程度" width="100" show-overflow-tooltip />
      <el-table-column prop="maritalStatus" label="婚姻状况" width="100" show-overflow-tooltip />
      <el-table-column prop="householdType" label="户口类型" width="120" show-overflow-tooltip />
      <el-table-column prop="disabilityCertNo" label="残疾证号" width="160" show-overflow-tooltip />
      <el-table-column prop="communityName" label="所属小区" min-width="130" show-overflow-tooltip />
      <el-table-column prop="guardian" label="监护人" width="100" show-overflow-tooltip />
      <el-table-column prop="guardianRelation" label="与残疾人关系" width="130" show-overflow-tooltip />
      <el-table-column prop="guardianPhone" label="监护人电话" width="140" show-overflow-tooltip />
      <el-table-column prop="phone" label="本人电话" width="130" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑残疾人信息' : '新增残疾人信息'">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" maxlength="18" /></el-form-item>
        <el-form-item label="残疾类型">
          <el-select v-model="form.disabilityType" style="width:100%">
            <el-option v-for="t in types" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="残疾等级">
          <el-select v-model="form.disabilityLevel" style="width:100%">
            <el-option label="一级" value="一级" />
            <el-option label="二级" value="二级" />
            <el-option label="三级" value="三级" />
            <el-option label="四级" value="四级" />
          </el-select>
        </el-form-item>
        <el-form-item label="民族"><el-input v-model="form.ethnicity" maxlength="10" /></el-form-item>
        <el-form-item label="文化程度"><el-input v-model="form.education" maxlength="50" /></el-form-item>
        <el-form-item label="婚姻状况"><el-input v-model="form.maritalStatus" maxlength="20" /></el-form-item>
        <el-form-item label="户口类型"><el-input v-model="form.householdType" maxlength="20" /></el-form-item>
        <el-form-item label="残疾证号"><el-input v-model="form.disabilityCertNo" maxlength="50" /></el-form-item>
        <el-form-item label="所属小区">
          <el-select v-model="form.communityId" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="本人电话" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="监护人"><el-input v-model="form.guardian" /></el-form-item>
        <el-form-item label="与残疾人关系">
          <el-select v-model="form.guardianRelation" clearable placeholder="请选择" style="width:100%">
            <el-option v-for="r in guardianRelations" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="监护人电话" prop="guardianPhone"><el-input v-model="form.guardianPhone" maxlength="11" /></el-form-item>
        <el-form-item label="家庭住址"><el-input v-model="form.address" /></el-form-item>
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
import { pageDisabled, saveDisabled, updateDisabled, deleteDisabled, batchDeleteDisabled, exportDisabled, importDisabled } from '../../api/disabled'
import { listCommunities } from '../../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'
import { validateIdCard } from '../../utils/validators'

const types = ['视力', '听力', '言语', '肢体', '智力', '精神', '多重']
const selectedIds = ref([])
const guardianRelations = ['配偶', '父母', '子女', '兄弟姐妹', '祖孙', '其他']
const list = ref([])
const total = ref(0)
const communities = ref([])
const query = reactive({ page: 1, size: 10, name: '', disabilityType: '', communityId: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const empty = { id: null, name: '', gender: '男', idCard: '', disabilityType: '', disabilityLevel: '', communityId: null, phone: '', guardian: '', guardianPhone: '', guardianRelation: '', disabilityCertNo: '', ethnicity: '', education: '', maritalStatus: '', householdType: '', address: '', remark: '' }
const form = reactive({ ...empty })
const formRef = ref()
const rules = {
  idCard: [{ validator: validateIdCard, trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  guardianPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function load() {
  pageDisabled(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}
function loadCommunities() {
  listCommunities().then(res => { communities.value = res.data })
}
function reset() {
  query.name = ''
  query.disabilityType = ''
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
    if (isEdit.value) updateDisabled(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
    else saveDisabled(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
  })
}
function remove(row) {
  ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    .then(() => deleteDisabled(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}

function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}

function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的记录')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    .then(() => batchDeleteDisabled(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}
function onExport() {
  exportDisabled({ name: query.name, disabilityType: query.disabilityType, communityId: query.communityId })
    .then(() => ElMessage.success('导出成功'))
    .catch(() => {})
}
function onImport(file) {
  importDisabled(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}
onMounted(() => { load(); loadCommunities() })
</script>
