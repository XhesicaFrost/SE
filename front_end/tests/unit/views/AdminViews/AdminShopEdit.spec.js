import { mount } from '@vue/test-utils'
import AdminShopEdit from '@/views/AdminViews/AdminShopEdit.vue'

// Mock dependencies
jest.mock('@/config.js', () => ({
  BASE_URL: 'http://test-api',
  fetchWithTimeout: jest.fn()
}))

jest.mock('@/components/topNav.vue', () => ({
  name: 'TopNav',
  template: '<div class="mock-top-nav"></div>',
  props: ['navInfo']
}))

const { fetchWithTimeout } = require('@/config.js')

describe('AdminShopEdit', () => {
  let wrapper

  const mockShop = {
    id: '123',
    name: '测试店铺',
    address: '测试地址123号',
    image: 'base64imagedata'
  }

  beforeEach(() => {
    fetchWithTimeout.mockClear()
    global.alert = jest.fn()
    global.URL.createObjectURL = jest.fn(() => 'mock-blob-url')
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  test('正向：成功加载店铺信息', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShop })
    })

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待组件挂载完成和fetchShopInfo执行
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.shopName).toBe('测试店铺')
    expect(wrapper.vm.shopAddress).toBe('测试地址123号')
  })

  test('正向：成功提交修改', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockShop })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待初始化完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 修改店铺信息
    const inputs = wrapper.findAll('input[type="text"]')
    await inputs[0].setValue('修改后的店铺名')
    await inputs[1].setValue('修改后的地址')

    // 提交修改并等待完成
    const submitPromise = wrapper.vm.handleEdit()
    await submitPromise

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/shop/edit',
      expect.objectContaining({
        method: 'POST',
        body: expect.any(FormData)
      })
    )
    expect(global.alert).toHaveBeenCalledWith('修改成功')
  })

  test('反向：表单验证失败', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShop })
    })

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    // 清空必填字段
    wrapper.vm.shopName = ''
    
    await wrapper.find('.submit-btn').trigger('click')

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息')
  })

  test('正向：返回按钮功能', async () => {
    const mockPush = jest.fn()

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: mockPush }
        }
      }
    })

    await wrapper.find('.cancel-btn').trigger('click')

    expect(mockPush).toHaveBeenCalledWith('/admin/shops')
  })

  test('反向：API错误处理', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待异步错误处理完成
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.errorMessage).toBe('网络错误，获取店铺信息失败')
  })

  test('正向：图片上传功能', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockShop })
    })

    wrapper = mount(AdminShopEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
    const fileInput = wrapper.find('input[type="file"]')

    Object.defineProperty(fileInput.element, 'files', {
      value: [mockFile]
    })

    await fileInput.trigger('change')

    expect(wrapper.vm.shopImage).toBe(mockFile)
    expect(wrapper.vm.previewImage).toBe('mock-blob-url')
  })
})
