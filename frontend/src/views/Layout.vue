<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <div class="logo">社区管理系统</div>
      <el-menu
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
        :default-active="activeMenu">
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/community">
          <el-icon><House /></el-icon>
          <span>小区管理</span>
        </el-menu-item>
        <el-menu-item index="/person">
          <el-icon><Avatar /></el-icon>
          <span>居民管理</span>
        </el-menu-item>
        <el-menu-item index="/disabled">
          <el-icon><Stamp /></el-icon>
          <span>残疾人管理</span>
        </el-menu-item>
        <el-menu-item index="/file">
          <el-icon><Folder /></el-icon>
          <span>文件管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;background:#fff;border-bottom:1px solid #ebeef5;padding:0 20px">
        <span style="font-size:16px;font-weight:bold;color:#1f2d3d">{{ title }}</span>
        <span class="user-box">当前用户：{{ username }} <el-button text type="primary" @click="logout">退出</el-button></span>
      </el-header>
      <el-main style="padding:16px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const title = computed(() => route.meta.title || '')
const username = localStorage.getItem('realName') || localStorage.getItem('username') || 'admin'

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('realName')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: bold;
  letter-spacing: 1px;
  background: #2b3a4d;
}
.el-header { height: 60px; }
.user-box { font-size: 14px; color: #606266; }
</style>`
