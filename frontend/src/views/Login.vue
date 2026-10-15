<template>
  <div class="login-wrap">
    <el-card class="login-box">
      <h2 style="text-align:center;margin-bottom:20px;">社区管理系统</h2>
      <el-form :model="form" label-width="70px" @keyup.enter="onLogin">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="123456" />
        </el-form-item>
        <el-button type="primary" style="width:100%" @click="onLogin">登录</el-button>
      </el-form>
      <p style="text-align:center;color:#999;font-size:12px;margin-top:10px;">演示账号：admin / 123456</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const form = reactive({ username: 'admin', password: '123456' })

function onLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  login({ username: form.username, password: form.password }).then(res => {
    const d = res.data
    localStorage.setItem('token', d.token)
    localStorage.setItem('username', d.username)
    localStorage.setItem('realName', d.realName || d.username)
    localStorage.setItem('role', d.role || '')
    ElMessage.success('登录成功')
    router.push('/')
  })
}
</script>

<style scoped>
.login-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff, #67c23a);
}
.login-box {
  width: 360px;
}
</style>
