// Import mock API service
import {
  mockLogin as mockLoginApi,
} from '../MockApi/mockApiService';

const isBrowser = typeof window !== "undefined";
const hostname = isBrowser ? window.location?.hostname : "";
const origin = isBrowser ? window.location?.origin : "";
const isLocalhost = hostname === "localhost" || hostname === "127.0.0.1";

// Use same-origin reverse proxy through nginx for all environments
const DEFAULT_BASE_URL = `${origin}/user-management`;

const envBaseUrl = process.env.REACT_APP_BASE_URL;
const allowCrossOriginApi = process.env.REACT_APP_ALLOW_CROSS_ORIGIN_API === "true";

// Ignore env urls that hard-code localhost in non-localhost builds.
const shouldIgnoreEnvUrl = !isLocalhost && typeof envBaseUrl === "string" && envBaseUrl.includes("localhost");

let BASE_URL = DEFAULT_BASE_URL;
if (!shouldIgnoreEnvUrl && typeof envBaseUrl === "string" && envBaseUrl.trim()) {
  try {
    const envUrl = new URL(envBaseUrl);
    const sameHost = envUrl.hostname === hostname;
    // For nginx-served builds, prefer same-origin. Allow cross-origin only if explicitly configured.
    if (sameHost || allowCrossOriginApi) {
      BASE_URL = envBaseUrl;
    }
  } catch {
    // If it's not a valid absolute URL, allow it as-is (e.g. relative path)
    BASE_URL = envBaseUrl;
  }
}
// On server: http://100.101.103.63:7083/user-management
// Backend context path is included in BASE_URL for server
const LOGIN_URL = `${BASE_URL}/api/v1/auth/login`;
const SCHOOL_CODES_URL = `${BASE_URL}/api/v1/school/codes`;

const USE_MOCK_API = false; // Set to true to enable mock API for testing

const loginUser = async (userData) => {
  // Use mock API if enabled
  if (USE_MOCK_API) {
    return await mockLoginApi(userData);
  }

  try {
    const headers = {
      "Content-Type": "application/json",
    };

    if (userData.userType !== "superadmin") {
      headers["X-School-Code"] = userData.schoolCode || "MGPS";
    }

    const response = await fetch(LOGIN_URL, {
      method: "POST",
      headers,
      body: JSON.stringify(userData),
    });

    const data = await response.json();

    if (data?.status === 200 && data?.message === "Login Success") {
      return { data_set: data.data, error: null };
    }

    const reason = data?.data?.reason || data?.message || "Login failed. Please try again.";
    return { data_set: null, error: reason };
  } catch (error) {
    return {
      data_set: null,
      error: "An error occurred. Please try again later.",
    };
  }
};

const fetchActiveSchools = async () => {
  try {
    const response = await fetch(SCHOOL_CODES_URL, {
      method: "GET",
      headers: {
        "Accept": "application/json",
      },
    });

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      return { data_set: [], error: data?.message || `Unable to load schools (HTTP ${response.status})` };
    }

    return { data_set: Array.isArray(data?.data) ? data.data : [], error: null };
  } catch (error) {
    return { data_set: [], error: "Unable to load schools. You can enter the school code manually." };
  }
};

const admissionQuery = async (formData) => {
  try {
    // Transform formData to match backend API expectations
    const enquiryData = {
      fullName: formData.fullName,
      email: formData.email,
      mobileNumber: formData.mobileNumber,
      studentClass: formData.class,  // Map 'class' to 'studentClass'
      query: formData.query
    };

    // Use the correct admission enquiry endpoint
    const response = await fetch(`${BASE_URL}/api/v1/user/registerEnquiry`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(enquiryData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || `Enquiry submission failed with status: ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Admission query error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
    };
  }
};

const registerEmployee = async (formData) => {
  try {
    const payload = {};
    Object.entries(formData || {}).forEach(([key, value]) => {
      if (value !== undefined && value !== null && `${value}`.trim() !== "") {
        payload[key] = value;
      }
    });

    const response = await fetch(`${BASE_URL}/api/v1/employee/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-School-Code": payload.schoolCode || "MGPS",
      },
      body: JSON.stringify(payload),
    });

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const msg = data?.message || `Signup failed (HTTP ${response.status})`;
      return { data_set: null, error: msg };
    }

    if (data?.statusCode && data?.statusCode !== 200) {
      return { data_set: null, error: data?.message || "Signup failed." };
    }

    return { data_set: data?.data ?? data, error: null };
  } catch (error) {
    return { data_set: null, error: "An error occurred. Please try again later." };
  }
};

export { loginUser, admissionQuery, registerEmployee, fetchActiveSchools };
