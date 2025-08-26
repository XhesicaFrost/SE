import { fetchWithTimeout, FETCH_TIMEOUT } from '@/config.js'

describe('fetchWithTimeout', () => {
  beforeEach(() => {
    jest.useFakeTimers()
    jest.spyOn(console, 'log').mockImplementation(() => {}) // 静默日志
  })
  afterEach(() => {
    jest.useRealTimers()
    jest.resetAllMocks()
  })

  test('成功：返回 200 且可解析 JSON', async () => {
    const mockJson = { success: true }
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => mockJson
    })
    const res = await fetchWithTimeout('http://test/api')
    expect(res.ok).toBe(true)
    await expect(res.json()).resolves.toEqual(mockJson)
  })

  test('失败：超时触发 rejected', async () => {
    // 模拟 fetch：不主动完成，但在 signal.abort 时触发 reject
    global.fetch = jest.fn((url, opts = {}) => {
      return new Promise((_, reject) => {
        const signal = opts.signal
        if (signal && typeof signal.addEventListener === 'function') {
          signal.addEventListener('abort', () => {
            reject(new Error('AbortError'))
          })
        }
      })
    })

    // 放大该用例的测试超时时间，超过 8 秒
    jest.setTimeout(FETCH_TIMEOUT + 2000)

    const p = fetchWithTimeout('http://test/slow')

    // 推进到固定超时时间
    jest.advanceTimersByTime(FETCH_TIMEOUT)
    // 冲刷微任务让 Promise 结算
    await Promise.resolve()

    // 你的实现会抛出 AbortError（不是“请求超时”文案）
    await expect(p).rejects.toThrow()
  })
})