import sellerStore from '@/store/modules/sellerStore.js'

describe('sellerStore mutations', () => {
  test('SET_seller_ID: 正常设置 sellerId', () => {
    const state = { sellerId: '', sellerName: '', sellerStatus: '' }
    sellerStore.mutations.SET_seller_ID(state, '100')
    expect(state.sellerId).toBe('100')
  })

  test('SET_seller_NAME: 正常设置 sellerName', () => {
    const state = { sellerId: '', sellerName: '', sellerStatus: '' }
    sellerStore.mutations.SET_seller_NAME(state, '测试店铺')
    expect(state.sellerName).toBe('测试店铺')
  })

  test('SET_seller_ID: 传入空值 sellerId', () => {
    const state = { sellerId: 'old', sellerName: '', sellerStatus: '' }
    sellerStore.mutations.SET_seller_ID(state, '')
    expect(state.sellerId).toBe('')
  })
})