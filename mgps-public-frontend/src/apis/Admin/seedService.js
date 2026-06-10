import apiClient from "../apiClient";

export const seedDefaultClasses = async () => {
  const response = await apiClient.post("/api/v1/admin/seed/default-classes");
  return response.data;
};

