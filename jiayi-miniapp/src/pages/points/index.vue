<script setup lang="ts">
import MsIcon from '@/components/MsIcon.vue'
const points = 2380
const level = '黄金会员'
const tiers = [
  { name: '白银会员', min: '0', color: '#8C9399', progress: '30', perks: ['基础积分累积', '生日月双倍积分', '会员专享价'] },
  { name: '黄金会员', min: '500', color: '#C8A27A', progress: '60', perks: ['1.5倍积分累积', '积分兑换专属礼品', '新品优先预览', '免费包装服务'] },
  { name: '铂金会员', min: '2000', color: '#775836', progress: '25', perks: ['2倍积分累积', '设计师1对1咨询', '年度定制礼品', '优先寄送', '线下VIP活动邀请'] },
  { name: '钻石会员', min: '8000', color: '#CF6679', progress: '0', perks: ['3倍积分累积', '年度免邮额度', '首席设计师定制', '私人购物顾问', '全球展邀约', '生日惊喜礼盒'] }
]
const nextTier = '铂金会员'
const pointsNeeded = 2000 - points
const history = [
  { action: '新品上市区浏览', pts: '+5', date: '今天 14:32' },
  { action: '购买 月光泪珍珠耳坠', pts: '+215', date: '昨天' },
  { action: 'AI智能试戴分享', pts: '+10', date: '前天' },
  { action: '每日签到', pts: '+2', date: '2024.07.12' },
  { action: '资讯文章点赞', pts: '+1', date: '2024.07.10' },
  { action: '购买 朝露玫瑰金戒指', pts: '+328', date: '2024.07.08' }
]
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <text class="top-title">积分商城</text>
    </view>
    <view class="points-card">
      <text class="points-label">当前积分</text>
      <text class="points-value">{{ points }}</text>
      <view class="points-bar-bg"><view class="points-bar-fill" :style="'width: ' + (points/8000)*100 + '%'" /></view>
      <view class="next-tier">
        <text class="next-text">距 <text class="tier-name">{{ nextTier }}</text> 还差 {{ pointsNeeded }} 分</text>
      </view>
    </view>
    <view class="section">
      <view class="section-header"><text class="section-title">会员等级</text><text class="section-link">查看全部</text></view>
      <view class="tier-horiz">
        <view v-for="(t, i) in tiers" :key="i" class="tier-item">
          <view class="tier-dot" :style="'background-color: ' + t.color" />
          <text class="tier-name-small" :style="'color: ' + (points >= parseInt(t.min) ? t.color : '#605E5A')">{{ t.name }}</text>
        </view>
      </view>
      <view class="tier-detail">
        <text class="detail-name">{{ level }}权益</text>
        <view class="perk-list">
          <view v-for="(p, j) in tiers[1].perks" :key="j" class="perk-item"><MsIcon name="check" size="24rpx" color="#775836" /><text class="perk-text">{{ p }}</text></view>
        </view>
      </view>
    </view>
    <view class="section">
      <view class="section-header"><text class="section-title">积分动态</text><text class="section-link">全部</text></view>
      <view v-for="(h, i) in history" :key="i" class="history-item">
        <view class="history-left">
          <text class="history-action">{{ h.action }}</text>
          <text class="history-date">{{ h.date }}</text>
        </view>
        <text class="history-pts">{{ h.pts }}</text>
      </view>
    </view>
    <view class="cta-area">
      <view class="cta-btn"><text>去签到赚积分</text></view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
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
.section-link { font-size: 22rpx; color: #775836; }
.tier-horiz { display: flex; justify-content: space-between; margin-bottom: 32rpx; }
.tier-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.tier-dot { width: 16rpx; height: 16rpx; border-radius: 50%; }
.tier-name-small { font-size: 20rpx; }
.tier-detail { background-color: #fff; border-radius: 24rpx; padding: 32rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.detail-name { font-size: 28rpx; font-weight: 600; color: #1C1B1B; }
.perk-list { margin-top: 16rpx; }
.perk-item { display: flex; align-items: center; gap: 12rpx; margin-bottom: 12rpx; }
.perk-text { font-size: 24rpx; color: #4F453C; }
.history-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 0; border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.history-left { flex: 1; }
.history-action { font-size: 24rpx; color: #1C1B1B; }
.history-date { font-size: 18rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.history-pts { font-size: 24rpx; color: #775836; font-weight: bold; }
.cta-area { padding: 32rpx; }
.cta-btn { background-color: #775836; color: #fff; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
</style>
