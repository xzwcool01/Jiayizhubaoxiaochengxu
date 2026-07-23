<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getAiWearQuota, generateAiWear, publishAiWear } from '@/api/ai-wear'

const productId = ref(0)
const categoryId = ref(0)
const quota = ref({ remaining: 10, total: 10 })
const uploading = ref(false)
const generating = ref(false)

const userPhotoUrl = ref('')
const resultUrl = ref('')
const hasResult = ref(false)
const currentRecordId = ref(0)
const showPublishDialog = ref(false)

const hasPhoto = computed(() => !!userPhotoUrl.value)
const stage = ref<'idle' | 'result'>('idle')

const BASE_URL = 'http://localhost:8080'
function toFullUrl(url: string) {
  if (!url) return url
  if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('data:')) return url
  if (url.startsWith('/')) return BASE_URL + url
  return url
}
const fullResultUrl = computed(() => toFullUrl(resultUrl.value))

const demoBefore = 'https://lh3.googleusercontent.com/aida-public/AB6AXuDv5Ip4FWlT6mzxrJBm0jcsPTu97rP9G3mDyWmFdvuE2F2KkUDrrGbAtQ6DcrKrQYUfGNVFM8ELeRUdoiBkzPMBLHHVK7vWROQfCBktKcnnACNSQpKZ4BfGXxmIaS9JonfA2hRomIpNTUSJKxg1jyyf2G0q247LGRdpbipieO9BUKMkPcaiPxKVLf51k62K35uyJPI9lXkcwS3VRf4ohpXiHgqW0r2lD-dXFnp8GYeh3iyiKUKvfeh_6Q'
const demoAfter = 'https://lh3.googleusercontent.com/aida-public/AB6AXuA8-MWBOJpGIyzR6JvfrYrCmz--ilh2pOg8xB6APFB6LASa9-NO7glWyW8A3Q10yfCD7d4prR0DWXQ_IuGDQAsqqyRIJa-xNhyxWJgiiHt0fcWuSXrWaq6I4gOW9CM9jUgbnMbO9K7Uf1GzQHzW-axmV7JyOBH-gD6VbMoGabrmaLFR1NsTXgxA_yopGrnp3cXmgFMwQuCFTM6F-jdsh8faCveO_DEjGLicwSe_joi6WFADxgu9l0gz8g'

const guideMap: Record<number, { tip: string; pose: string }> = {
  1: { tip: '戒指', pose: '请拍摄手部特写，手指自然展开，清晰展示手指根部' },
  2: { tip: '项链', pose: '请拍摄颈部到锁骨区域正面照，保持肩颈舒展' },
  3: { tip: '耳饰', pose: '请拍摄面部侧面，露出耳朵，角度清晰' },
  4: { tip: '手链', pose: '请拍摄手腕或手臂区域，正面朝上' },
  5: { tip: '胸针', pose: '请拍摄胸前衣物区域，平坦展示' },
  6: { tip: '套装', pose: '请拍摄上半身正面照，全身搭配展示' },
}

const guideText = computed(() => {
  const g = guideMap[categoryId.value]
  return g ? `试戴${g.tip} · ${g.pose}` : '请上传一张正面半身照'
})

onLoad((options) => {
  productId.value = Number(options?.productId) || 0
  categoryId.value = Number(options?.categoryId) || 0
  fetchQuota()
})

async function fetchQuota() {
  try {
    const res = await getAiWearQuota()
    if (res.code === 200) quota.value = res.data as any
  } catch {}
}

async function handleCamera() {
  await pickImage('camera')
}

async function handleAlbum() {
  await pickImage('album')
}

async function pickImage(sourceType: 'camera' | 'album') {
  if (quota.value.remaining <= 0) {
    uni.showToast({ title: '今日AI试戴次数已用完', icon: 'none' })
    return
  }
  try {
    const res = await uni.chooseImage({ count: 1, sizeType: ['compressed'], sourceType: [sourceType] })
    if (!res.tempFilePaths || !res.tempFilePaths.length) return
    userPhotoUrl.value = res.tempFilePaths[0]
  } catch {}
}

async function handleGenerate() {
  if (!userPhotoUrl.value || !productId.value) return
  if (quota.value.remaining <= 0) {
    uni.showToast({ title: '今日AI试戴次数已用完', icon: 'none' })
    return
  }
  generating.value = true
  try {
    const res = await generateAiWear(productId.value, userPhotoUrl.value, categoryId.value || undefined)
    if (res.code === 200) {
      resultUrl.value = res.data.resultUrl
      quota.value.remaining = res.data.remaining
      hasResult.value = true
      currentRecordId.value = res.data.recordId
      stage.value = 'result'
      showPublishDialog.value = true
    } else {
      uni.showToast({ title: res.message || '生成失败', icon: 'none' })
    }
  } catch {
    uni.showToast({ title: '网络错误', icon: 'none' })
  } finally {
    generating.value = false
  }
}

function resetPhoto() {
  userPhotoUrl.value = ''
  resultUrl.value = ''
  hasResult.value = false
  stage.value = 'idle'
}

async function handlePublish() {
  if (!currentRecordId.value) return
  try {
    const res = await publishAiWear(currentRecordId.value)
    if (res.code === 200) {
      resultUrl.value = res.data.resultUrl
      uni.showToast({ title: '已展示到发现页', icon: 'none' })
    } else {
      uni.showToast({ title: res.message || '发布失败', icon: 'none' })
    }
  } catch {
    uni.showToast({ title: '网络错误', icon: 'none' })
  } finally {
    showPublishDialog.value = false
  }
}

function handleDismissPublish() {
  showPublishDialog.value = false
}

function previewBefore() {
  if (userPhotoUrl.value) {
    uni.previewImage({ urls: [userPhotoUrl.value] })
  }
}

function previewResult() {
  if (fullResultUrl.value) {
    uni.previewImage({ urls: [fullResultUrl.value] })
  }
}

function handleSave() {
  const url = hasResult.value ? fullResultUrl.value : demoAfter
  if (!url) return
  uni.showLoading({ title: '保存中...' })
  uni.downloadFile({
    url,
    success: (res) => {
      if (res.statusCode !== 200) { uni.hideLoading(); return }
      uni.saveImageToPhotosAlbum({
        filePath: res.tempFilePath,
        success: () => { uni.hideLoading(); uni.showToast({ title: '已保存到相册' }) },
        fail: () => { uni.hideLoading(); uni.showToast({ title: '保存失败', icon: 'none' }) }
      })
    },
    fail: () => { uni.hideLoading(); uni.showToast({ title: '下载失败', icon: 'none' }) }
  })
}
</script>

<template>
  <view class="page">
    <view class="bg-deco">
      <view class="bg-blob top-right" />
      <view class="bg-blob mid-left" />
    </view>

    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()">
        <MsIcon name="arrow_back" size="40rpx" color="#775836" />
      </view>
    </view>

    <view class="main-content">
      <view class="intro">
        <text class="intro-title">AI 虚拟试戴</text>
        <text class="intro-desc">瞬息之间，遇见您的璀璨时刻</text>
      </view>

      <!-- Category Guidance -->
      <view class="guide-banner">
        <MsIcon name="auto_awesome" size="24rpx" color="#775836" />
        <text class="guide-text">{{ guideText }}</text>
      </view>

      <!-- Upload Area (always visible, resets to blank after selection) -->
      <view class="upload-section">
        <view class="upload-area">
          <view class="upload-inner">
            <view class="upload-text">
              <text class="upload-title">点击拍照或上传照片</text>
              <text class="upload-en">Upload photo to see wearing effect</text>
            </view>
            <view class="upload-btns">
              <view class="upload-btn-item" @tap="handleCamera">
                <view class="btn-icon-circle"><MsIcon name="photo_camera" size="36rpx" color="#775836" /></view>
                <text class="btn-label">拍照</text>
              </view>
              <view class="upload-btn-item" @tap="handleAlbum">
                <view class="btn-icon-circle"><MsIcon name="collections" size="36rpx" color="#775836" /></view>
                <text class="btn-label">相册选择</text>
              </view>
            </view>
          </view>
        </view>
        <view class="sparkle"><MsIcon name="auto_awesome" size="36rpx" color="#C9A52D" /></view>
      </view>

      <!-- Re-select button (visible when photo selected) -->
      <view class="reselect-section" v-if="hasPhoto">
        <view class="reselect-btn" @tap="resetPhoto">
          <MsIcon name="refresh" size="24rpx" color="#775836" />
          <text>重新选择</text>
        </view>
      </view>

      <!-- Before/After Comparison -->
      <view class="compare-section">
        <view class="compare-header">
          <text class="compare-title">试戴演示</text>
        </view>
        <view class="compare-grid">
          <view class="compare-card">
            <image class="compare-img" :src="hasPhoto ? userPhotoUrl : demoBefore" mode="aspectFill" @tap="previewBefore" />
            <view class="compare-label"><text>试戴前</text></view>
          </view>
          <view class="compare-card after">
            <image class="compare-img" :src="stage === 'result' ? fullResultUrl : demoAfter" mode="aspectFill" @tap="previewResult" />
            <view class="compare-label after-label">
              <MsIcon name="auto_awesome" size="16rpx" color="#775836" />
              <text>试戴后</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Quota Info -->
      <view class="quota-card">
        <view class="quota-left">
          <view class="quota-icon"><MsIcon name="bolt" size="24rpx" color="#605E5A" /></view>
          <view>
            <text class="quota-title">每日额度</text>
            <text class="quota-desc">今日剩余 {{ quota.remaining }} 次免费生成</text>
          </view>
        </view>
      </view>

      <!-- Generate Button (visible when photo selected) -->
      <view class="action-wrap" v-if="hasPhoto">
        <button class="action-btn" @tap="handleGenerate" :disabled="generating || quota.remaining <= 0">
          <MsIcon name="auto_awesome" size="28rpx" color="#fff" />
          <text v-if="generating">AI正在生成...</text>
          <text v-else-if="hasResult">重新生成效果</text>
          <text v-else>立即生成效果</text>
        </button>
      </view>

      <!-- Save -->
      <view class="save-wrap" v-if="hasResult">
        <button class="save-btn" @tap="handleSave">
          <MsIcon name="download" size="28rpx" color="#775836" />
          <text>保存效果到相册</text>
        </button>
      </view>
    </view>

    <!-- Generating overlay -->
    <view v-if="generating" class="loading-overlay">
      <view class="loading-box">
        <view class="loading-spinner" />
        <text class="loading-text">AI正在生成试戴效果...</text>
        <text class="loading-sub">请稍候，即将为您呈现</text>
      </view>
    </view>

    <!-- Publish dialog -->
    <view v-if="showPublishDialog" class="loading-overlay" @tap="handleDismissPublish">
      <view class="publish-dialog" @tap.stop>
        <view class="publish-icon"><MsIcon name="auto_awesome" size="40rpx" color="#C9A52D" /></view>
        <text class="publish-title">试戴效果已生成</text>
        <text class="publish-desc">是否在发现页 AI 试戴秀中展示？</text>
        <view class="publish-btns">
          <button class="publish-btn secondary" @tap="handleDismissPublish">暂时不展示</button>
          <button class="publish-btn primary" @tap="handlePublish">在发现页展示</button>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; position: relative; overflow-x: hidden; padding-top: calc(var(--status-bar-height) + 8rpx); }
.bg-deco { position: fixed; inset: 0; pointer-events: none; overflow: hidden; z-index: 0; }
.bg-blob { position: absolute; border-radius: 50%; }
.bg-blob.top-right { top: -96rpx; right: -96rpx; width: 384rpx; height: 384rpx; background: radial-gradient(circle, rgba(232,213,192,0.2), transparent 70%); }
.bg-blob.mid-left { top: 50%; left: -96rpx; width: 256rpx; height: 256rpx; background: radial-gradient(circle, rgba(233,195,73,0.1), transparent 70%); }
.top-bar { display: flex; align-items: center; padding: 0 32rpx; height: 88rpx; background: transparent; }
.back-btn { width: 64rpx; height: 64rpx; display: flex; align-items: center; justify-content: center; }
.main-content { position: relative; z-index: 10; padding: 0 32rpx 48rpx; max-width: 375px; margin: 0 auto; }
.intro { text-align: center; margin-bottom: 20rpx; padding-top: 8rpx; }
.intro-title { font-size: 28rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; display: block; }
.intro-desc { font-size: 14px; color: #605E5A; opacity: 0.8; display: block; margin-top: 4rpx; }

.guide-banner { display: flex; align-items: center; gap: 8px; background: rgba(200,162,122,0.1); border-radius: 12rpx; padding: 16rpx 20rpx; margin-bottom: 24rpx; border: 2rpx solid rgba(200,162,122,0.2); }
.guide-text { font-size: 12px; color: #775836; flex: 1; line-height: 1.5; }

.upload-section { position: relative; margin-bottom: 32rpx; }
.upload-area { border: 2rpx dashed #D2C4B8; border-radius: 12rpx; background: rgba(255,255,255,0.5); overflow: hidden; transition: all 0.2s; }
.upload-area:active { transform: scale(0.98); background: rgba(246,243,242,0.8); }
.upload-inner { display: flex; flex-direction: column; align-items: center; gap: 40rpx; padding: 88rpx 40rpx; }
.upload-text { text-align: center; }
.upload-title { font-size: 30rpx; font-weight: 600; color: #775836; display: block; }
.upload-en { font-size: 14px; color: #605E5A; display: block; margin-top: 8rpx; letter-spacing: 0.05em; }
.upload-btns { display: flex; gap: 16px; width: 100%; }
.upload-btn-item { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 28rpx; background: rgba(200,162,122,0.1); border-radius: 12rpx; border: 2rpx solid rgba(119,88,54,0.2); }
.upload-btn-item:active { transform: scale(0.95); }
.btn-icon-circle { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(200,162,122,0.2); display: flex; align-items: center; justify-content: center; margin-bottom: 12rpx; }
.btn-label { font-size: 14px; font-weight: 700; color: #775836; letter-spacing: 0.05em; }
.sparkle { position: absolute; top: -8rpx; right: -8rpx; animation: sparkle-pulse 2s infinite; }
@keyframes sparkle-pulse { 0% { opacity: 0.6; transform: scale(1); } 50% { opacity: 1; transform: scale(1.15); } 100% { opacity: 0.6; transform: scale(1); } }

.reselect-section { display: flex; justify-content: center; margin-bottom: 16rpx; }
.reselect-btn { display: flex; align-items: center; gap: 6px; padding: 12rpx 32rpx; border-radius: 40rpx; border: 2rpx solid #D9D2CC; color: #775836; background: #fff; font-size: 13px; }
.reselect-btn:active { background: #F6F3F2; }

.publish-dialog { background: #fff; border-radius: 32rpx; padding: 48rpx 40rpx 32rpx; width: 560rpx; text-align: center; }
.publish-icon { margin-bottom: 16rpx; }
.publish-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; display: block; }
.publish-desc { font-size: 14px; color: #605E5A; display: block; margin-top: 8rpx; margin-bottom: 32rpx; }
.publish-btns { display: flex; gap: 12px; }
.publish-btn { flex: 1; height: 80rpx; border-radius: 40rpx; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 600; border: none; }
.publish-btn.primary { background: linear-gradient(135deg, #775836, #C8A27A); color: #fff; }
.publish-btn.primary:active { transform: scale(0.95); }
.publish-btn.secondary { background: #F6F3F2; color: #775836; }
.publish-btn.secondary:active { transform: scale(0.95); }

.compare-section { margin-bottom: 32rpx; }
.compare-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16rpx; }
.compare-title { font-size: 22rpx; font-weight: 600; color: #1C1B1B; }
.compare-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.compare-card { background: #fff; border-radius: 12rpx; overflow: hidden; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06); }
.compare-card.after { border: 2rpx solid rgba(200,162,122,0.2); }
.compare-img { width: 100%; height: 340rpx; background: #F0EDED; display: block; }
.compare-label { padding: 8rpx; text-align: center; font-size: 12px; color: #605E5A; text-transform: uppercase; letter-spacing: 2px; }
.after-label { background: rgba(200,162,122,0.1); color: #775836; font-weight: 700; display: flex; align-items: center; justify-content: center; gap: 4rpx; }

.quota-card { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 24rpx; background: rgba(255,255,255,0.6); border-radius: 12rpx; border: 2rpx solid rgba(217,210,204,0.3); margin-bottom: 32rpx; }
.quota-left { display: flex; align-items: center; gap: 12px; }
.quota-icon { width: 40rpx; height: 40rpx; border-radius: 50%; background: #E6E2DD; display: flex; align-items: center; justify-content: center; }
.quota-title { font-size: 15px; font-weight: 700; color: #1C1B1B; display: block; }
.quota-desc { font-size: 12px; color: #605E5A; display: block; }

.action-wrap { margin-top: 32rpx; }
.action-btn { width: 100%; height: 96rpx; background: linear-gradient(135deg, #775836, #C8A27A); color: #fff; border-radius: 60rpx; display: flex; align-items: center; justify-content: center; gap: 8px; font-size: 22rpx; font-weight: 600; font-family: 'Noto Serif SC', serif; box-shadow: 0 8rpx 24rpx rgba(119,88,54,0.2); border: none; }
.action-btn:active { transform: scale(0.95); }
.action-btn[disabled] { opacity: 0.6; }

.save-wrap { margin-top: 16rpx; }
.save-btn { width: 100%; height: 72rpx; background: #fff; color: #775836; border-radius: 60rpx; display: flex; align-items: center; justify-content: center; gap: 8px; font-size: 14px; font-weight: 500; border: 2rpx solid #E8D5C0; }
.save-btn:active { background: #F6F3F2; }

.loading-overlay { position: fixed; z-index: 99; top: 0; left: 0; right: 0; bottom: 0; background: rgba(28,27,27,0.6); display: flex; align-items: center; justify-content: center; }
.loading-box { background: #fff; border-radius: 32rpx; padding: 64rpx 80rpx; text-align: center; }
.loading-spinner { width: 80rpx; height: 80rpx; border: 6rpx solid #F0EDED; border-top-color: #775836; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text { font-size: 28rpx; color: #1C1B1B; font-weight: 600; display: block; margin-top: 24rpx; }
.loading-sub { font-size: 22rpx; color: #605E5A; display: block; margin-top: 8rpx; }
</style>
