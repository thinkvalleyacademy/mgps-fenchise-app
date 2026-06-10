import React, { useState, useContext } from "react";
import "./style.css";
import PropTypes from "prop-types";
import Dropdown from "react-bootstrap/Dropdown";
import { useNavigate } from "react-router-dom";
import { RiLogoutBoxRLine } from "react-icons/ri";
import { ImProfile } from "react-icons/im";
import { FaBell } from "react-icons/fa";
import StudentProfile from "../content/studentProfile";
import TeacherProfile from "../content/teacherProfile";
import AuthContext from "../../../context/student/AuthContext";
import PersonImage from './person.png';

const Navbar = (props) => {
  const navigate = useNavigate();
  const { logout, userName } = useContext(AuthContext);
  const [show, setShow] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);

  const handleClose = () => {
    setShow(false);
  };

  const handleItemClick = () => {
    logout();
    navigate("/login");
  };

  const profileOnClick = () => {
    setShow(true);
  };

  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-dark modern-navbar">
        {/* Left Section - Logo Only (No Toggle - Toggle is in Sidebar) */}
        <div className="navbar-left-section">
          <a
            className="navbar-brand modern-logo"
            href="/"
          >
            <span className="logo-text">Mother's Goose Public School</span>
          </a>
        </div>

        {/* Right Section - User Profile & Actions */}
        <div className="navbar-right-section">
          {/* Notifications */}
          <div className="nav-action-item">
            <button
              className="action-btn notification-btn"
              onClick={() => setShowNotifications(!showNotifications)}
              aria-label="Notifications"
              title="Notifications (Coming Soon)"
            >
              <FaBell />
              <span className="notification-badge">3</span>
            </button>
          </div>

          {/* User Profile Dropdown */}
          <div className="user-profile-section">
            <Dropdown>
              <Dropdown.Toggle
                id="dropdown-basic"
                variant="Secondary"
                className="user-profile-toggle"
              >
                <div className="user-profile-content">
                  <div className="user-avatar-wrapper">
                    <img
                      src={PersonImage}
                      alt="User Avatar"
                      className="user-avatar-img"
                    />
                    <span className="online-indicator"></span>
                  </div>
                  <div className="user-info-navbar">
                    <span className="user-name">{props.name || "User"}</span>
                    <span className="user-role">{props.type || "Role"}</span>
                  </div>
                </div>
              </Dropdown.Toggle>

              <Dropdown.Menu align="end" className="modern-dropdown-menu">
                <div className="dropdown-header-section">
                  <div className="dropdown-user-info">
                    <img
                      src={PersonImage}
                      alt="User Avatar"
                      className="dropdown-avatar"
                    />
                    <div>
                      <div className="dropdown-user-name">{props.name || "User"}</div>
                      <div className="dropdown-user-role">{props.type || "Role"}</div>
                    </div>
                  </div>
                </div>

                <Dropdown.Item
                  onSelect={profileOnClick}
                  className="dropdown-item-custom"
                >
                  <ImProfile className="dropdown-icon" />
                  <span>Profile</span>
                </Dropdown.Item>

                <Dropdown.Divider className="dropdown-divider-custom" />

                <Dropdown.Item
                  onSelect={handleItemClick}
                  className="dropdown-item-custom logout-item"
                >
                  <RiLogoutBoxRLine className="dropdown-icon logout-icon" />
                  <span>Logout</span>
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          </div>
        </div>
      </nav>

      {/* Profile Modals */}
      {props.type === "Student" && (
        <StudentProfile
          student={userName}
          show={show}
          handleClose={handleClose}
        />
      )}

      {props.type === "Teacher" && (
        <TeacherProfile
          teacher={userName}
          show={show}
          handleClose={handleClose}
        />
      )}
    </>
  );
};

Navbar.propTypes = {
  name: PropTypes.string.isRequired,
  type: PropTypes.string.isRequired,
};

Navbar.defaultProps = {
  name: "User",
  type: "Role",
};

export default Navbar;
