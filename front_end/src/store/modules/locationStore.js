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
  // ✅ 修复：开始位置追踪
  startLocationTracking({ commit, rootState }) {
    if (state.isLocationTracking) return
    
    // ✅ 修复：正确获取用户ID
    const userId = rootState.userStore.userInfo?.userId
    
    if (!userId) {
      console.error('❌ 用户ID不存在，无法开始位置追踪')
      return
    }
    
    console.log('🚀 开始位置追踪，用户ID:', userId)
    commit('SET_LOCATION_TRACKING', true)
    
    const timer = setInterval(async () => {
      try {
        // 获取当前位置
        const position = await getCurrentLocation()
        console.log('📍 locationStore:当前位置:', position)
        
        // ✅ 修复：发送位置到后端
        const response = await fetchWithTimeout(`${BASE_URL}/rider/updateLocation`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            userId: userId,  // ✅ 使用正确的用户ID
            latitude: position.latitude,
            longitude: position.longitude
          })
        })
        
        // ✅ 新增：处理响应
        const result = await response.json()
        
        // ✅ 适配 ApiResponse 格式
        const isSuccess = response.ok || result.code === 200 || result.success === true
        
        if (isSuccess) {
          console.log('✅ 位置更新成功')
        } else {
          console.warn('⚠️ 位置更新失败:', result.message || '未知错误')
        }
      } catch (e) {
        console.error('❌ 位置更新失败:', e)
      }
    }, 10000) // 每10秒执行一次
    
    commit('SET_LOCATION_TIMER', timer)
  },
  
  // 停止位置追踪
  stopLocationTracking({ commit }) {
    console.log('🛑 停止位置追踪')
    
    if (state.locationTimer) {
      clearInterval(state.locationTimer)
      commit('SET_LOCATION_TIMER', null)
    }
    commit('SET_LOCATION_TRACKING', false)
  }
}

// ✅ 修复：获取地理位置的辅助函数，增加错误处理
function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('浏览器不支持地理位置'))
      return
    }
    
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const coords = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        }
        console.log('🌍 获取到地理位置:', coords)
        resolve(coords)
      },
      (error) => {
        console.error('❌ 获取地理位置失败:', error)
        // 提供更详细的错误信息
        let errorMsg = '获取位置失败'
        switch(error.code) {
          case error.PERMISSION_DENIED:
            errorMsg = '用户拒绝了地理位置请求'
            break
          case error.POSITION_UNAVAILABLE:
            errorMsg = '位置信息不可用'
            break
          case error.TIMEOUT:
            errorMsg = '获取位置请求超时'
            break
        }
        reject(new Error(errorMsg))
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