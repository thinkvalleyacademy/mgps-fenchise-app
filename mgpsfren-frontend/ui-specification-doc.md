For production, no public registration should exist. For development/bootstrap purposes, keep a hidden route:
/setup/superadmin

Flow:

First Install
    ↓
Create Super Admin
    ↓
Login (default first page)
    ↓
Dashboard with modules shown based on user permissions
    ↓
Create School / Manage Users / Other permitted modules
    ↓
School Admin Creates Additional Users

SMS UI Development Specification (Phase-wise)

# MGPS SMS - UI Development Specification

## UI Architecture

Application Type:
Multi-Tenant School Management System

Frontend:
React + TypeScript + Material UI

Layout Pattern:

Header
Sidebar
Breadcrumb
Page Content
Footer

Navigation Pattern:

Dashboard
→ List Screen
→ Detail Screen
→ Create/Edit Form
→ Reports

---

PHASE 0 - AUTHENTICATION & SYSTEM SETUP

Purpose:
User authentication and system initialization.

Screens:

1. Super Admin Bootstrap

Route:
/setup/superadmin

Fields:

* Name
* Email
* Mobile
* Password
* Confirm Password

Actions:

* Create Super Admin

Rules:
Visible only when no super admin exists.

---

2. Login Screen

Route:
/login

Notes:

* This should be the default first screen for all unauthenticated users.
* After successful login, the user should see the dashboard and only modules they are permitted to access.

Fields:

* School Code (optional)
* Username
* Password

Actions:

* Login
* Forgot Password

---

3. Forgot Password

Fields:

* Username/Email

Actions:

* Send Reset Link

---

4. Reset Password

Fields:

* New Password
* Confirm Password

---

PHASE 1 - SUPER ADMIN PORTAL

Purpose:
Manage franchise schools.

Screens:

1. Dashboard

Widgets:

* Total Schools
* Active Schools
* Expired Schools
* Total Students
* Total Staff
* Revenue

Charts:

* School Growth
* Subscription Revenue

---

2. School Management

List Screen

Columns:

* School Name
* School Code
* Admin
* Status
* Subscription
* Created Date

Actions:

* View
* Edit
* Activate
* Suspend
* Delete
* Login As School

---

3. Create School

Sections:

School Information

* School Name
* Code
* Logo
* Website

Contact Information

* Email
* Mobile

Address

* State
* District
* City
* Address

Subscription

* Plan
* Start Date
* End Date

Admin Account

* Name
* Username
* Email
* Password

---

4. School Details

Tabs:

* Overview
* Subscription
* Users
* Activity Logs

---

5. Subscription Management

Screens:

* Plans List
* Create Plan
* Assign Plan

---

6. Audit Logs

Filters:

* School
* User
* Date Range

---

PHASE 2 - SCHOOL ADMIN FOUNDATION

Purpose:
Configure academic structure.

Screens:

1. School Dashboard

Widgets:

* Students
* Teachers
* Attendance
* Fees Due
* Upcoming Exams

Quick Actions:

* Add Student
* Add Teacher
* Create Class

---

2. Academic Session

Screens:

* Session List
* Create Session

---

3. Class Management

Screens:

* Class List
* Create Class
* Edit Class

---

4. Section Management

Screens:

* Section List
* Create Section

---

5. Subject Management

Screens:

* Subject List
* Create Subject

---

6. Role & Permission Management

Screens:

* Role List
* Create Role
* Permission Matrix

---

PHASE 3 - USER MANAGEMENT

1. User Directory

Filters:

* User Type
* Status

---

2. Student Admission

Sections:

Basic Information
Parent Information
Academic Information
Address
Documents

---

3. Student List

Columns:

* Admission No
* Name
* Class
* Section
* Mobile

Actions:

* View
* Edit
* Promote
* Transfer

---

4. Student Details

Tabs:

* Profile
* Attendance
* Fees
* Exams
* Documents

---

5. Teacher Management

Screens:

* Teacher List
* Add Teacher
* Teacher Profile

Tabs:

* Personal
* Attendance
* Subjects
* Timetable

---

6. Parent Management

Screens:

* Parent List
* Parent Profile

---

7. Staff Management

Screens:

* Staff List
* Add Staff

---

PHASE 4 - TIMETABLE & SCHEDULING

1. Timetable Dashboard

2. Create Timetable

Features:

* Drag & Drop
* Teacher Assignment
* Room Assignment

3. Teacher Schedule

4. Class Schedule

5. Conflict Report

---

PHASE 5 - ATTENDANCE

1. Attendance Dashboard

2. Student Attendance Entry

3. Teacher Attendance Entry

4. Daily Attendance Report

5. Monthly Attendance Report

---

PHASE 6 - FEES

1. Fee Dashboard

2. Fee Structure

3. Fee Collection

4. Pending Fees

5. Receipt Generation

6. Fee Reports

---

PHASE 7 - EXAMINATION

1. Exam Dashboard

2. Exam Setup

3. Marks Entry

4. Result Generation

5. Report Cards

---

PHASE 8 - COMMUNICATION

1. Notice Board

2. SMS Center

3. Email Center

4. Push Notifications

---

PHASE 9 - REPORTS

1. Student Reports

2. Attendance Reports

3. Fee Reports

4. Exam Reports

5. Staff Reports

---

PHASE 10 - SETTINGS

1. School Profile

2. Branding

3. Academic Settings

4. SMS Settings

5. Email Settings

6. Backup Settings

7. User Preferences
