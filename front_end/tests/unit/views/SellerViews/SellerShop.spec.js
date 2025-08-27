import { shallowMount, mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import SellerShop from '@/views/SellerViews/SellerShop.vue'

// stub 子组件
jest.mock('@/components/bottomNav.vue', () => ({ default: { name: 'BottomNav', render: () => null } }))
jest.mock('@/components/topNav.vue', () => ({ default: { name: 'TopNav', render: () => null } }))

jest.mock('@/config.js', () => ({
  BASE_URL: 'http://localhost:12345',
  fetchWithTimeout: jest.fn()
}))
import { fetchWithTimeout } from '@/config.js'

describe('SellerShop.vue', () => {
  let store
  let routerMock

  beforeAll(() => {
    if (!global.confirm) global.confirm = () => true
    if (!global.alert) global.alert = () => {}
    jest.spyOn(console, 'log').mockImplementation(() => {})
    jest.spyOn(console, 'error').mockImplementation(() => {})
  })

  beforeEach(() => {
    jest.clearAllMocks()
    store = createStore({
      modules: {
        sellerStore: { namespaced: true, state: () => ({ sellerId: 'seller-001' }) }
      }
    })
    routerMock = { push: jest.fn() }
    // 避免 mounted 报错（mounted 会拉 shop 和 goods）
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200, data: [] }) })
  })

  // 导航与路由
  it('editShop 正向：跳转到编辑店铺', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.editShop()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/shop/edit')
  })

  it('editShop 反向：路由抛错时向外抛出', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: badRouter } } })
    expect(() => wrapper.vm.editShop()).toThrow('router failed')
  })

  it('editItem 正向：跳转到编辑商品', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.editItem(10)
    expect(routerMock.push).toHaveBeenCalledWith('/seller/item/10')
  })

  it('editItem 反向：路由抛错时向外抛出', () => {
    const badRouter = { push: jest.fn(() => { throw new Error('router failed') }) }
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: badRouter } } })
    expect(() => wrapper.vm.editItem(10)).toThrow('router failed')
  })

  // 排序与分页
  it('sortGoods 正向：修改排序重置到第1页', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    await wrapper.setData({ page: 3 })
    wrapper.vm.sortGoods()
    expect(wrapper.vm.page).toBe(1)
  })

  it('sortGoods 反向：已在第1页保持不变', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    wrapper.vm.sortGoods()
    expect(wrapper.vm.page).toBe(1)
  })

  it('changePage 正向：跳转到指定页并同步 jumpPage', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    await wrapper.setData({ goods: Array.from({ length: 25 }, (_, i) => ({ id: i + 1, name: 'A', price: 1, sales: 0 })) })
    wrapper.vm.changePage(2)
    expect(wrapper.vm.page).toBe(2)
    expect(wrapper.vm.jumpPage).toBe(2)
  })

  it('changePage 反向：越界页码被夹取', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    await wrapper.setData({ goods: Array.from({ length: 5 }, (_, i) => ({ id: i + 1, name: 'A', price: 1, sales: 0 })) })
    wrapper.vm.changePage(0)
    expect(wrapper.vm.page).toBe(1)
    wrapper.vm.changePage(999)
    expect(wrapper.vm.page).toBe(1)
  })

  // 其他路由跳转
  it('goToAddItem 正向：跳转新增商品', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToAddItem()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/item/register')
  })

  it('goToPromotion 正向：跳转促销管理', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToPromotion()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/promotion')
  })

  it('goToApproval 正向：跳转审批查看', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $router: routerMock } } })
    wrapper.vm.goToApproval()
    expect(routerMock.push).toHaveBeenCalledWith('/seller/approval')
  })

  // fetchShopInfo
  it('fetchShopInfo 正向：200 填充信息并处理图片', async () => {
    const wrapper = mount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: { shopName: '小店', shopImg: 'B64', shopAddress: '地A' }
      })
    })
    await wrapper.vm.fetchShopInfo()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.shopInfo.name).toBe('小店')
    expect(wrapper.vm.shopInfo.image).toBe('data:image/jpeg;base64,B64')
    expect(wrapper.vm.shopInfo.address).toBe('地A')
  })

  it('fetchShopInfo 反向：非 200 设置为获取失败', async () => {
    const wrapper = mount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500 }) })
    await wrapper.vm.fetchShopInfo()
    expect(wrapper.vm.shopInfo.name).toBe('获取失败')
  })

  // fetchGoods
  it('fetchGoods 正向：200 且数组时转换图片并补齐状态', async () => {
    const wrapper = mount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({
      json: async () => ({
        code: 200,
        data: [{ id: 1, name: 'N', price: 1, sales: 0, image: 'B64' }]
      })
    })
    await wrapper.vm.fetchGoods()
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(wrapper.vm.goods).toHaveLength(1)
    expect(wrapper.vm.goods[0].image).toBe('data:image/jpeg;base64,B64')
    expect(wrapper.vm.goods[0].status).toBe('正常')
    expect(wrapper.vm.goods[0].isUpdating).toBe(false)
  })

  it('fetchGoods 反向：非 200 置空', async () => {
    const wrapper = mount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 400 }) })
    await wrapper.vm.fetchGoods()
    expect(wrapper.vm.goods).toEqual([])
  })

  // 状态显示
  it('getStatusText 正向：正常 -> 正常', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusText('正常')).toBe('正常')
  })

  it('getStatusText 反向：未知 -> 未知', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusText('??')).toBe('未知')
  })

  it('getStatusClass 正向：正常 -> status-online', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusClass('正常')).toBe('status-online')
  })

  it('getStatusClass 反向：未知 -> status-unknown', () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    expect(wrapper.vm.getStatusClass('??')).toBe('status-unknown')
  })

  // toggleItemStatus
  it('toggleItemStatus 正向：正常 -> 下架流程，调用 offlineItem 并刷新', async () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true)
    const toast = jest.fn()
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store], mocks: { $toast: toast } } })
    const item = { id: 1, name: 'A', status: '正常', isUpdating: false }

    // 覆写实例方法避免真实网络
    wrapper.vm.offlineItem = jest.fn().mockResolvedValue({ code: 200 })
    wrapper.vm.fetchGoods = jest.fn().mockResolvedValue()

    await wrapper.vm.toggleItemStatus(item)

    expect(wrapper.vm.offlineItem).toHaveBeenCalledWith(1)
    expect(wrapper.vm.fetchGoods).toHaveBeenCalled()
    expect(toast).toHaveBeenCalledWith('下架成功')
    expect(item.isUpdating).toBe(false)
  })

  it('toggleItemStatus 反向：审批中直接提示并返回', async () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {})
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    const item = { id: 2, name: 'B', status: '审批中', isUpdating: false }

    wrapper.vm.offlineItem = jest.fn()
    wrapper.vm.onlineItem = jest.fn()

    await wrapper.vm.toggleItemStatus(item)

    expect(alertSpy).toHaveBeenCalled()
    expect(wrapper.vm.offlineItem).not.toHaveBeenCalled()
    expect(wrapper.vm.onlineItem).not.toHaveBeenCalled()
    expect(item.isUpdating).toBe(false)
  })

  // offlineItem / onlineItem
  it('offlineItem 正向：code=200 返回结果', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200 }) })
    const res = await wrapper.vm.offlineItem(5)
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/item/offline')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    const body = JSON.parse(fetchWithTimeout.mock.calls[0][1].body)
    expect(body.itemId).toBe(5)
    expect(body.sellerId).toBe('seller-001')
    expect(res.code).toBe(200)
  })

  it('offlineItem 反向：非 200 抛错', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 500, message: 'bad' }) })
    await expect(wrapper.vm.offlineItem(5)).rejects.toThrow('bad')
  })

  it('onlineItem 正向：code=200 返回结果', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 200 }) })
    const res = await wrapper.vm.onlineItem(6)
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1)
    expect(fetchWithTimeout.mock.calls[0][0]).toBe('http://localhost:12345/seller/item/online')
    expect(fetchWithTimeout.mock.calls[0][1].method).toBe('POST')
    const body = JSON.parse(fetchWithTimeout.mock.calls[0][1].body)
    expect(body.itemId).toBe(6)
    expect(body.sellerId).toBe('seller-001')
    expect(res.code).toBe(200)
  })

  it('onlineItem 反向：非 200 抛错', async () => {
    const wrapper = shallowMount(SellerShop, { global: { plugins: [store] } })
    fetchWithTimeout.mockClear()
    fetchWithTimeout.mockResolvedValue({ json: async () => ({ code: 400, message: 'bad' }) })
    await expect(wrapper.vm.onlineItem(6)).rejects.toThrow('bad')
  })
})