import React, { useState } from "react";
import "./style.css";
import logo from "../Logo.png";
import { useNavigate } from "react-router-dom";
import { FaBars } from "react-icons/fa";


const HomeNav = ({ isOtherOptions }) => {
  const navigate = useNavigate();
  const [activeLink, setActiveLink] = useState(" ");


  
  
  const handleScroll = (id, e ) => {
    e.preventDefault();
  setActiveLink(id);
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

  const NavigatePage = (pageName,e) => {
    e.preventDefault();
  setActiveLink(pageName);
    console.log(pageName);
    if (pageName === "login") {
      navigate(`/login`);
    } else {
      navigate(`/${pageName}`);
    }
  };

  return (
    <>
       <nav className="navbar sticky-top navbar-expand-lg home_nav">
        <div className="container-fluid">
          <div className="navbar-brand mb-0 h1">
            <a className="myLogo" href="/">
              <img className="home-logo-img" src={logo} alt="Logo" />
              <span className="ms-3">Mother's Goose</span>
            </a>
          </div>
          
          <button
            className="navbar-toggler home-nav-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <FaBars />

          </button>

          <div className="collapse navbar-collapse" id="navbarSupportedContent">
    <ul className="navbar-nav mr-auto w-100 justify-content-end">
	      {isOtherOptions ? (
	        <>
	          <li className="nav-item" onClick={(e) => handleScroll("Home", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "Home" ? "active" : ""}`}>
	              Home
	            </button>
	          </li>
	          <li className="nav-item" onClick={(e) => handleScroll("about", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "about" ? "active" : ""}`}>
	              About Us
	            </button>
	          </li>
	          <li className="nav-item" onClick={(e) => handleScroll("programs", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "programs" ? "active" : ""}`}>
	              Our Program
	            </button>
	          </li>
	          <li className="nav-item" onClick={(e) => handleScroll("contact", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "contact" ? "active" : ""}`}>
	              Contact
	            </button>
	          </li>
	          <li className="nav-item" onClick={(e) => NavigatePage("login", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "login" ? "active" : ""}`}>
	              Login
	            </button>
	          </li>
	          <li className="nav-item" onClick={(e) => NavigatePage("registrationQuery", e)}>
	            <button type="button" className={`nav-link home-nav-link ${activeLink === "registrationQuery" ? "active" : ""}`}>
	              Registration
	            </button>
	          </li>
	        </>
      ) : (
        <li className="nav-item">
          <button className="login-button" onClick={(e) => NavigatePage("login", e)}>
            <span className="icon">🔒</span> Login
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
