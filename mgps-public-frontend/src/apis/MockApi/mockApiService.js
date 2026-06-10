// Mock API Service for E2E Testing
// This service intercepts API calls and returns mock data instead of making real backend calls

import { 
  mockStudents, 
  mockTeachers, 
  mockAcademics, 
  mockCredentials,
  mockApiResponse 
} from './mockData';

// Enable/disable mock mode
const MOCK_MODE_ENABLED = true;

// Simulate network delay
const simulateDelay = (ms = 500) => new Promise(resolve => setTimeout(resolve, ms));

// Mock login API
export const mockLogin = async (credentials) => {
  await simulateDelay(800);
  
  const { username, password, userType } = credentials;
  
  // Check against mock credentials
  const validCredentials = Object.values(mockCredentials).find(
    cred => cred.username === username && cred.password === password && cred.userType.toLowerCase() === userType.toLowerCase()
  );
  
  if (validCredentials) {
    return {
      data_set: mockApiResponse.loginSuccess.data,
      error: null
    };
  }
  
  return {
    data_set: null,
    error: "Invalid credentials"
  };
};

// Mock student list API
export const mockGetStudentList = async (token, userType, grade, section) => {
  await simulateDelay(600);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  let filteredStudents = [...mockStudents];
  
  if (grade) {
    filteredStudents = filteredStudents.filter(s => s.grade === grade);
  }
  
  if (section) {
    filteredStudents = filteredStudents.filter(s => s.section === section);
  }
  
  return filteredStudents;
};

// Mock get student details API
export const mockGetStudentDetails = async (registrationNumber, token) => {
  await simulateDelay(500);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  const student = mockStudents.find(s => s.registrationNumber === registrationNumber);
  
  if (student) {
    return student;
  }
  
  throw new Error("Student not found");
};

// Mock student registration API
export const mockRegisterStudent = async (formData) => {
  await simulateDelay(1000);
  
  // Validate required fields
  const requiredFields = ['firstName', 'lastName', 'email', 'dateOfBirth', 'grade', 'section'];
  const missingFields = requiredFields.filter(field => !formData[field]);
  
  if (missingFields.length > 0) {
    throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
  }
  
  // Generate mock registration number
  const newRegNumber = `REG2025${String(mockStudents.length + 1).padStart(3, '0')}`;
  
  const newStudent = {
    id: mockStudents.length + 1,
    registrationNumber: newRegNumber,
    username: `${formData.firstName.toLowerCase()}_${formData.lastName.toLowerCase()}_2025`,
    userType: 'student',
    ...formData,
    userStatus: true
  };
  
  mockStudents.push(newStudent);
  
  return {
    status: 200,
    message: "Student registered successfully",
    data: {
      registrationNumber: newRegNumber,
      username: newStudent.username
    }
  };
};

// Mock update student API
export const mockUpdateStudent = async (formData, token) => {
  await simulateDelay(700);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  const studentIndex = mockStudents.findIndex(
    s => s.registrationNumber === formData.registrationNumber
  );
  
  if (studentIndex === -1) {
    throw new Error("Student not found");
  }
  
  // Update student data
  mockStudents[studentIndex] = {
    ...mockStudents[studentIndex],
    ...formData,
    registrationNumber: mockStudents[studentIndex].registrationNumber // Keep original registration number
  };
  
  return {
    status: 200,
    message: "Student updated successfully",
    data: null
  };
};

// Mock delete student API
export const mockDeleteStudent = async (registrationNumber, token) => {
  await simulateDelay(500);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  const studentIndex = mockStudents.findIndex(s => s.registrationNumber === registrationNumber);
  
  if (studentIndex === -1) {
    throw new Error("Student not found");
  }
  
  mockStudents.splice(studentIndex, 1);
  
  return {
    status: 200,
    message: "Student deleted successfully",
    data: null
  };
};

// Mock get teacher details API
export const mockGetTeacherDetails = async (username, token) => {
  await simulateDelay(500);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  const teacher = mockTeachers.find(t => t.username === username);
  
  if (teacher) {
    return teacher;
  }
  
  throw new Error("Teacher not found");
};

// Mock get academics API
export const mockGetAcademics = async (token) => {
  await simulateDelay(400);
  
  if (!token) {
    throw new Error("401 Unauthorized");
  }
  
  return {
    status: 200,
    data: mockAcademics.classes
  };
};

// Helper function to check if mock mode is enabled
export const isMockModeEnabled = () => MOCK_MODE_ENABLED;

// Export all mock data for direct access in tests
export { mockStudents, mockTeachers, mockAcademics, mockCredentials, mockApiResponse };
