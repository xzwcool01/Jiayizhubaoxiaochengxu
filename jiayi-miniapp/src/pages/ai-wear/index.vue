<script setup lang="ts">
import { ref } from 'vue'
import MsIcon from '@/components/MsIcon.vue'

const uploaded = ref(false)
const stage = ref<'upload'|'results'>('upload')
const styles = ref(['典雅金白', '暗夜玫瑰', '海蓝之谜', '极简素银'])
const selectedStyle = ref(0)
const img = ref('https://lh3.googleusercontent.com/aida-public/AB6AXuCUGJgpn3SVNhHYf5HAsXpsgbPSYKPF7BQy5HSoMXk99w4I2htRsNesAm-hH84JKFG3dByMLPcx7FqT_k0qQY3M5IGdqTLw0yO5dN0-2LNFVNW1ftfXvgFnZPmP-xdd8Y6cK7QoJL8-Sw7SDlJkDZxMMU9W3F6xH3tSPSQpQkmMfgM4FIXLxY3ILvvHRJ9U9PAYLx9BqWe86tCRYQIXH9bJMLj3F7QxEtqqA71OIGg0dE9plTT-m5sBYg')

const handleUpload = () => { uploaded.value = true; stage.value = 'results' }
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <text class="top-title">AI 智能试戴</text>
    </view>
    <view v-if="stage === 'upload'" class="upload-area">
      <view class="upload-box">
        <view class="upload-icon"><MsIcon name="add_a_photo" size="56rpx" color="#775836" /></view>
        <text class="upload-title">上传照片</text>
        <text class="upload-desc">选择一张正面半身照，我们为您智能匹配珠宝</text>
        <view class="sample-row">
          <image class="sample" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBPSL0RkVvENxpQ-W8utqLk9UX8CHewDoJC7uv0D8nRPmi4NoV2tcnw1Wys_L9l-PD2cO8yRwjQO6HE0fsw3XyFqUx37GMF-U2R3Lc1kmWnBC3dLfMcpPRqClYVCNZR5gSNE60LA7_nT6jWFmENZMKPzJCMmwdvL76vMOGSVyj0Y_A37tPM6n45KQR3rj5_jZqDG0eBGTEi-YweA-x9w9U4Z5jq0_0UV6Jz3hZxn8SfOSKB6d6yVF7w" mode="aspectFill" />
          <image class="sample" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBZqQ16K6UJWGABk5nTK4N5EupbLENBms3v5my3UZHi7nCkWYDSSQYyt5s-O-XS0mnvGGCCFzlih6eQoOjANMW0hGjmYZQJLVFt9OKBWRKbb3-mBOa7YltPRI4YMr53CQDmy2bRB39TKBV77nZm6MvX9EJCfCHqjylmdH3sc5PJvAsohqA0XaJbCUU3fHG0PdFWynFoG_o31StZQsHK6cA1wxWgAOxm3pt_FAXBw7TH5ysF2tYmIh6hRw" mode="aspectFill" />
          <image class="sample" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBmh_ZGjLsCXdAJO_G5vSnIZqR5eOdV43MVfDVngq30x_XH7VQjHM8CxfrT7BmTWfpYb_dqWF_iCcv2IZI5YWrZKC8ZGGqQr-BmzQQG5PozEN-H1TdF6iFnFvT5DGfM5jqGmjq5rG4dJ2WSlwlTqJ7Lp5l5btBNOsBSZt8pq0H93PCiIiMxLfY9dBPzA2OrFmMwnq-3hFLFwhUpKRzayHQdEjAe6SlsDQ_WoXF08zV5s3-0zHQy7Tp5QQ" mode="aspectFill" />
        </view>
        <view class="upload-btn" @tap="handleUpload"><text>上传照片开始试戴</text></view>
      </view>
      <view class="how-card">
        <view class="how-item"><view class="step-dot">1</view><text class="how-text">上传清晰正面照</text></view>
        <view class="arrow"><MsIcon name="arrow_forward" size="28rpx" color="#D2C4B8" /></view>
        <view class="how-item"><view class="step-dot">2</view><text class="how-text">选择喜好风格</text></view>
        <view class="arrow"><MsIcon name="arrow_forward" size="28rpx" color="#D2C4B8" /></view>
        <view class="how-item"><view class="step-dot">3</view><text class="how-text">即刻预览效果</text></view>
      </view>
    </view>
    <view v-else class="result-area">
      <view class="photo-box">
        <image class="result-img" :src="img" mode="aspectFill" />
        <view class="overlay-item top">朝露玫瑰金戒指 ¥3,280</view>
        <view class="overlay-item bottom">月光泪珍珠耳坠 ¥2,150</view>
      </view>
      <view class="style-select">
        <text class="style-label">选择风格</text>
        <view class="style-pills">
          <view v-for="(s, i) in styles" :key="i" :class="['pill', i === selectedStyle ? 'pill-active' : 'pill-inactive']" @tap="selectedStyle = i"><text>{{ s }}</text></view>
        </view>
      </view>
      <view class="action-row">
        <view class="action-btn secondary"><text>重新上传</text></view>
        <view class="action-btn primary"><text>分享试戴效果</text></view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.upload-area { padding: 0 32rpx; }
.upload-box { background-color: #fff; border-radius: 32rpx; padding: 64rpx 48rpx; text-align: center; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.06); }
.upload-icon { width: 120rpx; height: 120rpx; border-radius: 50%; background-color: #f0ebe7; display: flex; align-items: center; justify-content: center; margin: 0 auto; }
.upload-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; display: block; margin-top: 24rpx; }
.upload-desc { font-size: 22rpx; color: #605E5A; display: block; margin-top: 12rpx; }
.sample-row { display: flex; justify-content: center; gap: 16rpx; margin-top: 32rpx; }
.sample { width: 136rpx; height: 164rpx; border-radius: 16rpx; }
.upload-btn { background-color: #775836; color: #fff; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; margin-top: 32rpx; }
.how-card { display: flex; align-items: center; background-color: #fff; border-radius: 24rpx; padding: 32rpx; margin-top: 32rpx; gap: 16rpx; }
.how-item { display: flex; align-items: center; gap: 12rpx; flex: 1; }
.step-dot { width: 40rpx; height: 40rpx; border-radius: 50%; background-color: #775836; color: #fff; font-size: 20rpx; display: flex; align-items: center; justify-content: center; }
.how-text { font-size: 22rpx; color: #4F453C; }
.result-area { padding: 0 32rpx; }
.photo-box { position: relative; border-radius: 32rpx; overflow: hidden; }
.result-img { width: 100%; height: 700rpx; }
.overlay-item { position: absolute; background: rgba(28,27,27,0.7); color: #fff; padding: 12rpx 24rpx; border-radius: 20rpx; font-size: 22rpx; }
.overlay-item.top { top: 32rpx; left: 32rpx; }
.overlay-item.bottom { bottom: 32rpx; right: 32rpx; }
.style-select { margin-top: 32rpx; }
.style-label { font-size: 28rpx; font-weight: 600; color: #1C1B1B; }
.style-pills { display: flex; gap: 16rpx; margin-top: 16rpx; }
.pill { padding: 12rpx 32rpx; border-radius: 60rpx; font-size: 24rpx; }
.pill-active { background-color: #775836; color: #fff; }
.pill-inactive { background-color: #E6E2DD; color: #605E5A; }
.action-row { display: flex; gap: 32rpx; margin-top: 48rpx; margin-bottom: 64rpx; }
.action-btn { flex: 1; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
.action-btn.primary { background-color: #775836; color: #fff; }
.action-btn.secondary { border: 2rpx solid #D9D2CC; color: #775836; }
</style>
