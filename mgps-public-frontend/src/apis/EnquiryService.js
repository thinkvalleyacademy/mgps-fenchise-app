const origin = typeof window !== "undefined" ? window.location?.origin : "";
const BASE_URL = process.env.REACT_APP_BASE_URL || `${origin}/api`;
const cleanBaseUrl = BASE_URL.replace(/\/$/, "");

/**
 * Submit an admission enquiry to the backend.
 * @param {Object} formData - The enquiry form data.
 * @returns {Promise<Object>} The API response.
 */
export const admissionQuery = async (formData) => {
  try {
    const enquiryData = {
      fullName: formData.fullName,
      email: formData.email,
      mobileNumber: formData.mobileNumber,
      studentClass: formData.class || formData.studentClass,
      query: formData.query
    };

    const response = await fetch(`${cleanBaseUrl}/enquiries`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(enquiryData),
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || `Enquiry submission failed with status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error("Admission query error:", error);
    return {
      status: error.status || 500,
      message: error.message || "An error occurred. Please try again later.",
    };
  }
};
