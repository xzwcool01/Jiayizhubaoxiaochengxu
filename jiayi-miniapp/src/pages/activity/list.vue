<script setup lang="ts">
import MsIcon from '@/components/MsIcon.vue'
const activities = [
  { name: 'Summer Radiance', date: '2024.06.01 - 2024.08.31', status: '进行中', statusClass: 'status-active', desc: '夏日限定系列首发，凡购买指定款项即可获赠定制丝巾一份，并享受双倍会员积分。', btn: '立即参与', btnClass: 'btn-primary', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCNz7F9e-301IuMu34Sr_HftxGeIrNlJF2wJCZEoCQsXj_TcI8dQd750nSPFxdZpXVzTAvZ__L439ZgNPd3hhF8BUcqMXfdoK5V4Xr65QF0IL4Xo2F6FCvkE1b7AIincjZAHBlfkW4vlix3bec87a7Ku_o8iL6vNW0fGJs5Gvo8p7dMA4M0Hg85gWwD7mlm0kScGHXJsrJeuQ-toUTqnatZn8WkDwnDuDeLUMB8-EnpdTPjL1EsbGGk8g' },
  { name: 'Bridal Season', date: '2024.05.20 - 2024.06.30', status: '即将开始', statusClass: 'status-upcoming', desc: '定格真爱时刻。定制婚戒专场，由首席设计师为您亲手打造独一无二的永恒承诺。', btn: '预约到店', btnClass: 'btn-outline', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuB_2b42CjDwyuH4F0Gw9CLu7GYxmuJOugTiDAkznZQSmny5CkawPms-5RxFIVZg1bYcwbSvcrZ5qOcmty7YwZNVhusKht3pY-B07-mvepOGHzV1IBLcoiLm1c7ftQ7K3cjjJn8ibULhHjWGklA2IO-kfjCpbXZcV_XFbVa1mVBfZpdQaJWaL0bwy-juucJOf6YpE4EQcqXGYS_SUiysISC5Qk4onB2eGNUuJUWRyEXTMtRQquplYtJhMw' },
  { name: "Member's Night", date: '每周五 19:00 - 22:00', status: '长期活动', statusClass: 'status-active', desc: '嘉艺珠宝尊享会员之夜。到店享免费珠宝清洁服务，并可领取当季新款试戴礼券。', btn: '查看规则', btnClass: 'btn-secondary', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuADUXuqD2xAMyH9HJQEhfhXPXWwp2l6W3q1SSmLcDBJXDmSFF9ounivAt4zB4rkAWqxy-ZB90q55qNzfht7-APULp3s6Sgtf00axgKwtwLLhJfi9a3IbgCSElK7jaCMn9JBHDiN1uZdr1-Sgr-TPrFRREttsSpToAjAFP3sQTKUypzIftWDs2L6NV5-PRiinUSD7geLJDeeXTsC2IfilIJ_FFsL-yjTOSEftrbnO6caP5hBEJSmG1U5JA' }
]
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <text class="top-title">嘉怡珠宝</text>
    </view>
    <view class="hero" :style="'background-image: url(' + activities[0].img + ')'">
      <view class="hero-overlay" />
      <view class="hero-text">
        <text class="hero-tag">Seasonal Promo</text>
        <text class="hero-title">盛夏光芒系列专享</text>
        <text class="hero-desc">探索夏季限定珠宝，开启您的璀璨之旅</text>
      </view>
    </view>
    <view class="list">
      <view class="list-header">
        <text class="list-title">限时礼遇</text>
        <view class="list-divider" />
      </view>
      <view v-for="(a, i) in activities" :key="i" class="activity-card">
        <image class="activity-img" :src="a.img" mode="aspectFill" />
        <view class="activity-info">
          <view class="activity-top">
            <view><text class="activity-name">{{ a.name }}</text><text class="activity-date">{{ a.date }}</text></view>
            <text :class="['activity-status', a.statusClass]">{{ a.status }}</text>
          </view>
          <text class="activity-desc">{{ a.desc }}</text>
          <view :class="['activity-btn', a.btnClass]"><text>{{ a.btn }}</text></view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.back-btn { width: 80rpx; height: 80rpx; display: flex; align-items: center; justify-content: center; }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.hero { width: 100%; height: 400rpx; background-size: cover; background-position: center; position: relative; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(28,27,27,0.6), transparent); }
.hero-text { position: absolute; bottom: 32rpx; left: 32rpx; color: #fff; }
.hero-tag { font-size: 20rpx; background-color: #775836; padding: 8rpx 24rpx; border-radius: 40rpx; text-transform: uppercase; letter-spacing: 2px; display: inline-block; }
.hero-title { font-size: 56rpx; font-weight: 600; font-family: 'Noto Serif SC', serif; margin-top: 8rpx; }
.hero-desc { font-size: 24rpx; opacity: 0.9; margin-top: 4rpx; }
.list { padding: 48rpx 32rpx; }
.list-header { text-align: center; margin-bottom: 48rpx; }
.list-title { font-size: 40rpx; color: #775836; font-family: 'Noto Serif SC', serif; }
.list-divider { width: 96rpx; height: 4rpx; background-color: #C8A27A; margin: 12rpx auto 0; }
.activity-card { background-color: #fff; border-radius: 24rpx; overflow: hidden; margin-bottom: 48rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.activity-img { width: 100%; height: 340rpx; }
.activity-info { padding: 32rpx; }
.activity-top { display: flex; justify-content: space-between; align-items: flex-start; }
.activity-name { font-size: 32rpx; font-weight: 600; color: #1C1B1B; }
.activity-date { font-size: 20rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.activity-status { font-size: 22rpx; font-weight: bold; }
.status-active { color: #CF6679; }
.status-upcoming { color: #775836; }
.activity-desc { font-size: 24rpx; color: #4F453C; margin-top: 16rpx; line-height: 1.5; }
.activity-btn { width: 100%; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; margin-top: 24rpx; }
.btn-primary { background-color: #775836; color: #fff; }
.btn-outline { border: 2rpx solid #D9D2CC; color: #775836; }
.btn-secondary { background-color: #E6E2DD; color: #605E5A; }
</style>
