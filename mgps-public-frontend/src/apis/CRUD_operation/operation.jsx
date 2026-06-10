// Import mock API service
import {
  mockGetStudentList as mockGetListApi,
  mockGetStudentDetails as mockGetDetailsApi,
  mockRegisterStudent as mockRegisterApi,
  mockUpdateStudent as mockUpdateApi,
  mockDeleteStudent as mockDeleteApi,
} from '../MockApi/mockApiService';

// Use same-origin reverse proxy through nginx for all environments
const isBrowser = typeof window !== "undefined";
const hostname = isBrowser ? window.location?.hostname : "";
const origin = isBrowser ? window.location?.origin : "";
const isLocalhost = hostname === "localhost" || hostname === "127.0.0.1";

const DEFAULT_BASE_URL = `${origin}/user-management`;

const envBaseUrl = process.env.REACT_APP_BASE_URL;
const allowCrossOriginApi = process.env.REACT_APP_ALLOW_CROSS_ORIGIN_API === "true";
const shouldIgnoreEnvUrl = !isLocalhost && typeof envBaseUrl === "string" && envBaseUrl.includes("localhost");

let BASE_URL = DEFAULT_BASE_URL;
if (!shouldIgnoreEnvUrl && typeof envBaseUrl === "string" && envBaseUrl.trim()) {
  try {
    const envUrl = new URL(envBaseUrl);
    const sameHost = envUrl.hostname === hostname;
    if (sameHost || allowCrossOriginApi) {
      BASE_URL = envBaseUrl;
    }
  } catch {
    BASE_URL = envBaseUrl;
  }
}
// On server: http://100.101.103.63:7083/user-management
// Backend context path is included in BASE_URL for server
const API_BASE_URL = `${BASE_URL}/api/v1/student/`;

const USE_MOCK_API = false; // Set to true to enable mock API for testing

const handleApiError = (error, navigate) => {
  if (error.message.includes("401") || error.message.includes("Unauthorized")) {
    if (navigate) {
      alert("Token expired. Please log in again.");
      navigate("/login");
    }
  } else {
    console.error("API Error:", error);
    throw error;
  }
};

const RegisterUser = async (userType, postData) => {
  if (USE_MOCK_API) {
    return await mockRegisterApi(postData);
  }

  try {
    console.log("API Payload:", postData);

    const headers = {
      'Authorization': `Bearer ${localStorage.getItem('authToken') || ''}`,
    };

    let body;
    if (postData instanceof FormData) {
      const obj = {};
      for (const [key, value] of postData.entries()) {
        if (value instanceof File) continue; // skip files when converting to JSON
        obj[key] = value;
      }
      body = JSON.stringify(obj);
      headers['Content-Type'] = 'application/json';
    } else if (typeof postData === 'object') {
      body = JSON.stringify(postData);
      headers['Content-Type'] = 'application/json';
    } else {
      body = postData;
    }

    const response = await fetch(`${API_BASE_URL}register`, {
      method: 'POST',
      headers,
      body,
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Registration failed with status ${response.status}`);
    }

    const data = await response.json();
    return data.data;
  } catch (error) {
    console.error("Error in RegisterUser:", error);
    throw error;
  }
};

const BulkImportStudents = async (file) => {
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${API_BASE_URL}register/bulk`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("authToken") || ""}`,
      },
      body: formData,
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Bulk import failed with status ${response.status}`);
    }

    const data = await response.json();
    return data.data;
  } catch (error) {
    console.error("Error in BulkImportStudents:", error);
    throw error;
  }
};

const DeleteUser = async (accountType, registrationNumber, token) => {
  if (USE_MOCK_API) {
    return await mockDeleteApi(registrationNumber, token);
  }

  try {
    const response = await fetch(`${API_BASE_URL}delete?registrationNumber=${encodeURIComponent(registrationNumber)}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Delete failed with status ${response.status}`);
    }

    const data = await response.json();
    return { status: response.status, data: data.data };
  } catch (error) {
    console.error("Error in DeleteUser:", error);
    throw error;
  }
};

const getInfoByRegistrationNumber = async(registrationNumber, token) => {
  if (USE_MOCK_API) {
    return await mockGetDetailsApi(registrationNumber, token);
  }

  try {
    const response = await fetch(`${API_BASE_URL}getDetails?registrationNumber=${encodeURIComponent(registrationNumber)}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Get details failed with status ${response.status}`);
    }

    const data = await response.json();
    return data.data;
  } catch (error) {
    console.error("Error in getInfoByRegistrationNumber:", error);
    throw error;
  }
}

// Alias for backward compatibility
const getInfoByUserName = async(userType, userName, token) => {
  return getInfoByRegistrationNumber(userName, token);
}

const fetchListData = async (token, userType, grade = null, section = null, navigate) => {
  if (USE_MOCK_API) {
    return await mockGetListApi(token, userType, grade, section);
  }

  try {
    console.log("Fetching list data for userType:", userType);
    console.log("Grade passed to API:", grade);
    console.log("Section passed to API:", section);

    const queryParams = new URLSearchParams();
    if (grade) queryParams.append("grade", grade);
    if (section) queryParams.append("section", section);

    const response = await fetch(`${API_BASE_URL}list?${queryParams.toString()}`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Fetch list failed with status ${response.status}`);
    }

    const data = await response.json();
    return data.data;
  } catch (error) {
    handleApiError(error, navigate);
    throw error;
  }
};

const updateUser = async (postData, token) => {
  if (USE_MOCK_API) {
    return await mockUpdateApi(postData, token);
  }

  try {
    const response = await fetch(`${API_BASE_URL}updateDetails`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(postData),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Update failed with status ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error in updateUser:", error);
    throw error;
  }
}

export { RegisterUser, BulkImportStudents, DeleteUser, getInfoByRegistrationNumber, getInfoByUserName, fetchListData, updateUser };
