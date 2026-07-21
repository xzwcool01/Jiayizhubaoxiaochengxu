<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, shipOrder, trackOrder, previewWaybill, type AdminOrderVO } from '@/api/order'
import QRCode from 'qrcode'

const list = ref<AdminOrderVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 20, userName: '', status: undefined as number | undefined })
const shipDialogVisible = ref(false)
const shipForm = reactive({ orderId: 0, expressCompany: '顺丰速运', trackingNo: '' })
const shipping = ref(false)
const shipAddressInfo = ref('')
const trackDialogVisible = ref(false)
const trackData = ref<any>(null)
const trackingLoading = ref(false)
const currentTrackNo = ref('')
const currentPhone = ref('')
const previewDialogVisible = ref(false)
const previewData = ref<any>(null)
const previewLoading = ref(false)
const previewWaybillNo = ref('')
const previewQrCode = ref('')

const statusMap: Record<number, string> = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已关闭' }
const statusColors: Record<number, string> = { 0: '#e6a23c', 1: '#409eff', 2: '#67c23a', 3: '#909399', 4: '#c0c4cc' }
const statusOptions = [
  { value: undefined, label: '全部状态' },
  { value: 0, label: '待付款' },
  { value: 1, label: '待发货' },
  { value: 2, label: '待收货' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已关闭' }
]

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: query.page, size: query.size }
    if (query.userName.trim()) params.userName = query.userName.trim()
    if (query.status !== undefined) params.status = query.status
    const res = await getOrderList(params)
    if (res.code === 200) { list.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

function onSearch() { query.page = 1; fetchData() }
function onReset() { query.userName = ''; query.status = undefined; query.page = 1; fetchData() }

function formatTime(t?: string) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }

function parseAddress(raw?: string): { name: string; phone: string; address: string } | null {
  if (!raw || raw === '{}') return null
  try {
    const a = JSON.parse(raw)
    if (!a || !a.name) return null
    return {
      name: a.name || '',
      phone: a.phone || '',
      address: [a.province, a.city, a.district, a.detail].filter(Boolean).join('')
    }
  } catch { return null }
}

function parseSpecs(raw?: string): string {
  if (!raw) return ''
  try { const arr = JSON.parse(raw); return Array.isArray(arr) ? arr.map((s: any) => s.value || s).join(' / ') : '' } catch { return '' }
}

function openShipDialog(row: AdminOrderVO) {
  shipForm.orderId = row.id
  shipForm.expressCompany = '顺丰速运'
  shipForm.trackingNo = ''
  const a = parseAddress(row.addressSnapshot)
  shipAddressInfo.value = a ? [a.name, a.phone, a.address].filter(Boolean).join(' ') : '（无收货信息）'
  shipDialogVisible.value = true
}

async function handleShip() {
  if (!shipForm.trackingNo.trim()) { ElMessage.warning('请输入运单号'); return }
  shipping.value = true
  try {
    const res = await shipOrder({ orderId: shipForm.orderId, expressCompany: shipForm.expressCompany, trackingNo: shipForm.trackingNo.trim() })
    if (res.code === 200) {
      ElMessage.success('发货成功')
      shipDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } finally { shipping.value = false }
}

async function handleAutoShip() {
  shipping.value = true
  try {
    const res = await shipOrder({ orderId: shipForm.orderId, expressCompany: '顺丰速运', trackingNo: '' })
    if (res.code === 200) {
      ElMessage.success('顺丰运单已自动生成，发货成功')
      shipDialogVisible.value = false
      fetchData()
      if (res.data) {
        const list = res.data.waybillNoInfoList as Array<{ waybillNo: string }> | undefined
        if (list?.length) {
          previewWaybillNo.value = list[0].waybillNo
          previewData.value = res.data
          previewQrCode.value = ''
          previewDialogVisible.value = true
          await nextTick()
          await renderQrCode()
        }
      }
    } else {
      ElMessage.error(res.message || '自动生成失败')
    }
  } finally { shipping.value = false }
}

function copyAddressInfo() {
  if (shipAddressInfo.value) {
    navigator.clipboard.writeText(shipAddressInfo.value).then(() => ElMessage.success('已复制收货信息'))
  }
}

async function openTrackDialog(row: AdminOrderVO) {
  currentTrackNo.value = row.trackingNo || ''
  const a = parseAddress(row.addressSnapshot)
  currentPhone.value = a?.phone || ''
  trackData.value = null
  trackDialogVisible.value = true
  await fetchTrack()
}

async function fetchTrack() {
  if (!currentTrackNo.value) return
  trackingLoading.value = true
  try {
    const res = await trackOrder({ trackingNo: currentTrackNo.value, phone: currentPhone.value })
    if (res.code === 200) {
      trackData.value = res.data
    } else {
      ElMessage.error(res.message || '查询物流失败')
    }
  } catch {
    ElMessage.error('物流查询异常')
  } finally {
    trackingLoading.value = false
  }
}

function formatTrackTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

async function openPreviewDialog(row: AdminOrderVO) {
  previewWaybillNo.value = row.trackingNo || ''
  previewData.value = null
  previewQrCode.value = ''
  previewDialogVisible.value = true
  if (!previewWaybillNo.value) return
  previewLoading.value = true
  try {
    const res = await previewWaybill({ waybillNo: previewWaybillNo.value })
    if (res.code === 200) {
      previewData.value = res.data
      await nextTick()
      await renderQrCode()
    } else {
      ElMessage.error(res.message || '获取面单失败')
    }
  } catch {
    ElMessage.error('面单查询异常')
  } finally {
    previewLoading.value = false
  }
}

async function renderQrCode() {
  const canvas = document.getElementById('waybill-qrcode') as HTMLCanvasElement
  if (!canvas || !previewData.value) return
  const codeStr = previewData.value.twoDimensionCode || previewWaybillNo.value
  try {
    await QRCode.toCanvas(canvas, codeStr, { width: 180, margin: 1 })
  } catch {
    previewQrCode.value = ''
  }
}

function previewField(label: string, value?: string) {
  if (!value) return null
  return { label, value }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>订单管理</h2></div>

    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" @keyup.enter="onSearch">
        <el-form-item label="用户">
          <el-input v-model="query.userName" placeholder="输入用户昵称或手机号" clearable style="width:260px" @clear="onSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width:140px">
            <el-option v-for="opt in statusOptions" :key="opt.label" :value="opt.value" :label="opt.label" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div style="padding:8px 8px 8px 40px">
              <div v-for="item in row.items" :key="item.id" style="display:flex;align-items:center;gap:8px;padding:6px 0;border-bottom:1px solid #f0f0f0">
                <img :src="item.productImage" style="width:48px;height:48px;border-radius:4px;object-fit:cover;flex-shrink:0" />
                <div style="flex:1;min-width:0">
                  <div style="display:flex;align-items:center;gap:8px">
                    <span style="font-size:13px;font-weight:500;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;flex:1;min-width:0">{{ item.productName }}</span>
                    <span style="font-size:13px;color:#775836;white-space:nowrap;flex-shrink:0">¥{{ item.price }} × {{ item.quantity }}</span>
                  </div>
                  <div style="font-size:12px;color:#999;margin-top:2px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ item.productSpecs ? parseSpecs(item.productSpecs) : '' }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="编号" width="60" />
        <el-table-column prop="orderSn" label="订单号" width="190" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column label="收货人" width="100">
          <template #default="{ row }">{{ parseAddress(row.addressSnapshot)?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="联系方式" width="130">
          <template #default="{ row }">{{ parseAddress(row.addressSnapshot)?.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="收货地址" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <template v-if="parseAddress(row.addressSnapshot)">{{ parseAddress(row.addressSnapshot)?.address }}</template>
            <template v-else-if="row.addressSnapshot && row.addressSnapshot !== '{}'">{{ row.addressSnapshot }}</template>
            <template v-else>-</template>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="商品数量" width="80">
          <template #default="{ row }">{{ row.items?.reduce((s: number, i: any) => s + i.quantity, 0) || 0 }}</template>
        </el-table-column>
        <el-table-column label="订单状态" width="90">
          <template #default="{ row }">
            <el-tag :color="statusColors[row.status]" style="color:#fff;border:none;font-size:12px">{{ statusMap[row.status] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="快递单号" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.trackingNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="80">
          <template #default="{ row }">{{ row.paymentMethod === 1 ? '微信支付' : '未支付' }}</template>
        </el-table-column>
        <el-table-column label="支付时间" width="160">
          <template #default="{ row }">{{ row.paidAt ? formatTime(row.paidAt) : '-' }}</template>
        </el-table-column>
        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="note" label="备注" min-width="100" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="primary" size="small" @click="openShipDialog(row)">发货</el-button>
            <template v-if="row.trackingNo">
              <el-button v-if="row.status === 2" type="success" size="small" @click="openTrackDialog(row)">物流追踪</el-button>
              <el-button v-else size="small" @click="openTrackDialog(row)">物流追踪</el-button>
              <el-button size="small" @click="openPreviewDialog(row)">面单预览</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50]" style="margin-top:16px; justify-content:flex-end" @current-change="fetchData" @size-change="fetchData" />
    </el-card>

    <el-dialog v-model="shipDialogVisible" title="发货" width="460px">
      <div v-if="shipAddressInfo" style="background:#f5f5f5;border-radius:6px;padding:12px 16px;margin-bottom:16px;font-size:13px;line-height:1.6;word-break:break-all;cursor:pointer" @click="copyAddressInfo" title="点击复制">{{ shipAddressInfo }}</div>
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.expressCompany" style="width:100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="邮政EMS" value="邮政EMS" />
            <el-option label="京东快递" value="京东快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="运单号">
          <el-input v-model="shipForm.trackingNo" placeholder="输入快递运单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="success" :loading="shipping" @click="handleAutoShip">顺丰自动生成</el-button>
        <el-button type="primary" :loading="shipping" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="trackDialogVisible" title="物流追踪" width="520px">
      <div v-loading="trackingLoading">
        <div v-if="currentTrackNo" style="margin-bottom:12px;font-size:13px;color:#666">
          运单号：<strong>{{ currentTrackNo }}</strong>
        </div>
        <div v-if="trackData">
          <div v-if="trackData.routes?.length" style="padding-left:20px;border-left:2px solid #e8e8e8">
            <div v-for="(r, i) in trackData.routes" :key="i" style="position:relative;padding:0 0 16px 16px">
              <div :style="{position:'absolute',left:'-7px',top:'0',width:'12px',height:'12px',borderRadius:'50%',background:i===0?'#409eff':'#d9d9d9',border:'2px solid #fff',boxShadow:'0 0 0 2px #e8e8e8'}"></div>
              <div style="font-size:13px;font-weight:500;margin-bottom:2px">{{ r.remark || r.acceptAddress || '-' }}</div>
              <div style="font-size:12px;color:#999">{{ formatTrackTime(r.acceptTime) }}</div>
            </div>
          </div>
          <div v-else style="color:#999;text-align:center;padding:20px 0">暂无物流轨迹</div>
        </div>
        <div v-else-if="!trackingLoading" style="color:#999;text-align:center;padding:20px 0">暂无物流信息</div>
      </div>
      <template #footer>
        <el-button type="primary" :loading="trackingLoading" @click="fetchTrack">刷新</el-button>
        <el-button @click="trackDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewDialogVisible" title="顺丰面单预览" width="500px">
      <div v-loading="previewLoading">
        <div v-if="previewData" style="border:1px solid #e8e8e8;border-radius:8px;padding:20px;background:#fafafa">
          <div style="text-align:center;margin-bottom:16px">
            <canvas id="waybill-qrcode" style="display:inline-block"></canvas>
          </div>
          <div style="font-size:14px;font-weight:600;text-align:center;margin-bottom:16px">
            {{ previewWaybillNo }}
          </div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:13px">
            <div>
              <div style="color:#999;margin-bottom:2px">寄件人</div>
              <div style="font-weight:500">{{ previewData.sourceName || '-' }}</div>
              <div style="color:#666">{{ previewData.sourceMobile || '' }}</div>
              <div style="color:#999;font-size:12px">{{ [previewData.sourceCity, previewData.sourceCounty, previewData.sourceAddress].filter(Boolean).join(' ') }}</div>
            </div>
            <div>
              <div style="color:#999;margin-bottom:2px">收件人</div>
              <div style="font-weight:500">{{ previewData.destName || '-' }}</div>
              <div style="color:#666">{{ previewData.destMobile || '' }}</div>
              <div style="color:#999;font-size:12px">{{ [previewData.destCity, previewData.destCounty, previewData.destAddress].filter(Boolean).join(' ') }}</div>
            </div>
          </div>
          <div style="margin-top:12px;padding-top:12px;border-top:1px dashed #ddd;font-size:12px;color:#999;text-align:center">
            产品类型：{{ previewData.proName || '-' }}
          </div>
        </div>
        <div v-else-if="!previewLoading" style="color:#999;text-align:center;padding:20px 0">暂无面单数据</div>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>
