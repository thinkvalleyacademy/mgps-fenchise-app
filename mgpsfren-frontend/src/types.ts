export type SchoolStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface SubscriptionPlan {
  planId: string;
  planName: string;
  description?: string;
  maxStudents?: number;
  maxStaff?: number;
  maxUsers?: number;
  monthlyPrice?: string;
  isActive?: boolean;
}

export interface SchoolSummary {
  schoolId: string;
  name: string;
  adminEmail: string;
  adminPhone?: string;
  city?: string;
  state?: string;
  databaseName: string;
  status: SchoolStatus;
  subscriptionPlan?: SubscriptionPlan;
  createdAt?: string;
  updatedAt?: string;
}

export interface SchoolRegistrationPayload {
  name: string;
  adminEmail: string;
  adminPhone: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  subscriptionPlanId: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  profile: AuthProfile;
}

export interface AuthProfile {
  userId: string;
  schoolId?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  role: string;
  status: string;
  lastLoginAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SuperAdminSetupPayload {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
}

export interface SuperAdminStatus {
  hasSuperAdmin: boolean;
}
