<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
onLaunch(() => { console.log('App Launch') })
onShow(() => { console.log('App Show') })
onHide(() => { console.log('App Hide') })
</script>
<style>
page { background-color: #F8F6F3; color: #333; font-size: 28rpx; }
</style>
