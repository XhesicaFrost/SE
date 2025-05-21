export default {
  namespaced: true,
  state: () => ({
    merchantId: '',
    merchantName: ''
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