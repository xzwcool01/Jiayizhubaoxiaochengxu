import request from '@/utils/axios'

export interface UmsUserFavorite {
  id: number
  userId: number
  productId: number
  createTime: string
}

export function getFavoriteList(params: {
  page: number
  size: number
  userId?: number
  productId?: number
}) {
  return request.get<any, { code: number; data: { records: UmsUserFavorite[]; total: number } }>('/admin/favorite/list', { params })
}
