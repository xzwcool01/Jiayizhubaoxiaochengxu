<script setup lang="ts">
import MsIcon from '@/components/MsIcon.vue'
const items = [
  { name: 'Eternal Rose Diamond', desc: '1.5ct Pear-Cut, 18K Rose Gold Base', current: '84,200', starting: '45,000', bids: '24', time: '02:14:05', tag: '热抢', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDJmU10uoJuxFJMr5ztSa69y3_bf7Pn-FMFQUeBzSQUrPHtxP53P1PGvHkWXxgVEjmwcwC4kFOCPGpyAQ2jvH1v7MgecJdWOzVQPsFukD6luHeOFby-YJFkyhv6bwL4VDhnpYjdTaW67sPZYrFxhEqxPuPuNGl0B_x2o3NpgUaa1fNYQTbccNVEpXwfq5OAaA5L47U63Yg9-KD62YOn_xDOj8XbBVUZwnqE50DIU8onn-YwHuYAyO397Q' },
  { name: 'Royal Jadeite Pendant', desc: 'Natural Type A Jadeite with Diamond Halo', current: '32,500', starting: '20,000', bids: '12', time: '05:45:12', tag: '', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAgeWsGs4KCoTB88GIkIG_b1WgDZBGqIOQyW_BBunuVwwAPyrRLiKNlxHBAAWF1wEYLBNFYL1kpal-BxHJD7lUNh1FSGt37SQdT7x3_pMb8yPgWw_NjiVSDp0smWxmOY_5pQK7euHm7xpjQ10WIVezfYtfiPoVf182Fsjbf6Lh8Dej7YBj0afztIwMs6z3nNVmUxqu2kmGwvbr9R_Yan5ZC7poOcKcjN8lO42hxtmBS9XsrXvh8Wd-VjQ' },
  { name: 'Midnight Sapphire Drop', desc: 'Deep Sea Sapphires & Platinum', current: '18,900', starting: '12,000', bids: '8', time: '01:02:30', tag: '', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCUDSBHBXmi96GZSGN02mlVMYD8YdLxIEhkEpXIqlerttMQ_l4vuA0QOpqFsgQokg8T_iPqDFR58sciRl-mVrnIjx5oAv9ml7e47Mh6KRnzZmpFOG01Yj5lI9LK_gPtaCupL5_-FM0f0nRxEkdldxsVnu9sEmWL6lzmrLIxrbwMv0I-LElp67nm0FjgZyNeEAg9YUqRnZY98NfBnrxbelkPIpnkZkR8RCd0KNU7Jrpr3K0uoXriiT-Y-g' }
]
const stats = { count: '1.2k', label: '位收藏家参与竞拍' }
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <text class="top-title">嘉怡珠宝</text>
    </view>
    <view class="rules-link"><MsIcon name="gavel" size="28rpx" color="#605E5A" /><text class="rules-text">拍卖规则</text></view>
    <view class="hero-banner">
      <view class="hero-inner">
        <view class="hero-info">
          <text class="hero-title">限时特场</text>
          <text class="hero-sub">限时拍卖特场</text>
        </view>
        <view class="hero-status">
          <text class="hero-status-label">进行中</text>
          <text class="hero-timer">04:12:45</text>
        </view>
      </view>
    </view>
    <view class="list">
      <view v-for="(item, i) in items" :key="i" class="auction-card">
        <view class="card-img-wrap">
          <image class="card-img" :src="item.img" mode="aspectFill" />
          <view v-if="item.tag" class="card-tag"><text class="tag-dot" /><text>{{ item.tag }}</text></view>
          <view class="card-bid-info">
            <view><text class="bid-label">当前出价</text><text class="bid-price">¥{{ item.current }}</text></view>
            <view><text class="bid-label">距结束</text><text class="bid-time">{{ item.time }}</text></view>
          </view>
        </view>
        <view class="card-info">
          <text class="card-name">{{ item.name }}</text>
          <text class="card-desc">{{ item.desc }}</text>
          <view class="card-details">
            <view><text class="detail-label">起拍价</text><text class="detail-value">¥{{ item.starting }}</text></view>
            <view><text class="detail-label">出价次数</text><text class="detail-value">{{ item.bids }}</text></view>
          </view>
          <view class="bid-btn"><text>立即出价</text></view>
        </view>
      </view>
      <view class="stat-card">
        <view><text class="stat-title">拍卖动态</text><text class="stat-desc">已有 {{ stats.count }} {{ stats.label }}</text></view>
        <MsIcon name="trending_up" size="64rpx" color="#C8A27A" />
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.rules-link { text-align: center; padding: 24rpx 0 16rpx; }
.rules-text { font-size: 22rpx; color: #605E5A; border-bottom: 2rpx solid #D9D2CC; }
.hero-banner { padding: 0 32rpx 48rpx; }
.hero-inner { background-color: #313030; border-radius: 24rpx; padding: 48rpx 32rpx; display: flex; justify-content: space-between; align-items: center; }
.hero-title { font-size: 40rpx; color: #E8BF95; font-family: 'Noto Serif SC', serif; }
.hero-sub { font-size: 22rpx; color: #CAC6C1; }
.hero-status-label { font-size: 22rpx; color: #E9C349; text-transform: uppercase; letter-spacing: 2px; }
.hero-timer { font-size: 36rpx; color: #fff; font-weight: 600; }
.list { padding: 0 32rpx 96rpx; display: flex; flex-direction: column; gap: 48rpx; }
.auction-card { background-color: #fff; border-radius: 24rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.card-img-wrap { position: relative; height: 500rpx; }
.card-img { width: 100%; height: 100%; }
.card-tag { position: absolute; top: 32rpx; left: 32rpx; background-color: rgba(207,102,121,0.9); color: #fff; padding: 8rpx 24rpx; border-radius: 20rpx; font-size: 20rpx; display: flex; align-items: center; gap: 8rpx; }
.tag-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background-color: #fff; }
.card-bid-info { position: absolute; bottom: 0; left: 0; width: 100%; padding: 32rpx; background: linear-gradient(to top, rgba(0,0,0,0.7), transparent); display: flex; justify-content: space-between; align-items: flex-end; }
.bid-label { font-size: 22rpx; color: rgba(255,255,255,0.7); }
.bid-price { font-size: 48rpx; color: #E9C349; font-weight: 700; }
.bid-time { font-size: 28rpx; color: #fff; font-weight: bold; }
.card-info { padding: 32rpx; }
.card-name { font-size: 32rpx; font-weight: 600; color: #1C1B1B; }
.card-desc { font-size: 22rpx; color: #4F453C; display: block; margin-top: 8rpx; }
.card-details { display: flex; gap: 48rpx; margin: 24rpx 0; padding: 16rpx 0; border-top: 2rpx solid rgba(217,210,204,0.3); border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.detail-label { font-size: 22rpx; color: #605E5A; }
.detail-value { font-size: 28rpx; color: #1C1B1B; font-weight: 600; }
.bid-btn { background-color: #775836; color: #fff; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
.stat-card { background-color: #F6F3F2; border-radius: 24rpx; padding: 32rpx; display: flex; justify-content: space-between; align-items: center; }
.stat-title { font-size: 32rpx; font-weight: 600; color: #1C1B1B; }
.stat-desc { font-size: 22rpx; color: #605E5A; }

</style>
