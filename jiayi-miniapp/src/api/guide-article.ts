import { get } from './request'

export interface GuideArticleItem {
  id: number
  title: string
  summary: string
  coverImage: string
  content: string
  views: number
  author: string
  publishDate: string
  isHero: number
  status: number
  sortOrder: number
  createTime: string
}

export function getGuideArticles() {
  return get<GuideArticleItem[]>('/guide-article/list')
}

export function getGuideArticleDetail(id: number) {
  return get<GuideArticleItem>('/guide-article/detail', { id })
}
