<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getMemberOverview, getPointsLog, type MemberOverviewVO, type PointsLogVO } from '@/api/member'

const overview = ref<MemberOverviewVO | null>(null)
const history = ref<PointsLogVO[]>([])
const historyPage = ref(1)
const historyFinished = ref(false)

function getOpenid(): string {
  return uni.getStorageSync('token') || ''
}

onLoad(async () => {
  const openid = getOpenid()
  if (!openid) return
  const [ovRes, logRes] = await Promise.all([
    getMemberOverview(openid),
    getPointsLog(openid, 1, 50)
  ])
  if (ovRes.code === 200) overview.value = ovRes.data
  if (logRes.code === 200) {
    history.value = logRes.data.records || []
    if (history.value.length >= (logRes.data.total || 0)) historyFinished.value = true
  }
})

function loadMore() {
  if (historyFinished.value) return
  historyPage.value++
  const openid = getOpenid()
  if (!openid) return
  getPointsLog(openid, historyPage.value, 50).then(res => {
    if (res.code === 200) {
      const records = res.data.records || []
      history.value.push(...records)
      if (records.length < 50) historyFinished.value = true
    }
  })
}
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#775836" /></view>
      <text class="top-title">积分权益</text>
    </view>
    <view class="points-card">
      <text class="points-label">当前积分</text>
      <text class="points-value">{{ (overview?.points ?? 0).toLocaleString() }}</text>
      <view class="points-bar-bg"><view class="points-bar-fill" :style="'width: ' + (overview?.progress ?? 0) + '%'" /></view>
      <view class="next-tier">
        <text class="next-text">距 <text class="tier-name">{{ overview?.nextLevel?.name || '满级' }}</text> 还差 <text class="tier-name">{{ overview?.nextLevel ? (overview.nextLevel.minPoints - (overview?.points ?? 0)).toLocaleString() : 0 }}</text> 分</text>
      </view>
    </view>
    <view class="section">
      <view class="section-header"><text class="section-title">会员等级</text></view>
      <view class="tier-horiz">
        <view v-for="(t, i) in overview?.allLevels" :key="i" class="tier-item">
          <view class="tier-dot" :style="'background-color: ' + (t.color || '#775836')" />
          <text class="tier-name-small" :style="'color: ' + (overview && (overview.points ?? 0) >= t.minPoints ? (t.color || '#775836') : '#605E5A')">{{ t.name }}</text>
        </view>
      </view>
      <view class="tier-detail" v-if="overview?.currentLevel">
        <text class="detail-name">{{ overview.currentLevel.name }}权益</text>
        <view class="perk-list">
          <view v-for="(p, j) in overview.currentLevel.perks" :key="j" class="perk-item"><MsIcon name="check" size="24rpx" color="#775836" /><text class="perk-text">{{ p }}</text></view>
        </view>
      </view>
    </view>
    <view class="section">
      <view class="section-header"><text class="section-title">积分动态</text></view>
      <view v-if="!history.length" class="empty-log"><text class="empty-log-text">暂无积分记录</text></view>
      <view v-for="(h, i) in history" :key="i" class="history-item">
        <view class="history-left">
          <text class="history-action">{{ h.actionName }}</text>
          <text class="history-date">{{ h.createTime?.substring(0, 16) }}</text>
        </view>
        <text :class="['history-pts', h.points > 0 ? 'positive' : 'negative']">{{ h.points > 0 ? '+' : '' }}{{ h.points }}</text>
      </view>
      <view v-if="!historyFinished" class="load-more" @tap="loadMore"><text>加载更多</text></view>
    </view>
    <view class="cta-area">
      <view class="cta-btn" @tap="uni.switchTab({ url: '/pages/my/my' })"><text>去签到赚积分</text></view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 48rpx 32rpx 16rpx; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 40rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.points-card { background: linear-gradient(135deg, #775836, #5a3f28); margin: 0 32rpx; border-radius: 32rpx; padding: 48rpx; color: #fff; }
.points-label { font-size: 22rpx; opacity: 0.8; }
.points-value { font-size: 96rpx; font-weight: 700; }
.points-bar-bg { height: 12rpx; background-color: rgba(255,255,255,0.2); border-radius: 12rpx; margin-top: 16rpx; }
.points-bar-fill { height: 100%; background-color: #E9C349; border-radius: 12rpx; }
.next-tier { margin-top: 12rpx; font-size: 22rpx; opacity: 0.9; }
.tier-name { font-weight: bold; }
.section { padding: 48rpx 32rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.section-title { font-size: 40rpx; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.tier-horiz { display: flex; justify-content: space-between; margin-bottom: 32rpx; }
.tier-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.tier-dot { width: 16rpx; height: 16rpx; border-radius: 50%; }
.tier-name-small { font-size: 20rpx; }
.tier-detail { background-color: #fff; border-radius: 24rpx; padding: 32rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.detail-name { font-size: 28rpx; font-weight: 600; color: #1C1B1B; }
.perk-list { margin-top: 16rpx; }
.perk-item { display: flex; align-items: center; gap: 12rpx; margin-bottom: 12rpx; }
.perk-text { font-size: 24rpx; color: #4F453C; }
.empty-log { padding: 48rpx 0; text-align: center; }
.empty-log-text { font-size: 24rpx; color: #999; }
.history-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 0; border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.history-left { flex: 1; }
.history-action { font-size: 24rpx; color: #1C1B1B; }
.history-date { font-size: 18rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.history-pts { font-size: 24rpx; font-weight: bold; }
.history-pts.positive { color: #775836; }
.history-pts.negative { color: #CF6679; }
.load-more { text-align: center; padding: 24rpx; color: #775836; font-size: 24rpx; }
.cta-area { padding: 32rpx; }
.cta-btn { background-color: #775836; color: #fff; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
</style>
