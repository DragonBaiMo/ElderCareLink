<template>
  <div class="app-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-mark">
          <el-icon :size="20"><Promotion /></el-icon>
        </div>
        <h1 class="sidebar-title">ElderCare</h1>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-group">
          <div class="nav-label">MAIN</div>
          <router-link to="/" class="nav-item" exact-active-class="active">
            <el-icon :size="18"><Odometer /></el-icon>
            <span>工作台</span>
          </router-link>
        </div>

        <div class="nav-group">
          <div class="nav-label">CARE SERVICES</div>
          <router-link to="/elders" class="nav-item" active-class="active">
            <el-icon :size="18"><User /></el-icon>
            <span>老人档案</span>
          </router-link>
          <router-link to="/health" class="nav-item" active-class="active">
            <el-icon :size="18"><Monitor /></el-icon>
            <span>健康监测</span>
          </router-link>
          <router-link to="/visits" class="nav-item" active-class="active">
            <el-icon :size="18"><Bicycle /></el-icon>
            <span>探访记录</span>
          </router-link>
        </div>

        <div class="nav-group">
          <div class="nav-label">SYSTEM</div>
          <router-link to="/announcements" class="nav-item" active-class="active">
            <el-icon :size="18"><Bell /></el-icon>
            <span>通知公告</span>
          </router-link>
          <router-link to="/users" class="nav-item" active-class="active">
            <el-icon :size="18"><Setting /></el-icon>
            <span>用户权限</span>
          </router-link>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-card">
          <el-avatar :size="32" style="background: #BFDBFE; color: #1E40AF; font-size: 12px; font-weight: 600;">AD</el-avatar>
          <div class="user-info">
            <div class="user-name">Admin User</div>
            <div class="user-role">System Admin</div>
          </div>
          <el-button link @click="logout" class="logout-btn">
            <el-icon :size="16"><SwitchButton /></el-icon>
          </el-button>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Top Bar -->
      <header class="top-header">
        <div class="breadcrumb">
           <span class="text-gray-400">Application</span>
           <span class="divider">/</span>
           <span class="current">{{ currentRouteName }}</span>
        </div>
        <div class="header-tools">
          <div class="icon-btn">
            <el-icon :size="18"><Search /></el-icon>
          </div>
          <div class="icon-btn">
            <el-icon :size="18"><Bell /></el-icon>
            <span class="dot"></span>
          </div>
        </div>
      </header>

      <!-- Page View -->
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade-scale" mode="out-in">
            <div class="route-container">
               <component :is="Component" />
            </div>
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Odometer, User, Monitor, Bicycle, Bell, Setting, 
  Promotion, Search, SwitchButton 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const routeNameMap = {
  '/': '工作台',
  '/elders': '老人档案',
  '/health': '健康监测',
  '/visits': '探访记录',
  '/announcements': '通知公告',
  '/users': '用户权限'
}

const currentRouteName = computed(() => routeNameMap[route.path] || 'Dashboard')

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  background-color: var(--gray-50);
  overflow: hidden;
}

/* Sidebar Styles */
.sidebar {
  width: 260px;
  height: 100%;
  background: #fff;
  border-right: 1px solid var(--gray-200);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  z-index: 20;
}

.sidebar-header {
  height: 64px; /* Fixed Height */
  display: flex;
  align-items: center;
  padding: 0 24px;
  border-bottom: 1px solid transparent; /* Placeholder alignment */
}

.logo-mark {
  width: 32px;
  height: 32px;
  background: var(--primary-600);
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--gray-900);
  letter-spacing: -0.02em;
}

.sidebar-nav {
  flex: 1;
  padding: 24px 12px;
  overflow-y: auto;
}

.nav-group {
  margin-bottom: 24px;
}

.nav-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--gray-400);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
  padding-left: 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  height: 36px;
  padding: 0 12px;
  border-radius: 6px;
  color: var(--gray-600);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
  transition: all 0.15s ease;
  text-decoration: none;
  cursor: pointer;
}

.nav-item .el-icon {
  margin-right: 10px;
  color: var(--gray-400);
  transition: color 0.15s;
  vertical-align: middle; /* Ensure alignment */
}

.nav-item span {
  line-height: 1; /* Prevent text jump */
}

.nav-item:hover {
  background-color: var(--gray-100);
  color: var(--gray-900);
}

.nav-item:hover .el-icon {
  color: var(--gray-600);
}

.nav-item.active {
  background-color: var(--primary-50);
  color: var(--primary-700);
}

.nav-item.active .el-icon {
  color: var(--primary-600);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--gray-100);
}

.user-card {
  display: flex;
  align-items: center;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-card:hover {
  background: var(--gray-50);
}

.user-info {
  margin-left: 12px;
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--gray-900);
  line-height: 1.2;
}

.user-role {
  font-size: 11px;
  color: var(--gray-500);
  line-height: 1.2;
  margin-top: 2px;
}

.logout-btn {
  color: var(--gray-400);
  width: 32px;
  height: 32px;
  display: flex !important; 
  align-items: center;
  justify-content: center;
}
.logout-btn:hover {
  color: var(--error);
  background: transparent !important;
}

/* Main Content Area */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.top-header {
  height: 64px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9); /* More opaque */
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--gray-200);
  z-index: 10;
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  line-height: 1;
}

.breadcrumb .divider {
  margin: 0 8px;
  color: var(--gray-300);
}

.breadcrumb .current {
  font-weight: 600;
  color: var(--gray-900);
}

.header-tools {
  display: flex;
  gap: 4px;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--gray-500);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.icon-btn:hover {
  background: var(--gray-100);
  color: var(--gray-900);
}

.dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 6px;
  height: 6px;
  background: var(--error);
  border-radius: 50%;
}

.content-wrapper {
  flex: 1;
  overflow: hidden; /* Parent handles overflow */
  position: relative;
  background: var(--gray-50);
}

.route-container {
  height: 100%;
  width: 100%;
  overflow-y: auto; /* Scroll happens here */
  padding-bottom: 40px;
}

/* Route Transition */
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.fade-scale-enter-from {
  opacity: 0;
  transform: scale(0.99);
}

.fade-scale-leave-to {
  opacity: 0;
  transform: scale(1.01);
}
</style>