import { get, post } from './request'

export interface AiWearRecord {
  id: number
  productId: number
  categoryId: number
  userPhoto: string
  resultUrl: string
  style?: string
  showOnDiscovery: number
  createTime: string
}

export interface AiWearQuota {
  remaining: number
  total: number
}

export interface AiWearGenerateResult {
  recordId: number
  resultUrl: string
  remaining: number
}

export function getAiWearQuota() {
  return get<AiWearQuota>('/ai-wear/quota', { openid: uni.getStorageSync('token') || '' })
}

export function generateAiWear(productId: number, filePath: string, categoryId?: number) {
  return new Promise<any>((resolve, reject) => {
    const openid = uni.getStorageSync('token') || ''
    const formData: Record<string, any> = { productId: String(productId), openid }
    if (categoryId != null) formData.categoryId = String(categoryId)
    uni.uploadFile({
      url: 'http://localhost:8080/api/ai-wear/generate',
      filePath,
      name: 'file',
      formData,
      success: (res) => {
        try {
          resolve(JSON.parse(res.data))
        } catch { reject(new Error('解析失败')) }
      },
      fail: reject
    })
  })
}

export function publishAiWear(recordId: number) {
  return post<{ resultUrl: string }>('/ai-wear/publish', {
    openid: uni.getStorageSync('token') || '',
    recordId
  })
}

export function getAiWearRecords(productId: number) {
  return get<AiWearRecord[]>('/ai-wear/list', { productId, openid: uni.getStorageSync('token') || '' })
}

export function getAiWearShowcase() {
  return get<AiWearRecord[]>('/ai-wear/showcase')
}
