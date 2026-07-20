<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import NavBar from '@/components/NavBar.vue'
import { getCartList, updateQuantity, updateSelected, toggleSelectAll, removeCartItem, type CartItemVO } from '@/api/cart'
import { getProductList } from '@/api/product'
import type { PmsProduct } from '@/api/product'

const items = ref<CartItemVO[]>([])
const loading = ref(false)
const recommends = ref<PmsProduct[]>([])

async function fetchData() {
  if (!uni.getStorageSync('token')) { items.value = []; return }
  loading.value = true
  try {
    const res = await getCartList()
    if (res.code === 200) items.value = res.data || []
  } catch { uni.showToast({ title: '加载失败', icon: 'none' }) }
  finally { loading.value = false }
  fetchRecommend()
}

async function fetchRecommend() {
  try {
    let categoryId: number | undefined
    for (const item of items.value) {
      if (item.categoryId) { categoryId = item.categoryId; break }
    }
    const res = await getProductList({ page: 1, size: 50, productType: 0 })
    if (res.code !== 200 || !res.data?.records) { recommends.value = []; return }
    const all = res.data.records.filter(p => p.status === 1)
    const same = categoryId ? all.filter(p => p.categoryId === categoryId) : []
    const other = categoryId ? all.filter(p => p.categoryId !== categoryId) : all
    const picked: PmsProduct[] = []
    if (same.length > 0) picked.push(same[0])
    for (const p of other) { if (picked.length >= 3) break; picked.push(p) }
    if (picked.length === 0 && all.length > 0) {
      for (const p of all) { if (picked.length >= 3) break; picked.push(p) }
    }
    recommends.value = picked
  } catch { recommends.value = [] }
}

const allSelected = computed({
  get: () => items.value.length > 0 && items.value.every(i => i.selected),
  set: async (val: boolean) => {
    items.value.forEach(i => i.selected = val)
    try { await toggleSelectAll(val) } catch {}
  }
})

const totalPrice = computed(() => {
  return items.value
    .filter(i => i.selected)
    .reduce((sum, i) => sum + i.price * i.quantity, 0)
})

const selectedCount = computed(() => items.value.filter(i => i.selected).length)

async function handleQty(item: CartItemVO, delta: number) {
  const prevQty = item.quantity
  const newQty = Math.max(0, prevQty + delta)
  item.quantity = newQty
  if (newQty === 0 && item.selected) {
    item.selected = false
    try { await Promise.all([updateQuantity(item.id, 0), updateSelected(item.id, false)]) } catch {}
  } else if (prevQty === 0 && newQty === 1 && !item.selected) {
    item.selected = true
    try { await Promise.all([updateQuantity(item.id, 1), updateSelected(item.id, true)]) } catch {}
  } else {
    try { await updateQuantity(item.id, newQty) } catch {}
  }
}

async function handleSelected(item: CartItemVO) {
  item.selected = !item.selected
  try { await updateSelected(item.id, item.selected) } catch {}
}

async function handleRemove(item: CartItemVO) {
  try {
    await removeCartItem(item.id)
    items.value = items.value.filter(i => i.id !== item.id)
    uni.showToast({ title: '已移除', icon: 'success' })
  } catch { uni.showToast({ title: '操作失败', icon: 'none' }) }
}

function handleCheckout() {
  if (!selectedCount.value) { uni.showToast({ title: '请选择商品', icon: 'none' }); return }
  uni.navigateTo({ url: '/pages/order/confirm' })
}

function parseSpecs(raw: string | undefined | null): string {
  if (!raw) return ''
  try {
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr.map((s: any) => s.value || s).join(' / ') : ''
  } catch { return '' }
}

onShow(fetchData)
</script>

<template>
  <view class="page">
    <view class="top-bar"><text class="top-title">购物车</text></view>
    <scroll-view class="list" scroll-y>
      <view v-for="item in items" :key="item.id" class="cart-item">
        <view class="item-checkbox" :class="{ checked: item.selected }" @tap="handleSelected(item)" />
        <image class="item-img" :src="item.mainImage || ''" mode="aspectFill" @tap="uni.navigateTo({ url: '/pages/product/detail?id=' + item.productId })" />
        <view class="item-info">
          <text class="item-name" @tap="uni.navigateTo({ url: '/pages/product/detail?id=' + item.productId })">{{ item.name }}</text>
          <text class="item-spec">{{ parseSpecs(item.specs) }}</text>
          <view class="item-bottom">
            <text class="item-price">¥{{ (item.price * item.quantity).toLocaleString() }}</text>
            <view class="item-qty">
              <text class="qty-btn" @tap="handleQty(item, -1)">−</text>
              <text class="qty-num">{{ item.quantity }}</text>
              <text class="qty-btn" @tap="handleQty(item, 1)">+</text>
            </view>
          </view>
          <text class="item-remove" @tap="handleRemove(item)">删除</text>
        </view>
      </view>
      <view v-if="!items.length && !loading" class="empty-cart"><text class="empty-text">购物车是空的</text></view>

      <view class="recommend-section" v-if="recommends.length">
        <view class="rec-header">
          <text class="rec-title">猜你喜欢</text>
        </view>
        <scroll-view class="rec-scroll" scroll-x enhanced show-scrollbar="false">
          <view class="rec-inner">
            <view v-for="p in recommends" :key="p.id" class="rec-card" @tap="uni.navigateTo({ url: '/pages/product/detail?id=' + p.id })">
              <image class="rec-img" :src="p.mainImage || ''" mode="aspectFill" />
              <view class="rec-info">
                <text class="rec-name">{{ p.name }}</text>
                <view class="rec-price"><text style="font-size:22rpx">¥</text><text style="font-size:28rpx;font-weight:bold"> {{ Math.floor(p.price).toLocaleString() }}</text></view>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </scroll-view>

    <view class="bottom-bar">
      <view class="bottom-left">
        <view class="item-checkbox" :class="{ checked: allSelected }" @tap="allSelected = !allSelected" />
        <text class="all-label">全选</text>
      </view>
      <view class="bottom-right">
        <text class="total-label">合计: </text>
        <text class="total-price">¥{{ totalPrice.toLocaleString() }}</text>
        <view class="checkout-btn" @tap="handleCheckout"><text class="checkout-text">结算 ({{ selectedCount }})</text></view>
      </view>
    </view>
  </view>
  <NavBar :active="3" />
</template>

<style scoped>
.page { background-color: #FAFAF8; height: 100vh; width: 100%; display: flex; flex-direction: column; padding-bottom: 240rpx; box-sizing: border-box; overflow-x: hidden; }
.top-bar { display: flex; align-items: flex-end; justify-content: flex-start; height: 120rpx; padding: 0 32rpx 12rpx; background-color: rgba(255,255,255,0.8); flex-shrink: 0; box-sizing: border-box; }
.top-title { font-size: 40rpx; font-weight: 600; color: #1C1B1B; line-height: 1.2; }
.list { flex: 1; padding: 48rpx 32rpx; width: 100%; box-sizing: border-box; overflow-x: hidden; }
.cart-item { display: flex; align-items: flex-start; gap: 12rpx; background-color: #ffffff; border-radius: 24rpx; padding: 20rpx; margin-bottom: 16rpx; box-sizing: border-box; overflow: hidden; width: 100%; max-width: 100%; }
.item-checkbox { width: 32rpx; height: 32rpx; border-radius: 50%; border: 3rpx solid #D9D2CC; flex-shrink: 0; margin-top: 6rpx; }
.item-checkbox.checked { background-color: #775836; border-color: #775836; }
.item-img { width: 120rpx; height: 120rpx; border-radius: 14rpx; flex-shrink: 0; }
.item-info { flex: 1; display: flex; flex-direction: column; min-width: 0; max-width: 100%; position: relative; box-sizing: border-box; overflow: hidden; }
.item-name { font-size: 26rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 100%; }
.item-spec { font-size: 22rpx; color: #605E5A; margin-top: 4rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 100%; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 10rpx; max-width: 100%; overflow: hidden; }
.item-price { font-size: 26rpx; color: #775836; font-weight: bold; flex-shrink: 0; }
.item-qty { display: flex; align-items: center; border: 2rpx solid #D9D2CC; border-radius: 40rpx; flex-shrink: 0; }
.qty-btn { width: 44rpx; height: 44rpx; display: flex; align-items: center; justify-content: center; font-size: 26rpx; color: #605E5A; }
.qty-num { width: 48rpx; text-align: center; font-size: 24rpx; font-weight: bold; }
.item-remove { font-size: 22rpx; color: #c0836a; margin-top: 6rpx; text-align: right; overflow: hidden; }
.empty-cart { display: flex; align-items: center; justify-content: center; height: 200rpx; }
.empty-text { font-size: 28rpx; color: #999; }
.recommend-section { margin-top: 36rpx; }
.rec-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.rec-title { font-size: 34rpx; font-weight: 600; color: #1C1B1B; }
.rec-scroll { width: 100%; }
.rec-inner { display: flex; flex-direction: row; gap: 20rpx; padding-right: 8rpx; }
.rec-card { width: 280rpx; flex-shrink: 0; background-color: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.rec-img { width: 100%; height: 320rpx; }
.rec-info { padding: 20rpx; }
.rec-name { font-size: 28rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rec-price { font-size: 28rpx; color: #775836; font-weight: bold; margin-top: 8rpx; }
.bottom-bar { position: fixed; bottom: 160rpx; left: 32rpx; right: 32rpx; display: flex; align-items: center; justify-content: space-between; background-color: rgba(255,255,255,0.92); border-radius: 60rpx; padding: 14rpx 24rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.08); }
.bottom-left { display: flex; align-items: center; gap: 12rpx; }
.all-label { font-size: 24rpx; color: #1C1B1B; }
.bottom-right { display: flex; align-items: center; gap: 12rpx; }
.total-label { font-size: 24rpx; color: #1C1B1B; }
.total-price { font-size: 30rpx; color: #775836; font-weight: bold; }
.checkout-btn { background-color: #775836; padding: 18rpx 36rpx; border-radius: 60rpx; }
.checkout-text { color: #fff; font-size: 26rpx; font-weight: bold; }
</style>