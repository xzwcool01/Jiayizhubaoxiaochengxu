import request from '@/utils/axios'

export interface AdminCartVO {
  id: number
  userId: number
  userName: string
  productId: number
  productName: string
  quantity: number
  selected: boolean
  createTime: string
  updateTime: string
}

export function getCartList(params: {
  page: number
  size: number
  userId?: number
  productId?: number
  userName?: string
  productName?: string
}) {
  return request.get<any, { code: number; data: { records: AdminCartVO[]; total: number } }>('/admin/cart/list', { params })
}