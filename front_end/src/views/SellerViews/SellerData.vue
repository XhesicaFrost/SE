<template>
  <TopNav :navInfo="navInfo" />
  <div class="seller-data-view">
    <!-- 时间选择 -->
    <div class="date-picker-section">
      <label>开始时间：</label>
      <input type="date" v-model="startDate" />
      <label>结束时间：</label>
      <input type="date" v-model="endDate" />
      <button @click="fetchData">查询</button>
    </div>

    <!-- 销售额与订单数 -->
    <div class="summary-section">
      <div class="summary-text">
        时间段总销售额：￥{{ totalSales }}，总订单数：{{ totalOrders }}
      </div>
      <div class="chart-container">
        <canvas ref="salesOrderChart"></canvas>
      </div>
    </div>

    <!-- 用户评价 -->
    <div class="summary-section">
      <div class="summary-text">
        时间段总好评数：{{ totalGood }}，总差评数：{{ totalBad }}
      </div>
      <div class="chart-container">
        <canvas ref="commentChart"></canvas>
      </div>
    </div>

    <!-- 下载数据 -->
    <div class="download-section">
      <a :href="downloadUrl" target="_blank" class="download-btn">下载详细数据</a>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'
import Chart from 'chart.js/auto'

export default {
  name: 'sellerData',
  components: { BottomNav, TopNav },
  data() {
    return {
      startDate: '',
      endDate: '',
      totalSales: 0,
      totalOrders: 0,
      salesOrderData: [],
      salesOrderLabels: [],
      totalGood: 0,
      totalBad: 0,
      commentData: [],
      commentLabels: [],
      downloadUrl: '',
      navItems: [
        { label: '管理店铺', action: () => { this.$router.push('/seller/shop') } },
        { label: '管理订单', action: () => { this.$router.push('/seller/order') } },
        { label: '查看数据', action: () => { this.$router.push('/seller/data') }, isActive: true }
      ],
      navInfo: { title: '查看数据', pageReturn: () => { this.$router.push('/seller') } },
      salesOrderChartInstance: null,
      commentChartInstance: null
    }
  },
  computed: {
    ...mapState('sellerStore', ['sellerId'])
  },
  methods: {
    async fetchData() {
      if (!this.startDate || !this.endDate) {
        alert('请选择开始和结束时间')
        return
      }
      // 获取销售额和订单数数据
      try {
        const params = new URLSearchParams({
          sellerId: this.sellerId,
          startDate: this.startDate,
          endDate: this.endDate
        }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/data/sales?${params}`)
        const result = await response.json()
        if (result.code==200) {
          this.totalSales = result.data.totalSales
          this.totalOrders = result.data.totalOrders
          this.salesOrderLabels = result.data.labels // 如 ['2025-05-01', ...]
          this.salesOrderData = result.data.series   // 如 [{sales: 100, orders: 5}, ...]
          this.renderSalesOrderChart()
        }
      } catch (e) {
        this.totalSales = 0
        this.totalOrders = 0
        this.salesOrderLabels = []
        this.salesOrderData = []
        this.renderSalesOrderChart()
      }
      // 获取评价数据
      try {
        const params = new URLSearchParams({
          sellerId: this.sellerId,
          startDate: this.startDate,
          endDate: this.endDate
        }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/data/comment?${params}`)
        const result = await response.json()
        if (result.code==200) {
          this.totalGood = result.data.totalGood
          this.totalBad = result.data.totalBad
          this.commentLabels = result.data.labels // 如 ['2025-05-01', ...]
          this.commentData = result.data.series   // 如 [{good: 10, bad: 2}, ...]
          this.renderCommentChart()
        }
      } catch (e) {
        this.totalGood = 0
        this.totalBad = 0
        this.commentLabels = []
        this.commentData = []
        this.renderCommentChart()
      }
      // 下载链接
      this.downloadUrl = `${BASE_URL}/seller/data/download?sellerId=${this.sellerId}&startDate=${this.startDate}&endDate=${this.endDate}`
    },
    renderSalesOrderChart() {
      if (this.salesOrderChartInstance) {
        this.salesOrderChartInstance.destroy()
      }
      const ctx = this.$refs.salesOrderChart.getContext('2d')
      this.salesOrderChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.salesOrderLabels,
          datasets: [
            {
              label: '销售额',
              data: this.salesOrderData.map(d => d.sales),
              borderColor: '#42a5f5',
              backgroundColor: 'rgba(66,165,245,0.1)',
              yAxisID: 'y'
            },
            {
              label: '订单数',
              data: this.salesOrderData.map(d => d.orders),
              borderColor: '#66bb6a',
              backgroundColor: 'rgba(102,187,106,0.1)',
              yAxisID: 'y1'
            }
          ]
        },
        options: {
          responsive: true,
          interaction: { mode: 'index', intersect: false },
          stacked: false,
          plugins: { legend: { position: 'top' } },
          scales: {
            y: { type: 'linear', display: true, position: 'left', title: { display: true, text: '销售额' } },
            y1: { type: 'linear', display: true, position: 'right', title: { display: true, text: '订单数' }, grid: { drawOnChartArea: false } }
          }
        }
      })
    },
    renderCommentChart() {
      if (this.commentChartInstance) {
        this.commentChartInstance.destroy()
      }
      const ctx = this.$refs.commentChart.getContext('2d')
      this.commentChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.commentLabels,
          datasets: [
            {
              label: '好评数',
              data: this.commentData.map(d => d.good),
              borderColor: '#ffb300',
              backgroundColor: 'rgba(255,193,7,0.1)'
            },
            {
              label: '差评数',
              data: this.commentData.map(d => d.bad),
              borderColor: '#e53935',
              backgroundColor: 'rgba(229,57,53,0.1)'
            }
          ]
        },
        options: {
          responsive: true,
          plugins: { legend: { position: 'top' } }
        }
      })
    }
  }
}
</script>

<style scoped>
.seller-data-view {
  max-width: 600px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
.date-picker-section {
  display: flex;
  align-items: center;
  gap: 1em;
  margin-bottom: 1.5em;
}
.summary-section {
  margin-bottom: 2em;
}
.summary-text {
  font-size: 1.1em;
  margin-bottom: 0.7em;
  color: #333;
}
.chart-container {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1em;
  min-height: 220px;
}
.download-section {
  margin: 2em 0 1em 0;
  text-align: center;
}
.download-btn {
  background: #1976d2;
  color: #fff;
  padding: 0.7em 2em;
  border-radius: 8px;
  text-decoration: none;
  font-size: 1.1em;
  transition: background 0.2s;
}
.download-btn:hover {
  background: #0d47a1;
}
</style>