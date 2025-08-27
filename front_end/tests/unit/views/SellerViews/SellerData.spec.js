import { mount, shallowMount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerData from '@/views/SellerViews/SellerData.vue'

// stub 子组件
jest.mock('@/components/bottomNav.vue', () => ({
  default: { name: 'BottomNav', render: () => null }
}))
jest.mock('@/components/topNav.vue', () => ({
  default: { name: 'TopNav', render: () => null }
}))

// 拦截配置与网络
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

// mock Chart.js（注意变量名以 mock 开头，允许在工厂中引用）
const mockChartDestroy = jest.fn()
jest.mock('chart.js/auto', () => ({
  __esModule: true,
  default: jest.fn().mockImplementation(() => ({ destroy: mockChartDestroy }))
}))
import Chart from 'chart.js/auto'

describe('SellerData.vue', () => {
  let store

  beforeAll(() => {
    // jsdom canvas getContext
    if (!HTMLCanvasElement.prototype.getContext) {
      HTMLCanvasElement.prototype.getContext = jest.fn(() => ({}))
    }
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: {
        sellerStore: {
          namespaced: true,
          state: () => ({ sellerId: 'seller-001' })
        }
      }
    })
  })

  // renderSalesOrderChart
  it('renderSalesOrderChart 正向：有数据时调用 Chart', async () => {
    const wrapper = mount(SellerData, {
      global: { plugins: [store] }
    })
    // 准备数据并触发渲染
    await wrapper.setData({
      salesOrderLabels: ['2025-01-01'],
      salesOrderData: [{ sales: 100, orders: 5 }]
    })
    wrapper.vm.renderSalesOrderChart()
    expect(Chart).toHaveBeenCalled()
  })

  it('renderSalesOrderChart 反向：重复渲染会销毁旧实例', async () => {
    const wrapper = mount(SellerData, {
      global: { plugins: [store] }
    })
    await wrapper.setData({
      salesOrderLabels: ['2025-01-01'],
      salesOrderData: [{ sales: 100, orders: 5 }]
    })
    wrapper.vm.renderSalesOrderChart()
    wrapper.vm.renderSalesOrderChart()
    expect(mockChartDestroy).toHaveBeenCalled()
  })

  // fetchData
  it('fetchData 正向：两个接口均 200 时填充并渲染图表，设置下载链接', async () => {
    const wrapper = mount(SellerData, {
      global: { plugins: [store] }
    })
    await wrapper.setData({ startDate: '2025-05-01', endDate: '2025-05-31' })

    fetchWithTimeout
      .mockResolvedValueOnce({
        json: async () => ({
          code: 200,
          data: {
            totalSales: 1000,
            totalOrders: 50,
            labels: ['2025-05-01', '2025-05-02'],
            series: [{ sales: 100, orders: 5 }, { sales: 200, orders: 8 }]
          }
        })
      })
      .mockResolvedValueOnce({
        json: async () => ({
          code: 200,
          data: {
            totalGood: 30,
            totalBad: 5,
            labels: ['2025-05-01', '2025-05-02'],
            series: [{ good: 3, bad: 1 }, { good: 4, bad: 0 }]
          }
        })
      })

    await wrapper.vm.fetchData()

    // 状态更新
    expect(wrapper.vm.totalSales).toBe(1000)
    expect(wrapper.vm.totalOrders).toBe(50)
    expect(wrapper.vm.totalGood).toBe(30)
    expect(wrapper.vm.totalBad).toBe(5)
    // 图表被调用
    expect(Chart).toHaveBeenCalledTimes(2)
    // 下载链接
    expect(wrapper.vm.downloadUrl).toBe(
      'http://localhost:12345/seller/data/download?sellerId=seller-001&startDate=2025-05-01&endDate=2025-05-31'
    )
  })

  it('fetchData 反向：接口异常时重置数据并仍尝试渲染', async () => {
    const wrapper = shallowMount(SellerData, {
      global: { plugins: [store] }
    })
    await wrapper.setData({ startDate: '2025-05-01', endDate: '2025-05-31' })

    fetchWithTimeout
      .mockRejectedValueOnce(new Error('net-1'))
      .mockRejectedValueOnce(new Error('net-2'))

    // 替代 spyOn：直接覆写实例方法为 jest.fn()
    wrapper.vm.renderSalesOrderChart = jest.fn()
    wrapper.vm.renderCommentChart = jest.fn()

    await wrapper.vm.fetchData()

    expect(wrapper.vm.totalSales).toBe(0)
    expect(wrapper.vm.totalOrders).toBe(0)
    expect(wrapper.vm.salesOrderLabels).toEqual([])
    expect(wrapper.vm.salesOrderData).toEqual([])
    expect(wrapper.vm.totalGood).toBe(0)
    expect(wrapper.vm.totalBad).toBe(0)
    expect(wrapper.vm.commentLabels).toEqual([])
    expect(wrapper.vm.commentData).toEqual([])
    expect(wrapper.vm.renderSalesOrderChart).toHaveBeenCalled()
    expect(wrapper.vm.renderCommentChart).toHaveBeenCalled()
  })
})