// School Onboarding API Service
// Handles all school onboarding related API calls

const isBrowser = typeof window !== "undefined";
const origin = isBrowser ? window.location?.origin : "";

// Use same-origin reverse proxy through nginx for all environments
const BASE_URL = `${origin}/user-management`;

/**
 * Submit a new school onboarding request
 * @param {Object} onboardingData - The onboarding form data
 * @returns {Promise<Object>} API response
 */
export const submitOnboardingRequest = async (onboardingData) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(onboardingData),
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("School onboarding submission error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Get all onboarding requests (Admin only)
 * @param {String} token - JWT authentication token
 * @returns {Promise<Object>} API response with list of onboarding requests
 */
export const getAllOnboardingRequests = async (token) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Fetch all onboarding requests error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Get onboarding requests filtered by status (Admin only)
 * @param {String} token - JWT authentication token
 * @param {String} status - Status to filter by (PENDING, IN_PROGRESS, COMPLETED, REJECTED)
 * @returns {Promise<Object>} API response with filtered list
 */
export const getOnboardingRequestsByStatus = async (token, status) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/list/by-status?status=${encodeURIComponent(status)}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Fetch onboarding requests by status error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Update the status of an onboarding request (Admin only)
 * @param {String} token - JWT authentication token
 * @param {Number} id - Onboarding request ID
 * @param {String} status - New status
 * @param {String} adminNotes - Optional admin notes
 * @returns {Promise<Object>} API response
 */
export const updateOnboardingStatus = async (token, id, status, adminNotes = null) => {
  try {
    let url = `${BASE_URL}/api/v1/school-onboarding/admin/${id}/status?status=${encodeURIComponent(status)}`;
    if (adminNotes) {
      url += `&adminNotes=${encodeURIComponent(adminNotes)}`;
    }

    const response = await fetch(url, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Update onboarding status error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Assign an onboarding request to an admin (Admin only)
 * @param {String} token - JWT authentication token
 * @param {Number} id - Onboarding request ID
 * @param {String} adminUsername - Admin username to assign to
 * @returns {Promise<Object>} API response
 */
export const assignToAdmin = async (token, id, adminUsername) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/${id}/assign?adminUsername=${encodeURIComponent(adminUsername)}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Assign to admin error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Set callback date for an onboarding request (Admin only)
 * @param {String} token - JWT authentication token
 * @param {Number} id - Onboarding request ID
 * @param {String} callbackDate - ISO format date string
 * @returns {Promise<Object>} API response
 */
export const setCallbackDate = async (token, id, callbackDate) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/${id}/callback-date?callbackDate=${encodeURIComponent(callbackDate)}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Set callback date error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Update priority of an onboarding request (Admin only)
 * @param {String} token - JWT authentication token
 * @param {Number} id - Onboarding request ID
 * @param {String} priority - New priority (LOW, MEDIUM, HIGH, URGENT)
 * @returns {Promise<Object>} API response
 */
export const updatePriority = async (token, id, priority) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/${id}/priority?priority=${encodeURIComponent(priority)}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Update priority error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};

/**
 * Approve an onboarding request and create a new school (Superadmin only)
 * @param {String} token - JWT authentication token
 * @param {Number} id - Onboarding request ID
 * @param {String} schoolCode - Unique school code to assign
 * @param {String} adminUsername - Superadmin username
 * @returns {Promise<Object>} API response
 */
export const approveAndCreateSchool = async (token, id, schoolCode, adminUsername) => {
  try {
    const response = await fetch(`${BASE_URL}/api/v1/school-onboarding/admin/${id}/approve?schoolCode=${encodeURIComponent(schoolCode)}&adminUsername=${encodeURIComponent(adminUsername)}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || `Failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Approve and create school error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
      data: null,
    };
  }
};
