import { get, post } from './request'

export interface ReviewVO {
  id: number
  orderId: number
  productId: number
  userId: number
  rating: number
  content: string
  images: string[]
  isAnonymous: number
  status: number
  createTime: string
  nickname: string
  avatar: string
  productName: string
}

function getOpenid(): string {
  return uni.getStorageSync('token') || ''
}

export function getReviewByOrder(orderId: number) {
  return get<ReviewVO>('/review/by-order', { orderId, openid: getOpenid() })
}

export function createReview(data: {
  orderId: number
  productId: number
  rating: number
  content: string
  images: string[]
  isAnonymous: number
}) {
  return post<ReviewVO>('/review/create', { ...data, openid: getOpenid() })
}

export function getTop2Reviews(productId: number) {
  return get<ReviewVO[]>(`/review/product/${productId}/top2`)
}

export function getReviewCount(productId: number) {
  return get<number>(`/review/product/${productId}/count`)
}

export function getProductReviews(productId: number, page: number = 1, size: number = 20) {
  return get<{ records: ReviewVO[]; total: number }>(`/review/product/${productId}`, { page, size })
}
