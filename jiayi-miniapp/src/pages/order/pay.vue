<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, payOrder } from '@/api/order'

const orderId = ref(0)
const orderSn = ref('')
const payAmount = ref(0)
const paying = ref(false)

onLoad(async (opt) => {
  orderId.value = Number(opt?.id || 0)
  if (!orderId.value) return
  const res = await getOrderDetail(orderId.value)
  if (res.code === 200 && res.data) {
    orderSn.value = res.data.orderSn
    payAmount.value = res.data.payAmount
  }
})

async function handlePay() {
  paying.value = true
  try {
    const res = await payOrder(orderId.value)
    if (res.code === 200) {
      uni.redirectTo({ url: '/pages/order/success?id=' + orderId.value })
    } else {
      uni.showToast({ title: res.message || '支付失败', icon: 'none' })
    }
  } catch { uni.showToast({ title: '支付失败', icon: 'none' }) }
  finally { paying.value = false }
}
</script>

<template>
  <view class="page">
    <view class="pay-icon">
      <text class="pay-icon-text">支付</text>
    </view>

    <text class="amount-label">订单金额</text>
    <text class="amount-value">¥{{ payAmount.toFixed(2) }}</text>

    <view class="info-row">
      <text class="info-label">订单编号</text>
      <text class="info-value">{{ orderSn }}</text>
    </view>

    <view class="mock-btn" :class="{ loading: paying }" @tap="handlePay">
      <text>{{ paying ? '处理中...' : '模拟支付' }}</text>
    </view>

    <text class="hint">当前为测试环境，点击模拟支付完成订单</text>
  </view>
</template>

<style scoped>
.page { display: flex; flex-direction: column; align-items: center; min-height: 100vh; background: #FAFAF8; padding: 240rpx 48rpx 80rpx; }

.pay-icon { width: 140rpx; height: 140rpx; border-radius: 50%; background: #f6f4f2; display: flex; align-items: center; justify-content: center; margin-bottom: 32rpx; }
.pay-icon-text { font-size: 40rpx; color: #775836; font-weight: 600; }

.amount-label { font-size: 26rpx; color: #605E5A; margin-bottom: 8rpx; }
.amount-value { font-size: 60rpx; font-weight: bold; color: #775836; margin-bottom: 48rpx; }

.info-row { display: flex; align-items: center; gap: 16rpx; margin-bottom: 64rpx; }
.info-label { font-size: 26rpx; color: #605E5A; }
.info-value { font-size: 26rpx; color: #1C1B1B; }

.mock-btn { width: 100%; background: #775836; padding: 28rpx 0; border-radius: 60rpx; display: flex; align-items: center; justify-content: center; box-shadow: 0 4rpx 24rpx rgba(119,88,54,0.3); }
.mock-btn.loading { opacity: 0.8; }
.mock-btn text { color: #fff; font-size: 30rpx; font-weight: 600; }

.hint { font-size: 24rpx; color: #C0B8B0; margin-top: 24rpx; }
</style>
