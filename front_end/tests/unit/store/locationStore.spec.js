jest.mock('@/config.js', () => ({
  BASE_URL: 'http://test',
  fetchWithTimeout: jest.fn().mockResolvedValue({
    ok: true,
    json: async () => ({ code: 200, success: true })
  })
}))

import locationStore from '@/store/modules/locationStore.js'
import { fetchWithTimeout } from '@/config.js'

describe('locationStore actions', () => {
  beforeEach(() => {
    jest.useFakeTimers()
    jest.spyOn(global, 'setInterval')
    jest.spyOn(global, 'clearInterval')

    // 用 defineProperty 给 jsdom 的 navigator 注入 geolocation
    const geo = {
      getCurrentPosition: (success) =>
        success({ coords: { latitude: 39.9, longitude: 116.4 } })
    }
    if (global.navigator) {
      Object.defineProperty(global.navigator, 'geolocation', {
        value: geo,
        configurable: true
      })
    } else {
      Object.defineProperty(global, 'navigator', {
        value: { geolocation: geo },
        configurable: true
      })
    }
  })

  afterEach(() => {
    jest.useRealTimers()
    jest.restoreAllMocks()
    jest.resetModules()
  })

  test('成功：userId 存在时可启动位置追踪并定时上报', async () => {
    const commit = jest.fn()
    const rootState = { userStore: { userInfo: { userId: 123 } } }

    locationStore.actions.startLocationTracking({ commit, rootState })
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TRACKING', true)

    // 触发一次定时器回调
    const cb = setInterval.mock.calls[0][0]
    await cb()

    expect(fetchWithTimeout).toHaveBeenCalledWith(
      'http://test/rider/updateLocation',
      expect.objectContaining({ method: 'POST' })
    )
  })

  test('失败：userId 缺失时直接返回，不启动追踪', () => {
    const commit = jest.fn()
    const rootState = { userStore: { userInfo: { userId: null } } }

    locationStore.actions.startLocationTracking({ commit, rootState })
    expect(commit).not.toHaveBeenCalledWith('SET_LOCATION_TRACKING', true)
    expect(setInterval).not.toHaveBeenCalled()
  })

  test('停止：可清除定时器并重置状态', () => {
    const commit = jest.fn()
    // 手动设置一个假的定时器句柄
    locationStore.state.locationTimer = 111
    locationStore.actions.stopLocationTracking({ commit })
    expect(clearInterval).toHaveBeenCalledWith(111)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TIMER', null)
    expect(commit).toHaveBeenCalledWith('SET_LOCATION_TRACKING', false)
  })
})