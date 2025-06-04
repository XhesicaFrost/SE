export const BASE_URL = 'http://localhost:12345'

export const FETCH_TIMEOUT = 8000; // 超时时间（毫秒）
export const debug_seller = false; // 是否启用商家调试模式
export const debug_rider = false; // 是否启用骑手调试模式
export const debug_user = false; // 是否启用用户调试模式
export const debug_admin = false; // 是否启用管理员调试模式
export const debug_AuthCheck = false; // 是否启用权限检查
export const debug_seller_created = true;//是否默认商家已经创立
export async function fetchWithTimeout(resource, options = {}) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT);

  const currentMethod = options.method || 'GET';

  // 如果是GET请求，并且URL中有参数，则将参数解析出来放入body
  if (currentMethod === 'GET') {
    try {
      // 为了正确解析，需要一个完整的URL。如果resource是相对路径，我们用BASE_URL拼接。
      const fullUrlString = resource.startsWith('http') ? resource : `${BASE_URL}${resource.startsWith('/') ? resource : '/' + resource}`;
      const url = new URL(fullUrlString);
      
      if (url.searchParams && url.searchParams.toString() !== '') {
        const params = {};
        url.searchParams.forEach((value, key) => {
          params[key] = value;
        });
        // 将解析的参数JSON字符串化后放入options.body
        // 注意：这对于标准的GET请求是不寻常的
        options.body = JSON.stringify(params);
      }
    } catch (e) {
      console.warn('解析URL参数失败:', resource, e);
      // 如果解析失败，保持options.body不变
    }
  }

  try {
    console.log('发起请求:', {
      url: resource,
      method: currentMethod, // 使用确定的请求方法
      headers: options.headers,
      body: options.body // 注意：如果body是流，这里可能不会显示具体内容
    });
    const response = await fetch(resource, { ...options, method: currentMethod, signal: controller.signal });
    clearTimeout(timeoutId);
    return response;
  } catch (error) {
    clearTimeout(timeoutId);
    throw error;
  }
}