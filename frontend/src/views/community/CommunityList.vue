<template>
  <div>
    <!-- ============ 顶部统计卡片 ============ -->
    <div class="stat-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.label">
        <div class="stat-icon" :style="{ background: s.bg, color: s.color }">
          <el-icon :size="20"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </div>

    <el-card class="page-card">
      <div class="page-header">
        <span class="page-title">小区管理</span>
        <el-button type="success" @click="openAdd">新增小区</el-button>
      </div>

      <!-- ============ 搜索栏 ============ -->
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="小区名称">
          <el-input v-model="query.name" placeholder="小区名称" clearable @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="物业公司">
          <el-input v-model="query.company" placeholder="物业公司" clearable @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="所属社区">
          <el-input v-model="query.communityOrg" placeholder="乡镇/社区居委会" clearable @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="物业性质">
          <el-select v-model="query.propertyType" placeholder="全部" clearable style="width:120px">
            <el-option v-for="t in propertyTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="承接方式">
          <el-select v-model="query.takeoverMode" placeholder="全部" clearable style="width:130px">
            <el-option v-for="t in takeoverModes" :key="t" :label="t" :value="t" />
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

      <!-- ============ 表格 ============ -->
      <el-table :data="list" border stripe row-key="id" @selection-change="onSelectionChange"
                v-loading="loading">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="小区名称" min-width="130" fixed show-overflow-tooltip />
        <el-table-column prop="address" label="详细地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="company" label="物业公司" min-width="180" show-overflow-tooltip />
        <el-table-column prop="manager" label="负责人" width="90" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系方式" width="130" show-overflow-tooltip />
        <el-table-column label="物业性质" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.propertyType" size="small" effect="plain">{{ row.propertyType }}</el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="户数 住/商" width="110">
          <template #default="{ row }">
            {{ fmt(row.householdsResidential) }} / {{ fmt(row.householdsCommercial) }}
          </template>
        </el-table-column>
        <el-table-column label="建筑(万㎡)" width="105">
          <template #default="{ row }">{{ areaOf(row) }}</template>
        </el-table-column>
        <el-table-column label="电梯(部)" width="90">
          <template #default="{ row }">{{ fmt(row.elevatorCount) }}</template>
        </el-table-column>
        <el-table-column label="机动车位" width="95">
          <template #default="{ row }">{{ parkingOf(row) }}</template>
        </el-table-column>
        <el-table-column prop="takeoverMode" label="承接方式" width="105" show-overflow-tooltip />
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top:15px;justify-content:flex-end"
        background layout="total, sizes, prev, pager, next"
        :total="total" :current-page="query.page" :page-size="query.size"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="onPage" @size-change="onSize" />
    </el-card>

    <!-- ============ 新增/编辑弹窗 ============ -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑小区' : '新增小区'" width="920px" top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="edit-form">
        <div class="form-section">基础信息</div>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="小区名称" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="物业公司"><el-input v-model="form.company" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="详细地址"><el-input v-model="form.address" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="法人代表"><el-input v-model="form.legalPerson" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="企业联系电话"><el-input v-model="form.companyPhone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="项目负责人"><el-input v-model="form.manager" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="负责人联系方式" prop="phone"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="所属乡镇/社区"><el-input v-model="form.communityOrg" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="建设时间"><el-input v-model="form.buildTime" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="物业性质">
              <el-select v-model="form.propertyType" placeholder="请选择" clearable style="width:100%">
                <el-option v-for="t in propertyTypes" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-section">建筑与户数</div>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="建筑面积-住宅"><el-input-number v-model="form.areaResidential" :min="0" :precision="4" :controls="false" style="width:100%" /><span class="unit">万㎡</span></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="建筑面积-商业"><el-input-number v-model="form.areaCommercial" :min="0" :precision="4" :controls="false" style="width:100%" /><span class="unit">万㎡</span></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="住宅户数"><el-input-number v-model="form.householdsResidential" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="商业户数"><el-input-number v-model="form.householdsCommercial" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="建筑户数"><el-input-number v-model="form.buildingHouseholds" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="常住户数"><el-input-number v-model="form.residentHouseholds" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="常住人口"><el-input-number v-model="form.residentPopulation" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="多层楼栋/层数"><el-input v-model="form.buildingMulti" placeholder="如 2栋/6层" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="高层楼栋/层数"><el-input v-model="form.buildingHigh" placeholder="如 2栋/18层" /></el-form-item></el-col>
        </el-row>

        <div class="form-section">车位与充电</div>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="机动车位-地上"><el-input-number v-model="form.parkingGround" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="机动车位-地下"><el-input-number v-model="form.parkingUnderground" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="机动充电桩-地上"><el-input-number v-model="form.chargingGround" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="机动充电桩-地下"><el-input-number v-model="form.chargingUnderground" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="非机动车位-地上"><el-input-number v-model="form.nonmotorGround" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="非机动车位-地下"><el-input-number v-model="form.nonmotorUnderground" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="电动车充电车棚"><el-input v-model="form.ebikeShed" placeholder="如 3 或 3组" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="电动车充电头"><el-input-number v-model="form.ebikePort" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
        </el-row>

        <div class="form-section">安全与配套</div>
        <el-row :gutter="12">
          <el-col :span="6"><el-form-item label="电梯(部)"><el-input-number v-model="form.elevatorCount" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="微型消防站"><el-input-number v-model="form.fireStation" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="独立烟感"><el-input-number v-model="form.smokeDetector" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="消火栓"><el-input-number v-model="form.fireHydrant" :min="0" :controls="false" style="width:100%" /></el-form-item></el-col>
        </el-row>

        <div class="form-section">水电暖与承接方式</div>
        <el-row :gutter="12">
          <el-col :span="6"><el-form-item label="水一管到户"><YesNo v-model="form.waterManaged" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="电一管到户"><YesNo v-model="form.powerManaged" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="暖一管到户"><YesNo v-model="form.heatManaged" /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="承接方式">
              <el-select v-model="form.takeoverMode" placeholder="请选择" clearable style="width:100%" filterable allow-create>
                <el-option v-for="t in takeoverModes" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="台账备注"><el-input v-model="form.ledgerRemark" placeholder="未交房注明等" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- ============ 详情抽屉 ============ -->
    <el-drawer v-model="detailVisible" :title="detail.name ? detail.name + ' · 详情' : '小区详情'" size="620px">
      <el-descriptions :column="2" border size="small" class="detail-desc">
        <el-descriptions-item label="小区名称" :span="2">{{ dash(detail.name) }}</el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">{{ dash(detail.address) }}</el-descriptions-item>
        <el-descriptions-item label="所属乡镇/社区" :span="2">{{ dash(detail.communityOrg) }}</el-descriptions-item>
        <el-descriptions-item label="物业公司" :span="2">{{ dash(detail.company) }}</el-descriptions-item>
        <el-descriptions-item label="法人代表">{{ dash(detail.legalPerson) }}</el-descriptions-item>
        <el-descriptions-item label="企业联系电话">{{ dash(detail.companyPhone) }}</el-descriptions-item>
        <el-descriptions-item label="项目负责人">{{ dash(detail.manager) }}</el-descriptions-item>
        <el-descriptions-item label="负责人联系方式">{{ dash(detail.phone) }}</el-descriptions-item>
        <el-descriptions-item label="建设时间">{{ dash(detail.buildTime) }}</el-descriptions-item>
        <el-descriptions-item label="物业性质">{{ dash(detail.propertyType) }}</el-descriptions-item>

        <el-descriptions-item label="建筑面积-住宅">{{ dash(detail.areaResidential) }} 万㎡</el-descriptions-item>
        <el-descriptions-item label="建筑面积-商业">{{ dash(detail.areaCommercial) }} 万㎡</el-descriptions-item>
        <el-descriptions-item label="多层楼栋">{{ dash(detail.buildingMulti) }}</el-descriptions-item>
        <el-descriptions-item label="高层楼栋">{{ dash(detail.buildingHigh) }}</el-descriptions-item>
        <el-descriptions-item label="住宅户数">{{ dash(detail.householdsResidential) }}</el-descriptions-item>
        <el-descriptions-item label="商业户数">{{ dash(detail.householdsCommercial) }}</el-descriptions-item>
        <el-descriptions-item label="建筑户数">{{ dash(detail.buildingHouseholds) }}</el-descriptions-item>
        <el-descriptions-item label="常住户数">{{ dash(detail.residentHouseholds) }}</el-descriptions-item>
        <el-descriptions-item label="常住人口">{{ dash(detail.residentPopulation) }}</el-descriptions-item>

        <el-descriptions-item label="机动车位-地上">{{ dash(detail.parkingGround) }}</el-descriptions-item>
        <el-descriptions-item label="机动车位-地下">{{ dash(detail.parkingUnderground) }}</el-descriptions-item>
        <el-descriptions-item label="机动充电桩-地上">{{ dash(detail.chargingGround) }}</el-descriptions-item>
        <el-descriptions-item label="机动充电桩-地下">{{ dash(detail.chargingUnderground) }}</el-descriptions-item>
        <el-descriptions-item label="非机动车位-地上">{{ dash(detail.nonmotorGround) }}</el-descriptions-item>
        <el-descriptions-item label="非机动车位-地下">{{ dash(detail.nonmotorUnderground) }}</el-descriptions-item>
        <el-descriptions-item label="电动车充电车棚">{{ dash(detail.ebikeShed) }}</el-descriptions-item>
        <el-descriptions-item label="电动车充电头">{{ dash(detail.ebikePort) }}</el-descriptions-item>

        <el-descriptions-item label="电梯(部)">{{ dash(detail.elevatorCount) }}</el-descriptions-item>
        <el-descriptions-item label="微型消防站">{{ dash(detail.fireStation) }}</el-descriptions-item>
        <el-descriptions-item label="独立烟感">{{ dash(detail.smokeDetector) }}</el-descriptions-item>
        <el-descriptions-item label="消火栓">{{ dash(detail.fireHydrant) }}</el-descriptions-item>

        <el-descriptions-item label="水一管到户">{{ dash(detail.waterManaged) }}</el-descriptions-item>
        <el-descriptions-item label="电一管到户">{{ dash(detail.powerManaged) }}</el-descriptions-item>
        <el-descriptions-item label="暖一管到户">{{ dash(detail.heatManaged) }}</el-descriptions-item>
        <el-descriptions-item label="承接方式">{{ dash(detail.takeoverMode) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ dash(detail.remark) }}</el-descriptions-item>
        <el-descriptions-item label="台账备注" :span="2">{{ dash(detail.ledgerRemark) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, h } from 'vue'
import {
  pageCommunities, saveCommunity, updateCommunity, deleteCommunity,
  batchDeleteCommunities, exportCommunities, importCommunities, statsCommunities
} from '../../api/community'
import { ElMessage, ElMessageBox, ElSelect, ElOption } from 'element-plus'
import { OfficeBuilding, User, Shop, House, Van, Lightning, TopRight, Bell } from '@element-plus/icons-vue'

/* ---- 内联小组件：是/否/— 下拉 ---- */
const YesNo = {
  props: { modelValue: String },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    return () => h(ElSelect, {
      modelValue: props.modelValue,
      'onUpdate:modelValue': v => emit('update:modelValue', v),
      placeholder: '请选择', clearable: true, style: 'width:100%'
    }, () => ['是', '否', '—'].map(t => h(ElOption, { key: t, label: t, value: t })))
  }
}

const propertyTypes = ['住宅', '商住', '住宅、商住', '商业', '园区']
const takeoverModes = ['前期物业', '业委会选聘', '业主推荐', '自治', '单位管理', '协议选聘']

const list = ref([])
const loading = ref(false)
const selectedIds = ref([])
const total = ref(0)
const stats = ref({})
const query = reactive({
  page: 1, size: 10, name: '', company: '', communityOrg: '', propertyType: '', takeoverMode: ''
})

const statCards = computed(() => {
  const s = stats.value || {}
  const area = (Number(s.areaResidential || 0) + Number(s.areaCommercial || 0))
  return [
    { label: '小区总数', value: s.communityCount ?? '—', icon: OfficeBuilding, bg: '#ecf5ff', color: '#409eff' },
    { label: '住宅户数', value: s.householdsResidential ?? '—', icon: House, bg: '#f0f9eb', color: '#67c23a' },
    { label: '商业户数', value: s.householdsCommercial ?? '—', icon: Shop, bg: '#fdf6ec', color: '#e6a23c' },
    { label: '建筑面积(万㎡)', value: area.toFixed(2), icon: TopRight, bg: '#f4f0ff', color: '#8e5ef7' },
    { label: '机动车位(个)', value: s.parkingTotal ?? '—', icon: Van, bg: '#eef7f6', color: '#14b8a6' },
    { label: '电动车充电头', value: s.chargingTotal ?? '—', icon: Lightning, bg: '#fff1f0', color: '#f56c6c' },
    { label: '电梯(部)', value: s.elevatorCount ?? '—', icon: User, bg: '#eef3ff', color: '#5b8ff9' },
    { label: '消防设施(个)', value: (Number(s.fireStation || 0) + Number(s.smokeDetector || 0) + Number(s.fireHydrant || 0)), icon: Bell, bg: '#fff7e6', color: '#fa8c16' }
  ]
})

const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const detail = ref({})
const empty = {
  id: null, name: '', address: '', company: '', legalPerson: '', companyPhone: '',
  manager: '', phone: '', communityOrg: '', buildTime: '', propertyType: '',
  areaResidential: null, areaCommercial: null, buildingMulti: '', buildingHigh: '',
  householdsResidential: null, householdsCommercial: null,
  buildingHouseholds: null, residentHouseholds: null, residentPopulation: null,
  parkingGround: null, parkingUnderground: null, chargingGround: null, chargingUnderground: null,
  nonmotorGround: null, nonmotorUnderground: null, ebikeShed: '', ebikePort: null,
  elevatorCount: null, fireStation: null, smokeDetector: null, fireHydrant: null,
  waterManaged: '', powerManaged: '', heatManaged: '', takeoverMode: '',
  remark: '', ledgerRemark: ''
}
const form = reactive({ ...empty })
const formRef = ref()
const rules = {
  name: [{ required: true, message: '请输入小区名称', trigger: 'blur' }],
  phone: [{ pattern: /^[0-9\-() ]{7,20}$/, message: '电话格式不正确', trigger: 'blur' }]
}

function fmt(v) { return v === null || v === undefined || v === '' ? '—' : v }
function dash(v) { return v === null || v === undefined || v === '' ? '—' : v }
function areaOf(row) {
  const a = Number(row.areaResidential || 0) + Number(row.areaCommercial || 0)
  return a > 0 ? a.toFixed(2) : '—'
}
function parkingOf(row) {
  const a = Number(row.parkingGround || 0) + Number(row.parkingUnderground || 0)
  return a > 0 ? a : '—'
}

function load() {
  loading.value = true
  pageCommunities(query)
    .then(res => { list.value = res.data.records; total.value = res.data.total })
    .finally(() => { loading.value = false })
}
function loadStats() {
  statsCommunities().then(res => { stats.value = res.data || {} }).catch(() => {})
}
function reset() {
  Object.assign(query, { page: 1, name: '', company: '', communityOrg: '', propertyType: '', takeoverMode: '' })
  load()
}
function onPage(p) { query.page = p; nextTick(load) }
function onSize(s) { query.size = s; query.page = 1; nextTick(load) }
function openAdd() { isEdit.value = false; Object.assign(form, empty); dialogVisible.value = true }
function openEdit(row) {
  isEdit.value = true
  Object.assign(form, empty, row)
  dialogVisible.value = true
}
function openDetail(row) { detail.value = { ...row }; detailVisible.value = true }
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) return
    const done = (msg) => {
      ElMessage.success(msg)
      dialogVisible.value = false
      load(); loadStats()
    }
    if (isEdit.value) updateCommunity(form).then(() => done('修改成功'))
    else saveCommunity(form).then(() => done('新增成功'))
  })
}
function remove(row) {
  ElMessageBox.confirm(`确认删除「${row.name}」？`, '提示', { type: 'warning' })
    .then(() => deleteCommunity(row.id).then(() => { ElMessage.success('删除成功'); load(); loadStats() }))
}
function onSelectionChange(val) { selectedIds.value = val.map(r => r.id) }
function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的小区')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个小区？`, '提示', { type: 'warning' })
    .then(() => batchDeleteCommunities(selectedIds.value)
      .then(() => {
        ElMessage.success('删除成功')
        selectedIds.value = []
        load(); loadStats()
      }))
}
function onExport() {
  exportCommunities({
    name: query.name, company: query.company, communityOrg: query.communityOrg,
    propertyType: query.propertyType, takeoverMode: query.takeoverMode
  }).then(() => ElMessage.success('导出成功')).catch(() => {})
}
function onImport(file) {
  importCommunities(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load(); loadStats() })
    .catch(() => {})
  return false
}

onMounted(() => { load(); loadStats() })
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 14px;
}
@media (max-width: 1200px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-value { font-size: 20px; font-weight: 700; color: #1f2d3d; line-height: 1.2; }
.stat-label { font-size: 12px; color: #909399; margin-top: 2px; }

.search-bar { padding: 16px 16px 0; }
.form-section {
  font-size: 14px;
  font-weight: 600;
  color: #1f2d3d;
  border-left: 3px solid var(--cms-primary);
  padding-left: 8px;
  margin: 6px 0 14px;
}
.edit-form :deep(.el-form-item) { margin-bottom: 14px; }
.edit-form :deep(.el-form-item__label) { font-size: 13px; }
.unit { margin-left: 6px; color: #909399; font-size: 12px; }
.detail-desc :deep(.el-descriptions__label) { width: 130px; }
</style>
