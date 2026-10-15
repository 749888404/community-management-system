<template>
  <view class="file-page">
    <!-- 搜索区 -->
    <view class="search-bar">
      <view class="search-row">
        <text class="search-label">文件名</text>
        <input class="search-input" placeholder="请输入文件名" v-model="search.fileName" />
      </view>
      <view class="search-row">
        <text class="search-label">分类</text>
        <picker class="search-picker" mode="selector" :range="categoryOptions" range-key="label" @change="onCatSelect">
          <view class="search-picker-text">{{ catLabel }}</view>
        </picker>
      </view>
      <view class="search-actions">
        <button class="btn-mini" @tap="onSearch">搜索</button>
        <button class="btn-mini ghost" @tap="onReset">重置</button>
      </view>
    </view>

    <!-- 批量操作栏 -->
    <view class="batch-bar">
      <text class="batch-info">已选 {{ selectedIds.length }} 项</text>
      <button class="btn-mini danger" :disabled="!selectedIds.length" @tap="batchDelete">批量删除</button>
    </view>

    <!-- 列表 -->
    <scroll-view class="list-scroll" scroll-y :refresher-enabled="true" :refresher-triggered="refreshing"
                 @refresherrefresh="onRefresh" @scrolltolower="onLoadMore">
      <view v-for="item in records" :key="item.id" class="card" :class="{ active: isSelected(item) }">
        <view class="card-check" @tap="toggleSelect(item)">
          <view class="checkbox" :class="{ checked: isSelected(item) }">{{ isSelected(item) ? '✓' : '' }}</view>
        </view>
        <view class="card-icon" @tap="preview(item)">{{ iconOf(item.ext) }}</view>
        <view class="card-main" @tap="preview(item)">
          <view class="card-title">{{ item.fileName }}</view>
          <view class="card-line">分类：{{ item.category || '未分类' }}</view>
          <view class="card-line">大小：{{ fmtSize(item.fileSize) }} · {{ item.uploader || '—' }}</view>
          <view class="card-line light">{{ item.createTime || '' }}</view>
        </view>
        <view class="card-del" @tap="removeOne(item)">🗑</view>
      </view>
      <uni-load-more :status="loadStatus" />
    </scroll-view>

    <!-- 悬浮上传 -->
    <view class="fab" @tap="openUpload">＋</view>

    <!-- 上传弹层 -->
    <view v-if="uploadVisible" class="sheet-mask" @tap="uploadVisible = false">
      <view class="sheet" @tap.stop>
        <view class="sheet-head">
          <text class="sheet-title">上传文件</text>
          <text class="sheet-close" @tap="uploadVisible = false">✕</text>
        </view>
        <view class="sheet-body">
          <view class="form-item">
            <text class="form-label">分类</text>
            <picker class="form-picker" mode="selector" :range="uploadCatOptions" range-key="label" @change="onUploadCat">
              <view class="form-picker-text">{{ uploadCatLabel }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">备注</text>
            <textarea class="form-textarea" v-model="uploadRemark" placeholder="可选" />
          </view>
        </view>
        <view class="sheet-foot">
          <button class="btn-cancel" @tap="uploadVisible = false">取消</button>
          <button class="btn-save" @tap="chooseAndUpload">选择文件并上传</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { pageFile, removeFile, batchRemoveFile, uploadFiles, rawFileUrl } from '@/api/file'
import { get } from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const search = reactive({ fileName: '', category: '' })
const selectedIds = ref([])
const uploadVisible = ref(false)
const uploadCat = ref('')
const uploadRemark = ref('')

const categoryOptions = [
  { label: '全部', value: '' },
  { label: '政策文件', value: '政策文件' },
  { label: '通知公告', value: '通知公告' },
  { label: '居民档案', value: '居民档案' },
  { label: '社区文档', value: '社区文档' },
  { label: '其他', value: '其他' }
]
const uploadCatOptions = categoryOptions.filter((c) => c.value)
const catLabel = ref('全部')
const uploadCatLabel = ref('政策文件')
const uploaderOptions = ref([])

function clean(o) {
  const r = {}
  for (const k in o) if (o[k] !== '' && o[k] != null) r[k] = o[k]
  return r
}

async function load(refresh = false) {
  if (refresh) {
    page.value = 1
    records.value = []
    finished.value = false
  }
  if (finished.value || loading.value) return
  loading.value = true
  try {
    const res = await pageFile({ page: page.value, size: size.value, ...clean(search) })
    const list = (res && res.records) || []
    records.value = refresh ? list : records.value.concat(list)
    total.value = (res && res.total) || 0
    if (records.value.length >= total.value) finished.value = true
    else page.value++
  } catch (e) {}
  finally {
    loading.value = false
  }
}

const loadStatus = computed(() => (loading.value ? 'loading' : finished.value ? 'noMore' : 'more'))

function onSearch() { load(true) }
function onReset() {
  search.fileName = ''
  search.category = ''
  catLabel.value = '全部'
  load(true)
}
function onCatSelect(e) {
  const opt = categoryOptions[e.detail.value]
  search.category = opt.value
  catLabel.value = opt.label
}
function onUploadCat(e) {
  uploadCat.value = uploadCatOptions[e.detail.value].value
  uploadCatLabel.value = uploadCatOptions[e.detail.value].label
}

function isSelected(item) { return selectedIds.value.includes(item.id) }
function toggleSelect(item) {
  const i = selectedIds.value.indexOf(item.id)
  if (i >= 0) selectedIds.value.splice(i, 1)
  else selectedIds.value.push(item.id)
}
function onRefresh() {
  refreshing.value = true
  load(true).finally(() => (refreshing.value = false))
}
function onLoadMore() {
  if (!finished.value && !loading.value) load()
}

async function removeOne(item) {
  const ok = await new Promise((r) => uni.showModal({ title: '提示', content: '确认删除该文件？', success: (x) => r(x.confirm) }))
  if (!ok) return
  await removeFile(item.id)
  uni.showToast({ title: '删除成功', icon: 'success' })
  load(true)
}
async function batchDelete() {
  if (!selectedIds.value.length) return
  const ok = await new Promise((r) => uni.showModal({ title: '提示', content: `确认删除选中的 ${selectedIds.value.length} 个文件？`, success: (x) => r(x.confirm) }))
  if (!ok) return
  await batchRemoveFile(selectedIds.value)
  uni.showToast({ title: '删除成功', icon: 'success' })
  selectedIds.value = []
  load(true)
}

function fmtSize(bytes) {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}
function iconOf(ext) {
  ext = (ext || '').toLowerCase()
  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg'].includes(ext)) return '🖼'
  if (ext === 'pdf') return '📕'
  if (ext === 'doc' || ext === 'docx') return '📘'
  if (ext === 'xls' || ext === 'xlsx') return '📗'
  if (['txt', 'md', 'log'].includes(ext)) return '📝'
  return '📄'
}

function preview(item) {
  const url = rawFileUrl(item.id)
  const imgExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg']
  if (imgExts.includes((item.ext || '').toLowerCase())) {
    uni.previewImage({ urls: [url], current: url })
    return
  }
  // #ifdef H5
  window.open(url, '_blank')
  // #endif
  // #ifdef MP-WEIXIN
  uni.downloadFile({
    url,
    success: (r) => { if (r.statusCode === 200) uni.openDocument({ filePath: r.tempFilePath, showMenu: true }); else uni.showToast({ title: '预览失败', icon: 'none' }) },
    fail: () => uni.showToast({ title: '预览失败', icon: 'none' })
  })
  // #endif
  // #ifdef APP-PLUS
  uni.downloadFile({
    url,
    success: (r) => { if (r.statusCode === 200) uni.openDocument({ filePath: r.tempFilePath }); else uni.showToast({ title: '预览失败', icon: 'none' }) },
    fail: () => uni.showToast({ title: '预览失败', icon: 'none' })
  })
  // #endif
}

function openUpload() { uploadVisible.value = true }

function chooseAndUpload() {
  const doUpload = (paths) => {
    if (!paths || !paths.length) return
    uni.showLoading({ title: '上传中...' })
    uploadFiles(paths, {
      category: uploadCat.value || '其他',
      uploader: userStore.realName || '',
      remark: uploadRemark.value
    })
      .then(() => {
        uni.showToast({ title: '上传成功', icon: 'success' })
        uploadVisible.value = false
        uploadRemark.value = ''
        load(true)
      })
      .catch(() => {})
      .finally(() => uni.hideLoading())
  }

  // #ifdef MP-WEIXIN
  uni.chooseMessageFile({
    count: 9,
    success: (r) => doUpload(r.tempFiles.map((f) => f.path)),
    fail: () => {}
  })
  // #endif
  // #ifndef MP-WEIXIN
  uni.chooseFile({
    count: 9,
    success: (r) => doUpload(r.tempFiles.map((f) => f.path)),
    fail: () => {}
  })
  // #endif
}

onMounted(async () => {
  await load(true)
  try {
    const list = await get('/file/uploaders')
    uploaderOptions.value = (list || []).map((u) => ({ label: u, value: u }))
  } catch (e) {}
})
</script>

<style scoped>
.file-page { height: 100vh; display: flex; flex-direction: column; background: #f5f6f8; }
.search-bar { background: #fff; padding: 20rpx 24rpx; border-bottom: 1rpx solid #ebeef5; }
.search-row { display: flex; align-items: center; margin-bottom: 16rpx; }
.search-label { width: 120rpx; font-size: 26rpx; color: #606266; }
.search-input, .search-picker { flex: 1; background: #f5f6f8; border-radius: 10rpx; padding: 16rpx 20rpx; font-size: 26rpx; }
.search-picker-text { color: #303133; font-size: 26rpx; }
.search-actions { display: flex; gap: 20rpx; justify-content: flex-end; }
.btn-mini { font-size: 26rpx; padding: 10rpx 28rpx; margin: 0; background: #2979ff; color: #fff; border-radius: 10rpx; line-height: 1.8; }
.btn-mini.ghost { background: #f5f6f8; color: #606266; }
.btn-mini.danger { background: #fa3534; }
.btn-mini[disabled] { opacity: 0.5; }
.batch-bar { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 24rpx; background: #ecf3ff; }
.batch-info { font-size: 26rpx; color: #2979ff; }
.list-scroll { flex: 1; min-height: 0; padding: 20rpx 24rpx calc(140rpx + env(safe-area-inset-bottom)); box-sizing: border-box; }
.card { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 20rpx 24rpx; margin-bottom: 18rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); }
.card.active { border: 1rpx solid #2979ff; }
.card-check { padding: 6rpx 14rpx 6rpx 0; }
.checkbox { width: 36rpx; height: 36rpx; border: 1rpx solid #c0c4cc; border-radius: 50%; color: #fff; font-size: 22rpx; text-align: center; line-height: 36rpx; }
.checkbox.checked { background: #2979ff; border-color: #2979ff; }
.card-icon { font-size: 56rpx; padding: 0 18rpx 0 6rpx; }
.card-main { flex: 1; min-width: 0; }
.card-title { font-size: 28rpx; font-weight: 600; color: #303133; margin-bottom: 8rpx; }
.card-line { font-size: 24rpx; color: #606266; line-height: 1.6; }
.card-line.light { color: #c0c4cc; }
.card-del { padding: 6rpx 0 6rpx 16rpx; font-size: 32rpx; }
.fab { position: fixed; right: 40rpx; bottom: calc(140rpx + env(safe-area-inset-bottom)); width: 96rpx; height: 96rpx; border-radius: 50%; background: #2979ff; color: #fff; font-size: 56rpx; text-align: center; line-height: 92rpx; box-shadow: 0 8rpx 20rpx rgba(41,121,255,0.4); z-index: 50; }
.sheet-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 999; display: flex; align-items: flex-end; }
.sheet { width: 100%; background: #fff; border-radius: 24rpx 24rpx 0 0; }
.sheet-head { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 32rpx; border-bottom: 1rpx solid #ebeef5; }
.sheet-title { font-size: 32rpx; font-weight: 600; }
.sheet-close { font-size: 36rpx; color: #909399; padding: 0 10rpx; }
.sheet-body { padding: 20rpx 32rpx; }
.form-item { margin-bottom: 28rpx; }
.form-label { display: block; font-size: 26rpx; color: #606266; margin-bottom: 12rpx; }
.form-picker { width: 100%; background: #f5f6f8; border-radius: 12rpx; padding: 20rpx 24rpx; box-sizing: border-box; }
.form-picker-text { font-size: 28rpx; color: #303133; }
.form-textarea { width: 100%; height: 140rpx; background: #f5f6f8; border-radius: 12rpx; padding: 20rpx 24rpx; box-sizing: border-box; font-size: 28rpx; }
.sheet-foot { display: flex; gap: 24rpx; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); border-top: 1rpx solid #ebeef5; }
.btn-cancel, .btn-save { flex: 1; font-size: 30rpx; border-radius: 12rpx; margin: 0; }
.btn-cancel { background: #f5f6f8; color: #606266; }
.btn-save { background: #2979ff; color: #fff; }
</style>
