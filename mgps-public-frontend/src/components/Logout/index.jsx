import React, { useContext, useEffect } from 'react';
import LogoutImage from './LogoutNew.jpg';
import { useNavigate } from "react-router-dom";
import AuthContext from '../../context/student/AuthContext';

const Logout = () => {
  const navigate = useNavigate();
  const { logout } = useContext(AuthContext);

  // Trigger logout on component mount
  useEffect(() => {
    logout();
    notify("Token expired. Please log in again.");
  }, [logout]);

  // Navigate to login page
  const handleSubmit = () => {
    navigate("/login");
  };

  return (
    <>
      <div className="card text-black" style={{ borderRadius: "100px" }}>
        <div className="card-body p-md-5">
          <div className="row justify-content-center">
            {/* Left Section */}
            <div className="col-md-10 col-lg-6 col-xl-5 order-2 order-lg-1" style={{ marginTop: "200px" }}>
              <p className="text-center h3 mb-5 mx-1 mx-md-4 mt-4" style={{ fontFamily: "monospace" }}>
                You're logged out
              </p>
              <div className="col-md-12 text-center">
                <h4 className="text-muted">Click below</h4>
              </div>
              <div className="d-flex justify-content-center mx-4 mb-3 mb-lg-4" style={{ marginTop: "20px" }}>
                <button
                  type="button"
                  onClick={handleSubmit}
                  className="btn btn-primary btn-lg"
                >
                  Sign in
                </button>
              </div>
            </div>
            {/* Right Section */}
            <div className="col-md-10 col-lg-6 col-xl-7 d-flex align-items-center order-1 order-lg-2">
              <img
                src={LogoutImage}
                className="img-fluid"
                alt="Logout illustration"
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Logout;
