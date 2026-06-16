import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Gallery from './components/Gallery/Gallery';
import HomePage from './components/public_page/HomePage/HomePageChildFriendly';
import RegistrationQuery from './components/public_page/Registration_query';
import ClientOnboarding from './components/public_page/ClientOnboarding';
import OnboardingForm from './components/Onboarding/OnboardingForm';
import SuperAdmin from './components/Onboarding/SuperAdmin';

function App() {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/gallery" element={<Gallery />} />
        <Route path="/clientOnboarding" element={<ClientOnboarding />} />
        <Route path="/onboard" element={<OnboardingForm />} />
        <Route path="/super-admin/tenants" element={<SuperAdmin />} />
        <Route path="/registrationQuery" element={<RegistrationQuery />} />

        {/* Catch-all - redirect to home for now, as dashboard is removed */}
        <Route path="/*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
