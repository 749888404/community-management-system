<template>
  <el-card class="page-card">
    <div class="page-header">
      <span class="page-title">文件管理</span>
      <el-button type="success" @click="openUpload">上传文件</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="文件名">
        <el-input v-model="query.fileName" placeholder="文件名" clearable style="width:200px" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="query.category" placeholder="全部" clearable style="width:140px">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="上传人">
        <el-select v-model="query.uploader" placeholder="全部" clearable style="width:140px">
          <el-option v-for="u in uploaders" :key="u" :label="u" :value="u" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="batchRemove">
          {{ selectedIds.length ? '批量删除 (' + selectedIds.length + ')' : '批量删除' }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border stripe row-key="id" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="55" />
    <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="fileName" label="文件名" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">
          <el-link type="primary" :underline="false" @click="onDownload(row)">
            <el-icon style="vertical-align:-2px;margin-right:4px"><Document /></el-icon>{{ row.fileName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.category" size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="大小" width="100">
        <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
      </el-table-column>
      <el-table-column prop="ext" label="格式" width="80" />
      <el-table-column prop="uploader" label="上传人" width="110" />
      <el-table-column prop="createTime" label="上传时间" width="170" />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-tooltip :disabled="canPreview(row)" content="该格式暂不支持在线预览，请下载查看" placement="top">
            <span>
              <el-button size="small" :disabled="!canPreview(row)" @click="openPreview(row)">预览</el-button>
            </span>
          </el-tooltip>
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

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadVisible" title="批量上传文件" width="560px">
      <el-form label-width="80px">
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            drag
            multiple
            :auto-upload="false"
            accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.txt,.zip,.rar"
            :on-change="onFileChange"
            :on-remove="onFileRemove">
            <el-icon style="font-size:46px;color:#c0c4cc"><UploadFilled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或 <em>点击选择</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 pdf/word/excel/ppt/图片/压缩包等，可一次选择或拖入多个文件批量上传</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="uploadForm.category" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="uploadForm.remark" type="textarea" :rows="2" maxlength="200" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" :disabled="uploadForm.files.length === 0" @click="doUpload">确定上传（{{ uploadForm.files.length }} 个）</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑文件信息" width="480px">
      <el-form label-width="80px">
        <el-form-item label="文件名">
          <el-input v-model="editForm.fileName" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.category" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" :title="previewTitle" width="70%" top="5vh" destroy-on-close>
      <div class="preview-toolbar">
        <el-button-group>
          <el-button size="small" :icon="ZoomIn" :disabled="previewMode === 'xlsx'" @click="previewZoom(0.25)">放大</el-button>
          <el-button size="small" :icon="ZoomOut" :disabled="previewMode === 'xlsx'" @click="previewZoom(-0.25)">缩小</el-button>
          <el-button size="small" :icon="RefreshRight" :disabled="previewMode === 'xlsx'" @click="previewRotate90">旋转</el-button>
          <el-button size="small" :disabled="previewMode === 'xlsx'" @click="previewReset">重置</el-button>
        </el-button-group>
        <span class="preview-scale">{{ Math.round(previewScale * 100) }}%</span>
        <div class="preview-toolbar-right">
          <el-button size="small" type="primary" :disabled="previewMode === 'docx' || previewMode === 'xlsx'" @click="openPreviewWindow">新窗口打开</el-button>
          <el-button size="small" @click="downloadCurrent">下载</el-button>
        </div>
      </div>
      <div v-if="previewMode === 'image'" class="preview-body image-body">
        <img :src="previewUrl" :style="previewBodyStyle" alt="预览" />
      </div>
      <div v-else-if="previewMode === 'docx'" class="preview-body docx-body">
        <div class="docx-zoom" :style="docxZoomStyle">
          <div ref="docxContainer" class="docx-render-target"></div>
        </div>
        <div v-if="docxLoading" class="docx-loading-mask">
          <el-icon class="is-loading" style="font-size:24px"><Loading /></el-icon>
          <span>正在加载 Word 文档…</span>
        </div>
      </div>
      <div v-else-if="previewMode === 'xlsx'" class="preview-body xlsx-body">
        <VueOfficeExcel :src="previewUrl" :style="{ width: '100%', height: '100%' }" @rendered="onXlsxRendered" @error="onXlsxError" />
        <div v-if="xlsxLoading" class="docx-loading-mask">
          <el-icon class="is-loading" style="font-size:24px"><Loading /></el-icon>
          <span>正在加载 Excel 文档…</span>
        </div>
      </div>
      <iframe v-else :src="previewUrl" class="preview-body"></iframe>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { pageFile, uploadFiles, downloadFile, updateFile, deleteFile, batchDeleteFiles, listUploaders } from '../../api/file'
import { renderAsync } from 'docx-preview'
import VueOfficeExcel from '@vue-office/excel'
import '@vue-office/excel/lib/index.css'
import { ElMessage, ElMessageBox } from 'element-plus'
import { API_BASE } from '../../utils/request'

const categories = ['政策文件', '通知公告', '居民档案', '社区文档', '其他']
const list = ref([])
const total = ref(0)
const selectedIds = ref([])
const uploaders = ref([])
const query = reactive({ page: 1, size: 10, fileName: '', category: '', uploader: '' })

const uploadVisible = ref(false)
const uploading = ref(false)
const uploadRef = ref()
const uploadForm = reactive({ files: [], category: '', remark: '' })

const editVisible = ref(false)
const editForm = reactive({ id: null, fileName: '', category: '', remark: '' })

const previewVisible = ref(false)
const previewUrl = ref('')
const previewMode = ref('iframe') // 'image' | 'docx' | 'xlsx' | 'iframe'
const previewScale = ref(1)
const previewRotate = ref(0)
const previewCurrentRow = ref(null)
const docxContainer = ref(null)
const docxLoading = ref(false)
const xlsxLoading = ref(false)
// 支持浏览器内联预览的扩展名
const previewableExts = ['pdf', 'jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg', 'txt', 'md', 'log', 'json', 'xml', 'html', 'htm', 'docx', 'xlsx']
// 走图片预览（支持放大缩小旋转）的扩展名
const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg']
function canPreview(row) {
  return previewableExts.includes((row.ext || '').toLowerCase())
}
async function openPreview(row) {
  previewCurrentRow.value = row
  previewScale.value = 1
  previewRotate.value = 0
  const ext = (row.ext || '').toLowerCase()
  previewVisible.value = true
  previewUrl.value = `${API_BASE}/file/preview/${row.id}?t=${Date.now()}`
  if (imageExts.includes(ext)) {
    previewMode.value = 'image'
  } else if (ext === 'docx') {
    previewMode.value = 'docx'
    await nextTick()
    await renderDocx(row.id)
  } else if (ext === 'xlsx') {
    previewMode.value = 'xlsx'
    xlsxLoading.value = true
  } else {
    previewMode.value = 'iframe'
  }
}
function onXlsxRendered() {
  xlsxLoading.value = false
}
function onXlsxError() {
  xlsxLoading.value = false
  ElMessage.error('Excel 预览失败，请下载后查看')
}
async function renderDocx(id) {
  docxLoading.value = true
  try {
    // 等待预览容器挂载（dialog 内容懒渲染，可能晚于 nextTick）
    for (let i = 0; i < 30 && !docxContainer.value; i++) {
      await new Promise(r => setTimeout(r, 50))
    }
    const container = docxContainer.value
    if (!container) throw new Error('预览容器未就绪')
    container.innerHTML = ''
    const res = await fetch(`${API_BASE}/file/preview/${id}?t=${Date.now()}`)
    if (!res.ok) throw new Error('HTTP ' + res.status)
    const blob = await res.blob()
    await renderAsync(blob, container, null, {
      className: 'docx',
      inWrapper: false,
      ignoreWidth: false,
      ignoreHeight: false,
      ignoreFonts: false,
      breakPages: true,
      ignoreLastRenderedPageBreak: false,
      useBase64URL: true,
      debug: false
    })
  } catch (e) {
    if (docxContainer.value) docxContainer.value.innerHTML = ''
    ElMessage.error('Word 预览失败：' + (e.message || e))
  } finally {
    docxLoading.value = false
  }
}
const previewTitle = computed(() => previewCurrentRow.value ? `文件预览 - ${previewCurrentRow.value.fileName}` : '文件预览')
const previewBodyStyle = computed(() => ({
  transform: `scale(${previewScale.value}) rotate(${previewRotate.value}deg)`
}))
// docx 用 zoom 缩放（影响布局，滚动条尺寸正确）；旋转用 transform 叠加
const docxZoomStyle = computed(() => ({
  zoom: previewScale.value,
  transform: previewRotate.value ? `rotate(${previewRotate.value}deg)` : 'none'
}))
function previewZoom(delta) {
  previewScale.value = Math.min(3, Math.max(0.2, Math.round((previewScale.value + delta) * 100) / 100))
}
function previewRotate90() {
  previewRotate.value = (previewRotate.value + 90) % 360
}
function previewReset() {
  previewScale.value = 1
  previewRotate.value = 0
}
function openPreviewWindow() {
  window.open(previewUrl.value, '_blank')
}
function downloadCurrent() {
  if (previewCurrentRow.value) onDownload(previewCurrentRow.value)
}

function load() {
  pageFile(query).then(res => {
    list.value = res.data.records
    total.value = res.data.total
  })
}
function reset() {
  query.fileName = ''
  query.category = ''
  query.uploader = ''
  query.page = 1
  load()
}
function onPage(p) { query.page = p; nextTick(() => load()) }
function onSize(s) { query.size = s; query.page = 1; nextTick(() => load()) }

function formatSize(bytes) {
  if (bytes == null) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}

function openUpload() {
  uploadForm.files = []
  uploadForm.category = ''
  uploadForm.remark = ''
  uploadRef.value?.clearFiles()
  uploadVisible.value = true
}
function onFileChange(file) {
  const dup = uploadForm.files.find(f => f.name === file.raw.name && f.size === file.raw.size)
  if (!dup) uploadForm.files.push(file.raw)
  else uploadRef.value?.handleRemove(file)
}
function onFileRemove(file) {
  const idx = uploadForm.files.indexOf(file.raw)
  if (idx >= 0) uploadForm.files.splice(idx, 1)
}
function doUpload() {
  if (uploadForm.files.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  uploading.value = true
  const fd = new FormData()
  uploadForm.files.forEach(f => fd.append('files', f))
  if (uploadForm.category) fd.append('category', uploadForm.category)
  if (uploadForm.remark) fd.append('remark', uploadForm.remark)
  const uploader = localStorage.getItem('realName') || localStorage.getItem('username') || ''
  if (uploader) fd.append('uploader', uploader)

  uploadFiles(fd)
    .then(res => {
      if (res && res.code !== 200) {
        ElMessage.error(res.message || '上传失败，请稍后重试')
        return
      }
      ElMessage.success((res && res.message) || '上传成功')
      uploadVisible.value = false
      load()
    })
    .catch(err => {
      const msg = (err && (err.message || err.msg)) || '上传失败，请检查网络或后端服务'
      ElMessage.error(typeof msg === 'string' ? msg : '上传失败，请检查网络或后端服务')
    })
    .finally(() => { uploading.value = false })
}

function onDownload(row) {
  downloadFile(row.id).catch(() => {})
}
function openEdit(row) {
  Object.assign(editForm, { id: row.id, fileName: row.fileName, category: row.category || '', remark: row.remark || '' })
  editVisible.value = true
}
function submitEdit() {
  if (!editForm.fileName) {
    ElMessage.warning('文件名不能为空')
    return
  }
  updateFile({ id: editForm.id, fileName: editForm.fileName, category: editForm.category, remark: editForm.remark })
    .then(() => { ElMessage.success('修改成功'); editVisible.value = false; load() })
}
function remove(row) {
  ElMessageBox.confirm('确认删除该文件？删除后不可恢复。', '提示', { type: 'warning' })
    .then(() => deleteFile(row.id).then(() => { ElMessage.success('删除成功'); load() }))
    .catch(() => {})
}

function onSelectionChange(val) {
  selectedIds.value = val.map(r => r.id)
}

function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的文件')
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 个文件？删除后不可恢复。`, '提示', { type: 'warning' })
    .then(() => batchDeleteFiles(selectedIds.value)
      .then(() => { ElMessage.success('删除成功'); selectedIds.value = []; load() }))
    .catch(() => {})
}

onMounted(() => {
  listUploaders().then(res => { uploaders.value = res.data || [] })
  load()
})
</script>

<style scoped>
.preview-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.preview-scale {
  font-size: 13px;
  color: #909399;
  min-width: 52px;
}
.preview-toolbar-right {
  margin-left: auto;
}
.preview-body {
  width: 100%;
  height: 72vh;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}
.image-body {
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.image-body img {
  max-width: 95%;
  max-height: 95%;
  transition: transform 0.15s ease;
  user-select: none;
}
.docx-body {
  position: relative;
  overflow: auto;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: #f5f7fa;
}
.docx-zoom {
  transform-origin: center top;
  margin: 16px;
  line-height: 1.5;
}
.xlsx-body {
  position: relative;
  overflow: hidden;
  background: #f5f7fa;
}
.docx-render-target {
  background: #fff;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.15);
  padding: 8px 10px;
}
.docx-loading-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: rgba(245, 247, 250, 0.9);
  color: #909399;
  font-size: 14px;
  z-index: 3;
}
.docx-body :deep(.docx-wrapper) {
  background: #fff;
  padding: 0;
  margin: 0;
}
.docx-body :deep(.docx-wrapper) .docx {
  padding: 0;
  margin: 0;
}
.docx-body :deep(img.docx-img) {
  max-width: 100%;
}
</style>
