<template>
  <view class="login">
    <!-- 背景装饰光斑 -->
    <view class="bg-circle circle-1"></view>
    <view class="bg-circle circle-2"></view>

    <!-- 顶部品牌区 -->
    <view class="header">
      <view class="logo">
        <u-icon name="home-fill" size="64" color="#ffffff"></u-icon>
      </view>
      <text class="title">社区管理系统</text>
      <text class="subtitle">手机端 · 欢迎登录</text>
    </view>

    <!-- 登录卡片 -->
    <view class="login-card">
      <u-form :model="form" class="form">
        <u-form-item prop="username">
          <u-input
            v-model="form.username"
            placeholder="请输入账号"
            prefixIcon="account"
            shape="circle"
            clearable
          />
        </u-form-item>
        <u-form-item prop="password">
          <u-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefixIcon="lock"
            shape="circle"
            clearable
          />
        </u-form-item>
      </u-form>

      <u-button
        size="large"
        :loading="loading"
        text="登 录"
        @click="onLogin"
        class="btn"
        :customStyle="btnStyle"
      />

      <view class="demo-tip">
        <u-icon name="info-circle" size="24" color="#a0a8b8"></u-icon>
        <text class="tip-text">演示账号：admin / 123456</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

const btnStyle = {
  background: 'linear-gradient(135deg, #409eff 0%, #67c23a 100%)',
  color: '#ffffff',
  border: 'none',
  borderRadius: '999rpx',
  boxShadow: '0 12rpx 28rpx rgba(64, 158, 255, 0.35)'
}

onMounted(() => {
  if (userStore.isLogin) {
    uni.switchTab({ url: '/pages/index/index' })
  }
})

async function onLogin() {
  if (!form.value.username || !form.value.password) {
    uni.showToast({ title: '请输入账号和密码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const d = await login(form.value)
    userStore.setUser(d.token, {
      username: d.username,
      realName: d.realName,
      role: d.role
    })
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 500)
  } catch (e) {
    // 错误已在 request 层提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  overflow: hidden;
}
.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}
.circle-1 {
  width: 440rpx;
  height: 440rpx;
  top: -140rpx;
  right: -120rpx;
}
.circle-2 {
  width: 320rpx;
  height: 320rpx;
  bottom: 80rpx;
  left: -130rpx;
  background: rgba(255, 255, 255, 0.08);
}

.header {
  position: relative;
  z-index: 2;
  padding: 150rpx 0 70rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: 38rpx;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(64, 158, 255, 0.35);
}
.title {
  font-size: 46rpx;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 4rpx;
}
.subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.82);
  margin-top: 14rpx;
}

.login-card {
  position: relative;
  z-index: 2;
  margin: 20rpx 50rpx 0;
  background: #ffffff;
  border-radius: 36rpx;
  padding: 60rpx 44rpx 44rpx;
  box-shadow: 0 24rpx 60rpx rgba(0, 0, 0, 0.22);
}
.form {
  width: 100%;
  :deep(.u-input__content) {
    background: #f5f6fa;
    border-radius: 999rpx;
    padding: 0 28rpx;
    min-height: 92rpx;
  }
  :deep(.u-form-item) {
    margin-bottom: 28rpx;
  }
}
.btn {
  width: 100%;
  margin-top: 14rpx;
}
.demo-tip {
  margin-top: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tip-text {
  font-size: 24rpx;
  color: #a0a8b8;
  margin-left: 10rpx;
}
</style>
