<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2 class="page-title">工作台</h2>
        <p class="page-subtitle">欢迎回来，这里是您的今日概览</p>
      </div>
      <el-button type="primary" icon="Refresh" @click="loadStats">刷新数据</el-button>
    </div>

    <!-- Key Metrics -->
    <div class="grid grid-4 gap-6 mb-6">
      <div class="pro-card metric-card">
        <div class="metric-icon blue-bg">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-label">在册老人</div>
          <div class="metric-value">{{ stats.elderCount || 0 }}</div>
        </div>
      </div>
      
      <div class="pro-card metric-card">
        <div class="metric-icon green-bg">
          <el-icon :size="24"><Monitor /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-label">健康监测</div>
          <div class="metric-value">{{ stats.healthRecordCount || 0 }}</div>
        </div>
      </div>
      
      <div class="pro-card metric-card">
        <div class="metric-icon orange-bg">
          <el-icon :size="24"><Bicycle /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-label">探访服务</div>
          <div class="metric-value">{{ stats.visitCount || 0 }}</div>
        </div>
      </div>

      <div class="pro-card metric-card">
        <div class="metric-icon purple-bg">
          <el-icon :size="24"><Bell /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-label">待办事项</div>
          <div class="metric-value">12</div>
        </div>
      </div>
    </div>

    <!-- Analysis Section -->
    <div class="grid grid-3 gap-6">
      <!-- Left Col: Large -->
      <div class="pro-card col-span-2" style="grid-column: span 2;">
         <div class="card-header">
           <h3>探访趋势分析</h3>
         </div>
         <div class="chart-placeholder">
            <el-table :data="trendTable" style="width: 100%" :show-header="true">
              <el-table-column prop="date" label="日期" />
              <el-table-column prop="count" label="服务人次">
                 <template #default="scope">
                    <div class="bar-container">
                       <div class="bar-fill" :style="{ width: (scope.row.count * 10 + 10) + '%' }"></div>
                       <span class="bar-text">{{ scope.row.count }}</span>
                    </div>
                 </template>
              </el-table-column>
            </el-table>
         </div>
      </div>

      <!-- Right Col: Small -->
      <div class="pro-card">
        <div class="card-header">
          <h3>健康等级分布</h3>
        </div>
        <div class="health-list">
           <div v-for="(item, index) in healthTable" :key="index" class="health-item">
              <div class="health-info">
                 <span class="health-name">{{ item.level }}</span>
                 <span class="health-count">{{ item.count }} 人</span>
              </div>
              <el-progress :percentage="calculatePercentage(item.count, stats.elderCount)" :color="getProgressColor(index)" :show-text="false" :stroke-width="8" />
           </div>
           <div v-if="healthTable.length === 0" class="text-center text-gray-400 py-4">暂无数据</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue'
import api from '../api'
import { User, Monitor, Bicycle, Bell, Refresh } from '@element-plus/icons-vue'

const stats = reactive({ elderCount: 0, healthRecordCount: 0, visitCount: 0, healthLevelDistribution: {}, visitTrend: {} })

const healthTable = computed(() => Object.entries(stats.healthLevelDistribution || {}).map(([level, count]) => ({ level, count })))
const trendTable = computed(() => Object.entries(stats.visitTrend || {}).map(([date, count]) => ({ date, count })).slice(-5))

const calculatePercentage = (val, total) => total ? Math.round((val / total) * 100) : 0
const getProgressColor = (idx) => {
  const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444']
  return colors[idx % colors.length]
}

const loadStats = async () => {
  const res = await api.get('/statistics')
  if (res.code === 0) {
    Object.assign(stats, res.data)
  }
}

onMounted(loadStats)
</script>

<style scoped>
.metric-card {
  display: flex;
  align-items: center;
  padding: 24px;
  height: 100%;
}

.metric-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.blue-bg { background: var(--primary-50); color: var(--primary-600); }
.green-bg { background: #ecfdf5; color: #059669; }
.orange-bg { background: #fffbeb; color: #d97706; }
.purple-bg { background: #f5f3ff; color: #7c3aed; }

.metric-content {
  flex: 1;
  overflow: hidden;
}

.metric-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--gray-500);
  margin-bottom: 4px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--gray-900);
  line-height: 1;
}

.card-header {
  margin-bottom: 20px;
  border-bottom: 1px solid var(--gray-100);
  padding-bottom: 16px;
  margin-left: -24px;
  margin-right: -24px;
  padding-left: 24px;
  padding-right: 24px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-900);
}

.health-item {
  margin-bottom: 24px;
}

.health-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--gray-700);
  font-weight: 500;
}

.bar-container {
  display: flex;
  align-items: center;
  height: 100%;
  width: 100%;
}

.bar-fill {
  height: 8px;
  background: var(--primary-500);
  border-radius: 4px;
  transition: width 0.5s ease-out;
}

.bar-text {
  margin-left: 12px;
  font-size: 13px;
  color: var(--gray-600);
  font-weight: 500;
}
</style>