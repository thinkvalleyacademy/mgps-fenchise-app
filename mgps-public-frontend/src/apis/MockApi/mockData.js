// Mock Data for E2E Testing

export const mockStudents = [
  {
    id: 1,
    registrationNumber: "REG2025001",
    username: "john_doe_2025",
    userType: "student",
    firstName: "John",
    lastName: "Doe",
    fatherName: "Robert Doe",
    motherName: "Jane Doe",
    otherParents: "Uncle Sam",
    dateOfBirth: "2010-05-15",
    emailId: "john.doe@mgps.com",
    email: "john.doe@mgps.com",
    studentMob: "9876543210",
    gender: "Male",
    grade: "10",
    section: "A",
    bloodGroup: "O+",
    address: "123 Main Street, Springfield",
    userStatus: true
  },
  {
    id: 2,
    registrationNumber: "REG2025002",
    username: "jane_smith_2025",
    userType: "student",
    firstName: "Jane",
    lastName: "Smith",
    fatherName: "Michael Smith",
    motherName: "Mary Smith",
    otherParents: "Aunt Lucy",
    dateOfBirth: "2010-08-20",
    emailId: "jane.smith@mgps.com",
    email: "jane.smith@mgps.com",
    studentMob: "9876543211",
    gender: "Female",
    grade: "10",
    section: "A",
    bloodGroup: "A+",
    address: "456 Oak Avenue, Springfield",
    userStatus: true
  },
  {
    id: 3,
    registrationNumber: "REG2025003",
    username: "bob_wilson_2025",
    userType: "student",
    firstName: "Bob",
    lastName: "Wilson",
    fatherName: "David Wilson",
    motherName: "Sarah Wilson",
    otherParents: "",
    dateOfBirth: "2011-03-10",
    emailId: "bob.wilson@mgps.com",
    email: "bob.wilson@mgps.com",
    studentMob: "9876543212",
    gender: "Male",
    grade: "9",
    section: "B",
    bloodGroup: "B+",
    address: "789 Pine Road, Springfield",
    userStatus: true
  }
];

export const mockTeachers = [
  {
    id: 1,
    registrationNumber: "TEA2025001",
    username: "alice_johnson",
    userType: "teacher",
    firstName: "Alice",
    lastName: "Johnson",
    emailId: "alice.johnson@mgps.com",
    email: "alice.johnson@mgps.com",
    phone_num: "9123456789",
    gender: "Female",
    qualification: "M.Ed",
    specialization: "Mathematics",
    grade_level: "10",
    teaching_experience: "5",
    joining_date: "2020-06-01",
    subjects: ["Mathematics", "Physics"],
    position: "Senior Teacher",
    address: "321 Teacher Lane, Springfield",
    userStatus: true
  },
  {
    id: 2,
    registrationNumber: "TEA2025002",
    username: "charlie_brown",
    userType: "teacher",
    firstName: "Charlie",
    lastName: "Brown",
    emailId: "charlie.brown@mgps.com",
    email: "charlie.brown@mgps.com",
    phone_num: "9123456788",
    gender: "Male",
    qualification: "M.Sc",
    specialization: "Science",
    grade_level: "9",
    teaching_experience: "3",
    joining_date: "2022-07-15",
    subjects: ["Science", "Biology"],
    position: "Teacher",
    address: "654 Educator Street, Springfield",
    userStatus: true
  }
];

export const mockAcademics = {
  classes: [
    {
      id: 1,
      className: "10",
      sections: [
        { id: 1, sectionName: "A", capacity: 50 },
        { id: 2, sectionName: "B", capacity: 50 }
      ]
    },
    {
      id: 2,
      className: "9",
      sections: [
        { id: 3, sectionName: "A", capacity: 50 },
        { id: 4, sectionName: "B", capacity: 50 }
      ]
    },
    {
      id: 3,
      className: "8",
      sections: [
        { id: 5, sectionName: "A", capacity: 50 }
      ]
    }
  ],
  subjects: [
    { id: 1, name: "Mathematics", code: "MATH101" },
    { id: 2, name: "Science", code: "SCI101" },
    { id: 3, name: "English", code: "ENG101" },
    { id: 4, name: "History", code: "HIS101" },
    { id: 5, name: "Computer Science", code: "CS101" }
  ],
  departments: [
    { id: 1, name: "Science", head: "Dr. Smith" },
    { id: 2, name: "Mathematics", head: "Prof. Johnson" },
    { id: 3, name: "Arts", head: "Ms. Williams" }
  ]
};

export const mockCredentials = {
  admin: {
    username: "admin",
    password: "admin123",
    userType: "admin"
  },
  teacher: {
    username: "alice_johnson",
    password: "teacher123",
    userType: "teacher"
  },
  student: {
    username: "john_doe_2025",
    password: "student123",
    userType: "student"
  }
};

export const mockApiResponse = {
  success: {
    status: 200,
    message: "Success",
    data: null
  },
  loginSuccess: {
    status: 200,
    message: "Login successful",
    data: {
      token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocked-jwt-token",
      username: "john_doe_2025",
      firstName: "John",
      lastName: "Doe",
      userType: "student",
      emailId: "john.doe@mgps.com"
    }
  },
  registrationSuccess: {
    status: 200,
    message: "Student registered successfully",
    data: {
      registrationNumber: "REG2025004"
    }
  },
  error: {
    status: 400,
    message: "Request failed",
    data: null
  },
  unauthorized: {
    status: 401,
    message: "Unauthorized",
    data: null
  },
  notFound: {
    status: 404,
    message: "Not Found",
    data: null
  }
};
