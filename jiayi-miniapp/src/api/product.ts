import { get } from './request'

export interface PmsProduct {
  id: number
  categoryId: number
  categoryName?: string
  productType: number
  name: string
  subtitle?: string
  mainImage?: string
  images?: string
  description?: string
  price: number
  originalPrice?: number
  pointsPrice?: number
  stock?: number
  sales?: number
  status?: number
  descriptionText?: string
  specs?: string
}

export interface Category {
  id: number
  name: string
  sort: number
}

export interface PageRes<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export function getProductList(params?: {
  page?: number
  size?: number
  categoryId?: number
  productType?: number
}) {
  return get<PageRes<PmsProduct>>('/product/list', params)
}

export function getProduct(id: number) {
  return get<PmsProduct>('/product/' + id)
}

export function getCategories() {
  return get<Category[]>('/product/categories')
}

export function getSharePoster(id: number) {
  return get<string>('/share/poster/' + id)
}
