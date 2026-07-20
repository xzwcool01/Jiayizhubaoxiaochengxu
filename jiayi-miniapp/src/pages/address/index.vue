<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAddressList, addAddress, updateAddress, setDefaultAddress, removeAddress, type UmsUserAddress } from '@/api/address'

const list = ref<UmsUserAddress[]>([])
const showForm = ref(false)
const editing = ref<UmsUserAddress | null>(null)
const form = ref<UmsUserAddress>({ name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false })
const saving = ref(false)

async function fetchData() {
  if (!uni.getStorageSync('token')) return
  const res = await getAddressList()
  if (res.code === 200) list.value = res.data || []
}

function openAdd() {
  editing.value = null
  form.value = { name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: false }
  showForm.value = true
}

function openEdit(addr: UmsUserAddress) {
  editing.value = addr
  form.value = { ...addr }
  showForm.value = true
}

async function save() {
  if (!form.value.name || !form.value.phone || !form.value.detail) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' }); return
  }
  saving.value = true
  try {
    if (editing.value) {
      form.value.id = editing.value.id
      await updateAddress(form.value)
    } else {
      await addAddress(form.value)
    }
    showForm.value = false
    uni.showToast({ title: '保存成功', icon: 'success' })
    fetchData()
  } catch { uni.showToast({ title: '保存失败', icon: 'none' }) }
  finally { saving.value = false }
}

async function handleDefault(addr: UmsUserAddress) {
  if (addr.id) {
    await setDefaultAddress(addr.id)
    fetchData()
  }
}

async function handleRemove(addr: UmsUserAddress) {
  uni.showModal({ title: '提示', content: '确定删除该地址？', success: async (res) => {
    if (res.confirm && addr.id) {
      await removeAddress(addr.id)
      fetchData()
    }
  }})
}

function selectAndBack(addr: UmsUserAddress) {
  const pages = getCurrentPages()
  const prev = pages[pages.length - 2]
  if (prev && prev.route?.includes('confirm-order')) {
    uni.setStorageSync('selectedAddress', JSON.stringify(addr))
    uni.navigateBack()
  }
}

onShow(fetchData)
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="top-left">
        <view class="back-btn" @tap="uni.navigateBack()"><text>←</text></view>
        <text class="page-title">收货地址</text>
      </view>
      <text class="add-btn" @tap="openAdd">+ 新增</text>
    </view>

    <view v-if="list.length === 0" class="empty"><text>暂无收货地址</text></view>
    <view v-else class="addr-list">
      <view v-for="addr in list" :key="addr.id" class="addr-card" @tap="selectAndBack(addr)">
        <view class="addr-top">
          <text class="addr-name">{{ addr.name }}</text>
          <text class="addr-phone">{{ addr.phone }}</text>
          <text v-if="addr.isDefault" class="default-tag">默认</text>
        </view>
        <text class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</text>
        <view class="addr-actions">
          <view :class="['default-btn', addr.isDefault ? 'is-default' : '']" @tap.stop="handleDefault(addr)">设为默认</view>
          <view class="action-btn" @tap.stop="openEdit(addr)">编辑</view>
          <view class="action-btn danger" @tap.stop="handleRemove(addr)">删除</view>
        </view>
      </view>
    </view>

    <!-- Form overlay -->
    <view v-if="showForm" class="overlay" @tap="showForm = false">
      <view class="form-card" @tap.stop>
        <text class="form-title">{{ editing ? '编辑地址' : '新增地址' }}</text>
        <input class="form-input" v-model="form.name" placeholder="收件人姓名" />
        <input class="form-input" v-model="form.phone" placeholder="手机号" type="number" maxlength="11" />
        <view class="form-row">
          <input class="form-input flex-1" v-model="form.province" placeholder="省" />
          <input class="form-input flex-1" v-model="form.city" placeholder="市" />
          <input class="form-input flex-1" v-model="form.district" placeholder="区" />
        </view>
        <input class="form-input" v-model="form.detail" placeholder="详细地址" />
        <view class="default-row">
          <text>设为默认地址</text>
          <view :class="['toggle', form.isDefault ? 'on' : '']" @tap="form.isDefault = !form.isDefault"><view class="toggle-knob" /></view>
        </view>
        <view class="form-actions">
          <view class="form-btn cancel" @tap="showForm = false">取消</view>
          <view class="form-btn save" @tap="save">{{ saving ? '保存中...' : '保存' }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; justify-content: space-between; padding: 32rpx; height: 112rpx; }
.top-left { display: flex; align-items: center; gap: 20rpx; }
.back-btn { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; }
.back-btn text { font-size: 36rpx; color: #1C1B1B; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; }
.add-btn { font-size: 28rpx; color: #775836; font-weight: bold; }
.empty { display: flex; align-items: center; justify-content: center; height: 400rpx; color: #999; font-size: 28rpx; }
.addr-list { padding: 0 32rpx; }
.addr-card { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 20rpx; }
.addr-top { display: flex; align-items: center; gap: 16rpx; }
.addr-name { font-size: 30rpx; font-weight: 600; color: #1C1B1B; }
.addr-phone { font-size: 26rpx; color: #605E5A; }
.default-tag { font-size: 20rpx; color: #fff; background: #775836; padding: 4rpx 12rpx; border-radius: 8rpx; }
.addr-detail { font-size: 26rpx; color: #605E5A; margin-top: 12rpx; display: block; }
.addr-actions { display: flex; gap: 20rpx; margin-top: 16rpx; padding-top: 16rpx; border-top: 2rpx solid #f0f0f0; }
.default-btn { font-size: 24rpx; color: #775836; padding: 6rpx 16rpx; border: 2rpx solid #775836; border-radius: 12rpx; }
.default-btn.is-default { background: #775836; color: #fff; }
.action-btn { font-size: 24rpx; color: #605E5A; padding: 6rpx 16rpx; }
.action-btn.danger { color: #c0836a; }
.overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.form-card { background: #fff; border-radius: 32rpx; padding: 40rpx; width: 640rpx; max-height: 80vh; overflow-y: auto; }
.form-title { font-size: 34rpx; font-weight: 600; color: #1C1B1B; margin-bottom: 28rpx; display: block; }
.form-input { border: 2rpx solid #E0D8D0; border-radius: 16rpx; padding: 20rpx 24rpx; font-size: 26rpx; width: 100%; box-sizing: border-box; margin-bottom: 20rpx; color: #1C1B1B; }
.form-row { display: flex; gap: 16rpx; }
.flex-1 { flex: 1; }
.default-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28rpx; font-size: 26rpx; color: #1C1B1B; }
.toggle { width: 80rpx; height: 44rpx; border-radius: 44rpx; background: #D9D2CC; position: relative; transition: background 0.2s; }
.toggle.on { background: #775836; }
.toggle-knob { width: 36rpx; height: 36rpx; border-radius: 50%; background: #fff; position: absolute; top: 4rpx; left: 4rpx; transition: left 0.2s; }
.toggle.on .toggle-knob { left: 40rpx; }
.form-actions { display: flex; gap: 20rpx; }
.form-btn { flex: 1; text-align: center; padding: 20rpx; border-radius: 60rpx; font-size: 28rpx; font-weight: bold; }
.form-btn.cancel { border: 2rpx solid #D9D2CC; color: #605E5A; }
.form-btn.save { background: #775836; color: #fff; }
</style>