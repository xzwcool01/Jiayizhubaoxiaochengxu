import { get } from './request'

export interface UserCouponVO {
  id: number
  couponId: number
  name: string
  type: number
  value: number
  minAmount: number
  maxAmount: number
}

export function getMyCoupons() {
  const openid = uni.getStorageSync('token') || ''
  return get<UserCouponVO[]>('/coupon/my', { openid })
}

export function getMyApplicableCoupons(productIds: number[]) {
  const openid = uni.getStorageSync('token') || ''
  return get<UserCouponVO[]>('/coupon/my-applicable', { openid, productIds: productIds.join(',') })
}