<template>
  <u-popup
    :show="visible"
    mode="bottom"
    :round="20"
    @close="close"
    closeable
  >
    <view class="sheet">
      <view class="sheet-head">{{ title }}</view>
      <scroll-view scroll-y class="sheet-body">
        <u-form :model="form" labelPosition="top">
          <u-form-item
            v-for="f in fields"
            :key="f.key"
            :label="f.label + (f.required ? ' *' : '')"
            :required="!!f.required"
          >
            <u-input
              v-if="f.type === 'text' || f.type === 'number'"
              v-model="form[f.key]"
              :type="f.type === 'number' ? 'number' : 'text'"
              :placeholder="'请输入' + f.label"
              border="surround"
            />
            <u-textarea
              v-else-if="f.type === 'textarea'"
              v-model="form[f.key]"
              :placeholder="'请输入' + f.label"
            />
            <picker
              v-else-if="f.type === 'select'"
              mode="selector"
              :range="f.options || []"
              range-key="label"
              @change="onSelect(f, $event)"
            >
              <view class="picker-text" :class="{ placeholder: !hasValue(f) }">
                {{ selectedLabel(f) }}
              </view>
            </picker>
            <u-switch
              v-else-if="f.type === 'switch'"
              v-model="form[f.key]"
            />
          </u-form-item>
        </u-form>
      </scroll-view>
      <view class="sheet-foot">
        <u-button text="取消" type="default" @click="close" class="foot-btn" />
        <u-button type="primary" text="保存" @click="submit" class="foot-btn" />
      </view>
    </view>
  </u-popup>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  visible: Boolean,
  title: { type: String, default: '编辑' },
  fields: { type: Array, default: () => [] },
  modelValue: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['update:visible', 'submit'])

const form = reactive({})

function initForm() {
  for (const k in form) delete form[k]
  const src = props.modelValue || {}
  props.fields.forEach((f) => {
    let v = src[f.key]
    if (f.type === 'switch') v = !!v
    form[f.key] = v === undefined ? (f.type === 'number' ? null : '') : v
  })
}

watch(
  () => props.visible,
  (v) => {
    if (v) initForm()
  }
)

function hasValue(f) {
  const v = form[f.key]
  return v !== '' && v !== null && v !== undefined
}

function selectedLabel(f) {
  const opt = (f.options || []).find((o) => o.value === form[f.key])
  return opt ? opt.label : '请选择' + f.label
}

function onSelect(f, e) {
  const idx = e.detail.value
  form[f.key] = f.options[idx].value
}

function close() {
  emit('update:visible', false)
}

function submit() {
  for (const f of props.fields) {
    if (f.required && !hasValue(f)) {
      uni.showToast({ title: f.label + '不能为空', icon: 'none' })
      return
    }
  }
  const payload = {}
  props.fields.forEach((f) => {
    let v = form[f.key]
    if (f.type === 'number') v = v === '' || v == null ? null : Number(v)
    payload[f.key] = v
  })
  emit('submit', payload)
}
</script>

<style scoped>
.sheet {
  display: flex;
  flex-direction: column;
  max-height: 86vh;
}
.sheet-head {
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #ebeef5;
}
.sheet-body {
  padding: 20rpx 32rpx;
  flex: 1;
  min-height: 0;
}
.picker-text {
  font-size: 28rpx;
  color: #303133;
  padding: 10rpx 0;
}
.picker-text.placeholder {
  color: #c0c4cc;
}
.sheet-foot {
  display: flex;
  gap: 24rpx;
  padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #ebeef5;
}
.foot-btn {
  flex: 1;
  margin: 0;
}
</style>
