import { useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AuthContext from "../context/student/AuthContext";

const Logout = () => {
  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    // Call the logout function from context which clears all auth data
    logout();
    // Navigate to login page
    navigate("/login");
  }, [logout, navigate]);

  return null;
};

export default Logout;
