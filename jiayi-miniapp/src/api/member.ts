import { get } from './request'

export interface MemberLevelVO {
  id: number
  name: string
  minPoints: number
  maxPoints: number
  perks: string[]
  icon?: string
  color?: string
  sort: number
}

export interface MemberOverviewVO {
  points: number
  currentLevel: MemberLevelVO
  nextLevel: MemberLevelVO
  allLevels: MemberLevelVO[]
  progress: number
}

export interface PointsLogVO {
  id: number
  actionName: string
  points: number
  createTime: string
}

export function getMemberOverview(openid: string) {
  return get<MemberOverviewVO>('/member/overview', { openid })
}

export function getMemberLevels() {
  return get<MemberLevelVO[]>('/member/levels')
}

export function getPointsLog(openid: string, page = 1, size = 20) {
  return get<{ records: PointsLogVO[]; total: number }>('/member/points/log', { openid, page, size })
}
