<template>
  <view class="crud">
    <!-- 搜索区 -->
    <view class="search-bar" v-if="searches.length">
      <view v-for="s in searches" :key="s.key" class="search-row">
        <text class="search-label">{{ s.label }}</text>
        <u-input
          v-if="!s.options"
          v-model="search[s.key]"
          :placeholder="'请输入' + s.label"
          border="surround"
          clearable
        />
        <picker
          v-else
          mode="selector"
          :range="s.options || []"
          range-key="label"
          @change="onSearchSelect(s, $event)"
        >
          <view class="search-picker" :class="{ placeholder: !searchLabel(s) }">
            {{ searchLabel(s) || '请选择' + s.label }}
          </view>
        </picker>
      </view>
      <view class="search-actions">
        <u-button text="搜索" type="primary" size="mini" @click="onSearch" />
        <u-button text="重置" size="mini" plain @click="onReset" />
      </view>
    </view>

    <!-- 批量操作栏 -->
    <view class="batch-bar" v-if="showBatch">
      <text class="batch-info">已选 {{ selectedIds.length }} 项</text>
      <u-button
        text="批量删除"
        type="error"
        size="mini"
        :disabled="!selectedIds.length"
        @click="batchDelete"
      />
    </view>

    <!-- 列表 -->
    <scroll-view
      class="list-scroll"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <u-empty
        v-if="!records.length && !loading"
        mode="list"
        text="暂无数据"
        class="empty"
      />
      <view
        v-for="item in records"
        :key="item[pk]"
        class="card"
        :class="{ active: isSelected(item) }"
        @tap="openEdit(item)"
      >
        <view class="card-check" @tap.stop="toggleSelect(item)">
          <u-checkbox
            :modelValue="isSelected(item)"
            shape="circle"
            @change="() => toggleSelect(item)"
          />
        </view>
        <view class="card-main">
          <view class="card-title">{{ item[columns[0].key] }}</view>
          <view
            v-for="c in columns.slice(1, 4)"
            :key="c.key"
            class="card-line"
          >
            <text class="line-label">{{ c.label }}：</text>
            <text class="line-value">{{
              c.format ? c.format(item[c.key]) : item[c.key] || '—'
            }}</text>
          </view>
        </view>
        <view class="card-del" @tap.stop="removeOne(item)">
          <u-icon name="trash" size="36" color="#fa3534" />
        </view>
      </view>

      <u-loadmore :status="loadStatus" />
    </scroll-view>

    <!-- 悬浮新增 -->
    <u-button
      icon="plus"
      shape="circle"
      class="fab"
      @click="openAdd"
    />

    <!-- 表单弹层 -->
    <CrudForm
      :visible="formVisible"
      :title="formTitle"
      :fields="fields"
      :model-value="current"
      @update:visible="(v) => (formVisible = v)"
      @submit="onSubmit"
    />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCrud } from '@/composables/useCrud'
import CrudForm from '@/components/CrudForm.vue'

const props = defineProps({
  api: { type: Object, required: true },
  columns: { type: Array, required: true },
  fields: { type: Array, required: true },
  searches: { type: Array, default: () => [] },
  pk: { type: String, default: 'id' },
  showBatch: { type: Boolean, default: true }
})

const { records, search, selectedIds, load, onSearch, onReset, onSearchSelect, searchLabel, batchDelete, loading, finished } =
  useCrud(props.api)

const refreshing = ref(false)
const formVisible = ref(false)
const formTitle = ref('新增')
const current = ref({})

const loadStatus = computed(() => {
  if (loading.value) return 'loading'
  if (finished.value) return 'nomore'
  return 'loadmore'
})

function isSelected(item) {
  return selectedIds.value.includes(item[props.pk])
}
function toggleSelect(item) {
  const id = item[props.pk]
  const i = selectedIds.value.indexOf(id)
  if (i >= 0) selectedIds.value.splice(i, 1)
  else selectedIds.value.push(id)
}
function onRefresh() {
  refreshing.value = true
  load(true).finally(() => (refreshing.value = false))
}
function onLoadMore() {
  if (!finished.value && !loading.value) load()
}
function openAdd() {
  current.value = {}
  formTitle.value = '新增'
  formVisible.value = true
}
function openEdit(item) {
  current.value = JSON.parse(JSON.stringify(item))
  formTitle.value = '编辑'
  formVisible.value = true
}
async function onSubmit(data) {
  if (data[props.pk]) await props.api.update(data)
  else await props.api.create(data)
  uni.showToast({ title: '保存成功', icon: 'success' })
  formVisible.value = false
  load(true)
}
async function removeOne(item) {
  const ok = await new Promise((resolve) =>
    uni.showModal({
      title: '提示',
      content: '确认删除该记录？',
      success: (x) => resolve(x.confirm)
    })
  )
  if (!ok) return
  await props.api.remove(item[props.pk])
  uni.showToast({ title: '删除成功', icon: 'success' })
  load(true)
}

onMounted(() => {
  load(true)
})
</script>

<style scoped>
.crud {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f6f8;
}
.search-bar {
  background: #fff;
  padding: 20rpx 24rpx;
  border-bottom: 1rpx solid #ebeef5;
}
.search-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}
.search-label {
  width: 140rpx;
  font-size: 26rpx;
  color: #606266;
}
.search-picker {
  flex: 1;
  background: #f5f6f8;
  border-radius: 10rpx;
  padding: 16rpx 20rpx;
  font-size: 26rpx;
  color: #303133;
}
.search-picker.placeholder {
  color: #c0c4cc;
}
.search-actions {
  display: flex;
  gap: 20rpx;
  justify-content: flex-end;
}
.batch-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  background: #ecf3ff;
}
.batch-info {
  font-size: 26rpx;
  color: #2979ff;
}
.list-scroll {
  flex: 1;
  min-height: 0;
  padding: 20rpx 24rpx calc(140rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}
.empty {
  padding-top: 120rpx;
}
.card {
  display: flex;
  align-items: flex-start;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 18rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}
.card.active {
  border: 1rpx solid #2979ff;
}
.card-check {
  padding: 6rpx 16rpx 6rpx 0;
}
.card-main {
  flex: 1;
  min-width: 0;
}
.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10rpx;
}
.card-line {
  font-size: 25rpx;
  color: #909399;
  line-height: 1.6;
}
.line-label {
  color: #909399;
}
.line-value {
  color: #606266;
}
.card-del {
  padding: 6rpx 0 6rpx 16rpx;
}
.fab {
  position: fixed;
  right: 40rpx;
  bottom: calc(140rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  z-index: 50;
}
</style>
