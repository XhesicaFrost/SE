export const BASE_URL = 'http://localhost:3000'

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
  try {
    const response = await fetch(resource, { ...options, signal: controller.signal });
    clearTimeout(timeoutId);
    return response;
  } catch (error) {
    clearTimeout(timeoutId);
    throw error;
  }
}