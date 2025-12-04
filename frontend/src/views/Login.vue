<template>
  <div class="login-container">
    <!-- Mesh Gradient Background -->
    <div class="mesh-bg"></div>
    
    <!-- Glass Card -->
    <div class="login-content">
      <div class="login-card">
        <div class="card-header">
          <div class="logo-container">
            <el-icon class="logo-icon" :size="32"><Promotion /></el-icon>
          </div>
          <h1 class="brand-title">ElderCare</h1>
          <p class="brand-subtitle">智慧社区养老服务管理系统</p>
        </div>

        <el-form 
          ref="loginFormRef"
          :model="form"
          :rules="rules"
          class="login-form"
          size="large"
          @submit.prevent="onSubmit"
        >
          <el-form-item prop="username">
            <div class="input-group">
              <span class="input-label">账号</span>
              <el-input 
                v-model="form.username" 
                placeholder="请输入管理员账号"
                :prefix-icon="User"
                class="custom-input"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
             <div class="input-group">
              <span class="input-label">密码</span>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                class="custom-input"
              />
             </div>
          </el-form-item>

          <div class="form-actions">
            <el-checkbox v-model="rememberMe" label="记住我" />
            <a href="#" class="forgot-link">忘记密码?</a>
          </div>

          <el-button 
            type="primary" 
            class="submit-btn" 
            :loading="loading" 
            @click="onSubmit"
            round
          >
            登录系统
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <div class="card-footer">
          <p>© 2025 Graduation Project Team. Design System v2.0</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { ElMessage } from 'element-plus'
import { User, Lock, Promotion, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const form = reactive({ username: 'admin', password: '123456' })
const rememberMe = ref(true)
const loading = ref(false)
const loginFormRef = ref(null)

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const onSubmit = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // Simulate network delay for better UX feel
        await new Promise(r => setTimeout(r, 600))
        
        const res = await api.post('/auth/login', form)
        if (res.code === 0) {
          localStorage.setItem('token', res.data.token)
          ElMessage.success('欢迎回来，管理员')
          router.push('/')
        }
      } catch (e) {
        // Error handled by interceptor usually, but safe catch
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #f3f4f6;
}

/* Mesh Gradient Animation */
.mesh-bg {
  position: absolute;
  top: -50%;
  left: -50%;
  right: -50%;
  bottom: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 50% 50%, #e0e7ff 0%, transparent 50%),
              radial-gradient(circle at 0% 0%, #dbeafe 0%, transparent 50%),
              radial-gradient(circle at 100% 100%, #f3e8ff 0%, transparent 50%);
  animation: meshMove 20s ease infinite alternate;
  z-index: 0;
  filter: blur(80px);
}

@keyframes meshMove {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(-5%, -5%) rotate(5deg); }
}

.login-content {
  position: relative;
  z-index: 10;
  padding: 20px;
  width: 100%;
  max-width: 440px;
  animation: cardFloat 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
}

@keyframes cardFloat {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.login-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 
    0 20px 40px -10px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255,255,255,0.5) inset;
}

.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 16px -4px rgba(37, 99, 235, 0.3);
  color: white;
}

.brand-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  letter-spacing: -0.03em;
  margin: 0 0 8px;
}

.brand-subtitle {
  color: #6b7280;
  font-size: 15px;
  margin: 0;
}

.input-group {
  margin-bottom: 4px;
}

.input-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

/* Customizing Element Inputs inside Login */
:deep(.el-input__wrapper) {
  background: #fff !important;
  box-shadow: 0 0 0 1px #e5e7eb inset !important;
  padding: 8px 12px !important;
  height: 44px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #dbeafe inset, 0 0 0 1px #2563eb inset !important;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  margin-top: -8px;
}

.forgot-link {
  color: #2563eb;
  font-size: 13px;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  letter-spacing: 0.5px;
  background: linear-gradient(to right, #2563eb, #1d4ed8) !important;
  border: none !important;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -5px rgba(37, 99, 235, 0.4) !important;
}

.card-footer {
  margin-top: 32px;
  text-align: center;
  border-top: 1px solid rgba(0,0,0,0.05);
  padding-top: 20px;
}

.card-footer p {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}
</style>