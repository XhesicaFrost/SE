export const BASE_URL = 'http://localhost:3000'

export const FETCH_TIMEOUT = 8000; // 超时时间（毫秒）

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