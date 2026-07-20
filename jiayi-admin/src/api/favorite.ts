import request from '@/utils/axios'

export interface AdminFavoriteVO {
  id: number
  userId: number
  userName: string
  productId: number
  productName: string
  createTime: string
}

export function getFavoriteList(params: {
  page: number
  size: number
  userId?: number
  productId?: number
  userName?: string
  productName?: string
}) {
  return request.get<any, { code: number; data: { records: AdminFavoriteVO[]; total: number } }>('/admin/favorite/list', { params })
}
