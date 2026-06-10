import React, { useState } from "react";
import "./ChildFriendlyNav.css";
import logo from "../Logo.png";
import { useNavigate } from "react-router-dom";
import { FaBars, FaTimes, FaHome, FaInfoCircle, FaBook, FaEnvelope, FaSignInAlt, FaUserPlus } from "react-icons/fa";

const HomeNav = ({ isOtherOptions }) => {
  const navigate = useNavigate();
  const [activeLink, setActiveLink] = useState(" ");
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleScroll = (id, e) => {
    e.preventDefault();
    setActiveLink(id);
    setIsMenuOpen(false);
    if (id === "Home") {
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else {
      const element = document.getElementById(id);
      if (element) {
        window.scrollTo({
          top: element.offsetTop,
          behavior: "smooth",
        });
      }
    }
  };

  const NavigatePage = (pageName, e) => {
    e.preventDefault();
    setActiveLink(pageName);
    setIsMenuOpen(false);
    
    // For all public enrollment links, open the enquiry popup
    if (pageName === "registrationQuery" || pageName === "registration" || pageName === "enroll") {
      // Trigger the modal from AutoOpenModal
      const modalEvent = new CustomEvent('openEnquiryModal');
      window.dispatchEvent(modalEvent);
    } else if (pageName === "contact") {
      // Scroll to footer (contact section)
      const footerElement = document.getElementById('contact');
      if (footerElement) {
        footerElement.scrollIntoView({ behavior: 'smooth' });
      }
    } else if (pageName === "gallery") {
      navigate("/gallery");
    } else if (pageName === "login") {
      navigate(`/login`);
    } else {
      navigate(`/${pageName}`);
    }
  };

  return (
    <>
      <nav className="child-friendly-navbar">
        {/* Animated background elements */}
        <div className="nav-bg-decoration">
          <span className="floating-icon">🎈</span>
          <span className="floating-icon" style={{animationDelay: '1s'}}>⭐</span>
          <span className="floating-icon" style={{animationDelay: '2s'}}>🌈</span>
        </div>

        <div className="container-fluid">
          <div className="nav-brand-section">
            <a className="brand-logo" href="/">
              <img className="brand-logo-img" src={logo} alt="Mother's Goose Logo" />
              <div className="brand-text">
                <h1 className="brand-name">Mother's Goose 🪿</h1>
                <p className="brand-tagline">Building Strong Foundations for Bright Futures ✨</p>
              </div>
            </a>
          </div>

          {/* Mobile Menu Toggle */}
          <button
            className="mobile-menu-toggle"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            aria-label="Toggle Menu"
          >
            {isMenuOpen ? <FaTimes className="toggle-icon close" /> : <FaBars className="toggle-icon open" />}
          </button>

          {/* Navigation Links */}
          <div className={`nav-links-container ${isMenuOpen ? 'active' : ''}`}>
            <ul className="nav-menu">
              {isOtherOptions ? (
                <>
	                  <li className="nav-menu-item" onClick={(e) => handleScroll("Home", e)}>
	                    <button type="button" className={`nav-menu-link ${activeLink === "Home" ? "active" : ""}`}>
	                      <FaHome className="nav-icon" /> Home
	                    </button>
	                  </li>
	                  <li className="nav-menu-item" onClick={(e) => handleScroll("about", e)}>
	                    <button type="button" className={`nav-menu-link ${activeLink === "about" ? "active" : ""}`}>
	                      <FaInfoCircle className="nav-icon" /> About Us
	                    </button>
	                  </li>
	                  <li className="nav-menu-item" onClick={(e) => handleScroll("programs", e)}>
	                    <button type="button" className={`nav-menu-link ${activeLink === "programs" ? "active" : ""}`}>
	                      <FaBook className="nav-icon" /> Our Programs
	                    </button>
	                  </li>
	                  <li className="nav-menu-item" onClick={(e) => handleScroll("contact", e)}>
	                    <button type="button" className={`nav-menu-link ${activeLink === "contact" ? "active" : ""}`}>
	                      <FaEnvelope className="nav-icon" /> Contact
	                    </button>
	                  </li>
	                  <li className="nav-menu-item" onClick={(e) => NavigatePage("gallery", e)}>
	                    <button type="button" className={`nav-menu-link ${activeLink === "gallery" ? "active" : ""}`}>
	                      <span className="nav-icon">📸</span> Gallery
	                    </button>
	                  </li>
	                  <li className="nav-menu-item" onClick={(e) => NavigatePage("login", e)}>
	                    <button type="button" className={`nav-menu-link login-link ${activeLink === "login" ? "active" : ""}`}>
	                      <FaSignInAlt className="nav-icon" /> Login
	                    </button>
	                  </li>
                  <li className="nav-menu-item">
                    <button 
                      className="nav-enroll-btn"
                      onClick={(e) => NavigatePage("enroll", e)}
                    >
                      <FaUserPlus className="nav-icon" /> Enroll Now
                    </button>
                  </li>
                </>
              ) : (
                <li className="nav-menu-item">
                  <button className="nav-login-btn-simple" onClick={(e) => NavigatePage("login", e)}>
                    <FaSignInAlt className="nav-icon" /> Login
                  </button>
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </>
  );
};

export default HomeNav;
