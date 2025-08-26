import { mount } from '@vue/test-utils'
import AdminItemEdit from '@/views/AdminViews/AdminItemEdit.vue'

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

describe('AdminItemEdit', () => {
  let wrapper

  const mockItem = {
    id: '123',
    name: '测试商品',
    price: 29.99,
    description: '这是一个测试商品',
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

  test('正向：成功加载商品信息', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockItem })
    })

    wrapper = mount(AdminItemEdit, {
      props: { id: '123' },
      global: {
        mocks: {
          $router: { push: jest.fn() }
        }
      }
    })

    // 等待组件挂载完成和fetchItemInfo执行
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.vm.itemName).toBe('测试商品')
    expect(wrapper.vm.itemPrice).toBe(29.99)
    expect(wrapper.vm.itemDescription).toBe('这是一个测试商品')
  })

  test('正向：成功提交修改', async () => {
    fetchWithTimeout
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200, data: mockItem })
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve({ code: 200 })
      })

    wrapper = mount(AdminItemEdit, {
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

    // 修改商品信息
    await wrapper.find('input[type="text"]').setValue('修改后的商品')
    await wrapper.find('input[type="number"]').setValue('35.99')

    // 直接调用方法并等待完成
    const submitPromise = wrapper.vm.handleEdit()
    await submitPromise

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test-api/admin/item/edit',
      expect.objectContaining({
        method: 'POST',
        body: expect.any(FormData)
      })
    )
    expect(global.alert).toHaveBeenCalledWith('修改成功')
  })

  test('反向：表单验证失败', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockItem })
    })

    wrapper = mount(AdminItemEdit, {
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
    wrapper.vm.itemName = ''
    
    await wrapper.find('.submit-btn').trigger('click')

    expect(wrapper.vm.errorMessage).toBe('请填写完整信息')
    expect(fetchWithTimeout).toHaveBeenCalledTimes(1) // 只调用了获取商品信息的请求
  })

  test('正向：图片上传功能', async () => {
    fetchWithTimeout.mockResolvedValue({
      json: () => Promise.resolve({ code: 200, data: mockItem })
    })

    wrapper = mount(AdminItemEdit, {
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

    expect(wrapper.vm.itemImage).toBe(mockFile)
    expect(wrapper.vm.previewImage).toBe('mock-blob-url')
  })

  test('反向：API错误处理', async () => {
    fetchWithTimeout.mockRejectedValue(new Error('网络错误'))

    wrapper = mount(AdminItemEdit, {
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

    expect(wrapper.vm.errorMessage).toBe('网络错误，获取商品信息失败')
  })
})
