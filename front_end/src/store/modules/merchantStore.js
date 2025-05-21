export default {
  namespaced: true,
  state: () => ({
    merchantId: '',//店铺ID
    merchantName: '',//店铺名称
    merchantStatus: '',//店铺状态
  }),
  mutations: {
    SET_MERCHANT_ID(state, id) {
      state.merchantId = id
    },
    SET_MERCHANT_NAME(state, name) {
      state.merchantName = name
    }
  }
}