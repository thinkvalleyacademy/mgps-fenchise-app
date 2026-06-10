const isBrowser = typeof window !== "undefined";
const hostname = isBrowser ? window.location?.hostname : "";
const origin = isBrowser ? window.location?.origin : "";
const isLocalhost = hostname === "localhost" || hostname === "127.0.0.1";

// Use same-origin reverse proxy through nginx for all environments
const DEFAULT_BASE_URL = `${origin}/fee-management/api/v1/`;

const envBaseUrl = process.env.REACT_APP_FM_BASE_URL;
const allowCrossOriginApi = process.env.REACT_APP_ALLOW_CROSS_ORIGIN_API === "true";
const shouldIgnoreEnvUrl = !isLocalhost && typeof envBaseUrl === "string" && envBaseUrl.includes("localhost");

let URL = DEFAULT_BASE_URL;
if (!shouldIgnoreEnvUrl && typeof envBaseUrl === "string" && envBaseUrl.trim()) {
  try {
    const envUrl = new URL(envBaseUrl);
    const sameHost = envUrl.hostname === hostname;
    if (sameHost || allowCrossOriginApi) {
      URL = envBaseUrl;
    }
  } catch {
    URL = envBaseUrl;
  }
}

const RegisterCollection = async (token,postData) => {
  try {
    const response = await fetch(`${URL}feeCollection/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify(postData),
    });
    if (response.ok) {
      const data = await response.json();

      return data;
    } else {
      throw new Error('Register  failed');
    }
  } catch (error) {
    throw new Error('Register failed');
  }
}


const AddStructure = async (postData) => {
  try {
    const response = await fetch(`${URL}feeStructure/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(postData),
    });
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      throw new Error('Register  failed');
    }
  } catch (error) {
    throw new Error('Register failed');
  }
}


const GetAllStructure = async () => {
  try {
    const response = await fetch(`${URL}feeStructure/list`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      const data = await response.json();
      const data_set = data.data;
      return data_set;
    } else {
      throw new Error('Some Error occurred');
    }
  } catch (error) {
    throw new Error('Some Error occurred');
  }
}

const DeleteStructure = async (id) => {
  try {
    const response = await fetch(`${URL}feeStructure/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      throw new Error('Some Error occurred');
    }
  } catch (error) {
    throw new Error('Some Error occurred');
  }
}

const UpdateStructureByID = async (id, postData) => {
  try {
    const response = await fetch(`${URL}feeStructure/update/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(postData),
    });
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      throw new Error('Some Error occurred');
    }
  } catch (error) {
    throw new Error('Some Error occurred');
  }
}


const GetInvoice = async (userName, quarter, year) => {
  console.log("Invoice called")
  console.log(`${URL}invoice/get-invoice
      ?studentUsername=${userName}&quarter=${quarter}&financialYear=${year}`)
  try {
    const response = await fetch(`${URL}invoice/get-invoice?studentUsername=${userName}&quarter=${quarter}&financialYear=${year}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      throw new Error('Some Error occurred');
    }
  } catch (error) {
    throw new Error('Some Error occurred');
  }
}


export {RegisterCollection,GetAllStructure,AddStructure,DeleteStructure,UpdateStructureByID, GetInvoice}
