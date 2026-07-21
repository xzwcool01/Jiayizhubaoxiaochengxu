<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

async function checkUnpaid() {
  const openid = uni.getStorageSync('token')
  if (!openid) return
  try {
    const res = await uni.request({
      url: `http://localhost:8080/api/order/unpaid-count?openid=${openid}`,
      method: 'GET'
    })
    const data = res.data as any
    if (data?.code === 200 && data.data?.count > 0) {
      uni.showModal({
        title: '待支付提醒',
        content: `您有 ${data.data.count} 笔订单未支付，是否前往支付？`,
        confirmText: '去支付',
        cancelText: '稍后',
        success: (r) => {
          if (r.confirm) {
            uni.navigateTo({ url: '/pages/order/list' })
          }
        }
      })
    }
  } catch {}
}

onLaunch(() => { console.log('App Launch') })
onShow(() => { checkUnpaid() })
onHide(() => { console.log('App Hide') })
</script>
<style>
page { background-color: #F8F6F3; color: #333; font-size: 28rpx; }
</style>
