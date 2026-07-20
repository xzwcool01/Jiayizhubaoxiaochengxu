<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const orderId = ref(0)

onLoad((opt) => {
  orderId.value = Number(opt?.id || 0)
})

function goOrder() {
  uni.redirectTo({ url: '/pages/order/list' })
}

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<template>
  <view class="page">
    <view class="success-icon">✓</view>
    <text class="success-title">支付成功</text>
    <text class="success-desc">订单已提交，我们将尽快为您发货</text>
    <view class="actions">
      <view class="action-btn outline" @tap="goOrder">查看订单</view>
      <view class="action-btn primary" @tap="goHome">返回首页</view>
    </view>
  </view>
</template>

<style scoped>
.page { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; background: #FAFAF8; padding: 0 48rpx; }
.success-icon { width: 140rpx; height: 140rpx; border-radius: 50%; background: #775836; color: #fff; font-size: 72rpx; display: flex; align-items: center; justify-content: center; margin-bottom: 32rpx; }
.success-title { font-size: 40rpx; font-weight: 600; color: #1C1B1B; margin-bottom: 16rpx; }
.success-desc { font-size: 28rpx; color: #605E5A; margin-bottom: 64rpx; }
.actions { display: flex; gap: 24rpx; }
.action-btn { padding: 22rpx 56rpx; border-radius: 60rpx; font-size: 28rpx; font-weight: bold; }
.action-btn.outline { border: 2rpx solid #775836; color: #775836; }
.action-btn.primary { background: #775836; color: #fff; }
</style>