// 应用配置（直接请求后端，不使用代理）
export const BASE_URL = 'http://localhost:12345'  // 修正：添加了缺失的 //

export const FETCH_TIMEOUT = 8000; // 超时时间（毫秒）
export const debug_seller = false; // 是否启用商家调试模式
export const debug_rider = false; // 是否启用骑手调试模式
export const debug_user = false; // 是否启用用户调试模式
export const debug_admin = false; // 是否启用管理员调试模式
export const debug_AuthCheck = true; // 是否启用权限检查
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
  // 检查是否为事件对象
  if (data && typeof data === 'object') {
    // 检测常见的事件对象属性
    const eventProperties = ['isTrusted', 'type', 'target', 'currentTarget', 'preventDefault', 'stopPropagation', 'stopImmediatePropagation', '_vts'];
    // 使用安全的方式检查属性
    const hasEventProperties = eventProperties.some(prop => Object.prototype.hasOwnProperty.call(data, prop));
    
    if (hasEventProperties) {
      console.error(`❌ 检测到事件对象被传递为请求数据 (${source}):`, {
        hasIsTrusted: 'isTrusted' in data,
        hasType: 'type' in data,
        hasTarget: 'target' in data,
        hasVts: '_vts' in data,
        keys: Object.keys(data).slice(0, 10) // 只显示前10个属性
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
    // 检查是否包含事件对象相关的参数
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
    console.error('❌ 检测到URL中包含事件对象参数:', invalidParams);
    return false;
  }
  
  return true;
}

export async function fetchWithTimeout(resource, options = {}) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT);

  const currentMethod = options.method || 'GET';

  // 验证 options.body 数据（仅对非GET请求）
  if (options.body && currentMethod !== 'GET') {
    if (!validateRequestData(options.body, 'options.body')) {
      clearTimeout(timeoutId);
      throw new Error('请求被阻止：检测到无效的请求体数据');
    }
    
    // 如果body是字符串，尝试解析并验证
    if (typeof options.body === 'string') {
      try {
        const parsedBody = JSON.parse(options.body);
        if (!validateRequestData(parsedBody, 'parsed options.body')) {
          clearTimeout(timeoutId);
          throw new Error('请求被阻止：请求体包含无效数据');
        }
      } catch (e) {
        // JSON解析失败，可能是其他格式的数据，继续处理
      }
    }
  }

  // 对于GET请求，如果有body，发出警告并移除
  if (currentMethod === 'GET' && options.body) {
    console.warn('⚠️ GET请求不应该包含请求体，已自动移除');
    delete options.body;
  }

  // 获取用户 token 并添加到 Authorization Header
  const token = getUserToken();
  console.log('🔑 获取到用户 token:', token ? `${token.substring(0, 10)}...` : '无 token')
  
  // 初始化 headers
  if (!options.headers) {
    options.headers = {};
  }
  
  // 如果有 token，添加到 Authorization header
  if (token && token.trim() !== '') {
    options.headers['Authorization'] = `Bearer ${token}`;
    console.log('🔐 已添加 Authorization header');
  }

  // 修正：直接拼接 BASE_URL，不使用代理
  let finalUrl = resource;
  
  // 如果 resource 不是完整的 URL（不以 http 开头）
  if (!resource.startsWith('http')) {
    // 直接拼接 BASE_URL
    if (resource.startsWith('/')) {
      finalUrl = BASE_URL + resource;  // http://localhost:12345 + /login
    } else {
      finalUrl = BASE_URL + '/' + resource;  // http://localhost:12345 + / + login
    }
  }

  // 验证URL参数
  try {
    const urlForValidation = new URL(finalUrl);
    
    // 验证URL参数
    if (!validateUrlParams(urlForValidation.searchParams)) {
      clearTimeout(timeoutId);
      throw new Error('请求被阻止：URL包含无效参数');
    }
  } catch (e) {
    console.warn('解析URL参数失败:', finalUrl, e);
    clearTimeout(timeoutId);
    // 如果是我们的验证错误，重新抛出
    if (e.message.includes('请求被阻止')) {
      throw e;
    }
  }

  try {
    console.log('发起请求:', {
      originalResource: resource,    // 原始传入的 resource
      finalUrl: finalUrl,           // 最终请求的 URL
      method: currentMethod,
      headers: options.headers,
      body: options.body,
      hasToken: !!token
    });
    
    // 使用修正后的 finalUrl 发送请求
    const response = await fetch(finalUrl, { 
      ...options, 
      method: currentMethod, 
      signal: controller.signal 
    });
    
    // 检查 401 未授权错误
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
      
      // 尝试解析响应体获取详细错误信息
      let errorDetail = '';
      try {
        const errorData = await response.json();
        errorDetail = errorData.message || errorData.error || '未知错误';
      } catch (e) {
        errorDetail = response.statusText || '身份验证失败';
      }
      
      // 抛出特定的 401 错误
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
    
    // 如果是我们的 401 错误，直接重新抛出
    if (error.name === 'AuthenticationError') {
      throw error;
    }
    
    // 其他错误正常抛出
    throw error;
  }
}