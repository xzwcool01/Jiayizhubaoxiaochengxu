import request from '@/utils/axios'

export interface AdminReviewVO {
  id: number
  orderId: number
  productId: number
  userId: number
  nickname: string
  productName: string
  rating: number
  content: string
  images: string[]
  isAnonymous: number
  isTop: number
  status: number
  totalAmount: number
  payAmount: number
  createTime: string
}

export function getReviewList(params: {
  page: number
  size: number
  productName?: string
  nickname?: string
  rating?: number
}) {
  return request.get<any, { code: number; data: { records: AdminReviewVO[]; total: number } }>('/admin/review/list', { params })
}

export function getReview(id: number) {
  return request.get<any, { code: number; data: AdminReviewVO }>(`/admin/review/${id}`)
}

export function updateReview(id: number, data: { rating?: number; content?: string; isAnonymous?: number; isTop?: number; status?: number }) {
  return request.put<any, { code: number }>(`/admin/review/${id}`, data)
}

export function deleteReview(id: number) {
  return request.delete<any, { code: number }>(`/admin/review/${id}`)
}
