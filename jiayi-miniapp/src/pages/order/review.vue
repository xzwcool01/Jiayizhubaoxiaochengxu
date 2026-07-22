<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createReview } from '@/api/review'
import { getOrderDetail, type OrderVO } from '@/api/order'

const order = ref<OrderVO | null>(null)
const orderId = ref(0)
const productId = ref(0)
const rating = ref(5)
const content = ref('')
const isAnonymous = ref(0)
const images = ref<string[]>([])
const submitting = ref(false)

onLoad(async (opt) => {
  orderId.value = Number(opt?.orderId || 0)
  productId.value = Number(opt?.productId || 0)
  if (orderId.value) {
    const res = await getOrderDetail(orderId.value)
    if (res.code === 200) order.value = res.data
  }
})

function chooseImage() {
  if (images.value.length >= 6) {
    uni.showToast({ title: '最多6张图片', icon: 'none' })
    return
  }
  uni.chooseImage({
    count: 6 - images.value.length,
    success: (res) => {
      const tempPaths = res.tempFilePaths.map((p: string) => p)
      uploadImages(tempPaths, 0)
    }
  })
}

function uploadImages(paths: string[], idx: number) {
  if (idx >= paths.length) return
  uni.uploadFile({
    url: 'http://localhost:8080/api/upload/review-image',
    filePath: paths[idx],
    name: 'file',
    formData: { productId: productId.value },
    success: (res) => {
      try {
        const data = JSON.parse(res.data as string)
        if (data.code === 200 && data.data) {
          images.value.push(data.data)
        }
      } catch (e) {
        console.error('上传解析失败', e)
      }
    },
    fail: (e) => {
      console.error('上传失败', e)
    },
    complete: () => {
      uploadImages(paths, idx + 1)
    }
  })
}

function removeImage(idx: number) {
  images.value.splice(idx, 1)
}

async function handleSubmit() {
  if (rating.value < 1) {
    uni.showToast({ title: '请选择评分', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const res = await createReview({
      orderId: orderId.value,
      productId: productId.value,
      rating: rating.value,
      content: content.value,
      images: images.value,
      isAnonymous: isAnonymous.value
    })
    if (res.code === 200) {
      uni.showToast({ title: '评价成功', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1500)
    } else {
      uni.showToast({ title: res.msg || '提交失败', icon: 'none' })
    }
  } catch {
    uni.showToast({ title: '网络错误', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function parseSpecs(raw: string | undefined | null): string {
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
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text class="back-icon">←</text></view>
      <text class="page-title">发表评价</text>
    </view>

    <scroll-view class="content" scroll-y>
      <view class="section product-section" v-if="order?.items?.length">
        <image class="product-img" :src="order.items[0].productImage" mode="aspectFill" />
        <view class="product-info">
          <text class="product-name">{{ order.items[0].productName }}</text>
          <text class="product-spec" v-if="order.items[0].productSpecs">{{ parseSpecs(order.items[0].productSpecs) }}</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">商品评分</text>
        <view class="stars">
          <text v-for="i in 5" :key="i" :class="['star', i <= rating ? 'active' : '']" @tap="rating = i">★</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">评价内容</text>
        <textarea v-model="content" class="textarea" placeholder="写下你的使用体验…" maxlength="500" />
      </view>

      <view class="section">
        <text class="section-title">上传图片 <text style="font-size:22rpx;color:#999;font-weight:400">（最多6张）</text></text>
        <view class="upload-area">
          <view v-for="(img, i) in images" :key="i" class="upload-item">
            <image :src="img" mode="aspectFill" class="upload-preview" />
            <view class="upload-remove" @tap="removeImage(i)"><text>✕</text></view>
          </view>
          <view v-if="images.length < 6" class="upload-btn" @tap="chooseImage">
            <text class="upload-plus">+</text>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="anonymous-row">
          <text>匿名评价</text>
          <switch :checked="isAnonymous === 1" @change="isAnonymous = $event.detail.value ? 1 : 0" color="#775836" />
        </view>
      </view>

      <view class="submit-area">
        <view class="submit-btn" :class="{ disabled: submitting }" @tap="handleSubmit">
          <text>{{ submitting ? '提交中...' : '提交评价' }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; height: 80rpx; padding: calc(var(--status-bar-height) + 32rpx) 32rpx 0; flex-shrink: 0; }
.back-btn { width: 60rpx; }
.back-icon { font-size: 36rpx; color: #775836; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.content { padding: 40rpx 32rpx 40rpx; box-sizing: border-box; }
.section { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 16rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; margin-bottom: 16rpx; display: block; }
.product-section { display: flex; gap: 16rpx; align-items: center; }
.product-img { width: 120rpx; height: 120rpx; border-radius: 12rpx; flex-shrink: 0; }
.product-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; min-width: 0; }
.product-name { font-size: 26rpx; color: #1C1B1B; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-spec { font-size: 22rpx; color: #999; }
.stars { display: flex; gap: 12rpx; }
.star { font-size: 52rpx; color: #ddd; }
.star.active { color: #E9C349; }
.textarea { width: 100%; min-height: 200rpx; font-size: 26rpx; color: #1C1B1B; padding: 16rpx; box-sizing: border-box; border: 2rpx solid #F0EBE7; border-radius: 12rpx; background: #FAFAF8; }
.upload-area { display: flex; flex-wrap: wrap; gap: 16rpx; }
.upload-item { position: relative; width: 160rpx; height: 160rpx; }
.upload-preview { width: 100%; height: 100%; border-radius: 12rpx; }
.upload-remove { position: absolute; top: -8rpx; right: -8rpx; width: 36rpx; height: 36rpx; background: rgba(0,0,0,0.5); border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.upload-remove text { color: #fff; font-size: 20rpx; }
.upload-btn { width: 160rpx; height: 160rpx; border: 2rpx dashed #D9D2CC; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; background: #FAFAF8; }
.upload-plus { font-size: 48rpx; color: #D9D2CC; }
.anonymous-row { display: flex; justify-content: space-between; align-items: center; font-size: 26rpx; color: #1C1B1B; }
.submit-area { padding: 24rpx 0; }
.submit-btn { background: #775836; color: #fff; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
.submit-btn.disabled { opacity: 0.6; }
</style>
