import { get } from './request'

export interface ExpertPostItem {
  id: number
  content: string
  images: string[]
  expertTag: string
  expertLikes: number
  nickname: string
  avatar: string
  createTime: string
}

export function getExpertPosts() {
  return get<ExpertPostItem[]>('/expert-post/list')
}

export function getExpertPostDetail(id: number) {
  return get<ExpertPostItem>('/expert-post/detail', { id })
}
