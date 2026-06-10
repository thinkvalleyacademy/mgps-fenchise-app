import React, { useContext, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.css";
import { Routes, Route, useNavigate } from "react-router-dom";
import SideBar from "./MenuBar/SideBar";
import Navbar from "./Navbar";
import MainContent from "./content/mainContent";
import "./content/global-form-styles.css";
import StudentContent from "./content/studentContent";
import Admission from "./content/admission";
import TeacherList from "./content/teacher-list";
import Subjects from "../Academics/Subjects";
import AcademicClass from "../Academics/AcademicClass";
import Department from "../Academics/Department";
import ClassRoom from "../Academics/ClassRoom";
import AuthContext from "../../context/student/AuthContext";
import Routine from "../Academics/Routine";
import FeeManager from "../Academics/FeeManager";
import TeacherRegistration from "./TeacherRegistration";
import AdminRegisteration from "./content/Admin Registration";
import FeeStructure from "./content/feeStructure";
import FeeCollection from "./content/feeCollection";
import RegisterStructure from "./content/RegisterFeeStructure";
import OverView from "../FeeManagement/Overview";
import FeeDetailClass from "../FeeManagement/FeeDeatils/Classes";
import FeeDetailStudent from "../FeeManagement/FeeDeatils/Students";
import StudentFeeDetails from "../FeeManagement/FeeDeatils/Individual";
import FeeSubmission from "../FeeManagement/FeeDeatils/Fee Submission";
import FeeCollectionHome from "../FeeManagement/FeeReport/ReportHome";
import CollectionReport from "../FeeManagement/FeeReport/CollectionReport";
import PaymentHistory from "../FeeManagement/FeeReport/PaymentHistory";
import LoginFotter from "./LoginFooter";
import SeedDefaultData from "./Admin/SeedDefaultData";
import AdminApprovalDashboard from "./AdminApprovalDashboard";
import SchoolClientOnboarding from "./SchoolClientOnboarding";
import SchoolOnboardingDashboard from "./SchoolOnboardingDashboard";

const Home = () => {
  const { name, user, token } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      alert("Token expired. Please log in again.");
      navigate("/login");
    }
  }, [token, navigate]);

  return (
    <div className="app-wrapper" style={{ 
      display: "flex", 
      flexDirection: "column", 
      height: "100vh",
      minHeight: "-webkit-fill-available",
      width: "100%",
      overflow: "hidden"
    }}>
      <Navbar name={name} type={user} />
      <div style={{ 
        flex: "1", 
        display: "flex",
        overflow: "hidden",
        width: "100%"
      }}>
        <SideBar>
          <Routes>
            {/* Explicitly render MainContent for /Dashboard */}
            <Route path="/" element={<MainContent />} />
            <Route path="/users/student" element={<StudentContent />} />
            <Route path="/registration/student" element={<Admission />} />
            <Route path="/registration/teacher" element={<TeacherRegistration />} />
            <Route path="/users/teacher" element={<TeacherList />} />
            <Route path="/registration/admin" element={<AdminRegisteration />} />

            <Route path="/Alumni" element={<>Coming soon</>} />
            <Route path="/Academics/subject" element={<Subjects />} />
            <Route path="/Academics/class" element={<AcademicClass />} />
            <Route path="/Academics/department" element={<Department />} />
            <Route path="/Academics/class-room" element={<ClassRoom />} />
            <Route path="/Academics/Routine" element={<Routine />} />
            <Route path="/Accounting/fee-structure" element={<FeeStructure />} />
            <Route path="/Accounting/fee-structure/create" element={<RegisterStructure />} />
            <Route path="/Accounting/fee-collection" element={<FeeCollection />} />
            <Route path="/Accounting/invoice" element={<FeeManager />} />
            <Route path="/Accounting/overview" element={<OverView />} />
            <Route path="/Accounting/feeDetail" element={<FeeDetailClass />} />
            <Route path="/Accounting/feeDetail/Students" element={<FeeDetailStudent />} />
            <Route path="/Accounting/feeDetail/Students/Student" element={<StudentFeeDetails />} />
            <Route path="/Accounting/feeDetail/Students/Student/FeeSubmission" element={<FeeSubmission />} />
            <Route path="/Accounting/feeReport" element={<FeeCollectionHome />} />
            <Route path="/Accounting/CollectionReport" element={<CollectionReport />} />
            <Route path="/Accounting/PaymentHistory" element={<PaymentHistory />} />
            <Route path="/Admin/seed-default-data" element={<SeedDefaultData />} />
            <Route path="/admin/approvals" element={<AdminApprovalDashboard />} />
            <Route path="/admin/client-onboarding" element={<SchoolClientOnboarding />} />
            <Route path="/admin/onboarding-dashboard" element={<SchoolOnboardingDashboard />} />

            <Route path="/Live-Class" element={<>Coming soon</>} />
            <Route path="/Examination" element={<>Coming soon</>} />
            <Route path="/Accounting" element={<>Coming soon</>} />
            <Route path="/Back-Office" element={<>Coming soon</>} />
            {/* Fallback route */}
            <Route path="*" element={<div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
              <img alt="Coming soon" height={"400px"} src="https://www.shutterstock.com/image-vector/coming-soon-page-concept-man-600nw-1483421201.jpg"/>
            </div>} />
          </Routes>
        </SideBar>
      </div>
      <LoginFotter />
    </div>
  );
};

export default Home;
