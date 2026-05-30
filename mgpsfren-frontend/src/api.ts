import type {
  SchoolRegistrationPayload,
  SchoolSummary,
  SubscriptionPlan,
  SuperAdminSetupPayload,
  SuperAdminStatus,
  AuthResponse,
  AuthProfile
} from './types';

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '') ?? 'http://localhost:8080/api';

let authToken: string | null = null;

export function setAuthToken(token: string | null) {
  authToken = token;
}

type ApiResponse<T> = {
  success?: boolean;
  message?: string;
  data?: T;
};

function unwrapResponse<T>(payload: ApiResponse<T> | T): T {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiResponse<T>).data as T;
  }

  return payload as T;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(init?.headers as Record<string, string> ?? {})
  };

  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  const response = await fetch(`${apiBaseUrl}${path}`, {
    headers,
    ...init
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  return unwrapResponse<T>(await response.json());
}

export async function fetchSchools(): Promise<SchoolSummary[]> {
  const response = await request<{ content?: SchoolSummary[] } | SchoolSummary[]>('/schools');

  if (Array.isArray(response)) {
    return response;
  }

  return response.content ?? [];
}

export async function fetchSubscriptionPlans(): Promise<SubscriptionPlan[]> {
  return request('/subscription-plans');
}

export async function createSchool(payload: SchoolRegistrationPayload): Promise<unknown> {
  // The backend expects `subscriptionPlanId` as a UUID. Some frontend plan ids
  // are human-friendly keys (e.g. 'growth') — convert non-UUID values to null
  // to avoid deserialization errors on the server.
  const isUuid = (v: any) => typeof v === 'string' && /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/.test(v);
  const planId = isUuid((payload as any).subscriptionPlanId) ? (payload as any).subscriptionPlanId : null;

  return request('/schools', {
    method: 'POST',
    body: JSON.stringify({
      ...payload,
      subscriptionPlanId: planId
    })
  });
}

export async function checkSuperAdminStatus(): Promise<SuperAdminStatus> {
  return request('/setup/superadmin');
}

export async function registerSuperAdmin(payload: SuperAdminSetupPayload): Promise<AuthResponse> {
  return request('/setup/superadmin', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function login(payload: { schoolCode?: string; email: string; password: string }): Promise<AuthResponse> {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function getSchool(schoolId: string): Promise<SchoolSummary> {
  return request(`/schools/${schoolId}`);
}

export async function updateSchool(schoolId: string, payload: Partial<SchoolRegistrationPayload>): Promise<SchoolSummary> {
  return request(`/schools/${schoolId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export async function deleteSchool(schoolId: string): Promise<void> {
  return request(`/schools/${schoolId}`, {
    method: 'DELETE'
  });
}

export async function registerUser(payload: any): Promise<AuthProfile> {
  return request('/users', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function fetchUsers(schoolId?: string): Promise<AuthProfile[]> {
  const path = schoolId ? `/users?schoolId=${schoolId}` : '/users';
  const response = await request<{ content?: AuthProfile[] } | AuthProfile[]>(path);
  if (Array.isArray(response)) return response;
  return response.content ?? [];
}

// Academic Structure
export async function fetchAcademicYears(schoolId: string): Promise<any[]> {
  return request(`/academic/years?schoolId=${schoolId}`);
}

export async function createAcademicYear(payload: any): Promise<any> {
  return request('/academic/years', { method: 'POST', body: JSON.stringify(payload) });
}

export async function activateAcademicYear(yearId: string, schoolId: string): Promise<any> {
  return request(`/academic/years/${yearId}/activate?schoolId=${schoolId}`, { method: 'PATCH' });
}

export async function fetchClasses(schoolId: string): Promise<any[]> {
  return request(`/academic/classes?schoolId=${schoolId}`);
}

export async function createClass(payload: any): Promise<any> {
  return request('/academic/classes', { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchSections(classId: string): Promise<any[]> {
  return request(`/academic/sections?classId=${classId}`);
}

export async function createSection(payload: any): Promise<any> {
  return request('/academic/sections', { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchSubjects(classId: string): Promise<any[]> {
  return request(`/academic/subjects?classId=${classId}`);
}

export async function createSubject(payload: any): Promise<any> {
  return request('/academic/subjects', { method: 'POST', body: JSON.stringify(payload) });
}

// Student Management
export async function admitStudent(payload: any): Promise<any> {
  return request('/students/admission', { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchStudents(schoolId: string): Promise<any[]> {
  const response = await request<{ content?: any[] } | any[]>(`/students?schoolId=${schoolId}`);
  if (Array.isArray(response)) return response;
  return response.content ?? [];
}

// Staff Management
export async function onboardStaff(payload: any): Promise<any> {
  return request('/staff', { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchStaff(schoolId: string): Promise<any[]> {
  const response = await request<{ content?: any[] } | any[]>(`/staff?schoolId=${schoolId}`);
  if (Array.isArray(response)) return response;
  return response.content ?? [];
}

// Fee Management
export async function fetchFeeCategories(schoolId: string): Promise<any[]> {
  return request(`/fees/categories?schoolId=${schoolId}`);
}

export async function createFeeCategory(payload: any): Promise<any> {
  return request('/fees/categories', { method: 'POST', body: JSON.stringify(payload) });
}

export async function fetchFeeStructures(schoolId: string, academicYearId: string): Promise<any[]> {
  return request(`/fees/structures?schoolId=${schoolId}&academicYearId=${academicYearId}`);
}

export async function createFeeStructure(payload: any): Promise<any> {
  return request('/fees/structures', { method: 'POST', body: JSON.stringify(payload) });
}

export async function assignFeeToStudent(studentId: string, structureId: string): Promise<any> {
  return request(`/fees/assign?studentId=${studentId}&structureId=${structureId}`, { method: 'POST' });
}

export async function fetchStudentFees(studentId: string): Promise<any[]> {
  return request(`/fees/student/${studentId}`);
}

export async function processPayment(payload: any): Promise<any> {
  return request('/fees/payments', { method: 'POST', body: JSON.stringify(payload) });
}

export function buildSchoolPreview(name: string) {
  const slug = name
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');

  return {
    databaseName: slug ? `mgps_${slug}` : 'mgps_school_db',
    subdomain: slug ? `${slug}.smsapp.com` : 'school.smsapp.com'
  };
}
