import request from '@/utils/axios'

export interface AiWearPromptVO {
  categoryId: number
  categoryName: string
  promptTemplate: string
  promptId: number | null
}

export function getAiWearPromptList() {
  return request.get<any, { code: number; message: string; data: AiWearPromptVO[] }>('/admin/ai-wear/prompt/list')
}

export function saveAiWearPrompt(categoryId: number, promptTemplate: string) {
  return request.post<any, { code: number; message: string }>('/admin/ai-wear/prompt/save', { categoryId, promptTemplate })
}
