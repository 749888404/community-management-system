<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">通讯基站统计</span>
      <el-button type="success" @click="openAdd">新增基站记录</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="小区名称">
        <el-input v-model="query.communityName" placeholder="小区名称" clearable style="width:150px" />
      </el-form-item>
      <el-form-item label="乡镇/社区">
        <el-input v-model="query.townCommunity" placeholder="如 上庄镇" clearable style="width:130px" />
      </el-form-item>
      <el-form-item label="运营商">
        <el-select v-model="query.operatorName" placeholder="全部" clearable filterable allow-create style="width:130px">
          <el-option v-for="o in operatorOptions" :key="o" :label="o" :value="o" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型">
        <el-select v-model="query.deviceType" placeholder="全部" clearable filterable allow-create style="width:150px">
          <el-option v-for="d in deviceTypes" :key="d" :label="d" :value="d" />
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

    <el-table :data="list" border stripe row-key="id" size="small" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" fixed="left" />
      <el-table-column prop="id" label="ID" width="60" fixed="left" />
      <el-table-column prop="communityName" label="小区名称" width="150" fixed="left" show-overflow-tooltip />
      <el-table-column prop="townCommunity" label="所属乡镇/社区" width="150" show-overflow-tooltip />
      <el-table-column prop="buildTime" label="基站建成时间" width="160" show-overflow-tooltip />
      <el-table-column prop="operatorName" label="运营商/铁塔" width="170" show-overflow-tooltip />
      <el-table-column prop="deviceType" label="设备类型" width="130" show-overflow-tooltip />
      <el-table-column prop="occupyPosition" label="占用位置" width="140" show-overflow-tooltip />
      <el-table-column prop="partyA" label="合同甲方(签约主体)" width="200" show-overflow-tooltip />
      <el-table-column prop="partyB" label="合同乙方(运营主体)" width="230" show-overflow-tooltip />
      <el-table-column prop="signTime" label="合同签订时间" width="160" show-overflow-tooltip />
      <el-table-column prop="contractPeriod" label="合同约定期限" width="230" show-overflow-tooltip />
      <el-table-column prop="rentAgreement" label="租金收入约定" width="130" show-overflow-tooltip />
      <el-table-column prop="rentFee" label="租金/场地费(元/年)" width="160" show-overflow-tooltip />
      <el-table-column prop="payee" label="实际收款方" width="220" show-overflow-tooltip />
      <el-table-column prop="powerAccount" label="基站用电户头" width="150" show-overflow-tooltip />
      <el-table-column prop="powerPrice" label="电价(元/度)" width="150" show-overflow-tooltip />
      <el-table-column prop="annualPower" label="年用电量(度)" width="120" show-overflow-tooltip />
      <el-table-column prop="remark" label="备注" width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑基站信息' : '新增基站信息'" width="820px" top="5vh">
      <el-form ref="formRef" :model="form" label-width="150px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="小区名称">
              <el-select v-model="form.communityName" placeholder="可输入任意小区名" clearable filterable allow-create style="width:100%">
                <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属乡镇/社区"><el-input v-model="form.townCommunity" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基站建成时间"><el-input v-model="form.buildTime" placeholder="如 2025.5.15 / 无" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运营商/铁塔">
              <el-select v-model="form.operatorName" clearable filterable allow-create placeholder="请选择或输入" style="width:100%">
                <el-option v-for="o in operatorOptions" :key="o" :label="o" :value="o" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类型">
              <el-select v-model="form.deviceType" clearable filterable allow-create placeholder="请选择或输入" style="width:100%">
                <el-option v-for="d in deviceTypes" :key="d" :label="d" :value="d" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="占用位置">
              <el-select v-model="form.occupyPosition" clearable filterable allow-create placeholder="请选择或输入" style="width:100%">
                <el-option v-for="p in occupyPositions" :key="p" :label="p" :value="p" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同甲方(签约主体)"><el-input v-model="form.partyA" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同乙方(运营主体)"><el-input v-model="form.partyB" type="textarea" :rows="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同签订时间"><el-input v-model="form.signTime" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租金收入约定"><el-input v-model="form.rentAgreement" placeholder="如 年度支付 / 无" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同约定期限"><el-input v-model="form.contractPeriod" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租金/场地费(元/年)"><el-input v-model="form.rentFee" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际收款方"><el-input v-model="form.payee" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基站用电户头">
              <el-select v-model="form.powerAccount" clearable filterable allow-create placeholder="请选择或输入" style="width:100%">
                <el-option v-for="a in powerAccounts" :key="a" :label="a" :value="a" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电价(元/度)"><el-input v-model="form.powerPrice" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年用电量(度)"><el-input v-model="form.annualPower" /></el-form-item>
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
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { pageStations, saveStation, updateStation, deleteStation, batchDeleteStations, exportStations, importStations } from '../../api/baseStation'
import { listCommunities } from '../../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'

const operatorOptions = ['移动', '中国移动', '联通', '中国联通', '电信', '中国电信', '中国铁塔', '铁塔', '广电', '无', '/']
const deviceTypes = ['室内分布系统', '信号放大器', '信号覆盖系统', '固网机房', '室内', '室内分布租赁', '无线通讯', '宏站', '微站', '机房/机柜', '无', '/']
const occupyPositions = ['楼顶', '楼道', '地库', '车库', '地下室', '弱电井', '地库电信间', '楼顶、地库', '楼顶及室内楼道', '小区西山头', '无', '/']
const powerAccounts = ['独立电表', '转供表', '转供电', '小区总表转供', '无']

const selectedIds = ref([])
const list = ref([])
const total = ref(0)
const communities = ref([])
const query = reactive({ page: 1, size: 10, communityName: '', townCommunity: '', operatorName: '', deviceType: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const empty = {
  id: null, communityId: null, communityName: '', townCommunity: '', buildTime: '', operatorName: '',
  deviceType: '', occupyPosition: '', partyA: '', partyB: '', signTime: '', contractPeriod: '',
  rentAgreement: '', rentFee: '', payee: '', powerAccount: '', powerPrice: '', annualPower: '', remark: ''
}
const form = reactive({ ...empty })
const formRef = ref()

function load() {
  pageStations(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}
function loadCommunities() {
  listCommunities().then(res => { communities.value = res.data })
}
function reset() {
  query.communityName = ''
  query.townCommunity = ''
  query.operatorName = ''
  query.deviceType = ''
  query.page = 1
  load()
}
function onPage(p) { query.page = p; nextTick(() => load()) }
function onSize(s) { query.size = s; query.page = 1; nextTick(() => load()) }
function openAdd() { isEdit.value = false; Object.assign(form, empty); dialogVisible.value = true }
function openEdit(row) { isEdit.value = true; Object.assign(form, { ...empty, ...row }); dialogVisible.value = true }
function submit() {
  if (isEdit.value) {
    updateStation(form).then(() => { ElMessage.success('修改成功'); dialogVisible.value = false; load() })
  } else {
    saveStation(form).then(() => { ElMessage.success('新增成功'); dialogVisible.value = false; load() })
  }
}
function remove(row) {
  ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    .then(() => deleteStation(row.id).then(() => { ElMessage.success('删除成功'); load() }))
}
function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}
function batchRemove() {
  if (selectedIds.value.length === 0) { ElMessage.warning('请先勾选要删除的记录'); return }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    .then(() => batchDeleteStations(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
}
function onExport() {
  exportStations({
    communityName: query.communityName,
    townCommunity: query.townCommunity,
    operatorName: query.operatorName,
    deviceType: query.deviceType
  }).then(() => ElMessage.success('导出成功')).catch(() => {})
}
function onImport(file) {
  importStations(file)
    .then(res => { ElMessage.success(res.message || '导入成功'); load() })
    .catch(() => {})
  return false
}
onMounted(() => { load(); loadCommunities() })
</script>
