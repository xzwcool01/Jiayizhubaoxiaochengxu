<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, cancelOrder, payOrder, type OrderVO } from '@/api/order'

const order = ref<OrderVO | null>(null)
const orderId = ref(0)

onLoad(async (opt) => {
  orderId.value = Number(opt?.id || 0)
  if (orderId.value) await loadData()
})

async function loadData() {
  const res = await getOrderDetail(orderId.value)
  if (res.code === 200) order.value = res.data
}

async function handleCancel() {
  uni.showModal({ title: '提示', content: '确定取消该订单？', success: async (res) => {
    if (res.confirm) { await cancelOrder(orderId.value); uni.showToast({ title: '已取消', icon: 'success' }); loadData() }
  }})
}

async function handlePay() {
  await payOrder(orderId.value)
  uni.redirectTo({ url: '/pages/order/success?id=' + orderId.value })
}

function getStatusText(s: number) {
  return ['待付款', '待发货', '待收货', '已完成', '已关闭'][s] || '未知'
}

function parseAddress(raw?: string): string {
  if (!raw || raw === '{}') return ''
  try {
    const a = JSON.parse(raw)
    if (!a || !a.name) return raw
    return [a.name, a.phone, a.province, a.city, a.district, a.detail].filter(Boolean).join(' ')
  } catch {
    return raw
  }
}

function parseSpecs(raw?: string): string {
  if (!raw) return ''
  try {
    const arr = JSON.parse(raw)
    if (!Array.isArray(arr)) return raw
    return arr.map((s: any) => s.value || s).join(' / ')
  } catch {
    return raw
  }
}
</script>

<template>
  <view class="page" v-if="order">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text class="back-icon">←</text></view>
      <text class="page-title">订单详情</text>
    </view>

    <scroll-view class="content" scroll-y>
      <view class="status-banner" :class="'status-' + order.status">
        <text class="status-text">{{ getStatusText(order.status) }}</text>
        <text class="status-desc" v-if="order.status === 0">等待付款</text>
        <text class="status-desc" v-else-if="order.status === 1">等待卖家发货</text>
        <text class="status-desc" v-else-if="order.status === 2">运输中</text>
        <text class="status-desc" v-else-if="order.status === 3">已完成</text>
      </view>

      <view class="section address-section" v-if="order.addressSnapshot">
        <text class="section-title">收货信息</text>
        <text class="address-text">{{ parseAddress(order.addressSnapshot) }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ order.items.length }}件商品</text>
        <view v-for="item in order.items" :key="item.id" class="item">
          <image class="item-img" :src="item.productImage || ''" mode="aspectFill" />
          <view class="item-info">
            <text class="item-name">{{ item.productName }}</text>
            <text class="item-spec">{{ parseSpecs(item.productSpecs) }}</text>
            <view class="item-bottom">
              <text class="item-price">¥{{ item.price.toLocaleString() }}</text>
              <text class="item-qty">x{{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section info-section">
        <view class="info-row"><text>订单编号</text><text>{{ order.orderSn }}</text></view>
        <view class="info-row"><text>下单时间</text><text>{{ order.createTime }}</text></view>
        <view class="info-row" v-if="order.paidAt"><text>支付时间</text><text>{{ order.paidAt }}</text></view>
        <view class="info-row" v-if="order.note"><text>备注</text><text>{{ order.note }}</text></view>
      </view>

      <view class="section price-section">
        <view class="price-row"><text>商品总额</text><text>¥{{ order.totalAmount.toLocaleString() }}</text></view>
        <view class="price-row" v-if="order.couponAmount > 0"><text>优惠券抵扣</text><text style="color:#775836">-¥{{ order.couponAmount.toLocaleString() }}</text></view>
        <view class="price-row" v-if="order.pointsAmount > 0"><text>积分抵扣</text><text style="color:#775836">-¥{{ order.pointsAmount.toLocaleString() }}</text></view>
        <view class="price-row total-row"><text>实付金额</text><text class="pay-price">¥{{ order.payAmount.toLocaleString() }}</text></view>
      </view>
    </scroll-view>

    <view class="bottom-bar" v-if="order.status === 0">
      <view class="action-btn cancel" @tap="handleCancel">取消订单</view>
      <view class="action-btn pay" @tap="handlePay">立即支付</view>
    </view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; }
.top-bar { display: flex; align-items: center; height: 80rpx; padding: calc(var(--status-bar-height) + 16rpx) 32rpx 0; flex-shrink: 0; }
.back-btn { width: 60rpx; }
.back-icon { font-size: 36rpx; color: #775836; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.content { flex: 1; padding: 28rpx 24rpx 32rpx; overflow-x: hidden; box-sizing: border-box; }
.status-banner { border-radius: 20rpx; padding: 32rpx; margin-bottom: 16rpx; margin-top: 20rpx; }
.status-banner.status-0 { background: #f6f4f2; }
.status-banner.status-1 { background: #f0f5ee; }
.status-banner.status-2 { background: #f0f5ee; }
.status-banner.status-3 { background: #f6f4f2; }
.status-text { font-size: 36rpx; font-weight: 600; color: #1C1B1B; display: block; }
.status-desc { font-size: 26rpx; color: #605E5A; margin-top: 8rpx; display: block; }
.section { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 16rpx; margin-left: 0; margin-right: 0; }
.section-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; margin-bottom: 16rpx; display: block; }
.address-text { font-size: 26rpx; color: #605E5A; line-height: 1.6; word-break: break-all; }
.item { display: flex; gap: 16rpx; margin-bottom: 12rpx; }
.item:last-child { margin-bottom: 0; }
.item-img { width: 110rpx; height: 110rpx; border-radius: 12rpx; flex-shrink: 0; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: center; min-width: 0; }
.item-name { font-size: 26rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-spec { font-size: 22rpx; color: #999; line-height: 1.5; word-break: break-all; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 6rpx; }
.item-price { font-size: 26rpx; color: #775836; font-weight: bold; }
.item-qty { font-size: 22rpx; color: #999; }
.info-row { display: flex; justify-content: space-between; font-size: 24rpx; color: #605E5A; padding: 8rpx 0; }
.info-row text { word-break: break-all; }
.price-row { display: flex; justify-content: space-between; font-size: 26rpx; color: #605E5A; margin-bottom: 10rpx; }
.price-row:last-child { margin-bottom: 0; }
.total-row { border-top: 2rpx solid #f0f0f0; padding-top: 12rpx; margin-top: 4rpx; }
.pay-price { font-size: 32rpx; color: #775836; font-weight: bold; }
.bottom-bar { position: sticky; bottom: 0; display: flex; justify-content: flex-end; gap: 16rpx; padding: 16rpx 24rpx; background: rgba(255,255,255,0.95); }
.action-btn { padding: 16rpx 40rpx; border-radius: 40rpx; font-size: 26rpx; }
.action-btn.cancel { border: 2rpx solid #D9D2CC; color: #605E5A; }
.action-btn.pay { background: #775836; color: #fff; }
</style>