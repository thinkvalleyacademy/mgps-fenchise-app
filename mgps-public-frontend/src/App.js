import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";
import Login from './components/LoginChildFriendly';
import Home from './components/Dashboard/Home';
import Logout from './components/Logout';
import Gallery from './components/Gallery/Gallery';
import { AuthProvider, useAuth } from './context/student/AuthContext';
import HomePage from './components/public_page/HomePage/HomePageChildFriendly';
import RegistrationQuery from './components/public_page/Registration_query';
import E2ETestRunner from './components/E2ETestRunner/E2ETestRunner';
import Signup from './components/public_page/Signup';
import ClientOnboarding from './components/public_page/ClientOnboarding';
import AdminApprovalDashboard from './components/Dashboard/AdminApprovalDashboard';
import OnboardingForm from './components/Onboarding/OnboardingForm';
import SuperAdmin from './components/Onboarding/SuperAdmin';

// PrivateRoute component
const PrivateRoute = ({ children }) => {
  const { token } = useAuth();
  const location = useLocation();
  if (!token) {
    return <Navigate to="/login" replace state={{ from: location.pathname, reason: "unauthenticated" }} />;
  }
  return children;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/gallery" element={<Gallery />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/clientOnboarding" element={<ClientOnboarding />} />
          <Route path="/onboard" element={<OnboardingForm />} />
          <Route path="/super-admin/tenants" element={<SuperAdmin />} />
          <Route path="/registrationQuery" element={<RegistrationQuery />} />
          <Route path="/e2e-tests" element={<E2ETestRunner />} />

          {/* Protected Routes */}
          <Route path="/logout" element={<PrivateRoute><Logout /></PrivateRoute>} />
          <Route path="/admin/approvals" element={<PrivateRoute><AdminApprovalDashboard /></PrivateRoute>} />
          <Route path="/Dashboard/*" element={<PrivateRoute><Home /></PrivateRoute>} />
          <Route path="/home" element={<Navigate to="/Dashboard" replace />} />
          <Route path="/*" element={<PrivateRoute><Home /></PrivateRoute>} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
