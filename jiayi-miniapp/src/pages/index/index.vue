<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import NavBar from '@/components/NavBar.vue'

const currentSlide = ref(0)
const countdown = ref({ h: 2, m: 14, s: 59 })
let timer: ReturnType<typeof setInterval> | null = null

const banners = [
  { title: '璀璨系列·耀世而生', subtitle: 'Premium Collection', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCHGE2Ij-sDus8WZVpKSPVixB0NV_Pl1JLa7K0niL2yz3kHDUQzAY0JWPSZ7blW_TBd0ze2K8JB-4qjATJNmR-PAse76xe-zWKPJgSwxCoQOAudNkriYTcvs1bTVSHtsOQeYLjfob6eLSjTKvGZgJjuBSKzsmuIoK0972ZY5FoIPM8_S6ShunMwVn3MIH2MJbXLstYclV9ghv1IF2WGRiYKmcjNu6PIputfVuFGSqQHTF9226VWUrJdFw' },
  { title: '鎏金岁月', subtitle: 'Collection of the Month', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCZhZOwgmyfoKl4GmOuZ71U4IePT8lroVK91hWD5QuS5BmaW3JUW4aY2lJRJMJf9Eel_aXTWgq0_Hlio1KO4XqowNv_3bOMsV0URlhTNwvPuSJpi8nDvVSmMaXdIPAO_vDdoluxjgpOOpDT3_bQafb4PSmz1E35M4_x_eVlrqTh11ia-dKPmQiEggwdPfK7H4dtQdDjyeHd-sSbHBDBs7IaNf6Ts8CcDb7OzYjrfWeHXe3NWsginnLqLQ' },
  { title: '盛夏光芒', subtitle: 'Seasonal Promo', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBnbmQ4ycDoNaQnA0-nHrH4lm4Vl_S-evZidg0QS1uxyqbOzlKTyNygsvJ5j0ZY6LdfgbEghuiQuTMyuSx79OJpTSi_3Sbd4xQY8KxowE4aFTi9X1Cet9vznwgUXLfgpGjkdeqQ3DFtHMTMdjOG0bk6B3XPAOCuJb9wlZuKGyuKOAT89fm-6AV_PDzCuR50Mzhq0_pecQv2Ckt8PjL62-GdVGKkCIC1q4AWEvspy86kMx55V7EtqFIRMw' }
]

const iconPaths: Record<string, string> = {
  event_upcoming: 'M19 3h-1V1h-2v2H8V1H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z',
  gavel: 'M1 21h12v2H1v-2zM5.245 8.07l2.83-2.827 14.14 14.142-2.828 2.828L5.245 8.07zM12.317 1l5.657 5.656-2.83 2.83-5.654-5.66L12.317 1zM3.825 9.485l5.657 5.657-2.828 2.828-5.657-5.657 2.828-2.828z',
  new_releases: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z',
  auto_awesome: 'M19 9l1.25-2.75L23 5l-2.75-1.25L19 1l-1.25 2.75L15 5l2.75 1.25L19 9zm-7.5.5L9 4 6.5 9.5 1 12l5.5 2.5L9 20l2.5-5.5L17 12l-5.5-2.5zM19 15l-1.25 2.75L15 19l2.75 1.25L19 23l1.25-2.75L23 19l-2.75-1.25z',
  loyalty: 'M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z'
}
function getIconSvg(name: string, color: string): string {
  const d = iconPaths[name]
  if (!d) return ''
  const svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="' + d + '" fill="' + color + '"/></svg>'
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg)
}

const icons = [
  { icon: 'event_upcoming', label: '限时活动', color: '#C8A27A', page: '/pages/activity/list' },
  { icon: 'gavel', label: '限时拍卖', color: '#C9A52D', page: '/pages/auction/index' },
  { icon: 'new_releases', label: '新品上市', color: '#A67B52', page: '/pages/activity/new' },
  { icon: 'auto_awesome', label: 'AI穿戴', color: '#E8BF95', page: '/pages/ai-wear/index' },
  { icon: 'loyalty', label: '积分商城', color: '#605E5A', page: '/pages/points/index' }
]

const saleItems = [
  { name: '玫瑰金碎钻戒', price: '2,880', oldPrice: '3,388', tag: '8.5折', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDqyVxeG72S4cYDORfsptNTqzvDyTrki8fEnIkbXeJH_-D0HwILh8Z-ANwbeK0l3jz9XuWPbCJ76uTwCIbdDC7CIOLZaew0syfZe7tRE5hYsjI0FIe9qMl2VNQ_nVBeTpjS_AAV8x-0_kIQoAW5s4aQK_5ZMN7IsHpd6NYXnK3Kpx3UC-uNIJJNqIc71BxEDyCpmTWvlSF48mqm_wA_HmQfyZzZR_e2BgM_pPILzxT3JUV2gYAusrCOog' },
  { name: '南洋珍珠耳钉', price: '1,560', oldPrice: '2,100', tag: '秒杀', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBVb_0-CdLqXWmCa1Yz1L1cokH6gUGy6EUjfpNcvN9buuXLNoOgrbjgTAN98l1IFb8DJrGvbJd9z9VSZgpKHuX1vf4cIq8ZJgeCp6x_ZFcz4Y2LSUF0tSDINkzBl-pWDIZvbRlgAZmV1tJkKJYYxZuvRNxx9eKhkTW4QLUSFWy24ahsFxH0mc9Hu3J7v8lqpcUIRCEWV_jaHQ-CIb2ZAN7p0WcMjRl2lhNLEZKFKN9GEfbnQLkFtro0JA' },
  { name: '编织复古金镯', price: '8,900', oldPrice: '10,200', tag: '爆款', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAUXAtWrQ0KKv0UT1mBEyVaWV8O9RxWAqQYnW0elV194vcG7Q1zCiqnqET1y3cO_YdbK9b9gQH5bR_NSw2a4giWAsmEBOSpVP3AqabMWglrujCb0kPiF72Wvuv6SfLJtEibAL4gYUhTNq0N1UURMp6K7rdF1I-bK3YWYssB3EC4VoP2CRN95BlMDXiBEUe5l2j-oOlRpbHzwwfTmBTfff7M3QOMXxS0T2V4_cabmHcO9F27o3DULpohzw' }
]

const hotItems = [
  { name: '经典钻饰系列 手链', desc: '52颗真钻 / 18k白金', price: '12,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBi3u6h6xAl-L01vngu84VkVRcsQvLsOlq1qOy_NbM1JD5I0Ms_DZofW3q9XQ4-eBEcp2MIQQrakd9YK4UvGB0JzNl6TZHwkRbBor0MoJiDTk4apMaMFk-46KDjqtp7DCkQRAVJ2fDlwBl0SivyKVFSD23mirYXe6rPWhnVk1puchOdzlyQIWxM0mXWquABP3njJtX8EMY9-LePRD45W1LFtkw5f8ZlaqdTXGhjk2qy_-osi1P18eKZjw', span: '3/4' },
  { name: '几何美学 吊坠', desc: '原创设计 / 极简主义', price: '4,500', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuArauDoqpmT4Zu_9i9RqTAE24wE3dhHKW6_qw1Z6u1t7B97cn-dmIUtDmNGKca7zeVia2m0fghC4tPAoijDAqMQXMTXTgImsfi9Kxm81XhF2A1_A0JyVCJvBZp2u2NcZOjjFSgL2KYCNQw1dfcfK6ftOOaHhkXfrdBX2H0qe3LXN1TAsZpYqm4U2VVrsd2J9rrBJ6pDpgWaivbgYQ353js6QBzK-OR9gCAT76j0hbWW-ifsnSIJDyzD7Q', span: '1/1' },
  { name: '皇家蓝宝石 典藏戒', desc: '斯里兰卡原产蓝宝', price: '25,600', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBf-aG9G-14_LE9tbazEJkGSP_CI93rCteRTLeX2RROv-MrG2UVSjmmfcD6L4fgZxKSjMeYYqnMBU9gqWm4UpsgpJ16kxZOL682Nl_aVQxpzW_hmf9jZkyd-Bf1dHVvPBMCsu8zdxmOFZ-yVkuuZNL_y74ZzR-imGemBeDxFKB0AUBRGIsjaLcGDxDJKhx6YdgGngCZ3v2md-VtGpX20UzRpOs64h6IgCrmbLdvCu0mTWOa7HO-dE6YjA', span: '1/1' },
  { name: '祖母绿 垂坠耳坠', desc: '1.5克拉哥伦比亚绿', price: '38,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAzIBJAySDvTDit21t_K3GOYFvAM4zgzTMs41VjQvm5lZh3jQUHwZxvY6nWE2jr8FBFGcK16pQ2glhWtmVl4Bf6vi9FFD73pSXvpCnGvHEezz7Zd4YfFi_sZ9uGFfLfWCGZBZdrZ9kJr5ykqbkdJcbVCQ4CsTr03yggx_-fQdmWeVMu2gV9zb9cHwHz38N6n58IApolIHKWwnsRYOBnEAU6Nf73sMhCmSryCvcHZ143NG5kmOOekr2GAA', span: '3/4' }
]

function goPage(url: string) { uni.navigateTo({ url }) }
function goTab(url: string) { uni.switchTab({ url }) }

timer = setInterval(() => {
  let total = countdown.value.h * 3600 + countdown.value.m * 60 + countdown.value.s
  if (total > 0) total--
  else total = 2 * 3600 + 14 * 60 + 59
  countdown.value.h = Math.floor(total / 3600)
  countdown.value.m = Math.floor((total % 3600) / 60)
  countdown.value.s = total % 60
}, 1000)

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<template>
  <view class="page">
    <!-- Top Bar -->
    <view class="top-bar">
      <text class="top-title">嘉怡珠宝</text>
    </view>

    <!-- Banner Carousel -->
    <swiper class="banner" :indicator-dots="false" autoplay circular @change="(e: any) => currentSlide = e.detail.current">
      <swiper-item v-for="(b, i) in banners" :key="i">
        <view class="banner-slide" :style="'background-image: url(' + b.img + ')'">
          <view class="banner-overlay" />
          <view class="banner-text">
            <text class="banner-subtitle">{{ b.subtitle }}</text>
            <text class="banner-title">{{ b.title }}</text>
          </view>
        </view>
      </swiper-item>
    </swiper>
    <view class="banner-dots">
      <view v-for="(b, i) in banners" :key="i" :class="['dot', i === currentSlide ? 'dot-active' : '']" />
    </view>

    <!-- Icon Grid -->
    <view class="icon-grid">
      <view v-for="(item, i) in icons" :key="i" class="icon-item" @tap="goPage(item.page)">
        <view class="icon-circle" :style="{ backgroundColor: item.color + '20' }">
          <image class="icon-svg" :src="getIconSvg(item.icon, item.color)" mode="scaleToFill" />
        </view>
        <text class="icon-label">{{ item.label }}</text>
      </view>
    </view>

    <!-- Flash Sale -->
    <view class="section">
      <view class="section-header">
        <view class="section-header-left">
          <text class="section-title">限时抢购</text>
          <view class="countdown">
            <text class="countdown-num">{{ String(countdown.h).padStart(2,'0') }}</text>
            <text class="countdown-colon">:</text>
            <text class="countdown-num">{{ String(countdown.m).padStart(2,'0') }}</text>
            <text class="countdown-colon">:</text>
            <text class="countdown-num">{{ String(countdown.s).padStart(2,'0') }}</text>
          </view>
        </view>
        <text class="section-more">更多 ›</text>
      </view>
      <scroll-view class="sale-scroll" scroll-x enhanced show-scrollbar="false">
        <view v-for="(item, i) in saleItems" :key="i" class="sale-card">
          <view class="sale-img-wrap">
            <image class="sale-img" :src="item.img" mode="aspectFill" />
            <view class="sale-tag">{{ item.tag }}</view>
          </view>
          <text class="sale-name">{{ item.name }}</text>
          <view class="sale-price">
            <text class="sale-price-current">¥{{ item.price }}</text>
            <text class="sale-price-old">¥{{ item.oldPrice }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- Hot Picks -->
    <view class="section hot-section">
      <text class="section-title" style="padding: 0 32rpx">热门推荐</text>
      <view class="hot-grid">
        <view class="hot-col">
          <view v-for="(item, i) in hotItems.filter((_, idx) => idx % 2 === 0)" :key="i" class="hot-card" @tap="goPage('/pages/product/detail')">
            <image class="hot-img" :class="item.span === '3/4' ? 'hot-img-tall' : 'hot-img-square'" :src="item.img" mode="aspectFill" />
            <view class="hot-info">
              <text class="hot-name">{{ item.name }}</text>
              <text class="hot-desc">{{ item.desc }}</text>
              <view class="hot-bottom">
                <text class="hot-price">¥{{ item.price }}</text>
                <view class="hot-add">+</view>
              </view>
            </view>
          </view>
        </view>
        <view class="hot-col hot-col-offset">
          <view v-for="(item, i) in hotItems.filter((_, idx) => idx % 2 === 1)" :key="i" class="hot-card" @tap="goPage('/pages/product/detail')">
            <image class="hot-img" :class="item.span === '3/4' ? 'hot-img-tall' : 'hot-img-square'" :src="item.img" mode="aspectFill" />
            <view class="hot-info">
              <text class="hot-name">{{ item.name }}</text>
              <text class="hot-desc">{{ item.desc }}</text>
              <view class="hot-bottom">
                <text class="hot-price">¥{{ item.price }}</text>
                <view class="hot-add">+</view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

  </view>

  <NavBar :active="0" />
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; padding-bottom: 160rpx; }
.top-bar { display: flex; align-items: center; height: 112rpx; padding: 0 32rpx; background-color: rgba(252,249,248,0.8); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); position: sticky; top: 0; z-index: 50; }
.top-title { font-size: 56rpx; color: #775836; font-weight: 600; letter-spacing: 2px; font-family: 'Noto Serif SC', serif; }
.banner { width: 100%; height: 750rpx; }
.banner-slide { width: 100%; height: 100%; background-size: cover; background-position: center; position: relative; }
.banner-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(28,27,27,0.4), transparent); }
.banner-text { position: absolute; bottom: 64rpx; left: 32rpx; z-index: 2; display: flex; flex-direction: column; }
.banner-subtitle { font-size: 24rpx; color: #fff; letter-spacing: 4px; text-transform: uppercase; margin-bottom: 8rpx; }
.banner-title { font-size: 72rpx; color: #fff; font-weight: 700; line-height: 1.1; font-family: 'Noto Serif SC', serif; }
.banner-dots { display: flex; justify-content: center; gap: 8rpx; margin-top: -32rpx; position: relative; z-index: 3; }
.dot { width: 12rpx; height: 12rpx; border-radius: 50%; background-color: rgba(0,0,0,0.15); }
.dot-active { background-color: #C8A27A; width: 24rpx; border-radius: 12rpx; }
.icon-grid { display: flex; flex-wrap: wrap; justify-content: flex-start; padding: 40rpx 32rpx; row-gap: 40rpx; column-gap: 40rpx; }
.icon-item { flex: 0 0 calc((100% - 80rpx) / 3); display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.icon-circle { width: 80rpx; height: 80rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.icon-svg { width: 40rpx; height: 40rpx; }
.icon-label { font-size: 22rpx; color: #1C1B1B; font-weight: 500; }
.section { margin-bottom: 24rpx; }
.section-header { display: flex; justify-content: space-between; align-items: flex-end; padding: 0 32rpx; margin-bottom: 24rpx; }
.section-header-left { display: flex; align-items: center; gap: 16rpx; }
.section-title { font-size: 22px; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.section-more { font-size: 12px; color: #605E5A; display: flex; align-items: center; }
.countdown { display: flex; align-items: center; gap: 4rpx; }
.countdown-num { background-color: #775836; color: #fff; font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 4rpx; font-weight: bold; }
.countdown-colon { color: #775836; font-weight: bold; font-size: 20rpx; }
.sale-scroll { display: flex; padding: 0 32rpx; white-space: nowrap; padding-bottom: 8rpx; }
.sale-card { display: inline-block; width: 280rpx; flex-shrink: 0; margin-right: 40rpx; }
.sale-img-wrap { width: 280rpx; height: 280rpx; border-radius: 24rpx; overflow: hidden; position: relative; background-color: #fff; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06); }
.sale-img { width: 100%; height: 100%; }
.sale-tag { position: absolute; top: 12rpx; left: 12rpx; background-color: rgba(207,102,121,0.9); color: #fff; font-size: 20rpx; padding: 4rpx 16rpx; border-radius: 20rpx; font-weight: bold; }
.sale-name { font-size: 24rpx; color: #1C1B1B; margin-top: 10rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.sale-price { display: flex; align-items: baseline; gap: 8rpx; margin-top: 6rpx; }
.sale-price-current { font-size: 28rpx; color: #775836; font-weight: bold; }
.sale-price-old { font-size: 20rpx; color: #999; text-decoration: line-through; }
.hot-section { margin-top: 16rpx; }
.hot-grid { display: flex; gap: 16rpx; padding: 0 32rpx; }
.hot-col { display: flex; flex-direction: column; gap: 16rpx; flex: 1; }
.hot-col-offset { padding-top: 48rpx; }
.hot-card { background-color: #fff; border-radius: 24rpx; overflow: hidden; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06); }
.hot-img { width: 100%; }
.hot-img-tall { height: 540rpx; }
.hot-img-square { height: 340rpx; }
.hot-info { padding: 24rpx; }
.hot-name { font-size: 30rpx; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.hot-desc { font-size: 22rpx; color: #605E5A; margin-top: 12rpx; }
.hot-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 16rpx; }
.hot-price { font-size: 34rpx; color: #775836; font-weight: bold; }
.hot-add { width: 64rpx; height: 64rpx; border-radius: 50%; background-color: #C8A27A; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 36rpx; font-weight: bold; }
</style>
