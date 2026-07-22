<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getOrderList, cancelOrder, receiveOrder, type OrderVO } from '@/api/order'

const orders = ref<OrderVO[]>([])
const currentTab = ref(0)
const statusBarHeight = ref(0)

const tabs = ['全部', '待付款', '待发货', '待收货', '已完成', '已评价']
const tabStatus = [undefined, 0, 1, 2, 3]
const tabReviewed = [undefined, undefined, undefined, undefined, undefined, 1]

async function fetchData() {
  if (!uni.getStorageSync('token')) { uni.switchTab({ url: '/pages/my/my' }); return }
  const res = await getOrderList(tabStatus[currentTab.value], tabReviewed[currentTab.value])
  if (res.code === 200) orders.value = res.data || []
}

function switchTab(idx: number) {
  currentTab.value = idx
  fetchData()
}

async function handleCancel(id: number) {
  uni.showModal({ title: '提示', content: '确定取消该订单？', success: async (res) => {
    if (res.confirm) {
      await cancelOrder(id)
      fetchData()
    }
  }})
}

function goDetail(id: number) {
  uni.navigateTo({ url: '/pages/order/detail?id=' + id })
}

function goPay(id: number) {
  uni.navigateTo({ url: '/pages/order/detail?id=' + id })
}

function goDelivery(id: number) {
  uni.navigateTo({ url: '/pages/order/delivery?orderId=' + id })
}

function goReview(o: OrderVO) {
  if (!o.items?.length) return
  uni.navigateTo({ url: '/pages/order/review?orderId=' + o.id + '&productId=' + o.items[0].productId })
}

async function handleReceive(id: number) {
  uni.showModal({ title: '提示', content: '确认已收到商品？', success: async (res) => {
    if (res.confirm) {
      await receiveOrder(id)
      fetchData()
    }
  }})
}

function parseSpecs(raw: string | undefined | null): string {
  if (!raw) return ''
  try {
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr.map((s: any) => s.value || s).join(' / ') : ''
  } catch { return '' }
}

onShow(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 44
  fetchData()
})
</script>

<template>
  <view class="page" :style="'padding-top:' + (statusBarHeight + 6) + 'px'">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.switchTab({ url: '/pages/my/my' })"><text>←</text></view>
      <text class="page-title">我的订单</text>
    </view>

    <view class="tabs-wrap">
      <scroll-view class="tabs" scroll-x show-scrollbar="false">
        <view v-for="(t, i) in tabs" :key="i" :class="['tab', currentTab === i ? 'active' : '']" @tap="switchTab(i)">
          <text>{{ t }}</text>
        </view>
        <view style="width:32rpx;flex-shrink:0"></view>
      </scroll-view>
    </view>

    <scroll-view class="list" scroll-y>
      <view class="list-inner">
        <view v-if="orders.length === 0" class="empty"><text>暂无订单</text></view>
        <view v-for="o in orders" :key="o.id" class="order-card" @tap="goDetail(o.id)">
          <view class="order-header">
            <text class="order-sn">订单号: {{ o.orderSn }}</text>
            <text :class="['status', o.status === 0 ? 'pay' : '', o.status === 3 && o.reviewed === 1 ? 'reviewed' : '']">{{ o.status === 3 && o.reviewed === 1 ? '已评价' : (['待付款','待发货','待收货','已完成','已关闭'][o.status] || '未知') }}</text>
          </view>
          <view v-for="item in o.items" :key="item.id" class="order-item">
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
          <view class="order-footer">
            <text class="total">共{{ o.items.reduce((s, i) => s + i.quantity, 0) }}件 合计</text>
            <text class="pay-amount">¥{{ o.payAmount.toLocaleString() }}</text>
          </view>
          <view class="order-actions" v-if="o.status === 0">
            <view class="action-btn cancel" @tap.stop="handleCancel(o.id)">取消订单</view>
            <view class="action-btn pay" @tap.stop="goPay(o.id)">去支付</view>
          </view>
          <view class="order-actions" v-else-if="o.status === 2">
            <view class="action-btn cancel" @tap.stop="goDelivery(o.id)">查看物流</view>
            <view class="action-btn pay" @tap.stop="handleReceive(o.id)">确认收货</view>
          </view>
          <view class="order-actions" v-else-if="o.status === 3 && o.reviewed === 0">
            <view class="action-btn pay" @tap.stop="goReview(o)">去评价</view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; height: 80rpx; padding: 0 32rpx; }
.back-btn { width: 60rpx; }
.back-btn text { font-size: 36rpx; color: #775836; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.tabs-wrap { padding: 0 32rpx; }
.tabs { display: flex; white-space: nowrap; padding: 8rpx 0 20rpx; }
.tab { display: inline-block; padding: 12rpx 28rpx; font-size: 26rpx; color: #605E5A; margin-right: 16rpx; border-radius: 40rpx; background: #fff; flex-shrink: 0; }
.tab.active { color: #fff; background: #775836; }
.list { width: 100%; }
.list-inner { padding: 0 32rpx; }
.empty { text-align: center; padding: 200rpx 0; color: #999; font-size: 28rpx; }
.order-card { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 20rpx; overflow: hidden; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.order-sn { font-size: 22rpx; color: #999; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.status { font-size: 24rpx; color: #605E5A; padding: 4rpx 16rpx; border-radius: 8rpx; background: #f0f0f0; flex-shrink: 0; margin-left: 12rpx; }
.status.pay { color: #775836; background: #f6f4f2; }
.status.reviewed { color: #5a8a5a; background: #edf5ed; }
.order-item { display: flex; gap: 16rpx; padding: 12rpx 0; border-top: 2rpx solid #f5f5f5; }
.order-item:first-of-type { border-top: none; }
.item-img { width: 110rpx; height: 110rpx; border-radius: 12rpx; flex-shrink: 0; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: center; min-width: 0; }
.item-name { font-size: 26rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-spec { font-size: 22rpx; color: #999; margin-top: 4rpx; line-height: 1.4; word-break: break-all; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 6rpx; }
.item-price { font-size: 26rpx; color: #775836; font-weight: bold; }
.item-qty { font-size: 22rpx; color: #999; }
.order-footer { display: flex; justify-content: flex-end; align-items: center; gap: 8rpx; padding-top: 16rpx; margin-top: 8rpx; border-top: 2rpx solid #f5f5f5; }
.total { font-size: 24rpx; color: #605E5A; }
.pay-amount { font-size: 30rpx; color: #775836; font-weight: bold; }
.order-actions { display: flex; justify-content: flex-end; gap: 16rpx; margin-top: 16rpx; flex-shrink: 0; }
.action-btn { padding: 12rpx 32rpx; border-radius: 40rpx; font-size: 24rpx; }
.action-btn.cancel { border: 2rpx solid #D9D2CC; color: #605E5A; }
.action-btn.pay { background: #775836; color: #fff; }
</style>