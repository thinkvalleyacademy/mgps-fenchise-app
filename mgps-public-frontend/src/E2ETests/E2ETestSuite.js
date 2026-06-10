/**
 * E2E Test Suite for School Management System
 * Tests all major functionalities with mocked API
 */

import { 
  mockLogin,
  mockGetStudentList,
  mockGetStudentDetails,
  mockRegisterStudent,
  mockUpdateStudent,
  mockDeleteStudent,
  mockGetTeacherDetails,
  mockGetAcademics,
  mockStudents,
  mockCredentials
} from '../apis/MockApi/mockApiService';

// Test Results Tracker
const testResults = {
  passed: 0,
  failed: 0,
  tests: []
};

// Test Helper Functions
const assert = (condition, testName) => {
  if (condition) {
    testResults.passed++;
    testResults.tests.push({ name: testName, status: 'PASSED' });
    console.log(`✅ PASSED: ${testName}`);
    return true;
  } else {
    testResults.failed++;
    testResults.tests.push({ name: testName, status: 'FAILED' });
    console.error(`❌ FAILED: ${testName}`);
    return false;
  }
};

const assertEquals = (actual, expected, testName) => {
  return assert(actual === expected, `${testName} (expected: ${expected}, got: ${actual})`);
};

// ============== TEST SUITE ==============

console.log('\n========================================');
console.log('🧪 E2E TEST SUITE - School Management System');
console.log('========================================\n');

// Test 1: Login Functionality
console.log('\n📝 TEST 1: Login Functionality\n');

const testLogin = async () => {
  // Test 1.1: Valid Admin Login
  try {
    const result = await mockLogin(mockCredentials.admin);
    assert(result.error === null, 'Valid admin login should succeed');
    assert(result.data_set !== null, 'Login should return token data');
    assert(
      result.data_set.token !== undefined || result.data_set.accessToken !== undefined,
      'Login response should contain token'
    );
  } catch (error) {
    assert(false, `Valid admin login should not throw error: ${error.message}`);
  }

  // Test 1.2: Valid Teacher Login
  try {
    const result = await mockLogin(mockCredentials.teacher);
    assert(result.error === null, 'Valid teacher login should succeed');
    assert(result.data_set.userType === 'student', 'Login response should contain user type');
  } catch (error) {
    assert(false, `Valid teacher login should not throw error: ${error.message}`);
  }

  // Test 1.3: Valid Student Login
  try {
    const result = await mockLogin(mockCredentials.student);
    assert(result.error === null, 'Valid student login should succeed');
    assert(result.data_set.username === 'john_doe_2025', 'Login should return correct username');
  } catch (error) {
    assert(false, `Valid student login should not throw error: ${error.message}`);
  }

  // Test 1.4: Invalid Login - Wrong Password
  try {
    const result = await mockLogin({ username: 'admin', password: 'wrongpassword', userType: 'admin' });
    assert(result.error !== null, 'Invalid password should return error');
    assertEquals(result.error, 'Invalid credentials', 'Error message should be "Invalid credentials"');
  } catch (error) {
    assert(false, `Invalid login should not throw error: ${error.message}`);
  }

  // Test 1.5: Invalid Login - Non-existent User
  try {
    const result = await mockLogin({ username: 'nonexistent', password: 'password', userType: 'student' });
    assert(result.error !== null, 'Non-existent user should return error');
  } catch (error) {
    assert(false, `Invalid login should not throw error: ${error.message}`);
  }
};

// Test 2: Student List Functionality
console.log('\n📝 TEST 2: Student List Functionality\n');

const testStudentList = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 2.1: Get All Students
  try {
    const students = await mockGetStudentList(mockToken, 'student', null, null);
    assert(Array.isArray(students), 'Should return an array of students');
    assert(students.length > 0, 'Should have at least one student');
    assert(students[0].registrationNumber !== undefined, 'Each student should have registration number');
    assert(students[0].firstName !== undefined, 'Each student should have first name');
  } catch (error) {
    assert(false, `Get student list should not throw error: ${error.message}`);
  }

  // Test 2.2: Filter Students by Grade
  try {
    const students = await mockGetStudentList(mockToken, 'student', '10', null);
    assert(students.every(s => s.grade === '10'), 'All returned students should be from grade 10');
  } catch (error) {
    assert(false, `Filter by grade should not throw error: ${error.message}`);
  }

  // Test 2.3: Filter Students by Section
  try {
    const students = await mockGetStudentList(mockToken, 'student', null, 'A');
    assert(students.every(s => s.section === 'A'), 'All returned students should be from section A');
  } catch (error) {
    assert(false, `Filter by section should not throw error: ${error.message}`);
  }

  // Test 2.4: Filter by Both Grade and Section
  try {
    const students = await mockGetStudentList(mockToken, 'student', '10', 'A');
    assert(students.every(s => s.grade === '10' && s.section === 'A'), 
      'All returned students should match both grade and section filters');
  } catch (error) {
    assert(false, `Filter by grade and section should not throw error: ${error.message}`);
  }

  // Test 2.5: Unauthorized Access
  try {
    await mockGetStudentList(null, 'student', null, null);
    assert(false, 'Should throw error for unauthorized access');
  } catch (error) {
    assert(error.message.includes('401'), 'Should throw 401 error for missing token');
  }
};

// Test 3: Student Details Functionality
console.log('\n📝 TEST 3: Student Details Functionality\n');

const testStudentDetails = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 3.1: Get Existing Student Details
  try {
    const student = await mockGetStudentDetails('REG2025001', mockToken);
    assert(student !== null, 'Should return student data');
    assertEquals(student.registrationNumber, 'REG2025001', 'Should return correct student');
    assert(student.firstName !== undefined, 'Student should have first name');
    assert(student.email !== undefined, 'Student should have email');
  } catch (error) {
    assert(false, `Get student details should not throw error: ${error.message}`);
  }

  // Test 3.2: Get Non-existent Student
  try {
    await mockGetStudentDetails('INVALID001', mockToken);
    assert(false, 'Should throw error for non-existent student');
  } catch (error) {
    assert(error.message.includes('not found'), 'Should throw "not found" error');
  }

  // Test 3.3: Unauthorized Access
  try {
    await mockGetStudentDetails('REG2025001', null);
    assert(false, 'Should throw error for unauthorized access');
  } catch (error) {
    assert(error.message.includes('401'), 'Should throw 401 error for missing token');
  }
};

// Test 4: Student Registration
console.log('\n📝 TEST 4: Student Registration Functionality\n');

const testStudentRegistration = async () => {
  // Test 4.1: Valid Registration
  try {
    const newStudent = {
      firstName: 'Test',
      lastName: 'Student',
      email: 'test.student@mgps.com',
      dateOfBirth: '2012-01-15',
      grade: '8',
      section: 'A',
      gender: 'Male',
      fatherName: 'Test Father',
      motherName: 'Test Mother',
      address: '123 Test Street'
    };
    
    const result = await mockRegisterStudent(newStudent);
    assert(result.status === 200, 'Registration should return 200 status');
    assert(result.data.registrationNumber !== undefined, 'Should return registration number');
    assert(result.message === 'Student registered successfully', 'Should return success message');
  } catch (error) {
    assert(false, `Valid registration should not throw error: ${error.message}`);
  }

  // Test 4.2: Registration with Missing Required Fields
  try {
    const incompleteStudent = {
      firstName: 'Test',
      // Missing lastName, email, etc.
    };
    
    await mockRegisterStudent(incompleteStudent);
    assert(false, 'Should throw error for missing required fields');
  } catch (error) {
    assert(error.message.includes('Missing required fields'), 'Should mention missing fields');
  }
};

// Test 5: Student Update
console.log('\n📝 TEST 5: Student Update Functionality\n');

const testStudentUpdate = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 5.1: Update Existing Student
  try {
    const updateData = {
      registrationNumber: 'REG2025001',
      firstName: 'John Updated',
      email: 'john.updated@mgps.com',
      studentMob: '9999999999'
    };
    
    const result = await mockUpdateStudent(updateData, mockToken);
    assert(result.status === 200, 'Update should return 200 status');
    assert(result.message === 'Student updated successfully', 'Should return success message');
  } catch (error) {
    assert(false, `Valid update should not throw error: ${error.message}`);
  }

  // Test 5.2: Update Non-existent Student
  try {
    const updateData = {
      registrationNumber: 'INVALID001',
      firstName: 'Invalid'
    };
    
    await mockUpdateStudent(updateData, mockToken);
    assert(false, 'Should throw error for non-existent student');
  } catch (error) {
    assert(error.message.includes('not found'), 'Should throw "not found" error');
  }

  // Test 5.3: Update Without Token
  try {
    const updateData = {
      registrationNumber: 'REG2025001',
      firstName: 'Test'
    };
    
    await mockUpdateStudent(updateData, null);
    assert(false, 'Should throw error for missing token');
  } catch (error) {
    assert(error.message.includes('401'), 'Should throw 401 error for missing token');
  }
};

// Test 6: Student Delete
console.log('\n📝 TEST 6: Student Delete Functionality\n');

const testStudentDelete = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 6.1: Delete Existing Student
  try {
    // First add a student to delete
    await mockRegisterStudent({
      firstName: 'ToDelete',
      lastName: 'Student',
      email: 'delete.test@mgps.com',
      dateOfBirth: '2012-01-01',
      grade: '8',
      section: 'A',
      gender: 'Male'
    });
    
    const newStudentReg = `REG2025${String(mockStudents.length).padStart(3, '0')}`;
    const result = await mockDeleteStudent(newStudentReg, mockToken);
    assert(result.status === 200, 'Delete should return 200 status');
    assert(result.message === 'Student deleted successfully', 'Should return success message');
  } catch (error) {
    assert(false, `Valid delete should not throw error: ${error.message}`);
  }

  // Test 6.2: Delete Non-existent Student
  try {
    await mockDeleteStudent('INVALID999', mockToken);
    assert(false, 'Should throw error for non-existent student');
  } catch (error) {
    assert(error.message.includes('not found'), 'Should throw "not found" error');
  }

  // Test 6.3: Delete Without Token
  try {
    await mockDeleteStudent('REG2025001', null);
    assert(false, 'Should throw error for missing token');
  } catch (error) {
    assert(error.message.includes('401'), 'Should throw 401 error for missing token');
  }
};

// Test 7: Teacher Details
console.log('\n📝 TEST 7: Teacher Details Functionality\n');

const testTeacherDetails = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 7.1: Get Existing Teacher
  try {
    const teacher = await mockGetTeacherDetails('alice_johnson', mockToken);
    assert(teacher !== null, 'Should return teacher data');
    assertEquals(teacher.username, 'alice_johnson', 'Should return correct teacher');
    assert(teacher.specialization !== undefined, 'Teacher should have specialization');
  } catch (error) {
    assert(false, `Get teacher details should not throw error: ${error.message}`);
  }

  // Test 7.2: Get Non-existent Teacher
  try {
    await mockGetTeacherDetails('nonexistent', mockToken);
    assert(false, 'Should throw error for non-existent teacher');
  } catch (error) {
    assert(error.message.includes('not found'), 'Should throw "not found" error');
  }
};

// Test 8: Academics
console.log('\n📝 TEST 8: Academics Functionality\n');

const testAcademics = async () => {
  const mockToken = 'mock-jwt-token';

  // Test 8.1: Get Classes
  try {
    const result = await mockGetAcademics(mockToken);
    assert(result.status === 200, 'Should return 200 status');
    assert(Array.isArray(result.data), 'Should return array of classes');
    assert(result.data.length > 0, 'Should have at least one class');
    assert(result.data[0].className !== undefined, 'Class should have name');
    assert(Array.isArray(result.data[0].sections), 'Class should have sections array');
  } catch (error) {
    assert(false, `Get academics should not throw error: ${error.message}`);
  }

  // Test 8.2: Unauthorized Access
  try {
    await mockGetAcademics(null);
    assert(false, 'Should throw error for unauthorized access');
  } catch (error) {
    assert(error.message.includes('401'), 'Should throw 401 error for missing token');
  }
};

// ============== RUN ALL TESTS ==============

const runAllTests = async () => {
  await testLogin();
  await testStudentList();
  await testStudentDetails();
  await testStudentRegistration();
  await testStudentUpdate();
  await testStudentDelete();
  await testTeacherDetails();
  await testAcademics();

  // Print Test Summary
  console.log('\n========================================');
  console.log('📊 TEST SUMMARY');
  console.log('========================================');
  console.log(`Total Tests: ${testResults.passed + testResults.failed}`);
  console.log(`✅ Passed: ${testResults.passed}`);
  console.log(`❌ Failed: ${testResults.failed}`);
  console.log('========================================\n');

  // Print Detailed Results
  console.log('📋 DETAILED RESULTS:\n');
  testResults.tests.forEach((test, index) => {
    const icon = test.status === 'PASSED' ? '✅' : '❌';
    console.log(`${index + 1}. ${icon} ${test.name} - ${test.status}`);
  });

  // Final Status
  if (testResults.failed === 0) {
    console.log('\n🎉 ALL TESTS PASSED! 🎉\n');
  } else {
    console.log(`\n⚠️  ${testResults.failed} TEST(S) FAILED ⚠️\n`);
  }

  return testResults;
};

// Export for use in other files
export { runAllTests, testResults };

// Run tests if this file is executed directly
if (typeof window !== 'undefined') {
  runAllTests();
}
