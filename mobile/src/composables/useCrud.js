import { ref, reactive } from 'vue'

// 通用 CRUD 列表状态管理
export function useCrud(api) {
  const records = ref([])
  const total = ref(0)
  const page = ref(1)
  const size = ref(10)
  const loading = ref(false)
  const finished = ref(false)
  const search = reactive({})
  const selectedIds = ref([])

  function clean(obj) {
    const o = {}
    for (const k in obj) {
      const v = obj[k]
      if (v !== '' && v !== null && v !== undefined) o[k] = v
    }
    return o
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
      const res = await api.page({
        page: page.value,
        size: size.value,
        ...clean(search)
      })
      const list = (res && res.records) || []
      if (refresh) records.value = list
      else records.value.push(...list)
      total.value = (res && res.total) || 0
      if (records.value.length >= total.value) finished.value = true
      else page.value++
    } catch (e) {
      // 错误提示已在 request 层处理
    } finally {
      loading.value = false
    }
  }

  function onSearch() {
    load(true)
  }

  function onReset() {
    for (const k in search) search[k] = ''
    load(true)
  }

  function onSearchSelect(s, e) {
    search[s.key] = s.options[e.detail.value].value
  }

  function searchLabel(s) {
    const o = (s.options || []).find((x) => x.value === search[s.key])
    return o ? o.label : '全部'
  }

  async function batchDelete() {
    if (!selectedIds.value.length) return
    const ok = await new Promise((resolve) => {
      uni.showModal({
        title: '提示',
        content: `确认删除选中的 ${selectedIds.value.length} 条记录？`,
        success: (r) => resolve(r.confirm)
      })
    })
    if (!ok) return
    await api.batchRemove(selectedIds.value)
    uni.showToast({ title: '删除成功', icon: 'success' })
    selectedIds.value = []
    load(true)
  }

  return {
    records,
    total,
    page,
    size,
    loading,
    finished,
    search,
    selectedIds,
    load,
    onSearch,
    onReset,
    onSearchSelect,
    searchLabel,
    batchDelete
  }
}
