const isBrowser = typeof window !== "undefined";
const hostname = isBrowser ? window.location?.hostname : "";
const origin = isBrowser ? window.location?.origin : "";
const isLocalhost = hostname === "localhost" || hostname === "127.0.0.1";

// Use same-origin reverse proxy through nginx for all environments
const DEFAULT_BASE_URL = `${origin}/school-management/api/v1`;

const envBaseUrl = process.env.REACT_APP_SM_BASE_URL;
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

const handleApiError = (error, navigate) => {
  if (error.message.includes("401") || error.message.includes("Unauthorized")) {
    alert("Token expired. Please log in again.");
    navigate("/login"); // Redirect to login
  } else {
    console.error("API Error:", error);
    throw error;
  }
};

const CreateEntity = async (postData, name, token) => {
  console.log("POst Data is ", postData);
  try {
    const response = await fetch(`${BASE_URL}/${name}/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(postData),
    });

    if (response.ok) {
      const data = await response.json();
      console.log(data)
      return data;
    }
  } catch (error) {
    throw new Error('Create entity failed', error);
  }
}

const GetAll = async (entity, token, navigate) => {
  try {
    console.log("Token being used:", token); // Debugging: Log the token
    const response = await fetch(`${BASE_URL}/${entity}/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      return data;
    } else if (response.status === 401) {
      alert("Session expired. Redirecting to login.");
      navigate("/login"); // Redirect to login on 401
    } else {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
  } catch (error) {
    console.error("Error in GetAll:", error);
    if (navigate) handleApiError(error, navigate); // Handle 401 errors
  }
};

const Delete = async (id, name, token) => {
  try {
    const response = await fetch(`${BASE_URL}/${name}/delete/${id}`, {
      method: 'Delete',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return data;
    }
  } catch (error) {
    throw new Error('Delete entity failed', error);
  }

}

const UpdateEnitity = async (id, postData, name, token) => {
  try {
    const response = await fetch(`${BASE_URL}/${name}/update/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json', 'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(postData),

    });

    if (response.ok) {
      const data = await response.json();
      return data;
    }
  } catch (error) {
    throw new Error('Update entity failed', error);
  }
}

export { CreateEntity, GetAll, Delete, UpdateEnitity }
