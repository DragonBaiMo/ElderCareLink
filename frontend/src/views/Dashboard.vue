<template>
  <div class="panel-card">
    <div class="section-title">数据驾驶舱</div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">老人总数</div>
          <div class="stat-value">{{ stats.elderCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">健康记录数</div>
          <div class="stat-value">{{ stats.healthRecordCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">探访记录数</div>
          <div class="stat-value">{{ stats.visitCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-3">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>健康等级分布</template>
          <el-table :data="healthTable" size="small" stripe>
            <el-table-column prop="level" label="等级" />
            <el-table-column prop="count" label="数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>探访趋势（按日）</template>
          <el-table :data="trendTable" size="small" stripe>
            <el-table-column prop="date" label="日期" />
            <el-table-column prop="count" label="次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-3">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>志愿者服务排行</template>
          <el-table :data="rankingTable" size="small" stripe>
            <el-table-column prop="name" label="志愿者" />
            <el-table-column prop="count" label="探访次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue'
import api from '../api'

const stats = reactive({ elderCount: 0, healthRecordCount: 0, visitCount: 0, healthLevelDistribution: {}, visitTrend: {}, volunteerRanking: {} })

const healthTable = computed(() => Object.entries(stats.healthLevelDistribution || {}).map(([level, count]) => ({ level, count })))
const trendTable = computed(() => Object.entries(stats.visitTrend || {}).map(([date, count]) => ({ date, count })))
const rankingTable = computed(() => Object.entries(stats.volunteerRanking || {}).map(([name, count]) => ({ name, count })))

onMounted(async () => {
  const res = await api.get('/statistics')
  if (res.code === 0) {
    Object.assign(stats, res.data)
  }
})
</script>

<style scoped>
.stat-card {
  border: none;
  background: linear-gradient(135deg, #e8f4ff, #ffffff);
  min-height: 120px;
}

.stat-label {
  color: #607080;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: #1f2d3d;
}
</style>
