<template>
  <div class="rider-order-detail">
    <!-- 错误状态显示 -->
    <div v-if="hasError" class="error-section">
      <div class="error-card">
        <div class="error-icon">⚠️</div>
        <h3 class="error-title">订单信息加载失败</h3>
        <p class="error-message">{{ errorMessage }}</p>
        <div class="error-actions">
          <button @click="retryFetchOrder" class="retry-btn">重新加载</button>
          <button @click="goBack" class="back-btn">返回主页</button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="isLoading" class="loading-section">
      <div class="loading-card">
        <div class="loading-spinner"></div>
        <p class="loading-text">正在加载订单信息...</p>
      </div>
    </div>

    <!-- 正常订单内容 -->
    <div v-else>
      <!-- 订单详情 -->
      <div class="order-detail-section">
        <h3 class="section-title">订单详情</h3>
        <div class="detail-card">
          <div class="detail-item">
            <label>订单号：</label>
            <span>{{ orderDetail.id || '未知' }}</span>
          </div>
          <div class="detail-item">
            <label>商家名称：</label>
            <span>{{ orderDetail.sellerName || '信息缺失' }}</span>
          </div>
          <div class="detail-item">
            <label>商家地址：</label>
            <span>{{ orderDetail.sellerAddress || '地址信息缺失' }}</span>
          </div>
          <div class="detail-item">
            <label>用户地址：</label>
            <span>{{ orderDetail.userAddress || '地址信息缺失' }}</span>
          </div>
          <div class="detail-item">
            <label>用户手机：</label>
            <span>{{ orderDetail.userPhone || '联系方式缺失' }}</span>
          </div>
          <div class="detail-item">
            <label>订单时间：</label>
            <span>{{ orderDetail.createTime || '时间信息缺失' }}</span>
          </div>
          <div class="detail-item">
            <label>订单状态：</label>
            <span class="status-badge" :class="getStatusClass(orderDetail.status)">
              {{ getStatusText(orderDetail.status) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 导航地图 -->
      <div class="navigation-section">
        <h3 class="section-title">配送导航</h3>
        
        <!-- 地址信息不完整提示 -->
        <div v-if="!canNavigate" class="nav-error">
          <div class="nav-error-icon">📍</div>
          <p class="nav-error-text">地址信息不完整，无法进行导航</p>
          <small class="nav-error-detail">
            缺失信息：{{ getMissingAddressInfo() }}
          </small>
        </div>
        
        <!-- 正常导航控件 -->
        <div v-else>
          <div class="nav-controls">
            <button @click="startNavigation" class="nav-btn" :disabled="!orderDetail.id || !mapInitialized">
              {{ isNavigating ? '导航中' : '开始导航' }}
            </button>
            <button @click="refreshRoute" class="refresh-btn" :disabled="!isNavigating">
              刷新路线
            </button>
          </div>
          
          <div id="nav-map" class="nav-map"></div>
          
          <div class="route-info" v-if="routeInfo.distance">
            <div class="info-item">
              <span class="label">预计时间：</span>
              <span class="value">{{ routeInfo.time }}</span>
            </div>
            <div class="info-item">
              <span class="label">距离：</span>
              <span class="value">{{ routeInfo.distance }}</span>
            </div>
            <div class="info-item">
              <span class="label">当前目标：</span>
              <span class="value">{{ getCurrentTarget() }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 状态更新按钮 -->
      <div class="action-section">
        <button 
          class="status-update-btn"
          :class="{ 
            'pickup-btn': orderDetail.status === 'ACCEPTED', 
            'complete-btn': orderDetail.status === 'PICKED',
            'disabled': !orderDetail.id
          }"
          @click="updateOrderStatus"
          :disabled="!orderDetail.id || isUpdatingStatus"
        >
          {{ getUpdateButtonText() }}
        </button>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { AMAP_CONFIG } from '@/config/amap.js'
import { mapState, mapActions } from 'vuex'
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'RiderOrderDetailView',
  components: { BottomNav },
  data() {
    return {
      // 状态管理
      isLoading: true,
      hasError: false,
      errorMessage: '',
      isUpdatingStatus: false,
      mapInitialized: false,
      
      // 订单数据
      orderDetail: {
        id: null,
        sellerName: '',
        sellerAddress: '',
        sellerLng: null,
        sellerLat: null,
        userAddress: '',
        userLng: null,
        userLat: null,
        userPhone: '',
        createTime: '',
        status: 'ACCEPTED' 
      },
      
      // 地图相关
      navMap: null,
      AMap: null,
      driving: null,
      geolocation: null,
      isNavigating: false,
      routeInfo: {
        time: '',
        distance: '',
        currentPosition: null
      },
      locationUpdateTimer: null,
      
      // 导航
      navItems: [
        { label: '订单搜索', action: () => { this.$router.push('/rider/orders') }, isActive: true },
        { label: '历史订单', action: () => { this.$router.push('/rider/history') } },
        { label: '个人中心', action: () => { /* 暂时不跳转 */ } }
      ],
    }
  },
  
  computed: {
    ...mapState('userStore', ['userInfo']),
    
    userId() {
      return this.userInfo?.userId || ''
    },
    
    orderId() {
      return this.$route.params.id
    },
    
    // 是否可以导航（地址信息是否完整）
    canNavigate() {
      return this.orderDetail.sellerAddress && 
             this.orderDetail.userAddress && 
             this.orderDetail.sellerAddress.trim() !== '' &&
             this.orderDetail.userAddress.trim() !== ''
    }
  },
  
  methods: {
    ...mapActions('locationStore', ['startLocationTracking', 'stopLocationTracking']),

    // 获取订单详情 - 移除测试数据
    async fetchOrderDetail() {
      if (!this.orderId) {
        this.hasError = true
        this.errorMessage = '订单ID缺失，无法加载订单信息'
        this.isLoading = false
        return
      }

      try {
        this.isLoading = true
        this.hasError = false
        
        const params = new URLSearchParams({ 
          riderId: this.userId, 
          orderId: this.orderId 
        }).toString()
        
        const response = await fetchWithTimeout(`${BASE_URL}/rider/orderdetail?${params}`)
        const result = await response.json()
        
        console.log('📋 获取订单详情响应:', result)
        
        const isSuccess = response.ok || result.code === 200 || result.success === true
        
        if (isSuccess && result.data) {
          this.orderDetail = {
            ...this.orderDetail,
            ...result.data
          }
          console.log('订单详情加载成功:', this.orderDetail)
        } else {
          this.hasError = true
          this.errorMessage = result.message || '服务器返回错误信息，请稍后重试'
          console.error('获取订单详情失败:', result)
        }
      } catch (error) {
        console.error('获取订单详情失败:', error)
        this.hasError = true
        
        if (error.name === 'AbortError') {
          this.errorMessage = '请求超时，请检查网络连接后重试'
        } else if (error.message.includes('请求被阻止')) {
          this.errorMessage = '请求参数错误，请返回重新进入'
        } else {
          this.errorMessage = '网络连接失败，请检查网络状态后重试'
        }
      } finally {
        this.isLoading = false
      }
    },

    // 重新获取订单信息
    async retryFetchOrder() {
      await this.fetchOrderDetail()
      
      // 如果成功加载且可以导航，初始化地图
      if (!this.hasError && this.canNavigate && !this.mapInitialized) {
        await this.initAMap()
      }
    },

    // 返回主页
    goBack() {
      this.$router.push('/rider')
    },

    // 获取缺失的地址信息
    getMissingAddressInfo() {
      const missing = []
      if (!this.orderDetail.sellerAddress || this.orderDetail.sellerAddress.trim() === '') {
        missing.push('商家地址')
      }
      if (!this.orderDetail.userAddress || this.orderDetail.userAddress.trim() === '') {
        missing.push('用户地址')
      }
      return missing.join('、')
    },

    // 获取更新按钮文本
    getUpdateButtonText() {
      if (this.isUpdatingStatus) {
        return '更新中...'
      }
      if (!this.orderDetail.id) {
        return '订单信息缺失'
      }
      return this.orderDetail.status === 'ACCEPTED' ? '确认接餐' : '确认送达'
    },

    // 初始化高德地图
    async initAMap() {
      if (!this.canNavigate) {
        console.warn('地址信息不完整，跳过地图初始化')
        return
      }

      try {
        console.log('开始初始化高德地图...')
        
        window._AMapSecurityConfig = {
          securityJsCode: AMAP_CONFIG.securityJsCode,
        };

        this.AMap = await AMapLoader.load({
          key: AMAP_CONFIG.key,
          version: AMAP_CONFIG.version,
          plugins: [
            'AMap.Geolocation',
            'AMap.Driving',
            'AMap.Marker',
            'AMap.InfoWindow',
            'AMap.Geocoder'
          ]
        })

        // 创建地图实例
        this.navMap = new this.AMap.Map('nav-map', {
          zoom: 11,
          center: [116.3974, 39.9093], // 北京市中心
          mapStyle: 'amap://styles/normal'
        })

        // 初始化驾车路径规划
        this.driving = new this.AMap.Driving({
          map: this.navMap,
          hideMarkers: false,
          showTraffic: true,
          autoFitView: true
        })

        // 初始化定位
        this.AMap.plugin('AMap.Geolocation', () => {
          this.geolocation = new this.AMap.Geolocation({
            enableHighAccuracy: true,
            timeout: 10000,
            convert: true,
            showButton: false,
            showMarker: true,
            showCircle: true,
            panToLocation: true
          })
        })

        this.mapInitialized = true
        console.log('地图初始化成功')

      } catch (error) {
        console.error('地图初始化失败:', error)
        // 地图加载失败不设置为整体错误，仅禁用导航功能
        this.mapInitialized = false
      }
    },

    // 更新订单状态
    async updateOrderStatus() {
      if (!this.orderDetail.id || this.isUpdatingStatus) return

      const nextStatus = this.orderDetail.status === 'ACCEPTED' ? 'PICKED' : 'COMPLETED'
      
      try {
        this.isUpdatingStatus = true
        
        const response = await fetchWithTimeout(`${BASE_URL}/rider/updateorder`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ 
            riderId: this.userId,
            orderId: this.orderDetail.id,
            status: nextStatus
          })
        })
        const result = await response.json()
        
        console.log('状态更新响应:', result)
        
        const isSuccess = response.ok || result.code === 200 || result.success === true
        
        if (isSuccess) {
          this.orderDetail.status = nextStatus
          
          if (nextStatus === 'COMPLETED') {
            // 订单完成，停止导航
            this.stopNavigation()
            alert('订单完成！3秒后返回主页')
            setTimeout(() => {
              this.$router.push('/rider')
            }, 3000)
          } else {
            alert('状态更新成功！')
            // 如果正在导航，重新规划到用户地址的路线
            if (this.isNavigating && this.routeInfo.currentPosition) {
              this.planRoute(this.routeInfo.currentPosition)
            }
          }
        } else {
          console.error('状态更新失败:', result)
          alert('状态更新失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('状态更新失败:', error)
        if (error.name === 'AbortError') {
          alert('请求超时，请重试')
        } else {
          alert('网络错误，状态更新失败')
        }
      } finally {
        this.isUpdatingStatus = false
      }
    },

    async startNavigation() {
      if (!this.mapInitialized) {
        alert('地图尚未初始化完成，请稍后重试')
        return
      }

      if (!this.orderDetail.id) {
        alert('订单信息缺失')
        return
      }

      try {
        console.log('开始导航...')
        this.isNavigating = true

        // 获取当前位置
        await this.getCurrentPositionAndPlan()
        
        // 开始实时位置追踪
        this.startRealTimeTracking()
        
        console.log('导航已启动')
      } catch (error) {
        console.error('启动导航失败:', error)
        this.isNavigating = false
        alert('启动导航失败：' + error.message)
      }
    },


    async getCurrentPositionAndPlan() {
      return new Promise((resolve, reject) => {
        if (!this.geolocation) {
          reject(new Error('定位服务未初始化'))
          return
        }

        this.geolocation.getCurrentPosition((status, result) => {
          if (status === 'complete') {
            console.log('获取到当前位置:', result.position)
            this.planRoute(result.position)
            resolve(result.position)
          } else {
            const error = this.getLocationError(result)
            console.error('获取位置失败:', error)
            reject(new Error(error))
          }
        })
      })
    },

    getLocationError(result) {
      const errorMap = {
        'PERMISSION_DENIED': '用户拒绝了地理位置请求，请在浏览器设置中允许位置权限',
        'POSITION_UNAVAILABLE': '位置信息不可用，请检查GPS或网络连接',
        'TIMEOUT': '获取位置请求超时，请重试',
        'NOT_SUPPORTED': '浏览器不支持地理定位'
      }
      return errorMap[result.info] || '获取位置失败：' + result.message
    },

    planRoute(currentPosition) {
      if (!this.AMap || !this.driving) {
        console.error('地图或路径规划服务未初始化')
        return
      }

      // 根据当前订单状态确定目标地址
      const targetAddress = this.orderDetail.status === 'ACCEPTED' 
        ? this.orderDetail.sellerAddress 
        : this.orderDetail.userAddress
      
      const targetName = this.orderDetail.status === 'ACCEPTED' 
        ? this.orderDetail.sellerName 
        : '用户地址'

      console.log('规划路线:', {
        from: `${currentPosition.lng}, ${currentPosition.lat}`,
        to: targetAddress,
        target: targetName
      })

      // 使用地理编码将地址转换为坐标
      this.AMap.plugin('AMap.Geocoder', () => {
        const geocoder = new this.AMap.Geocoder({
          city: '全国', // 扩大搜索范围
          radius: 5000
        })
        
        geocoder.getLocation(targetAddress, (status, result) => {
          if (status === 'complete' && result.geocodes.length > 0) {
            const targetCoords = result.geocodes[0].location
            console.log('目标坐标:', targetCoords.lng, targetCoords.lat)
            
            // 清除地图上的之前标记
            this.navMap.clearMap()
            
            // 开始路径规划
            this.driving.search(
              [currentPosition.lng, currentPosition.lat],
              [targetCoords.lng, targetCoords.lat],
              (status, result) => {
                if (status === 'complete' && result.routes && result.routes.length > 0) {
                  const route = result.routes[0]
                  console.log(' 路线规划成功:', route)
                  
                  // 更新路线信息
                  this.routeInfo = {
                    time: Math.round(route.time / 60) + ' 分钟',
                    distance: (route.distance / 1000).toFixed(1) + ' 公里',
                    currentPosition: currentPosition
                  }
                  
                  // 添加标记
                  this.addCustomMarkers(currentPosition, targetCoords.lng, targetCoords.lat, targetName)
                  
                  console.log('路线信息更新:', this.routeInfo)
                } else {
                  console.error('路线规划失败:', result)
                  alert('路线规划失败，请检查地址是否正确')
                }
              }
            )
          } else {
            console.error('地址解析失败:', result)
            alert(`无法解析地址：${targetAddress}，请检查地址是否正确`)
          }
        })
      })
    },


    startLocationTimer() {
      if (this.locationUpdateTimer) {
        clearInterval(this.locationUpdateTimer)
      }

      this.locationUpdateTimer = setInterval(() => {
        if (this.isNavigating && this.geolocation) {
          this.geolocation.getCurrentPosition((status, result) => {
            if (status === 'complete') {
              console.log('⏰ 定时位置更新:', result.position)
              this.routeInfo.currentPosition = result.position
            }
          })
        }
      }, 30000) // 每30秒更新一次

      console.log('启动定时位置更新')
    },


    stopNavigation() {
      console.log(' 停止导航')
      this.isNavigating = false
      
      if (this.locationUpdateTimer) {
        clearInterval(this.locationUpdateTimer)
        this.locationUpdateTimer = null
      }
      
      if (this.geolocation) {
        try {
          this.geolocation.clearWatch()
        } catch (e) {
          console.warn('清除位置监听失败:', e)
        }
      }
    },

    // 添加自定义标记
    addCustomMarkers(currentPosition, targetLng, targetLat, targetName) {
      new this.AMap.Marker({
        position: [currentPosition.lng, currentPosition.lat],
        title: '我的位置',
        icon: new this.AMap.Icon({
          size: new this.AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png'
        })
      }).setMap(this.navMap)

      new this.AMap.Marker({
        position: [targetLng, targetLat],
        title: targetName,
        icon: new this.AMap.Icon({
          size: new this.AMap.Size(25, 34),
          image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
        })
      }).setMap(this.navMap)
    },

    // 实时位置追踪
    startRealTimeTracking() {
      try {
        this.geolocation.watchPosition()
        
        this.geolocation.on('complete', (data) => {
          this.routeInfo.currentPosition = data.position
        })
        
        this.geolocation.on('error', (error) => {
          console.error('定位错误:', error)
        })
      } catch (error) {
        this.locationUpdateTimer = setInterval(() => {
          if (this.isNavigating && this.geolocation) {
            this.geolocation.getCurrentPosition((status, result) => {
              if (status === 'complete') {
                this.routeInfo.currentPosition = result.position
              }
            })
          }
        }, 30000)
      }
    },

    // 刷新路线
    refreshRoute() {
      if (this.routeInfo.currentPosition) {
        this.planRoute(this.routeInfo.currentPosition)
      }
    },

    // 获取当前目标
    getCurrentTarget() {
      if (this.orderDetail.status === 'ACCEPTED') {
        return `前往 ${this.orderDetail.sellerName || '商家'} 取餐`
      } else if (this.orderDetail.status === 'PICKED') {
        return '前往用户地址送餐'
      }
      return '订单已完成'
    },

    getStatusText(status) {
      const statusMap = {
        ACCEPTED: '已接单',
        PICKED: '已取餐', 
        COMPLETED: '已完成'
      }
      return statusMap[status] || '状态未知'
    },

    getStatusClass(status) {
      return {
        'status-accepted': status === 'ACCEPTED',
        'status-picked': status === 'PICKED',
        'status-completed': status === 'COMPLETED'
      }
    }
  },

  async mounted() {
    console.log('组件挂载，订单ID:', this.orderId)
    await this.fetchOrderDetail()
    
    // 只有在订单加载成功且地址信息完整时才初始化地图
    if (!this.hasError && this.canNavigate) {
      await this.initAMap()
    }
  },

  beforeUnmount() {
    this.stopNavigation()
    
    if (this.navMap) {
      this.navMap.destroy()
    }
  }
}
</script>

<style scoped>
.rider-order-detail {
  padding: 16px;
  background-color: #f9f9f9;
  min-height: 100vh;
}

/* 错误状态样式 */
.error-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.error-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-title {
  color: #d32f2f;
  margin-bottom: 12px;
  font-size: 20px;
}

.error-message {
  color: #666;
  margin-bottom: 24px;
  line-height: 1.5;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.retry-btn, .back-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.retry-btn {
  background-color: #1976d2;
  color: white;
}

.retry-btn:hover {
  background-color: #1565c0;
}

.back-btn {
  background-color: #757575;
  color: white;
}

.back-btn:hover {
  background-color: #616161;
}

/* 加载状态样式 */
.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.loading-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1976d2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #666;
  margin: 0;
}

/* 导航错误样式 */
.nav-error {
  text-align: center;
  padding: 24px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  margin-bottom: 16px;
}

.nav-error-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.nav-error-text {
  color: #856404;
  font-weight: 500;
  margin-bottom: 8px;
}

.nav-error-detail {
  color: #6c757d;
  font-size: 14px;
}

/* 其他样式保持不变 */
.order-detail-section, .navigation-section, .action-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}

.detail-card {
  display: grid;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item label {
  font-weight: 500;
  color: #333;
}

.detail-item span {
  color: #666;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.status-accepted {
  background-color: #e1f5fe;
  color: #01579b;
}

.status-picked {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-completed {
  background-color: #f3e5f5;
  color: #6a1b9a;
}

.nav-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.nav-btn, .refresh-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.nav-btn:disabled, .refresh-btn:disabled {
  background: #ddd;
  cursor: not-allowed;
}

.nav-btn {
  background-color: #007bff;
  color: #fff;
}

.refresh-btn {
  background-color: #28a745;
  color: #fff;
}

.nav-map {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  margin-bottom: 12px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #f1f1f1;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.info-item .label {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.info-item .value {
  color: #666;
}

.action-section {
  display: flex;
  justify-content: center;
}

.status-update-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.status-update-btn:disabled {
  background: #ddd;
  cursor: not-allowed;
}

.pickup-btn {
  background-color: #007bff;
  color: #fff;
}

.complete-btn {
  background-color: #28a745;
  color: #fff;
}

.disabled {
  background-color: #6c757d !important;
  color: #fff !important;
}
</style>