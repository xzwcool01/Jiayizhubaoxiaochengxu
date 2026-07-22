<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import NavBar from '@/components/NavBar.vue'
import { post, get } from '@/api/request'


const showLogin = ref(false)
const loading = ref(false)
const token = ref(uni.getStorageSync('token') || '')
const userInfo = ref<any>(uni.getStorageSync('userInfo') || null)
const loginNickname = ref('')
const loginAvatar = ref('')

const isLoggedIn = computed(() => !!token.value)

const defaultAvatar = 'data:image/svg+xml;charset=utf-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22100%22%20height%3D%22100%22%20viewBox%3D%220%200%20100%20100%22%3E%3Crect%20fill%3D%22%23E0D8D0%22%20width%3D%22100%22%20height%3D%22100%22%2F%3E%3Ctext%20x%3D%2250%22%20y%3D%2255%22%20text-anchor%3D%22middle%22%20fill%3D%22%23999%22%20font-size%3D%2230%22%3E%3F%3C%2Ftext%3E%3C%2Fsvg%3E'

function onChooseAvatar(e: any) {
  loginAvatar.value = e.detail.avatarUrl
}

async function handleWxLogin() {
  console.log('handleWxLogin start', loginNickname.value, loginAvatar.value)
  if (!loginNickname.value) { uni.showToast({ title: '请点击昵称输入框获取昵称', icon: 'none' }); return }
  loading.value = true
  try {
    console.log('calling uni.login')
    const loginRes = await uni.login()
    console.log('uni.login res', loginRes)
    const code = loginRes.code
    const rawData = JSON.stringify({ nickName: loginNickname.value, avatarUrl: loginAvatar.value })
    console.log('calling wx-login')
    const res = await post('/user/wx-login', { code, rawData, signature: '' })
    console.log('wx-login response', res)
    if (res?.data) {
      const d = res.data
      if (res.message === 'new' && loginAvatar.value && loginAvatar.value.startsWith('http://tmp')) {
        try {
          const fs = uni.getFileSystemManager()
          const base64: string = await new Promise((resolve, reject) => {
            fs.readFile({ filePath: loginAvatar.value, encoding: 'base64', success: (r) => resolve(r.data as string), fail: reject })
          })
          const ext = loginAvatar.value.includes('.') ? loginAvatar.value.split('.').pop()! : 'jpeg'
          const up = await post<string>('/upload/image-base64', { base64, ext })
          if (up?.data) {
            d.avatar = up.data
            try {
              const avatarRes = await post('/user/avatar', { openid: d.openid, avatar: up.data })
              console.log('avatar save response', avatarRes)
            } catch (e2) { console.warn('avatar save failed:', e2) }
          }
        } catch (e) { console.warn('avatar upload failed:', e) }
      }
      if (!d.avatar && loginAvatar.value) d.avatar = loginAvatar.value
      token.value = d.openid
      userInfo.value = d
      uni.setStorageSync('token', d.openid)
      uni.setStorageSync('userInfo', d)
      showLogin.value = false
      fetchLevels(); checkSigninStatus()
    }
  } catch {
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}



function goPoints() {
  uni.navigateTo({ url: '/pages/points/index' })
}
function onMenuTap(m: any) {
  if (m.label === '我的收藏') { uni.navigateTo({ url: '/pages/favorite/index' }); return }
  if (m.label === '我的订单') { uni.navigateTo({ url: '/pages/order/list' }); return }
  if (m.label === '收货地址') { uni.navigateTo({ url: '/pages/address/index' }); return }
  if (m.label === '优惠券') { uni.navigateTo({ url: '/pages/my/coupon' }); return }
  uni.showToast({ title: m.label + '（开发中）', icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        token.value = ''
        userInfo.value = null
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        showLogin.value = true
      }
    }
  })
}

const signedIn = ref(false)
const signinLoading = ref(false)
const levels = ref<any[]>([])

const sinceYear = computed(() => {
  const t = userInfo.value?.createTime
  if (!t) return ''
  return typeof t === 'string' ? t.substring(0, 4) : ''
})

const loadingLevels = ref(true)

const currentLevel = computed(() => {
  const pts = userInfo.value?.points || 0
  const arr = levels.value
  if (!Array.isArray(arr) || !arr.length) return null
  return arr.find((l: any) => l && pts >= l.minPoints && pts <= l.maxPoints) || arr[0]
})

const nextLevel = computed(() => {
  const cur = currentLevel.value
  const arr = levels.value
  if (!Array.isArray(arr) || !arr.length) return undefined
  if (!cur?.id) return undefined
  const idx = arr.findIndex((l: any) => l && l.id === cur.id)
  if (idx >= 0 && idx < arr.length - 1) return arr[idx + 1]
  return null
})

const progressPercent = computed(() => {
  const pts = userInfo.value?.points || 0
  const cur = currentLevel.value
  const next = nextLevel.value
  if (!cur?.id) return 0
  if (next === null) return 100
  if (!next) return 0
  const range = (next.minPoints || 0) - (cur?.minPoints || 0)
  if (range <= 0) return 100
  return Math.min(100, Math.round(((pts - (cur?.minPoints || 0)) / range) * 100))
})

const pointsRemaining = computed(() => {
  const next = nextLevel.value
  if (!next) return 0
  return Math.max(0, (next.minPoints || 0) - (userInfo.value?.points || 0))
})

async function checkSigninStatus() {
  const openid = token.value
  if (!openid) return
  try {
    const res = await get('/user/signin/status', { openid })
    signedIn.value = !!(res?.code === 200 && res?.data)
  } catch (e) { console.warn('signin status fail', e) }
}

async function handleSignin() {
  if (signedIn.value || signinLoading.value) return
  signinLoading.value = true
  try {
    const res = await post('/user/signin', { openid: token.value })
    if (res?.code === 200 && res?.data) {
      signedIn.value = true
      userInfo.value = { ...userInfo.value, points: res.data.points || 0, lastSigninTime: new Date().toISOString() }
      uni.setStorageSync('userInfo', userInfo.value)
      uni.showToast({ title: '签到成功 +10 积分', icon: 'success' })
    } else {
      uni.showToast({ title: res?.message || '签到失败', icon: 'none' })
    }
  } catch (e) {
    console.warn('signin fail', e)
    uni.showToast({ title: '签到失败', icon: 'none' })
  } finally {
    signinLoading.value = false
  }
}

async function fetchLevels() {
  try {
    const res = await get('/level/list')
    console.log('fetchLevels response', res)
    if (res?.code === 200 && Array.isArray(res?.data) && res.data.length > 0) {
      levels.value = res.data
      console.log('levels set from API', levels.value)
    }
  } catch (e) { console.warn('fetch levels fail', e) }
  finally { loadingLevels.value = false }
}

async function fetchUserInfo() {
  if (!token.value) return
  try {
    const res = await get('/user/info', { openid: token.value })
    if (res?.code === 200 && res?.data) {
      userInfo.value = res.data
      uni.setStorageSync('userInfo', res.data)
    }
  } catch (e) { console.warn('fetch user info fail', e) }
}

const unpaidCount = ref(0)

async function fetchUnpaidCount() {
  if (!token.value) return
  try {
    const res = await get('/order/unpaid-count', { openid: token.value })
    if (res?.code === 200 && res?.data) {
      unpaidCount.value = res.data.count || 0
    }
  } catch {}
}

onShow(() => {
  if (!isLoggedIn.value) showLogin.value = true
  else { fetchLevels(); checkSigninStatus(); fetchUserInfo(); fetchUnpaidCount() }

})

const benefits = [
  { icon: 'auto_awesome', label: 'AI试戴上限', desc: '每日限额50次' },
  { icon: 'cake', label: '生日礼遇', desc: '专享神秘珠宝礼' },
  { icon: 'local_shipping', label: '全场包邮', desc: '顺丰尊享配送' },
  { icon: 'support_agent', label: '专属客服', desc: '24/7私人顾问' }
]

const recommendations = [
  { name: '永恒之光项链', price: '12,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAHDsHxZR5CDIHz1sM0CPRPYiEZKG3UccusQVQBb_-LCR11wF3xnN3huxtXrr0WEuwEVAmtlFo2Nk81VCkbxrbB2Ad8Sgsd1uSfAgaXhOnk569wFswATReRnZj4FLeRetdaQSgRMt3LCfc2Ezosq9DNX1lg8OdpZNHAbYYOKqiH5KIFcl5sRY486Rr2odN4s-cSIepj1_77GMp9XegSuq5XxBJxCsimifsjhLVMFfLv7YlNGEuJmHTG3Q' },
  { name: '优雅绽放戒指', price: '8,500', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA0d6kOH8GKvyZk-_U5x9JbkFFGwPyezXICfx6-M1jgeR0HmSazSuOHmXRb15am0y-Jj_zS64fi1g5kIeMbaZi8doo6lZnrE4BFfzvDiw_o0Ey8q-iHglo_sYAvb0bn6mnknRwA52iN74zsY6_xuzGp8YAaCarB7jUXOhU2iq6gzShtRcI4nOBNkrQIAIKZzB0NuJwDzlrG_k7bDVxePfz3jzYVUxtMEEZkHB5Yzn7Htf-aaRNqK2pCVw' }
]

const menus = [
  { icon: 'inventory_2', label: '我的订单', color: '#775836', badgeKey: 'unpaidCount' as const },
  { icon: 'favorite', label: '我的收藏', color: '#CF6679' },
  { icon: 'location_on', label: '收货地址', color: '#A67B52' },
  { icon: 'card_giftcard', label: '优惠券', color: '#C9A52D' },
  { icon: 'support_agent', label: '联系客服', color: '#605E5A' },
  { icon: 'settings', label: '设置', color: '#8C9399' }
]
</script>

<template>
  <view class="page">
    <view class="member-card">
      <view class="card-top">
        <view class="card-user">
          <image v-if="userInfo?.avatar" class="card-avatar-img" :src="userInfo.avatar" mode="aspectFill" />
          <view v-else class="card-avatar" />
          <view>
            <text class="card-name">{{ userInfo?.nickname || '嘉怡贵宾' }}</text>
            <view class="card-badge">
              <MsIcon name="diamond" color="#E9C349" size="24rpx" />
              <text class="badge-text">{{ currentLevel?.name || '普通会员' }}</text>
            </view>
          </view>
        </view>
        <MsIcon name="qr_code_2" color="rgba(255,255,255,0.8)" size="48rpx" />
      </view>
      <view class="card-bottom">
        <view>
          <text class="card-meta-label">Membership No.</text>
          <text class="card-meta-no">{{ userInfo?.memberNo || '--------' }}</text>
        </view>
        <text class="card-since" v-if="sinceYear">自 {{ sinceYear }} 年起</text>
      </view>
    </view>

    <view class="points-section">
      <view class="points-header">
        <view>
          <text class="points-label">当前可用积分</text>
          <text class="points-value">{{ (userInfo?.points || 0).toLocaleString() }}</text>
        </view>
        <view class="points-link" @tap="goPoints">
          <text>积分权益</text>
          <MsIcon name="chevron_right" size="28rpx" />
        </view>
      </view>
      <view class="progress-card">
        <view class="progress-row">
          <text class="progress-current">{{ currentLevel?.name || '普通会员' }}</text>
          <text class="progress-next" v-if="nextLevel">下一等级: {{ nextLevel.name }} ({{ nextLevel.minPoints?.toLocaleString() }} 积分)</text>
          <text class="progress-next" v-else-if="nextLevel === null">已达最高等级</text>
          <text class="progress-next" v-else>正在加载等级配置...</text>
        </view>
        <view class="progress-bar"><view class="progress-fill" :style="'width:' + progressPercent + '%'" /></view>
        <text class="progress-hint" v-if="nextLevel">距离升级还需 {{ pointsRemaining.toLocaleString() }} 积分，继续加油！</text>
        <text class="progress-hint" v-else-if="nextLevel === null">您已到达最高等级，感谢支持！</text>
      </view>
    </view>

    <view class="checkin-card">
      <view class="checkin-left">
        <view class="checkin-icon"><MsIcon name="calendar_today" size="40rpx" color="#775836" /></view>
        <view>
          <text class="checkin-title">每日签到</text>
          <text class="checkin-desc">{{ signedIn ? '今日已签到' : '今日签到可领 10 积分' }}</text>
        </view>
      </view>
      <view :class="['checkin-btn', signedIn ? 'checkin-btn-disabled' : '']" @tap="handleSignin">
        <text>{{ signinLoading ? '签到中...' : signedIn ? '今日已签到' : '立即签到' }}</text>
      </view>
    </view>

    <view class="benefits">
      <text class="benefits-title">专属权益</text>
      <view class="benefits-grid">
        <view v-for="(b, i) in benefits" :key="i" class="benefit-item">
          <view class="benefit-icon"><MsIcon :name="b.icon" size="44rpx" color="#775836" /></view>
          <text class="benefit-label">{{ b.label }}</text>
          <text class="benefit-desc">{{ b.desc }}</text>
        </view>
      </view>
    </view>

    <view class="recommend">
      <view class="recommend-header">
        <text class="recommend-title">会员推荐</text>
        <text class="recommend-sub">专属定制</text>
      </view>
      <scroll-view class="recommend-scroll" scroll-x enhanced show-scrollbar="false">
        <view class="recommend-track">
          <view v-for="(r, i) in recommendations" :key="i" class="recommend-card">
            <image class="recommend-img" :src="r.img" mode="aspectFill" />
            <view class="recommend-info">
              <text class="recommend-name">{{ r.name }}</text>
              <text class="recommend-price">¥{{ r.price }}</text>
              <view class="recommend-btn"><text>会员专享</text></view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="menu-list">
      <view v-for="(m, i) in menus" :key="i" class="menu-item" @tap="onMenuTap(m)">
        <view class="menu-left">
          <view class="menu-icon-wrap" :style="'background-color:' + m.color + '20'">
            <MsIcon :name="m.icon" :color="m.color" size="36rpx" />
          </view>
          <text class="menu-label">{{ m.label }}</text>
        </view>
        <view class="menu-right">
          <text v-if="m.badgeKey && unpaidCount > 0" class="menu-badge">{{ unpaidCount }}</text>
          <MsIcon name="chevron_right" size="36rpx" color="#D2C4B8" />
        </view>
      </view>
    </view>

    <view class="logout-btn" @tap="handleLogout"><text>退出登录</text></view>
  </view>


  <!-- Login Overlay -->
  <view v-if="showLogin" class="overlay">
    <view class="login-card">
      <text class="login-title">嘉怡珠宝</text>
      <text class="login-desc">请确认您的微信资料</text>
      <view class="login-avatar-row">
        <button class="avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
          <image class="login-avatar" :src="loginAvatar || defaultAvatar" mode="aspectFill" />
        </button>
      </view>
      <input class="login-nickname" type="nickname" v-model="loginNickname" placeholder="点击获取微信昵称" />
      <button class="login-btn" :disabled="loading || !loginNickname" @tap="handleWxLogin">
        <text>{{ loading ? '登录中...' : '微信用户一键登录' }}</text>
      </button>
      <text class="login-tip">登录即代表同意《用户协议》和《隐私政策》</text>
    </view>
  </view>

  <NavBar :active="4" />
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; padding-top: calc(var(--status-bar-height) + 16rpx); padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); }
.member-card { background: linear-gradient(135deg, #775836, #C8A27A, #E8BF95); margin: 32rpx 32rpx 0; border-radius: 24rpx; padding: 32rpx; position: relative; overflow: hidden; }
.member-card::after { content: ''; position: absolute; top: -20%; right: -10%; width: 300rpx; height: 300rpx; background: radial-gradient(circle, rgba(255,255,255,0.15), transparent 70%); border-radius: 50%; }
.card-top { display: flex; justify-content: space-between; align-items: flex-start; position: relative; z-index: 1; }
.card-user { display: flex; align-items: center; gap: 16rpx; }
.card-avatar, .card-avatar-img { width: 112rpx; height: 112rpx; border-radius: 50%; border: 4rpx solid #E8D5C0; background: rgba(255,255,255,0.2); }
.card-avatar-img { overflow: hidden; }
.card-name { font-size: 36rpx; font-weight: 600; color: #fff; font-family: 'Noto Serif SC', serif; }
.card-badge { display: flex; align-items: center; gap: 4rpx; background: rgba(255,255,255,0.2); padding: 4rpx 16rpx; border-radius: 40rpx; margin-top: 8rpx; }
.badge-text { font-size: 22rpx; color: #fff; font-weight: 500; }
.card-bottom { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 32rpx; position: relative; z-index: 1; }
.card-meta-label { font-size: 18rpx; color: rgba(255,255,255,0.7); text-transform: uppercase; letter-spacing: 2px; }
.card-meta-no { font-size: 28rpx; color: #fff; font-family: monospace; letter-spacing: 2px; display: block; }
.card-since { font-size: 20rpx; color: rgba(255,255,255,0.8); }

.points-section { padding: 48rpx 32rpx 0; }
.points-header { display: flex; justify-content: space-between; align-items: flex-end; }
.points-label { font-size: 20rpx; color: #605E5A; letter-spacing: 1px; }
.points-value { font-size: 56rpx; font-weight: 700; color: #775836; }
.points-link { display: flex; align-items: center; gap: 4rpx; font-size: 22rpx; color: #775836; font-weight: bold; }
.progress-card { background: #fff; border-radius: 24rpx; padding: 24rpx; margin-top: 24rpx; border: 2rpx solid rgba(210,196,184,0.3); }
.progress-row { display: flex; justify-content: space-between; font-size: 20rpx; color: #605E5A; margin-bottom: 12rpx; }
.progress-current { font-weight: 600; color: #1C1B1B; }
.progress-bar { height: 12rpx; background-color: #F0EDED; border-radius: 12rpx; overflow: hidden; }
.progress-fill { height: 100%; background-color: #775836; border-radius: 12rpx; }
.progress-hint { font-size: 18rpx; color: #605E5A; font-style: italic; margin-top: 8rpx; }

.checkin-card { display: flex; align-items: center; justify-content: space-between; margin: 32rpx 32rpx 0; background: rgba(200,162,122,0.1); border-radius: 24rpx; padding: 24rpx; border: 2rpx solid rgba(200,162,122,0.2); }
.checkin-left { display: flex; align-items: center; gap: 16rpx; }
.checkin-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(200,162,122,0.2); display: flex; align-items: center; justify-content: center; }
.checkin-title { font-size: 28rpx; font-weight: bold; color: #775836; }
.checkin-desc { font-size: 20rpx; color: #605E5A; }
.checkin-btn { background-color: #775836; color: #fff; padding: 12rpx 40rpx; border-radius: 60rpx; font-size: 22rpx; font-weight: bold; }
.checkin-btn-disabled { background-color: #ccc; }

.benefits { padding: 48rpx 32rpx; }
.benefits-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; margin-bottom: 24rpx; }
.benefits-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.benefit-item { background: #fff; border-radius: 24rpx; padding: 24rpx; text-align: center; border: 2rpx solid rgba(210,196,184,0.2); }
.benefit-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(232,213,192,0.2); display: flex; align-items: center; justify-content: center; margin: 0 auto; }
.benefit-label { font-size: 24rpx; font-weight: 600; color: #1C1B1B; display: block; margin-top: 12rpx; }
.benefit-desc { font-size: 20rpx; color: #605E5A; display: block; margin-top: 4rpx; }

.recommend { padding: 0 32rpx 48rpx; }
.recommend-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.recommend-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }
.recommend-sub { font-size: 20rpx; color: #605E5A; font-style: italic; }
.recommend-scroll { overflow: hidden; margin: 0 -32rpx; padding: 0 32rpx; }
.recommend-track { display: flex; gap: 24rpx; }
.recommend-card { min-width: 340rpx; background: #fff; border-radius: 24rpx; overflow: hidden; border: 2rpx solid rgba(210,196,184,0.2); }
.recommend-img { width: 100%; height: 320rpx; }
.recommend-info { padding: 16rpx; }
.recommend-name { font-size: 24rpx; color: #1C1B1B; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.recommend-price { font-size: 28rpx; color: #775836; font-weight: bold; display: block; margin-top: 8rpx; }
.recommend-btn { border: 2rpx solid #775836; color: #775836; font-size: 20rpx; font-weight: bold; text-align: center; padding: 8rpx; border-radius: 8rpx; margin-top: 12rpx; }

.menu-list { margin: 0 32rpx 48rpx; border-radius: 24rpx; overflow: hidden; background: #fff; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 32rpx; border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.menu-item:last-child { border-bottom: none; }
.menu-left { display: flex; align-items: center; gap: 24rpx; }
.menu-icon-wrap { width: 72rpx; height: 72rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.menu-label { font-size: 26rpx; color: #1C1B1B; font-weight: 500; }
.menu-right { display: flex; align-items: center; gap: 12rpx; }
.menu-badge { background: #CF6679; color: #fff; font-size: 20rpx; font-weight: bold; min-width: 36rpx; height: 36rpx; line-height: 36rpx; text-align: center; border-radius: 18rpx; padding: 0 8rpx; }

.logout-btn { margin: 0 32rpx 48rpx; padding: 32rpx; text-align: center; font-size: 26rpx; color: #999; border-radius: 24rpx; background: #fff; border: 2rpx solid rgba(210,196,184,0.2); }
.overlay { position: fixed; z-index: 100; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.login-card { background: #fff; border-radius: 32rpx; padding: 40rpx 48rpx; margin: 0 48rpx; text-align: center; width: 560rpx; }
.login-title { font-size: 36rpx; font-weight: 700; color: #775836; font-family: 'Noto Serif SC', serif; }
.login-desc { font-size: 24rpx; color: #605E5A; margin-top: 8rpx; }
.login-avatar-row { display: flex; justify-content: center; margin-top: 28rpx; }
.avatar-btn { width: 128rpx; height: 128rpx; border-radius: 50%; padding: 0; margin: 0; border: 4rpx solid #E0D8D0; background: #f5f0eb; line-height: 0; overflow: hidden; }
.avatar-btn::after { border: none; }
.login-avatar { width: 100%; height: 100%; }
.login-nickname { border: 2rpx solid #E0D8D0; border-radius: 60rpx; padding: 24rpx 32rpx; font-size: 26rpx; width: 100%; box-sizing: border-box; margin-top: 28rpx; color: #1C1B1B; min-height: 80rpx; line-height: 1.4; }
.login-btn { background: #07C160; color: #fff; font-size: 28rpx; font-weight: bold; padding: 24rpx 0; border-radius: 60rpx; width: 100%; margin-top: 36rpx; }
.login-btn[disabled] { opacity: 0.6; }
.login-tip { font-size: 20rpx; color: #999; margin-top: 20rpx; }
</style>

