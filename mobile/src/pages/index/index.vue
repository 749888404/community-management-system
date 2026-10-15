<template>
  <view class="home">
    <u-navbar
      :title="`你好，${realName || '访客'}`"
      :autoBack="false"
      bgColor="#2979ff"
      titleColor="#fff"
      leftIcon=""
    >
      <template #right>
        <u-avatar
          :text="(realName || '访')[0]"
          :size="64"
          bg-color="rgba(255,255,255,0.25)"
          color="#fff"
          fontSize="30"
        />
      </template>
    </u-navbar>

    <view class="role-bar">
      <text class="role-text">{{ roleText }}</text>
    </view>

    <view class="stats card">
      <view class="stat" v-for="s in stats" :key="s.label">
        <text class="num">{{ s.value }}</text>
        <text class="lab">{{ s.label }}</text>
      </view>
    </view>

    <view class="section-title">功能导航</view>
    <view class="card grid-wrap">
      <u-grid :border="false" col="3">
        <u-grid-item v-for="g in menus" :key="g.page" @click="go(g)">
          <view class="grid-icon">
            <u-icon :name="g.icon" size="48" color="#2979ff" />
          </view>
          <text class="grid-label">{{ g.label }}</text>
        </u-grid-item>
      </u-grid>
    </view>

    <u-button
      text="退出登录"
      icon="logout"
      type="error"
      plain
      class="logout-btn"
      @click="logout"
    />

    <view class="footer-tip">社区管理系统 · 手机端 v1.0</view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { pageCommunity } from '@/api/community'
import { pagePerson } from '@/api/person'
import { pageDisabled } from '@/api/disabled'
import { pageFile } from '@/api/file'

const userStore = useUserStore()
const realName = computed(() => userStore.realName)
const isAdmin = computed(() => userStore.role === 'ADMIN')
const roleText = computed(() => (isAdmin.value ? '系统管理员' : '普通用户'))

const counts = ref({ community: 0, person: 0, disabled: 0, file: 0 })
const stats = computed(() => [
  { label: '小区', value: counts.value.community },
  { label: '居民', value: counts.value.person },
  { label: '残疾人', value: counts.value.disabled },
  { label: '文件', value: counts.value.file }
])

const menus = [
  { label: '小区管理', icon: 'home', page: '/pages/community/community', tab: true },
  { label: '居民管理', icon: 'man', page: '/pages/person/person', tab: true },
  { label: '残疾人管理', icon: 'heart', page: '/pages/disabled/disabled', tab: true },
  { label: '文件管理', icon: 'file-text', page: '/pages/file/file', tab: true },
  { label: '用户管理', icon: 'account', page: '/pages/user/user', tab: false, admin: true }
].filter((m) => !m.admin || isAdmin.value)

function go(g) {
  if (g.tab) uni.switchTab({ url: g.page })
  else uni.navigateTo({ url: g.page })
}

function logout() {
  uni.showModal({
    title: '提示',
    content: '确认退出登录？',
    success: (r) => {
      if (r.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

async function loadStats() {
  try {
    const [c, p, d, f] = await Promise.all([
      pageCommunity({ page: 1, size: 1 }),
      pagePerson({ page: 1, size: 1 }),
      pageDisabled({ page: 1, size: 1 }),
      pageFile({ page: 1, size: 1 })
    ])
    counts.value = {
      community: c.total || 0,
      person: p.total || 0,
      disabled: d.total || 0,
      file: f.total || 0
    }
  } catch (e) {
    // ignore
  }
}

onShow(() => {
  loadStats()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f6f8;
  padding-bottom: 40rpx;
}
.role-bar {
  background: #2979ff;
  padding: 10rpx 40rpx 40rpx;
  margin-top: -2rpx;
}
.role-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 26rpx;
}
.card {
  background: #fff;
  border-radius: 20rpx;
  margin: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.05);
}
.stats {
  display: flex;
  padding: 36rpx 0;
  margin-top: -24rpx;
}
.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.num {
  font-size: 44rpx;
  font-weight: 700;
  color: #2979ff;
}
.lab {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  margin: 36rpx 36rpx 8rpx;
}
.grid-wrap {
  padding: 20rpx 0;
}
.grid-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background: #ecf3ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14rpx;
}
.grid-label {
  font-size: 26rpx;
  color: #606266;
  margin-top: 10rpx;
}
.logout-btn {
  margin: 36rpx 24rpx 0;
  color: #fa3534;
}
.footer-tip {
  text-align: center;
  font-size: 22rpx;
  color: #c0c4cc;
  margin: 30rpx 0 20rpx;
}
</style>
