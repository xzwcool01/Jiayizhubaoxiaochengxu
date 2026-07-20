<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getProduct, getSharePoster, getProductPageConfig, PmsProduct, ProductPageConfig } from '@/api/product'
import { toggleFavorite, getFavoriteStatus } from '@/api/favorite'
import { addToCart } from '@/api/cart'
import AiWearCard from '@/components/AiWearCard.vue'
import VideoShowcase from '@/components/VideoShowcase.vue'
import ProductGallery from '@/components/ProductGallery.vue'
import DisclaimerFooter from '@/components/DisclaimerFooter.vue'

const product = ref<PmsProduct | null>(null)
const images = ref<string[]>([])
const currentImg = ref(0)
const loading = ref(true)
const posterUrl = ref('')
const showPoster = ref(false)
const savingPoster = ref(false)
const pageConfig = ref<ProductPageConfig | null>(null)
const galleryImages = ref<string[]>([])
const isFavorited = ref(false)
const favoriting = ref(false)

async function handleAddCart() {
  if (!product.value) return
  if (!uni.getStorageSync('token')) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    uni.switchTab({ url: '/pages/my/my' })
    return
  }
  try {
    await addToCart(product.value.id)
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch { uni.showToast({ title: '操作失败', icon: 'none' }) }
}

async function toggleFav() {
  if (!product.value || favoriting.value) return
  const token = uni.getStorageSync('token') || ''
  if (!token) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    uni.switchTab({ url: '/pages/my/my' })
    return
  }
  favoriting.value = true
  try {
    const res = await toggleFavorite(product.value.id)
    if (res.code === 200) isFavorited.value = res.data
  } catch { uni.showToast({ title: '操作失败', icon: 'none' }) }
  finally { favoriting.value = false }
}

function parseImages(p: PmsProduct): string[] {
  const list: string[] = []
  const seen = new Set<string>()
  if (p.mainImage) { list.push(p.mainImage); seen.add(p.mainImage) }
  if (p.images) {
    try {
      const parsed = JSON.parse(p.images)
      if (Array.isArray(parsed)) {
        for (const item of parsed) {
          const url = (typeof item === 'string' ? item : item?.url || '').trim()
          if (!url) continue
          const filename = url.substring(url.lastIndexOf('/') + 1)
          const exists = [...seen].some(u => u.substring(u.lastIndexOf('/') + 1) === filename)
          if (!exists) { list.push(url); seen.add(url) }
        }
      }
    } catch {}
  }
  return list
}

const specs = ref<{ label: string; value: string }[]>([])

function parseSpecs(raw: string | undefined | null): { label: string; value: string }[] {
  if (!raw) return []
  try {
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr.filter((s: any) => s?.label) : []
  } catch { return [] }
}

function renderHtml(html: string): string {
  if (!html) return ''
  return html
    .replace(/<!DOCTYPE[^>]*>/gi, '')
    .replace(/<html[^>]*>/gi, '')
    .replace(/<\/html>/gi, '')
    .replace(/<head>[\s\S]*?<\/head>/gi, '')
    .replace(/<body[^>]*>/gi, '')
    .replace(/<\/body>/gi, '')
    .replace(/<table[\s\S]*?<\/table>/gi, '')
    .replace(/\\n/g, '')
    .trim()
}

function extractSpecs(html: string): { label: string; value: string }[] {
  const result: { label: string; value: string }[] = []
  if (!html) return result
  const tableMatch = html.match(/<table[\s\S]*?<\/table>/i)
  if (!tableMatch) return result
  const rowRegex = /<tr[^>]*>[\s\S]*?<td[^>]*>([\s\S]*?)<\/td>[\s\S]*?<td[^>]*>([\s\S]*?)<\/td>[\s\S]*?<\/tr>/gi
  let m
  while ((m = rowRegex.exec(tableMatch[0])) !== null) {
    const label = m[1].replace(/<[^>]+>/g, '').trim()
    const value = m[2].replace(/<[^>]+>/g, '').trim()
    if (label) result.push({ label, value })
  }
  return result
}

function formatPrice(price: number): string {
  return Math.floor(price).toLocaleString('zh-CN')
}

async function handleShare() {
  if (!product.value) return
  if (!posterUrl.value) {
    uni.showLoading({ title: '生成海报...', mask: true })
    try {
      const res = await getSharePoster(product.value.id)
      if (res.code === 200) posterUrl.value = res.data
      else { uni.hideLoading(); uni.showToast({ title: '海报生成失败', icon: 'none' }); return }
    } catch { uni.hideLoading(); uni.showToast({ title: '网络错误', icon: 'none' }); return }
    uni.hideLoading()
  }
  showPoster.value = true
}

function savePoster() {
  if (!posterUrl.value) return
  savingPoster.value = true
  uni.downloadFile({
    url: posterUrl.value,
    success: (res) => {
      if (res.statusCode !== 200) {
        uni.showToast({ title: '下载失败', icon: 'none' })
        savingPoster.value = false
        return
      }
      uni.saveImageToPhotosAlbum({
        filePath: res.tempFilePath,
        success: () => { uni.showToast({ title: '已保存到相册' }); savingPoster.value = false },
        fail: () => { uni.showToast({ title: '保存失败', icon: 'none' }); savingPoster.value = false }
      })
    },
    fail: () => { uni.showToast({ title: '下载失败', icon: 'none' }); savingPoster.value = false }
  })
}

onLoad((options) => {
  let id = Number(options?.id)
  if (!id && options?.scene) {
    const m = options.scene.match(/id=(\d+)/)
    id = m ? Number(m[1]) : 0
  }
  if (!id) { loading.value = false; return }
  getProduct(id).then(res => {
    if (res.code === 200) {
      product.value = res.data
      images.value = parseImages(res.data)
      specs.value = parseSpecs(res.data.specs)
      if (!specs.value.length) specs.value = extractSpecs(res.data.description || '')
      getProductPageConfig(id).then(r => {
        if (r.code === 200) {
          pageConfig.value = r.data
          galleryImages.value = Array.isArray(r.data.galleryImages) ? r.data.galleryImages : []
        }
      }).catch(() => {})
      if (uni.getStorageSync('token')) {
        getFavoriteStatus(id).then(r => { if (r.code === 200) isFavorited.value = r.data }).catch(() => {})
      }
    }
    loading.value = false
  }).catch(() => { loading.value = false })
})

onShareAppMessage(() => {
  if (!product.value) return {}
  return {
    title: [product.value.name, product.value.subtitle].filter(Boolean).join(' - '),
    path: '/pages/product/detail?id=' + product.value.id,
    imageUrl: posterUrl.value || images.value[0] || ''
  }
})
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="top-left">
        <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="44rpx" color="#1C1B1B" /></view>
        <view class="share-btn" @tap="handleShare"><MsIcon name="share" size="36rpx" color="#1C1B1B" /></view>
      </view>
    </view>
    <view v-if="loading" class="loading-mask"><text>加载中...</text></view>
    <view class="gallery" v-if="images.length">
      <image class="gallery-main" :src="images[currentImg]" mode="aspectFill" />
      <view class="gallery-indicator"><text>{{ currentImg + 1 }} / {{ images.length }}</text></view>
      <view class="gallery-thumbs">
        <image v-for="(img, i) in images" :key="i" :class="['thumb', i === currentImg ? 'thumb-active' : '']" :src="img" mode="aspectFill" @tap="currentImg = i" />
      </view>
    </view>
    <view class="info-section" v-if="product">
      <view class="info-header">
        <view><text class="info-name">{{ product.name }}</text><text v-if="product.subtitle" class="info-sub">{{ product.subtitle }}</text></view>
      </view>
      <view class="info-price">
        ¥{{ formatPrice(product.price) }}
        <text v-if="product.originalPrice" style="font-size:24rpx;color:#999;text-decoration:line-through;margin-left:12rpx;font-weight:400">¥{{ formatPrice(product.originalPrice) }}</text>
      </view>
      <!-- NEW: AI穿戴 + 抖音视频（价格下方、描述上方） -->
      <AiWearCard v-if="pageConfig?.aiEnabled" />
      <VideoShowcase v-if="pageConfig?.videoEnabled" :cover="pageConfig?.videoCover || images[0] || ''" :videoUrl="pageConfig?.videoUrl || ''" />
      <!-- END NEW -->
      <view class="desc">
        <text class="desc-label">商品描述</text>
        <text class="desc-text" v-if="product.descriptionText">{{ product.descriptionText }}</text>
        <rich-text class="desc-text" :nodes="renderHtml(product.description)" v-else-if="product.description" />
        <text class="desc-text" v-else>暂无描述</text>
      </view>
      <view class="specs-list" v-if="specs.length">
        <view v-for="(s, i) in specs" :key="i" class="spec-row">
          <text class="spec-key">{{ s.label }}</text>
          <text class="spec-val">{{ s.value }}</text>
        </view>
      </view>
    </view>
    <view class="reviews">
      <view class="review-header"><text class="review-title">用户评价 (128)</text><text class="review-link">查看全部</text></view>
      <view class="review-item">
        <view class="reviewer"><text class="reviewer-name">C.**</text><text class="reviewer-date">2024.07.10</text></view>
        <view class="review-stars"><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /></view>
        <text class="review-text">非常精致的戒指，实物比图片更有质感。珍珠的光泽非常温润，戴上去也很合适</text>
      </view>
      <view class="review-item">
        <view class="reviewer"><text class="reviewer-name">L***y</text><text class="reviewer-date">2024.07.08</text></view>
        <view class="review-stars"><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /></view>
        <text class="review-text">送朋友的结婚礼物，包装非常精美，朋友很喜欢。玫瑰金很显白。</text>
      </view>
    </view>
    <!-- NEW: 竖向大图展示 + 免责声明（置于用户评价底部） -->
    <ProductGallery v-if="pageConfig?.galleryEnabled" :images="galleryImages" />
    <DisclaimerFooter v-if="pageConfig?.disclaimerEnabled" :content="pageConfig?.disclaimerText || ''" :textColor="pageConfig?.disclaimerColor || '#999'" />
    <!-- END NEW -->
    <view class="bottom-bar">
      <view class="bar-icon" @tap="toggleFav"><text :class="['fav-icon', isFavorited ? 'fav-active' : 'fav-inactive']">{{ isFavorited ? '♥' : '♡' }}</text></view>
      <view class="bar-btn secondary" @tap="handleAddCart"><text>加入购物车</text></view>
      <view class="bar-btn primary"><text>立即购买</text></view>
    </view>

    <view v-if="showPoster" class="poster-overlay" @tap="showPoster = false">
      <view class="poster-card" @tap.stop>
        <view class="poster-close" @tap="showPoster = false"><text>✕</text></view>
        <image class="poster-img" :src="posterUrl" mode="widthFix" />
        <view class="poster-actions">
          <view class="poster-btn" @tap="savePoster"><text>{{ savingPoster ? '保存中...' : '保存到相册' }}</text></view>
          <button open-type="share" class="poster-btn poster-btn-primary"><text>分享给好友</text></button>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); }
.top-bar { display: flex; align-items: center; justify-content: space-between; padding: 32rpx; height: 112rpx; }
.top-left { display: flex; flex-direction: row; align-items: center; gap: 28rpx; padding-top: 28rpx; }
.gallery { padding: 0 32rpx; }
.gallery-main { width: 100%; height: 560rpx; border-radius: 24rpx; }
.gallery-indicator { text-align: center; margin-top: 8rpx; font-size: 22rpx; color: #605E5A; }
.gallery-thumbs { display: flex; gap: 16rpx; margin-top: 16rpx; justify-content: center; }
.thumb { width: 120rpx; height: 120rpx; border-radius: 16rpx; border: 4rpx solid transparent; }
.thumb-active { border-color: #775836; }
.info-section { padding: 48rpx 32rpx; }
.info-header { }
.info-name { font-size: 40rpx; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.info-sub { font-size: 22rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.info-price { font-size: 56rpx; font-weight: 700; color: #775836; margin-top: 16rpx; }
.info-tags { display: flex; gap: 16rpx; margin-top: 24rpx; }
.tag { background-color: #F0EBE7; font-size: 20rpx; color: #775836; padding: 8rpx 20rpx; border-radius: 40rpx; }
.selector { margin-top: 32rpx; }
.selector-label { font-size: 24rpx; font-weight: 600; color: #1C1B1B; }
.size-pills { display: flex; gap: 24rpx; margin-top: 16rpx; }
.pill { width: 100rpx; height: 100rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24rpx; }
.pill-active { background-color: #775836; color: #fff; }
.pill-inactive { background-color: #F0EBE7; color: #605E5A; }
.desc { margin-top: 48rpx; }
.desc-label { font-size: 28rpx; font-weight: 600; color: #1C1B1B; display: block; margin-bottom: 12rpx; }
.desc-text { font-size: 24rpx; color: #4F453C; line-height: 1.6; word-break: break-word; }
.specs-list { margin-top: 32rpx; border-top: 2rpx solid #F0EBE7; padding-top: 8rpx; }
.spec-row { display: flex; justify-content: space-between; align-items: center; padding: 22rpx 0; border-bottom: 2rpx solid #F0EBE7; }
.spec-key { font-size: 24rpx; color: #605E5A; }
.spec-val { font-size: 24rpx; color: #1C1B1B; text-align: right; max-width: 55%; }
.reviews { padding: 0 32rpx 48rpx; }
.review-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.review-title { font-size: 32rpx; font-weight: 600; color: #1C1B1B; }
.review-link { font-size: 22rpx; color: #775836; }
.review-item { background-color: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.reviewer { display: flex; justify-content: space-between; }
.reviewer-name { font-size: 22rpx; font-weight: 600; color: #1C1B1B; }
.reviewer-date { font-size: 18rpx; color: #605E5A; }
.review-stars { margin: 8rpx 0; }
.review-text { font-size: 24rpx; color: #4F453C; line-height: 1.5; }
.loading-mask { display: flex; align-items: center; justify-content: center; height: 400rpx; font-size: 28rpx; color: #999; }
.bottom-bar { position: fixed; bottom: 24rpx; left: 24rpx; right: 24rpx; width: auto; background-color: #fff; padding: 16rpx 24rpx; display: flex; align-items: center; gap: 20rpx; border-radius: 24rpx; box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.08); z-index: 100; }
.share-btn { background: none; border: none; padding: 0; margin: 0; line-height: 1; min-width: 0; width: auto; height: auto; display: inline-flex; align-items: center; }
.share-btn::after { border: none; }
.bar-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background-color: #F0EBE7; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.fav-icon { font-size: 40rpx; line-height: 1; transition: color 0.2s; }
.fav-inactive { color: #605E5A; }
.fav-active { color: #CF6679; }
.bar-btn { flex: 1; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
.bar-btn.primary { background-color: #775836; color: #fff; }
.bar-btn.secondary { border: 2rpx solid #D9D2CC; color: #775836; }
.poster-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 2000; }
.poster-card { width: 590rpx; background: #fff; border-radius: 24rpx; overflow: hidden; position: relative; }
.poster-img { width: 100%; display: block; }
.poster-close { position: absolute; top: 0; right: 0; width: 72rpx; height: 72rpx; display: flex; align-items: center; justify-content: center; z-index: 1; }
.poster-close text { font-size: 36rpx; color: #fff; text-shadow: 0 2rpx 6rpx rgba(0,0,0,0.5); }
.poster-actions { display: flex; padding: 24rpx; gap: 20rpx; }
.poster-btn { flex: 1; text-align: center; padding: 24rpx 0; border-radius: 16rpx; font-size: 28rpx; font-weight: 600; background: #F0EBE7; color: #775836; line-height: 1.2; }
.poster-btn-primary { background: #775836; color: #fff; border: none; margin: 0; }
.poster-btn-primary::after { border: none; }
</style>
