<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">车位情况统计</span>
      <el-button type="success" @click="openAdd">新增车位记录</el-button>
    </div>

    <!-- 统计概览 -->
    <div class="stat-row">
      <div v-for="s in statCards" :key="s.label" class="stat-card">
        <div class="stat-icon" :style="{ background: s.bg }">
          <el-icon :size="22" color="#fff"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="小区名称">
        <el-input v-model="query.communityName" placeholder="小区名称" clearable style="width:150px" />
      </el-form-item>
      <el-form-item label="社区">
        <el-input v-model="query.communityOrg" placeholder="如 寺家庄社区" clearable style="width:140px" />
      </el-form-item>
      <el-form-item label="开发企业">
        <el-input v-model="query.developer" placeholder="开发企业名称" clearable style="width:170px" />
      </el-form-item>
      <el-form-item label="物业企业">
        <el-input v-model="query.propertyCompany" placeholder="物业服务企业" clearable style="width:170px" />
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

    <el-table :data="list" border stripe row-key="id" size="small" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" fixed="left" />
      <el-table-column prop="id" label="ID" width="60" fixed="left" />
      <el-table-column prop="seqNo" label="序号" width="60" />
      <el-table-column prop="communityName" label="住宅小区名称" width="180" fixed="left" show-overflow-tooltip />
      <el-table-column prop="communityOrg" label="社区" width="130" show-overflow-tooltip />
      <el-table-column prop="developer" label="开发企业名称" width="240" show-overflow-tooltip />
      <el-table-column prop="propertyCompany" label="物业服务企业名称" width="240" show-overflow-tooltip />
      <el-table-column prop="plannedHouseholds" label="规划住宅户数" width="120" align="center" />
      <el-table-column prop="deliveryTime" label="交房时间" width="150" show-overflow-tooltip />
      <el-table-column prop="plannedTotal" label="规划车位总数" width="130" align="center" show-overflow-tooltip />
      <el-table-column prop="soldCount" label="已出售" width="90" align="center" />
      <el-table-column prop="rentedCount" label="已出租" width="90" align="center" />
      <el-table-column prop="idleCount" label="闲置" width="90" align="center" />
      <el-table-column prop="remark" label="备注" width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
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

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑车位信息' : '新增车位信息'" width="860px" top="5vh">
      <el-form ref="formRef" :model="form" label-width="140px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="住宅小区名称">
              <el-select v-model="form.communityName" placeholder="可输入任意小区名" clearable filterable allow-create style="width:100%">
                <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="序号"><el-input v-model="form.seqNo" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="县（市、区）"><el-input v-model="form.county" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="街道办"><el-input v-model="form.town" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="社区"><el-input v-model="form.communityOrg" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交房时间"><el-input v-model="form.deliveryTime" placeholder="如 2019 / 一期2019二期2024" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="开发企业名称"><el-input v-model="form.developer" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="物业服务企业名称"><el-input v-model="form.propertyCompany" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规划住宅户数"><el-input v-model="form.plannedHouseholds" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规划车位总数"><el-input v-model="form.plannedTotal" placeholder="可填「规划11个，实际15」这类说明" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="已出售车位数"><el-input v-model="form.soldCount" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="已出租车位数"><el-input v-model="form.rentedCount" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="目前闲置车位数"><el-input v-model="form.idleCount" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情 -->
    <el-drawer v-model="detailVisible" title="车位情况详情" size="620px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="住宅小区名称">{{ current.communityName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联本系统小区">{{ current.communityId ? '已关联 (ID ' + current.communityId + ')' : '未关联' }}</el-descriptions-item>
        <el-descriptions-item label="序号">{{ current.seqNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="县（市、区）">{{ current.county || '-' }}</el-descriptions-item>
        <el-descriptions-item label="街道办">{{ current.town || '-' }}</el-descriptions-item>
        <el-descriptions-item label="社区">{{ current.communityOrg || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开发企业名称">{{ current.developer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物业服务企业名称">{{ current.propertyCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规划住宅户数">{{ current.plannedHouseholds || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交房时间">{{ current.deliveryTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规划车位总数">{{ current.plannedTotal || '-' }}</el-descriptions-item>
        <el-descriptions-item label="已出售车位数">{{ current.soldCount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="已出租车位数">{{ current.rentedCount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目前闲置车位数">{{ current.idleCount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ current.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import {
  pageParkings, statsParkings, saveParking, updateParking, deleteParking,
  batchDeleteParkings, exportParkings, importParkings
} from '../../api/parking'
import { listCommunities } from '../../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, House, Van, Sell, Tickets, Clock } from '@element-plus/icons-vue'

const selectedIds = ref([])
const list = ref([])
const total = ref(0)
const communities = ref([])
const stats = ref({ recordCount: 0, plannedHouseholds: 0, plannedTotal: 0, soldCount: 0, rentedCount: 0, idleCount: 0, linkedCount: 0 })
const query = reactive({ page: 1, size: 10, communityName: '', communityOrg: '', developer: '', propertyCompany: '' })

const statCards = computed(() => [
  { label: '统计小区数', value: stats.value.recordCount, icon: OfficeBuilding, bg: '#409eff' },
  { label: '规划住宅户数', value: stats.value.plannedHouseholds, icon: House, bg: '#67c23a' },
  { label: '规划车位总数', value: stats.value.plannedTotal, icon: Van, bg: '#909399' },
  { label: '已出售车位', value: stats.value.soldCount, icon: Sell, bg: '#e6a23c' },
  { label: '已出租车位', value: stats.value.rentedCount, icon: Tickets, bg: '#909399' },
  { label: '目前闲置车位', value: stats.value.idleCount, icon: Clock, bg: '#f56c6c' }
])

const dialogVisible = ref(false)
const detailVisible = ref(false)
const current = ref(null)
const isEdit = ref(false)
const empty = {
  id: null, communityId: null, seqNo: '', county: '鹿泉区', town: '寺家庄镇', communityOrg: '寺家庄社区',
  communityName: '', developer: '', propertyCompany: '', plannedHouseholds: '', deliveryTime: '',
  plannedTotal: '', soldCount: '', rentedCount: '', idleCount: '', remark: ''
}
const form = reactive({ ...empty })
const formRef = ref()

function filterParams() {
  return {
    communityName: query.communityName,
    communityOrg: query.communityOrg,
    developer: query.developer,
    propertyCompany: query.propertyCompany
  }
}
function load() {
  pageParkings(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
  statsParkings(filterParams()).then(res => { stats.value = res.data })
}
function loadCommunities() {
  listCommunities().then(res => { communities.value = res.data })
}
function reset() {
  query.communityName = ''
  query.communityOrg = ''
  query.developer = ''
  query.propertyCompany = ''
  query.page = 1
  load()
}
function onPage(p) { query.page = p; nextTick(() => load()) }
function onSize(s) { query.size = s; query.page = 1; nextTick(() => load()) }
function openAdd() { isEdit.value = false; Object.assign(form, empty); dialogVisible.value = true }
function openEdit(row) { isEdit.value = true; Object.assign(form, { ...empty, ...row }); dialogVisible.value = true }
function openDetail(row) { current.value = row; detailVisible.value = true }
function submit() {
  if (isEdit.value) {
    updateParking(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
  } else {
    saveParking(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
  }
}
function remove(row) {
  ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    .then(() => deleteParking(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}
function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}
function batchRemove() {
  if (selectedIds.value.length === 0) { ElMessage.warning('请先勾选要删除的记录'); return }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    .then(() => batchDeleteParkings(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}
function onExport() {
  exportParkings(filterParams()).then(() => ElMessage.success('导出成功')).catch(() => {})
}
function onImport(file) {
  importParkings(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}
onMounted(() => { load(); loadCommunities() })
</script>

<style scoped>
.stat-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.stat-card {
  flex: 1 1 150px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
}
.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}
.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
