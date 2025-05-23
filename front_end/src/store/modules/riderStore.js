import { BASE_URL, fetchWithTimeout } from '@/config.js'

const state = {
  locationTimer: null,
  isLocationTracking: false
}

const mutations = {
  SET_LOCATION_TIMER(state, timer) {
    state.locationTimer = timer
  },
  SET_LOCATION_TRACKING(state, status) {
    state.isLocationTracking = status
  }
}

const actions = {
  // 开始位置追踪
  startLocationTracking({ commit, rootState }) {
    if (state.isLocationTracking) return
    
    commit('SET_LOCATION_TRACKING', true)
    
    const timer = setInterval(async () => {
      try {
        // 获取当前位置
        const position = await getCurrentLocation()
        
        // 发送位置到后端
        await fetchWithTimeout(`${BASE_URL}/rider/updateLocation`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            userId: rootState.userStore.userId,
            latitude: position.latitude,
            longitude: position.longitude
          })
        })
      } catch (e) {
        console.error('位置更新失败', e)
      }
    }, 10000) // 每10秒执行一次
    
    commit('SET_LOCATION_TIMER', timer)
  },
  
  // 停止位置追踪
  stopLocationTracking({ commit }) {
    if (state.locationTimer) {
      clearInterval(state.locationTimer)
      commit('SET_LOCATION_TIMER', null)
    }
    commit('SET_LOCATION_TRACKING', false)
  }
}

// 获取地理位置的辅助函数
function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('浏览器不支持地理位置'))
      return
    }
    
    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        })
      },
      (error) => {
        reject(error)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 600000
      }
    )
  })
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}