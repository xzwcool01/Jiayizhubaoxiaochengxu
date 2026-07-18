<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '' })

function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  localStorage.setItem('token', 'mock_token')
  router.push('/')
}
</script>
<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="title">嘉怡珠宝</h1>
      <p class="subtitle">后台管理系统</p>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%; background:#C8A27A; border-color:#C8A27A" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<style scoped>
.login-container { display: flex; align-items: center; justify-content: center; height: 100vh; background: linear-gradient(135deg, #F8F6F3, #E8D5C0); }
.login-card { width: 400px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
.title { text-align: center; color: #C8A27A; font-size: 28px; margin: 0 0 4px; }
.subtitle { text-align: center; color: #999; margin: 0 0 32px; font-size: 14px; }
</style>
