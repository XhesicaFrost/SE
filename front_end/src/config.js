// 根据环境动态设置 BASE_URL
const getBaseUrl = () => {
  // 检查是否有环境变量设置的API地址
  if (process.env.VUE_APP_API_BASE_URL) {
    return process.env.VUE_APP_API_BASE_URL
  }
  
  // Docker 环境下后端服务名
  if (process.env.NODE_ENV === 'production') {
    return 'http://localhost:12345'  // 生产环境，通过宿主机端口访问
  }
  return 'http://localhost:12345'  // 开发环境
}

export const BASE_URL = getBaseUrl()

export const FETCH_TIMEOUT = 8000; // 超时时间（毫秒）
export const debug_seller = false; // 是否启用商家调试模式
export const debug_rider = false; // 是否启用骑手调试模式
export const debug_user = false; // 是否启用用户调试模式
export const debug_admin = false; // 是否启用管理员调试模式
export const debug_AuthCheck = false; // 是否启用权限检查
export const debug_seller_created = false;//是否默认商家已经创立

let storeInstance = null;

/**
 * 设置 store 实例引用
 * 在 main.js 中调用此方法来设置 store 引用
 * @param {Object} store - Vuex store 实例
 */
export function setStoreInstance(store) {
  storeInstance = store;
}

/**
 * 获取当前用户的 token
 * @returns {string} - 用户 token 或空字符串
 */
function getUserToken() {
  try {
    if (storeInstance && storeInstance.state.userStore) {
      return storeInstance.state.userStore.userInfo.token || '';
    }
    return '';
  } catch (error) {
    console.warn('获取用户token失败:', error);
    return '';
  }
}

/**
 * 验证请求参数是否为有效数据
 * @param {any} data - 要验证的数据
 * @param {string} source - 数据来源描述（用于日志）
 * @returns {boolean} - 是否为有效数据
 */
function validateRequestData(data, source = 'unknown') {
  if (data && typeof data === 'object') {
    const eventProperties = ['isTrusted', 'type', 'target', 'currentTarget', 'preventDefault', 'stopPropagation', 'stopImmediatePropagation', '_vts'];

    const hasEventProperties = eventProperties.some(prop => Object.prototype.hasOwnProperty.call(data, prop));
    
    if (hasEventProperties) {
      console.error(` 检测到事件对象被传递为请求数据 (${source}):`, {
        hasIsTrusted: 'isTrusted' in data,
        hasType: 'type' in data,
        hasTarget: 'target' in data,
        hasVts: '_vts' in data,
        keys: Object.keys(data).slice(0, 10) 
      });
      return false;
    }
  }
  
  return true;
}

/**
 * 验证URL参数
 * @param {URLSearchParams} searchParams - URL查询参数
 * @returns {boolean} - 是否包含无效参数
 */
function validateUrlParams(searchParams) {
  const invalidParams = [];
  
  for (const [key, value] of searchParams) {
    if (key === 'isTrusted' || 
        key === '_vts' || 
        key.includes('stopImmediatePropagation') ||
        key.includes('preventDefault') ||
        value.includes('originalStop.call') ||
        value.includes('_stopped')) {
      invalidParams.push({ key, value: value.substring(0, 100) }); // 限制value长度用于日志
    }
  }
  
  if (invalidParams.length > 0) {
    console.error(' 检测到URL中包含事件对象参数:', invalidParams);
    return false;
  }
  
  return true;
}

export async function fetchWithTimeout(resource, options = {}) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT);

  const currentMethod = options.method || 'GET';

  if (options.body && currentMethod !== 'GET') {
    if (!validateRequestData(options.body, 'options.body')) {
      clearTimeout(timeoutId);
      throw new Error('请求被阻止：检测到无效的请求体数据');
    }
    
    if (typeof options.body === 'string') {
      try {
        const parsedBody = JSON.parse(options.body);
        if (!validateRequestData(parsedBody, 'parsed options.body')) {
          clearTimeout(timeoutId);
          throw new Error('请求被阻止：请求体包含无效数据');
        }
      } catch (e) {
        // 如果是字符串但不是有效的 JSON，直接抛出错误
      }
    }
  }

  if (currentMethod === 'GET' && options.body) {
    console.warn('⚠️ GET请求不应该包含请求体，已自动移除');
    delete options.body;
  }

  const token = getUserToken();
  console.log('获取到用户 token:', token ? `${token.substring(0, 10)}...` : '无 token')
  
  // 初始化 headers
  if (!options.headers) {
    options.headers = {};
  }
  
  if (token && token.trim() !== '') {
    options.headers['Authorization'] = `Bearer ${token}`;
    console.log('🔐 已添加 Authorization header');
  }

  let finalUrl = resource;
  
  if (!resource.startsWith('http')) {
    // 直接拼接 BASE_URL
    if (resource.startsWith('/')) {
      finalUrl = BASE_URL + resource;  
    } else {
      finalUrl = BASE_URL + '/' + resource; 
    }
  }

  try {
    const urlForValidation = new URL(finalUrl);
    
    if (!validateUrlParams(urlForValidation.searchParams)) {
      clearTimeout(timeoutId);
      throw new Error('请求被阻止：URL包含无效参数');
    }
  } catch (e) {
    console.warn('解析URL参数失败:', finalUrl, e);
    clearTimeout(timeoutId);
    if (e.message.includes('请求被阻止')) {
      throw e;
    }
  }

  try {
    console.log('发起请求:', {
      originalResource: resource,    
      finalUrl: finalUrl,           
      method: currentMethod,
      headers: options.headers,
      body: options.body,
      hasToken: !!token
    });
    
    const response = await fetch(finalUrl, { 
      ...options, 
      method: currentMethod, 
      signal: controller.signal 
    });
    
    if (response.status === 401) {
      clearTimeout(timeoutId);
      
      console.error('🚫 身份验证失败 (401):', {
        url: finalUrl,
        method: currentMethod,
        hasToken: !!token,
        token: token ? `${token.substring(0, 10)}...` : 'null',
        response: {
          status: response.status,
          statusText: response.statusText
        }
      });
      
      let errorDetail = '';
      try {
        const errorData = await response.json();
        errorDetail = errorData.message || errorData.error || '未知错误';
      } catch (e) {
        errorDetail = response.statusText || '身份验证失败';
      }
      
      const authError = new Error(`身份验证失败: ${errorDetail}`);
      authError.name = 'AuthenticationError';
      authError.status = 401;
      authError.response = response;
      throw authError;
    }
    
    clearTimeout(timeoutId);
    return response;
  } catch (error) {
    clearTimeout(timeoutId);
    
    if (error.name === 'AuthenticationError') {
      throw error;
    }
    
    throw error;
  }
}