// src/apis/apiClient.js
// Axios instance configured for multi-tenant architecture

import axios from 'axios';

const isBrowser = typeof window !== 'undefined';
const hostname = isBrowser ? window.location?.hostname : '';
const origin = isBrowser ? window.location?.origin : '';
const port = isBrowser ? window.location?.port : '';
const isLocalhost = hostname === 'localhost' || hostname === '127.0.0.1';
const isDevServer = isLocalhost && (port === '3000' || port === '7902');

const DEFAULT_API_BASE_URL = `${origin}/user-management`;

const envApiUrl = process.env.REACT_APP_API_URL;
const envBaseUrl = process.env.REACT_APP_BASE_URL;
const allowCrossOriginApi = process.env.REACT_APP_ALLOW_CROSS_ORIGIN_API === 'true';

// Avoid shipping a build that hard-codes localhost URLs in production.
const inferredEnvUrl = envApiUrl || envBaseUrl;
const shouldIgnoreEnvUrl = !isLocalhost && typeof inferredEnvUrl === 'string' && inferredEnvUrl.includes('localhost');

let API_BASE_URL = DEFAULT_API_BASE_URL;
if (!shouldIgnoreEnvUrl && typeof inferredEnvUrl === 'string' && inferredEnvUrl.trim()) {
  try {
    const envUrl = new URL(inferredEnvUrl);
    const sameHost = envUrl.hostname === hostname;
    if (isDevServer || sameHost || allowCrossOriginApi) {
      API_BASE_URL = inferredEnvUrl;
    }
  } catch {
    API_BASE_URL = inferredEnvUrl;
  }
}

/**
 * Create axios instance with default configuration
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds
});

/**
 * Extract school code from domain/subdomain
 * @returns {string} School code
 */
const extractSchoolCodeFromDomain = () => {
  const host = window.location.hostname;
  const parts = host.split('.');
  
  // For subdomain: mgps.mgps.com -> mgps
  if (parts.length > 2) {
    return parts[0].toUpperCase();
  }
  
  // For localhost or custom domain, use environment variable
  return process.env.REACT_APP_DEFAULT_SCHOOL_CODE || 'MGPS';
};

/**
 * Request interceptor - Add school context and auth token to all requests
 */
apiClient.interceptors.request.use(
  (config) => {
    // Add school code header (extracted from subdomain)
    const schoolCode = extractSchoolCodeFromDomain();
    if (schoolCode) {
      config.headers['X-School-Code'] = schoolCode;
    }

    // Add auth token if available
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle common errors
 */
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle 401 Unauthorized - Token expired or invalid
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userRole');
      window.location.href = '/login';
    }
    
    // Handle 404 School Not Found
    if (error.response?.status === 404 && 
        error.response?.data?.errorCode === 'SCHOOL_NOT_FOUND') {
      window.location.href = '/school-not-found';
    }
    
    // Handle 403 Subscription Expired
    if (error.response?.status === 403 && 
        error.response?.data?.errorCode === 'SUBSCRIPTION_EXPIRED') {
      window.location.href = '/subscription-expired';
    }
    
    // Handle 402 Quota Exceeded
    if (error.response?.status === 402 && 
        error.response?.data?.errorCode === 'QUOTA_EXCEEDED') {
      // Show quota exceeded message
      console.warn('Quota exceeded:', error.response?.data?.message);
    }
    
    return Promise.reject(error);
  }
);

/**
 * Set auth token in localStorage and headers
 * @param {string} token JWT token
 */
export const setAuthToken = (token) => {
  if (token) {
    localStorage.setItem('authToken', token);
  } else {
    localStorage.removeItem('authToken');
  }
};

/**
 * Remove auth token and redirect to login
 */
export const removeAuthToken = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userRole');
  window.location.href = '/login';
};

export default apiClient;
