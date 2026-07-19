<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import NavBar from '@/components/NavBar.vue'
import { getProductList, getCategories, PmsProduct, Category } from '@/api/product'

const categories = ref<Category[]>([])
const activeCat = ref(0)
const products = ref<PmsProduct[]>([])
const loading = ref(false)
const keyword = ref('')
let currentCategoryId: number | undefined = undefined

async function loadCategories() {
  const res = await getCategories()
  if (res.code === 200) categories.value = [{ id: 0, name: '全部', sort: -1 } as Category, ...res.data]
}

async function fetchProducts() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: 1, size: 50 }
    if (currentCategoryId) params.categoryId = currentCategoryId
    if (keyword.value) params.keyword = keyword.value
    const res = await getProductList(params)
    if (res.code === 200) products.value = res.data.records
  } finally {
    loading.value = false
  }
}

function doSearch() {
  fetchProducts()
}

function getImageUrl(p: PmsProduct): string {
  if (p.mainImage) return p.mainImage
  if (p.images) {
    try { const arr = JSON.parse(p.images); return Array.isArray(arr) && arr.length > 0 ? arr[0] : '' } catch { return '' }
  }
  return ''
}

function goDetail(id: number) {
  uni.navigateTo({ url: '/pages/product/detail?id=' + id })
}

onMounted(async () => {
  await loadCategories()
  await fetchProducts()
})

watch(activeCat, (idx) => {
  const cat = categories.value[idx]
  currentCategoryId = cat && cat.id > 0 ? cat.id : undefined
  keyword.value = ''
  fetchProducts()
})
</script>

<template>
  <view class="page">
    <view class="top-area">
      <text class="brand-text">嘉怡珠宝</text>
      <view class="search-bar">
        <input class="search-input" placeholder="搜索奢华珠宝..." v-model="keyword" @confirm="doSearch" />
      </view>
    </view>
    <view class="content">
      <scroll-view class="sidebar" scroll-y enhanced show-scrollbar="false">
        <view v-for="(cat, i) in categories" :key="i" :class="['cat-item', i === activeCat ? 'cat-active' : '']" @tap="activeCat = i">
          <view v-if="i === activeCat" class="cat-indicator" />
          <text :class="['cat-text', i === activeCat ? 'cat-text-active' : '']">{{ cat.name }}</text>
        </view>
      </scroll-view>
      <scroll-view class="right-panel" scroll-y enhanced>
        <view class="grid" v-if="!loading">
          <view v-for="(p, i) in products" :key="p.id" class="product-card" @tap="goDetail(p.id)">
            <image class="product-img" :src="getImageUrl(p)" mode="aspectFill" />
            <view class="product-like"><text class="like-icon">♡</text></view>
            <view class="product-info">
              <text class="product-name">{{ p.name }}</text>
              <view class="product-price"><text style="font-size:24rpx">¥</text><text style="font-size:32rpx; font-weight:bold"> {{ p.price }}</text></view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>

  <NavBar :active="1" />
</template>

<style scoped>
.page { background-color: #FAFAF8; height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; box-sizing: border-box; }
.top-area { padding: 32rpx 32rpx 0; background-color: #FCF9F8; }
.brand-text { font-size: 48rpx; color: #775836; font-weight: 600; display: block; margin-bottom: 16rpx; }
.search-bar { display: flex; align-items: center; }
.search-input { width: 100%; height: 80rpx; background-color: #F6F3F2; border-radius: 100rpx; padding-left: 32rpx; padding-right: 32rpx; font-size: 26rpx; border: none; color: #1C1B1B; }
.content { display: flex; flex: 1; overflow: hidden; }
.sidebar { width: 192rpx; background-color: #fff; border-right: 2rpx solid rgba(210,196,184,0.3); }
.cat-item { position: relative; padding: 48rpx 16rpx; text-align: center; }
.cat-indicator { position: absolute; left: 0; top: 50%; transform: translateY(-50%); width: 6rpx; height: 32rpx; background-color: #775836; border-radius: 0 8rpx 8rpx 0; }
.cat-text { font-size: 24rpx; color: #605E5A; }
.cat-text-active { color: #775836; font-weight: bold; }
.right-panel { flex: 1; padding: 32rpx; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24rpx; }
.product-card { background-color: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); position: relative; }
.product-img { width: 100%; height: 340rpx; }
.product-like { position: absolute; top: 16rpx; right: 16rpx; width: 64rpx; height: 64rpx; border-radius: 50%; background-color: rgba(255,255,255,0.8); display: flex; align-items: center; justify-content: center; }
.like-icon { font-size: 36rpx; color: #775836; line-height: 1; }
.product-info { padding: 24rpx; }
.product-name { font-size: 28rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { display: flex; align-items: baseline; gap: 4rpx; margin-top: 8rpx; color: #775836; }
</style>
