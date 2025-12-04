<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">字典管理</h2>
      <el-button type="primary" icon="Plus" @click="showAdd=true">新增字典项</el-button>
    </div>

    <div class="grid grid-3 gap-6">
       <!-- Left: Sidebar List style for Types -->
       <div class="pro-card no-padding" style="height: fit-content;">
          <div class="type-header">字典类型</div>
          <ul class="type-menu">
             <li v-for="t in types" :key="t.value" :class="{active: query.type === t.value}" @click="selectType(t.value)">
                <span>{{ t.label }}</span>
                <span class="code">{{ t.value }}</span>
             </li>
          </ul>
       </div>

       <!-- Right: Table -->
       <div class="pro-card no-padding col-span-2" style="grid-column: span 2;">
          <el-table :data="list" style="width: 100%">
             <el-table-column prop="dictKey" label="键 (Key)">
                <template #default="scope">
                   <span class="font-medium">{{ scope.row.dictKey }}</span>
                </template>
             </el-table-column>
             <el-table-column prop="dictValue" label="值 (Value)" />
             <el-table-column label="操作" width="150">
                <template #default="scope">
                   <el-button link type="primary" @click="edit(scope.row)">编辑</el-button>
                   <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
                </template>
             </el-table-column>
          </el-table>
          <div v-if="list.length===0" class="empty-state">暂无数据</div>
       </div>
    </div>
    
    <el-dialog v-model="showAdd" :title="form.id ? '编辑字典' : '新增字典'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
           <el-select v-model="form.dictType" disabled style="width:100%">
              <el-option v-for="t in types" :key="t.value" :label="t.label" :value="t.value" />
           </el-select>
        </el-form-item>
        <el-form-item label="键"><el-input v-model="form.dictKey" placeholder="例如：MALE" /></el-form-item>
        <el-form-item label="值"><el-input v-model="form.dictValue" placeholder="例如：男" /></el-form-item>
      </el-form>
      <template #footer>
         <el-button @click="showAdd=false">取消</el-button>
         <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const query = reactive({ type: 'living_type' })
const list = ref([])
const showAdd = ref(false)
const form = reactive({ id: null, dictType: 'living_type', dictKey: '', dictValue: '' })

const types = [
  { label: '居住类型', value: 'living_type' },
  { label: '健康等级', value: 'health_level' },
  { label: '性别', value: 'gender' }
]

const selectType = (val) => {
  query.type = val
  form.dictType = val
  load()
}

const load = async () => {
  const res = await api.get(`/dicts/${query.type}`)
  if (res.code === 0) list.value = res.data
}

const save = async () => {
  if (form.id) await api.put(`/dicts/${form.id}`, form) // Assuming put exists or post handles update
  else await api.post('/dicts', form)
  
  ElMessage.success('保存成功')
  showAdd.value = false
  load()
}

const edit = (row) => {
  Object.assign(form, row)
  showAdd.value = true
}

const remove = async (id) => {
   await ElMessageBox.confirm('确认删除?')
   await api.delete(`/dicts/${id}`)
   load()
}

onMounted(load)
</script>

<style scoped>
.type-header {
  padding: 16px 20px;
  font-weight: 600;
  font-size: 14px;
  color: var(--gray-900);
  border-bottom: 1px solid var(--gray-100);
  background: var(--gray-50);
}

.type-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.type-menu li {
  padding: 14px 20px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--gray-50);
  transition: all 0.2s;
  font-size: 14px;
  color: var(--gray-700);
}

.type-menu li:hover {
  background: var(--gray-50);
  color: var(--gray-900);
}

.type-menu li.active {
  background: var(--primary-50);
  color: var(--primary-700);
  font-weight: 500;
  border-right: 3px solid var(--primary-600);
}

.type-menu li .code {
  font-size: 12px;
  color: var(--gray-400);
  font-family: monospace;
}
.type-menu li.active .code {
  color: var(--primary-400);
}

.empty-state {
  padding: 40px;
  text-align: center;
  color: var(--gray-400);
  font-size: 14px;
}
</style>
