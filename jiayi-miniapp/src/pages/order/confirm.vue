<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getCartList, type CartItemVO } from '@/api/cart'
import { getMyCoupons, type UserCouponVO } from '@/api/coupon'
import { getPointsInfo } from '@/api/points'
import { createOrder } from '@/api/order'

const items = ref<CartItemVO[]>([])
const addressText = ref('请选择收货地址')
const addressId = ref<number>(0)
const showAddressPicker = ref(false)
const addressList = ref<any[]>([])
const coupons = ref<UserCouponVO[]>([])
const selectedCoupon = ref<UserCouponVO | null>(null)
const showCouponPicker = ref(false)
const pointsInfo = ref<{ points: number; rules: any[] }>({ points: 0, rules: [] })
const usePoints = ref(false)
const selectedRuleIdx = ref(0)
const note = ref('')
const submitting = ref(false)

const activeRule = computed(() => {
  if (!pointsInfo.value.rules.length) return null
  return pointsInfo.value.rules[selectedRuleIdx.value] || pointsInfo.value.rules[0]
})

const totalAmount = computed(() => items.value.reduce((s, i) => s + i.price * i.quantity, 0))
const couponAmount = computed(() => {
  if (!selectedCoupon.value) return 0
  const c = selectedCoupon.value
  if (c.type === 0) return Math.min(c.value, Math.max(0, totalAmount.value - c.minAmount))
  return Math.min(totalAmount.value * c.value / 100, c.maxAmount || Infinity)
})
const pointsAmount = computed(() => {
  if (!usePoints.value || !activeRule.value) return 0
  const r = activeRule.value
  if (r.type === 1) {
    const maxDeduct = Math.floor(pointsInfo.value.points / r.points) * r.amount
    return Math.min(maxDeduct, totalAmount.value - couponAmount.value)
  }
  return r.amount
})
const pointsDeduct = computed(() => {
  if (!usePoints.value || !activeRule.value) return 0
  const r = activeRule.value
  if (r.type === 1) return Math.floor(pointsInfo.value.points / r.points) * r.points
  return r.points
})
const payAmount = computed(() => Math.max(0, totalAmount.value - couponAmount.value - pointsAmount.value))
const totalDiscount = computed(() => couponAmount.value + pointsAmount.value)

const productIds = computed(() => items.value.map(i => i.productId))

async function loadData() {
  if (!uni.getStorageSync('token')) { uni.switchTab({ url: '/pages/my/my' }); return }
  const cartRes = await getCartList()
  if (cartRes.code === 200) items.value = (cartRes.data || []).filter(i => i.selected)
  if (!items.value.length) { uni.showToast({ title: '请选择商品', icon: 'none' }); uni.navigateBack(); return }

  const addrStr = uni.getStorageSync('selectedAddress')
  if (addrStr) {
    try {
      const addr = JSON.parse(addrStr)
      addressText.value = `${addr.name} ${addr.phone} ${addr.province}${addr.city}${addr.district}${addr.detail}`
      addressId.value = addr.id
    } catch {}
  }

  const [couponRes, pointsRes] = await Promise.all([
    getMyCoupons(),
    getPointsInfo(productIds.value)
  ])
  if (couponRes.code === 200) coupons.value = couponRes.data || []
  if (pointsRes.code === 200) pointsInfo.value = pointsRes.data || { points: 0, rules: [] }
}

function selectAddress() {
  uni.navigateTo({ url: '/pages/address/index' })
}

function selectCoupon(c: UserCouponVO | null) {
  selectedCoupon.value = c
  showCouponPicker.value = false
}

async function submitOrder() {
  if (!addressId.value) { uni.showToast({ title: '请选择收货地址', icon: 'none' }); return }
  submitting.value = true
  try {
    const res = await createOrder({
      addressId: addressId.value,
      couponId: selectedCoupon.value?.id,
      usePoints: usePoints.value,
      note: note.value,
      cartItemIds: items.value.map(i => i.id)
    })
    if (res.code === 200 && res.data) {
      uni.removeStorageSync('selectedAddress')
      uni.redirectTo({ url: '/pages/order/success?id=' + res.data.id })
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch { uni.showToast({ title: '提交失败', icon: 'none' }) }
  finally { submitting.value = false }
}

function parseSpecs(raw: string | undefined | null): string {
  if (!raw) return ''
  try {
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr.map((s: any) => s.value || s).join(' / ') : ''
  } catch { return '' }
}

const couponLabel = computed(() => {
  if (!selectedCoupon.value) return coupons.value.length ? '请选择' : '暂无可用'
  const c = selectedCoupon.value
  return c.type === 0 ? `-¥${couponAmount.value.toFixed(2)}` : `-${c.value}%`
})

onShow(loadData)
</script>

<template>
  <view class="page">
    <!-- Top Header -->
    <view class="top-header">
      <text class="top-title">确认订单</text>
    </view>

    <scroll-view class="content" scroll-y>
      <!-- Shipping Address -->
      <view class="card address-card" @tap="selectAddress">
        <view class="card-left">
          <MsIcon name="location_on" size="48rpx" color="#775836" />
        </view>
        <view class="card-body">
          <view class="address-name-row">
            <text class="address-name">收货地址</text>
          </view>
          <text class="address-text">{{ addressText }}</text>
        </view>
        <MsIcon name="chevron_right" size="48rpx" color="#81756a" />
      </view>

      <!-- Order Items -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">商品清单</text>
        </view>
        <view v-for="(item, idx) in items" :key="item.id" class="order-item" :class="{ last: idx === items.length - 1 }">
          <image class="item-img" :src="item.mainImage || ''" mode="aspectFill" />
          <view class="item-info">
            <text class="item-name">{{ item.name }}</text>
            <text class="item-spec">{{ parseSpecs(item.specs) || '默认规格' }}</text>
            <view class="item-bottom">
              <text class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</text>
              <text class="item-qty">x {{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Discount Section -->
      <view class="card discount-card">
        <!-- Coupon -->
        <view class="discount-row" @tap="coupons.length ? showCouponPicker = true : undefined">
          <view class="discount-left">
            <MsIcon name="confirmation_number" size="48rpx" color="#775836" />
            <text class="discount-label">优惠券</text>
          </view>
          <view class="discount-right">
            <text class="discount-value" :class="{ active: selectedCoupon }">{{ couponLabel }}</text>
            <MsIcon name="chevron_right" size="48rpx" color="#81756a" />
          </view>
        </view>
        <!-- Points Toggle -->
        <view class="discount-row no-border" v-if="activeRule">
          <view class="discount-left">
            <MsIcon name="loyalty" size="48rpx" color="#775836" />
            <view class="points-text">
              <text class="discount-label">积分抵扣</text>
              <text class="points-hint" v-if="!usePoints">
                {{ activeRule.type === 0 ? `${activeRule.points}积分抵¥${activeRule.amount}` : `${activeRule.points}积分=¥${activeRule.amount}` }}
                {{ pointsInfo.points >= activeRule.points ? '' : '（积分不足）' }}
              </text>
              <text class="points-hint active" v-else>
                使用 {{ pointsDeduct.toLocaleString() }} 积分抵扣 ¥{{ pointsAmount.toFixed(2) }}
              </text>
            </view>
          </view>
          <view v-if="pointsInfo.points >= activeRule.points" :class="['toggle', usePoints ? 'on' : '']" @tap="usePoints = !usePoints">
            <view class="toggle-knob" />
          </view>
        </view>
      </view>

      <!-- Order Summary -->
      <view class="card summary-card">
        <view class="summary-row">
          <text class="summary-label">商品总计</text>
          <text class="summary-value">¥{{ totalAmount.toFixed(2) }}</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">运费费率</text>
          <text class="summary-value">¥0.00 (尊享免邮)</text>
        </view>
        <view class="summary-row" v-if="totalDiscount > 0">
          <text class="summary-label">优惠抵扣</text>
          <text class="summary-value discount">- ¥{{ totalDiscount.toFixed(2) }}</text>
        </view>
      </view>

      <!-- Remarks -->
      <view class="card remark-card">
        <MsIcon name="edit_note" size="48rpx" color="#775836" />
        <input class="remark-input" v-model="note" placeholder="如有特殊要求，请在此备注" placeholder-style="color:#d2c4b8;font-size:28rpx" />
      </view>
    </scroll-view>

    <!-- Bottom Bar -->
    <view class="bottom-bar">
      <view class="bottom-left">
        <text class="pay-label">合计支付</text>
        <text class="pay-amount">¥{{ payAmount.toFixed(2) }}</text>
      </view>
      <view class="submit-btn" :class="{ loading: submitting }" @tap="submitOrder">
        <text>{{ submitting ? '支付中...' : '提交订单' }}</text>
      </view>
    </view>

    <!-- Coupon Picker Overlay -->
    <view v-if="showCouponPicker" class="overlay" @tap="showCouponPicker = false">
      <view class="picker-card" @tap.stop>
        <text class="picker-title">选择优惠券</text>
        <view v-for="c in coupons" :key="c.id" class="coupon-option" @tap="selectCoupon(c)">
          <view class="coupon-info">
            <text class="coupon-name">{{ c.name }}</text>
            <text class="coupon-type">{{ c.type === 0 ? '满减' : '折扣' }}</text>
          </view>
          <text :class="['check', selectedCoupon?.id === c.id ? 'checked' : '']">{{ selectedCoupon?.id === c.id ? '✓' : '' }}</text>
        </view>
        <view class="coupon-option" @tap="selectCoupon(null)"><text style="font-size:28rpx;color:#1c1b1b">不使用优惠券</text></view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; }

/* Top Header */
.top-header { position: fixed; top: 0; left: 0; right: 0; z-index: 50; background: rgba(252,249,248,0.8); backdrop-filter: blur(20px); height: 112rpx; display: flex; align-items: flex-end; justify-content: center; padding-bottom: 16rpx; }
.top-title { font-size: 44rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }

/* Content */
.content { flex: 1; padding: 128rpx 32rpx 200rpx; }

/* Card base */
.card { background: #ffffff; border-radius: 24rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 16rpx rgba(0,0,0,0.06); }

/* Address Card */
.address-card { display: flex; align-items: flex-start; gap: 24rpx; }
.card-left { flex-shrink: 0; margin-top: 4rpx; }
.card-body { flex: 1; min-width: 0; }
.address-name-row { display: flex; align-items: center; gap: 16rpx; margin-bottom: 8rpx; }
.address-name { font-size: 30rpx; font-weight: bold; color: #1c1b1b; }
.address-phone { font-size: 24rpx; color: #4f453c; }
.address-text { font-size: 28rpx; color: #4f453c; line-height: 1.6; }

/* Order Items */
.card-header { border-bottom: 2rpx solid #f6f3f2; padding-bottom: 24rpx; margin-bottom: 24rpx; }
.card-title { font-size: 34rpx; color: #775836; font-weight: 600; }
.order-item { display: flex; gap: 24rpx; padding: 24rpx 0; border-bottom: 2rpx solid #f6f3f2; }
.order-item.last { border-bottom: none; padding-bottom: 0; }
.item-img { width: 160rpx; height: 160rpx; border-radius: 16rpx; flex-shrink: 0; background: #f6f3f2; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.item-name { font-size: 30rpx; color: #1c1b1b; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-spec { font-size: 24rpx; color: #666460; margin-top: 8rpx; }
.item-bottom { display: flex; justify-content: space-between; align-items: baseline; margin-top: 12rpx; }
.item-price { font-size: 30rpx; color: #775836; font-weight: bold; }
.item-qty { font-size: 24rpx; color: #4f453c; }

/* Discount Card */
.discount-card { padding: 0; }
.discount-row { display: flex; align-items: center; justify-content: space-between; padding: 32rpx; border-bottom: 2rpx solid #f6f3f2; }
.discount-row.no-border { border-bottom: none; }
.discount-left { display: flex; align-items: center; gap: 24rpx; flex: 1; }
.discount-label { font-size: 30rpx; color: #1c1b1b; display: block; }
.discount-right { display: flex; align-items: center; gap: 8rpx; }
.discount-value { font-size: 28rpx; color: #4f453c; }
.discount-value.active { color: #CF6679; }
.points-text { flex: 1; }
.points-hint { font-size: 24rpx; color: #666460; margin-top: 4rpx; display: block; }
.points-hint.active { color: #775836; font-weight: 500; }

/* Toggle Switch */
.toggle { width: 88rpx; height: 48rpx; border-radius: 48rpx; background: #e5e2e1; position: relative; flex-shrink: 0; transition: background 0.2s; }
.toggle.on { background: #c8a27a; }
.toggle-knob { width: 40rpx; height: 40rpx; border-radius: 50%; background: #fff; position: absolute; top: 4rpx; left: 4rpx; transition: left 0.2s; box-shadow: 0 2rpx 4rpx rgba(0,0,0,0.15); }
.toggle.on .toggle-knob { left: 44rpx; }

/* Summary Card */
.summary-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.summary-row:last-child { margin-bottom: 0; }
.summary-label { font-size: 28rpx; color: #4f453c; }
.summary-value { font-size: 28rpx; color: #1c1b1b; }
.summary-value.discount { color: #CF6679; }

/* Remark Card */
.remark-card { display: flex; align-items: center; gap: 24rpx; }
.remark-input { flex: 1; font-size: 28rpx; color: #1c1b1b; background: transparent; border: none; min-height: 48rpx; }

/* Bottom Bar */
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #ffffff; border-top: 2rpx solid #f6f3f2; padding: 24rpx 32rpx; display: flex; align-items: center; justify-content: space-between; z-index: 50; }
.bottom-left { display: flex; flex-direction: column; }
.pay-label { font-size: 24rpx; color: #4f453c; }
.pay-amount { font-size: 44rpx; font-weight: bold; color: #775836; margin-top: 4rpx; }
.submit-btn { background: #775836; padding: 24rpx 64rpx; border-radius: 40rpx; box-shadow: 0 4rpx 24rpx rgba(119,88,54,0.3); }
.submit-btn.loading { opacity: 0.8; }
.submit-btn text { color: #ffffff; font-size: 30rpx; font-weight: 600; }

/* Overlay */
.overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: flex-end; justify-content: center; }
.picker-card { background: #fff; border-radius: 32rpx 32rpx 0 0; padding: 40rpx; width: 100%; max-height: 60vh; overflow-y: auto; }
.picker-title { font-size: 34rpx; font-weight: 600; color: #1c1b1b; margin-bottom: 24rpx; display: block; }
.coupon-option { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 0; border-bottom: 2rpx solid #f6f3f2; }
.coupon-info { display: flex; align-items: center; gap: 12rpx; }
.coupon-name { font-size: 28rpx; color: #1c1b1b; }
.coupon-type { font-size: 22rpx; color: #775836; background: #f6f4f2; padding: 4rpx 16rpx; border-radius: 8rpx; }
.check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 3rpx solid #d2c4b8; text-align: center; line-height: 40rpx; font-size: 24rpx; color: transparent; flex-shrink: 0; }
.check.checked { background: #775836; border-color: #775836; color: #fff; }
</style>