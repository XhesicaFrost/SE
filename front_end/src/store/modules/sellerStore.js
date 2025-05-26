export default {
  namespaced: true,
  state: () => ({
    sellerId: '',//店铺ID
    sellerName: '',//店铺名称
    sellerStatus: '',//店铺状态
  }),
  mutations: {
    SET_seller_ID(state, id) {
      state.sellerId = id
    },
    SET_seller_NAME(state, name) {
      state.sellerName = name
    }
  }
}