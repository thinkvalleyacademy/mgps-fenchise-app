import React from "react";
import "./ChildFriendlyFooter.css";
import logo from "../Logo.png";
import { CONTACT_DETAILS, WHATSAPP_LINK } from "../../../../constants/contactDetails";
import { FaPhone, FaEnvelope, FaMapMarkerAlt, FaWhatsapp, FaFacebook, FaInstagram, FaYoutube } from "react-icons/fa";

const MyFooter = () => {
  const handleScroll = (id) => {
    const element = document.getElementById(id);
    if (element) {
      window.scrollTo({
        top: element.offsetTop,
        behavior: "smooth",
      });
    }
  };

  return (
    <>
      <footer className="child-friendly-footer">
        {/* Rainbow Top Border */}
        <div className="footer-rainbow-border"></div>

        {/* Decorative Elements */}
        <div className="footer-decorations">
          <span className="footer-icon">🌟</span>
          <span className="footer-icon" style={{animationDelay: '1s'}}>🎨</span>
          <span className="footer-icon" style={{animationDelay: '2s'}}>📚</span>
          <span className="footer-icon" style={{animationDelay: '3s'}}>🎈</span>
        </div>

        <div className="container">
          <div className="row">
            {/* About Section */}
            <div className="col-lg-5 col-md-12 footer-section about-section">
              <div className="footer-logo-container">
                <img src={logo} className="footer-logo" alt="Mother's Goose Logo" />
                <div className="footer-brand-text">
                  <h3 className="footer-brand-name">Mother's Goose 🪿</h3>
                  <p className="footer-tagline">Building Strong Foundations for Bright Futures ✨</p>
                </div>
              </div>
              <p className="footer-description">
                A trusted preschool focused on the complete development of young children. 
                We provide a safe, caring, and engaging environment where children develop 
                strong foundations in learning, communication, and social skills.
	              </p>
	              <div className="footer-social-links">
	                <button type="button" className="social-icon" aria-label="Facebook">
	                  <FaFacebook />
	                </button>
	                <a href={CONTACT_DETAILS.instagram} target="_blank" rel="noopener noreferrer" className="social-icon" aria-label="Instagram">
	                  <FaInstagram />
	                </a>
	                <button type="button" className="social-icon" aria-label="YouTube">
	                  <FaYoutube />
	                </button>
	              </div>
            </div>

            {/* Quick Links Section */}
            <div className="col-lg-3 col-md-6 footer-section links-section">
              <h3 className="footer-heading">
                <span className="heading-icon">🗺️</span> Explore Us
              </h3>
              <ul className="footer-links">
                <li>
                  <button onClick={() => handleScroll("Home")}>
                    <span className="link-bullet">🏠</span> Home
                  </button>
                </li>
                <li>
                  <button onClick={() => handleScroll("about")}>
                    <span className="link-bullet">ℹ️</span> About Us
                  </button>
                </li>
                <li>
                  <button onClick={() => handleScroll("programs")}>
                    <span className="link-bullet">📚</span> Our Programs
                  </button>
                </li>
                <li>
                  <button onClick={() => handleScroll("contact")}>
                    <span className="link-bullet">📧</span> Contact
                  </button>
                </li>
              </ul>
            </div>

            {/* Contact Section */}
            <div className="col-lg-4 col-md-6 footer-section contact-section">
              <h3 className="footer-heading">
                <span className="heading-icon">📍</span> Get In Touch
              </h3>
              
              <div className="contact-info">
                <div className="contact-item">
                  <FaMapMarkerAlt className="contact-icon" />
                  <span>{CONTACT_DETAILS.address}</span>
                </div>
                
                <div className="contact-item">
                  <FaPhone className="contact-icon" />
                  <a href={`tel:${CONTACT_DETAILS.phone1}`}>
                    {CONTACT_DETAILS.phone1}
                  </a>
                </div>
                
                <div className="contact-item">
                  <FaWhatsapp className="contact-icon whatsapp" />
                  <a href={WHATSAPP_LINK} target="_blank" rel="noopener noreferrer">
                    {CONTACT_DETAILS.phone2}
                  </a>
                </div>
                
                <div className="contact-item">
                  <FaEnvelope className="contact-icon" />
                  <a href={`mailto:${CONTACT_DETAILS.email}`}>
                    {CONTACT_DETAILS.email}
                  </a>
                </div>
              </div>

              {/* Map */}
              <div className="footer-map-container">
                <iframe
                  className="footer-map"
                  frameBorder="0"
                  style={{ border: 0 }}
                  allowFullScreen
                  src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d1813.0547569239166!2d83.0610103!3d24.6543585!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x398ef900137a2047%3A0xf346ae70844d5064!2sChurk%20sahijan%20khurd!5e0!3m2!1sen!2sin!4v1724190389979!5m2!1sen!2sin"
                  title="School Location Map"
                />
              </div>
            </div>
          </div>

          {/* Copyright Section */}
          <div className="footer-bottom">
            <div className="copyright-text">
              <p>© {new Date().getFullYear()} Mother's Goose Preschool. All rights reserved.</p>
              <p className="made-with-love">Made with ❤️ for our little learners</p>
            </div>
          </div>
        </div>
      </footer>
    </>
  );
};

export default MyFooter;
