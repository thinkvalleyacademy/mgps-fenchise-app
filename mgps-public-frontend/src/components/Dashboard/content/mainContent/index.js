import React, { useContext, useEffect } from "react";
import Cards from "../../Cards/Card";
import AuthContext from "../../../../context/student/AuthContext";
import "./style.css";

const MainContent = () => {
  const { token } = useContext(AuthContext); // Ensure token is available for API calls

  useEffect(() => {
    if (!token) {
      console.error("Token is missing. Ensure the user is logged in.");
    }
  }, [token]);

  return (
    <>
      <div className="dashboard-page">
        <div className="dashboard-shell">
          <div className="dashboard-hero">
            <div>
              <h1>Dashboard</h1>
              <p>Quick stats and shortcuts for daily work.</p>
            </div>
          </div>

          <div className="dashboard-grid">
            <div className="dashboard-col-6">
              <Cards title="student" token={token} />
            </div>
            <div className="dashboard-col-6">
              <Cards title="teacher" token={token} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default MainContent;
