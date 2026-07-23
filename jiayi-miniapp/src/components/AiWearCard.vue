<script setup lang="ts">
import MsIcon from './MsIcon.vue'

const props = defineProps<{ productId?: number; categoryId?: number }>()
function goAiWear() {
  uni.navigateTo({ url: '/pages/ai-wear/index?productId=' + (props.productId || 0) + '&categoryId=' + (props.categoryId || 0) })
}
</script>

<template>
  <view class="ai-wrap" @tap="goAiWear">
    <view class="ai-card">
      <view class="ai-left">
        <view class="ai-icon">
          <MsIcon name="auto_awesome" size="36rpx" color="#fff" />
        </view>
        <view class="ai-text">
          <text class="ai-title">✨ AI穿戴试试</text>
          <text class="ai-desc">上传照片，即刻预览佩戴效果</text>
        </view>
      </view>
      <MsIcon name="chevron_right" size="36rpx" color="#605E5A" />
    </view>
  </view>
</template>

<style scoped>
.ai-wrap { position: relative; padding: 24rpx 0 0; }
.ai-card { display: flex; align-items: center; justify-content: space-between; width: 100%; padding: 28rpx; background: #fff; border: 2rpx solid #D9D2CC; border-radius: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); animation: pulse-glow 2s infinite; }
.ai-left { display: flex; align-items: center; gap: 20rpx; }
.ai-icon { width: 72rpx; height: 72rpx; border-radius: 50%; background: linear-gradient(135deg, #E8D5C0, #C8A27A); display: flex; align-items: center; justify-content: center; }
.ai-text { display: flex; flex-direction: column; }
.ai-title { font-size: 26rpx; font-weight: 600; color: #1C1B1B; }
.ai-desc { font-size: 22rpx; color: #605E5A; margin-top: 4rpx; }
@keyframes pulse-glow {
  0% { box-shadow: 0 0 0 0 rgba(232, 213, 192, 0.4); }
  70% { box-shadow: 0 0 0 12rpx rgba(232, 213, 192, 0); }
  100% { box-shadow: 0 0 0 0 rgba(232, 213, 192, 0); }
}
.ai-card { box-sizing: border-box; }
.ai-wrap { margin-bottom: 48rpx; }
</style>
