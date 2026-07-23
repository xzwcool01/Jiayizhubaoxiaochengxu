<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAiWearPromptList, saveAiWearPrompt, type AiWearPromptVO } from '@/api/aiWearPrompt'

const list = ref<AiWearPromptVO[]>([])
const editVisible = ref(false)
const editing = ref<AiWearPromptVO | null>(null)

async function fetchData() {
  const res = await getAiWearPromptList()
  if (res.code === 200) list.value = res.data || []
}

function openEdit(row: AiWearPromptVO) {
  editing.value = { ...row }
  editVisible.value = true
}

async function handleSave() {
  if (!editing.value) return
  await saveAiWearPrompt(editing.value.categoryId, editing.value.promptTemplate)
  ElMessage.success('保存成功')
  editVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>AI 试戴提示词配置</h2></div>
    <p style="color:#999;font-size:13px;margin-bottom:16px">为每个商品分类配置 AI 试戴的提示词模板。模板中使用 <code>{name}</code> 代表商品名称、<code>{desc}</code> 代表商品描述。</p>
    <el-card shadow="never">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column prop="categoryId" label="编号" width="60" />
        <el-table-column prop="categoryName" label="商品分类" width="120" />
        <el-table-column label="提示词模板" min-width="400">
          <template #default="{ row }">
            <span style="font-size:13px;color:#605E5A;line-height:1.6;white-space:pre-wrap">{{ row.promptTemplate || '（未配置，使用默认提示词）' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">{{ row.promptTemplate ? '编辑' : '配置' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑提示词" width="600px">
      <el-form v-if="editing" label-width="100px">
        <el-form-item label="商品分类"><el-tag>{{ editing.categoryName }}</el-tag></el-form-item>
        <el-form-item label="提示词模板">
          <el-input v-model="editing.promptTemplate" type="textarea" :rows="6" placeholder="输入提示词模板，使用 {name} 和 {desc} 作为变量占位" />
        </el-form-item>
        <el-form-item label="变量说明">
          <div style="font-size:12px;color:#999;line-height:1.8">
            <code>{name}</code> = 商品名称<br>
            <code>{desc}</code> = 商品描述文字
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
code { background:#f4f4f4; padding:2px 6px; border-radius:4px; font-size:12px; color:#775836; }
</style>
