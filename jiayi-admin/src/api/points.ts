import request from '@/utils/axios'

export interface SmsPointsRule {
  id?: number
  points: number
  amount: number
  type: number
  status?: number
  createTime?: string
}

export function getPointsRules() {
  return request.get<any, { code: number; data: SmsPointsRule[] }>('/admin/points/rules')
}

export function savePointsRule(data: SmsPointsRule) {
  return request.post<any, { code: number }>('/admin/points/rule', data)
}

export function getPointsProductIds(ruleId: number) {
  return request.get<any, { code: number; data: number[] }>('/admin/points/products', { params: { ruleId } })
}

export function savePointsProducts(ruleId: number, productIds: number[]) {
  return request.post<any, { code: number }>('/admin/points/products', { ruleId, productIds })
}