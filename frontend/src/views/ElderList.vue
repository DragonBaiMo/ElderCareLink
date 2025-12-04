<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">老人档案管理</h2>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="openDialog()">新增档案</el-button>
        <el-button icon="Download" @click="exportData">导出 Excel</el-button>
      </div>
    </div>

    <!-- Search Filter -->
    <div class="filter-container">
      <el-form :inline="true" :model="query">
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="请输入姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="居住类型">
          <el-select v-model="query.livingType" clearable placeholder="请选择" style="width: 160px">
            <el-option v-for="item in livingTypes" :key="item.id" :label="item.dictValue" :value="item.dictKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康等级">
          <el-select v-model="query.healthLevel" clearable placeholder="请选择" style="width: 160px">
            <el-option v-for="item in healthLevels" :key="item.id" :label="item.dictValue" :value="item.dictKey" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Data Table -->
    <div class="pro-card no-padding">
      <el-table :data="table.records" style="width: 100%">
        <el-table-column prop="name" label="姓名" width="120" fixed>
           <template #default="scope">
              <span class="font-bold">{{ scope.row.name }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="livingType" label="居住类型">
           <template #default="scope">
             <el-tag :type="scope.row.livingType === '独居' ? 'warning' : 'info'" effect="plain">{{ scope.row.livingType }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="healthLevel" label="健康等级">
           <template #default="scope">
              <div class="flex items-center">
                 <span class="status-dot" :class="getHealthColor(scope.row.healthLevel)"></span>
                 {{ scope.row.healthLevel }}
              </div>
           </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="responsibleVolunteerName" label="责任志愿者" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
            <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="disable(scope.row.id)">注销</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Pagination -->
      <div style="padding: 16px; display: flex; justify-content: flex-end;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="table.total"
          :page-size="query.size"
          @current-change="(p) => { query.page = p; load() }"
        />
      </div>
    </div>

    <!-- Edit Dialog -->
    <el-dialog v-model="dialog.visible" :title="dialog.form.id ? '编辑档案' : '新增档案'" width="600px">
      <el-form :model="dialog.form" label-width="100px">
        <el-row :gutter="20">
           <el-col :span="12"><el-form-item label="姓名"><el-input v-model="dialog.form.name" /></el-form-item></el-col>
           <el-col :span="12">
             <el-form-item label="性别">
               <el-radio-group v-model="dialog.form.gender">
                 <el-radio label="男">男</el-radio>
                 <el-radio label="女">女</el-radio>
               </el-radio-group>
             </el-form-item>
           </el-col>
        </el-row>
        <el-row :gutter="20">
           <el-col :span="12"><el-form-item label="年龄"><el-input-number v-model="dialog.form.age" :min="50" :max="120" style="width:100%" /></el-form-item></el-col>
           <el-col :span="12"><el-form-item label="电话"><el-input v-model="dialog.form.phone" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="家庭地址"><el-input v-model="dialog.form.address" /></el-form-item>
        <el-row :gutter="20">
           <el-col :span="12">
              <el-form-item label="居住类型">
                <el-select v-model="dialog.form.livingType" style="width:100%">
                  <el-option v-for="i in livingTypes" :key="i.id" :label="i.dictValue" :value="i.dictKey" />
                </el-select>
              </el-form-item>
           </el-col>
           <el-col :span="12">
              <el-form-item label="健康等级">
                 <el-select v-model="dialog.form.healthLevel" style="width:100%">
                  <el-option v-for="i in healthLevels" :key="i.id" :label="i.dictValue" :value="i.dictKey" />
                </el-select>
              </el-form-item>
           </el-col>
        </el-row>
        <el-divider>关联信息</el-divider>
        <el-form-item label="责任志愿者">
           <el-select v-model="dialog.form.responsibleVolunteerId" filterable clearable placeholder="搜索志愿者" style="width:100%" @change="syncVolunteerName">
             <el-option v-for="v in volunteers" :key="v.id" :label="v.realName" :value="v.id" />
           </el-select>
        </el-form-item>
        <el-form-item label="备注说明"><el-input type="textarea" v-model="dialog.form.remarks" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="save">确认保存</el-button>
      </template>
    </el-dialog>
    
    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="档案详情" size="500px">
       <div v-if="detail" class="detail-content">
          <div class="detail-header">
             <div class="detail-avatar">{{ detail.elder.name.charAt(0) }}</div>
             <div>
                <h3 class="text-lg font-bold m-0">{{ detail.elder.name }}</h3>
                <p class="text-gray-500 m-0 text-sm mt-1">{{ detail.elder.gender }} / {{ detail.elder.age }}岁 / {{ detail.elder.livingType }}</p>
             </div>
          </div>
          <div class="detail-section mt-6">
             <h4 class="section-title">最近健康记录</h4>
             <div v-for="h in detail.recentHealthRecords" :key="h.id" class="record-item">
                <div class="record-date">{{ h.recordDate.substring(0,10) }}</div>
                <div class="record-desc">{{ h.healthDesc || '常规检查' }} - 血压 {{ h.bloodPressure }}</div>
             </div>
          </div>
       </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'

const query = reactive({ page: 1, size: 10, name: '', livingType: '', healthLevel: '' })
const table = reactive({ records: [], total: 0 })
const dialog = reactive({ visible: false, form: {} })
const livingTypes = ref([])
const healthLevels = ref([])
const volunteers = ref([])
const detailVisible = ref(false)
const detail = ref(null)

const loadDicts = async () => {
  const living = await api.get('/dicts/living_type')
  const health = await api.get('/dicts/health_level')
  livingTypes.value = living.code === 0 ? living.data : []
  healthLevels.value = health.code === 0 ? health.data : []
}

const loadVolunteers = async () => {
  const res = await api.get('/users', { params: { role: 'VOLUNTEER', page: 0, size: 50 } })
  volunteers.value = res.code === 0 ? res.data.content : []
}

const load = async () => {
  const params = { ...query, page: query.page - 1 }
  const res = await api.get('/elders', { params })
  if (res.code === 0) {
    table.records = res.data.content
    table.total = res.data.totalElements
  }
}

const resetQuery = () => {
  query.name = ''
  query.livingType = ''
  query.healthLevel = ''
  load()
}

const openDialog = (row = null) => {
  dialog.visible = true
  dialog.form = row ? { ...row } : { name: '', gender: '男', age: 65, livingType: '', healthLevel: '', phone: '', responsibleVolunteerId: '' }
}

const syncVolunteerName = (val) => {
  const user = volunteers.value.find(v => v.id === val)
  if (user) dialog.form.responsibleVolunteerName = user.realName
}

const save = async () => {
  if (dialog.form.id) await api.put(`/elders/${dialog.form.id}`, dialog.form)
  else await api.post('/elders', dialog.form)
  ElMessage.success('保存成功')
  dialog.visible = false
  load()
}

const disable = async (id) => {
  await ElMessageBox.confirm('确认注销吗？')
  await api.patch(`/elders/${id}/disable`)
  ElMessage.success('已注销')
  load()
}

const viewDetail = async (row) => {
  const res = await api.get(`/elders/${row.id}`)
  if(res.code===0) {
    detail.value = res.data
    detailVisible.value = true
  }
}

const exportData = () => {
   ElMessage.info('导出功能正在准备中')
}

const getHealthColor = (level) => {
  if(!level) return 'bg-gray'
  if(level.includes('健康')) return 'bg-success'
  if(level.includes('风险')) return 'bg-warning'
  return 'bg-error'
}

onMounted(() => {
  loadDicts()
  loadVolunteers()
  load()
})
</script>

<style scoped>
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
  display: inline-block;
}
.bg-success { background-color: #52c41a; }
.bg-warning { background-color: #faad14; }
.bg-error { background-color: #ff4d4f; }
.bg-gray { background-color: #d9d9d9; }

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-avatar {
  width: 56px;
  height: 56px;
  background: #e6f4ff;
  color: #1677ff;
  font-size: 24px;
  font-weight: 600;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-title {
  font-size: 16px;
  margin-bottom: 12px;
  color: #1f1f1f;
}

.record-item {
  background: #fafafa;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}
.record-date { font-size: 12px; color: #999; margin-bottom: 4px; }
</style>
