import React, { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchListData } from "../../../apis/CRUD_operation/operation";
import { FaArrowRight, FaChalkboardTeacher, FaUserGraduate, FaUsers } from "react-icons/fa";
import "./style.css";

const Cards = ({ title, token }) => {
  const [totalUser, setUser] = useState(null);
  const navigate = useNavigate();

  const fetchData = useCallback(async () => {
    try {
      console.log(`Fetching data for ${title}...`); // Debugging log
      const data = await fetchListData(token, title); // Use token for API call
      if (Array.isArray(data)) {
        setUser(data.length);
      } else {
        setUser(0); // Default to 0 if data is not an array
      }
    } catch (error) {
      console.error("Error fetching data:", error);
      setUser(0); // Handle error case
    }
  }, [title, token]);

  useEffect(() => {
    if (token) {
      fetchData(); // Fetch data only if token is available
    } else {
      console.error("Token is missing. Cannot fetch data."); // Debugging log
    }
  }, [fetchData, token]);

  const NavigatePage = () => {
    navigate(`/users/${title}`);
  };

  const meta = (() => {
    const normalized = String(title || "").toLowerCase();
    if (normalized === "student") {
      return { label: "Students", icon: <FaUserGraduate /> };
    }
    if (normalized === "teacher") {
      return { label: "Teachers", icon: <FaChalkboardTeacher /> };
    }
    return { label: normalized ? `${normalized}s` : "Users", icon: <FaUsers /> };
  })();

  return (
    <>
      <div className="stat-card" id={title}>
        <div className="stat-card-top">
          <div>
            <h5 className="stat-card-title">{meta.label}</h5>
            <div className="stat-card-subtitle">Quick view of total count</div>
          </div>
          <div className="stat-card-icon" aria-hidden="true">
            {meta.icon}
          </div>
        </div>

        <div className="stat-card-value">
          {totalUser !== null ? totalUser : "—"}
        </div>

        <div className="stat-card-actions">
          <div className="stat-card-hint">Go to list</div>
          <button
            type="button"
            className="stat-card-btn"
            onClick={NavigatePage}
            disabled={!token}
          >
            View <FaArrowRight />
          </button>
        </div>
      </div>
    </>
  );
};

Cards.defaultProps = {
  title: "student",
};

export default Cards;
