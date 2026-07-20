import { get, post } from './request'
import type { PmsProduct } from './product'

export interface FavoriteStatus {
  code: number
  data: boolean
}

export function toggleFavorite(productId: number) {
  const openid = uni.getStorageSync('token') || ''
  return post<boolean>('/favorite/toggle', { openid, productId })
}

export function getFavoriteStatus(productId: number) {
  const openid = uni.getStorageSync('token') || ''
  return get<boolean>('/favorite/status', { openid, productId })
}

export function getFavoriteList() {
  const openid = uni.getStorageSync('token') || ''
  return get<PmsProduct[]>('/favorite/list', { openid })
}

export function removeFavorite(productId: number) {
  const openid = uni.getStorageSync('token') || ''
  return post<boolean>('/favorite/remove', { openid, productId })
}
