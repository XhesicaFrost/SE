export const BASE_URL = 'http://localhost:12345'

export const FETCH_TIMEOUT = 8000; // 超时时间（毫秒）
export const debug_seller = false; // 是否启用商家调试模式
export const debug_rider = false; // 是否启用骑手调试模式
export const debug_user = false; // 是否启用用户调试模式
export const debug_admin = false; // 是否启用管理员调试模式
export const debug_AuthCheck = false; // 是否启用权限检查
export const debug_seller_created = true;//是否默认商家已经创立

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

  // 验证URL参数（对所有请求类型）
  try {
    const fullUrlString = resource.startsWith('http') ? resource : `${BASE_URL}${resource.startsWith('/') ? resource : '/' + resource}`;
    const url = new URL(fullUrlString);
    
    // 验证URL参数
    if (!validateUrlParams(url.searchParams)) {
      clearTimeout(timeoutId);
      throw new Error('请求被阻止：URL包含无效参数');
    }
  } catch (e) {
    console.warn('解析URL参数失败:', resource, e);
    clearTimeout(timeoutId);
    // 如果是我们的验证错误，重新抛出
    if (e.message.includes('请求被阻止')) {
      throw e;
    }
  }

  try {
    console.log('发起请求:', {
      url: resource,
      method: currentMethod,
      headers: options.headers,
      body: options.body // GET请求这里应该是 undefined
    });
    
    const response = await fetch(resource, { ...options, method: currentMethod, signal: controller.signal });
    clearTimeout(timeoutId);
    return response;
  } catch (error) {
    clearTimeout(timeoutId);
    throw error;
  }
}