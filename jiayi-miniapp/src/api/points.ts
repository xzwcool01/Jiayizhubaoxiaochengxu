import { get } from './request'

export interface PointsRuleVO {
  id: number
  points: number
  amount: number
  type: number
  productsAvailable: boolean
}

export function getPointsInfo(productIds?: number[]) {
  const openid = uni.getStorageSync('token') || ''
  const params: Record<string, any> = { openid }
  if (productIds && productIds.length) params.productIds = productIds.join(',')
  return get<{ points: number; rules: PointsRuleVO[] }>('/points/info', params)
}