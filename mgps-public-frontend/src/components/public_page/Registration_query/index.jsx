import React, { useState } from "react";
import "./style.css";
import AboutUs from "../HomePage/aboutPage";
import MyFooter from "../HomePage/footerComponent";
import HomeNav from "../HomePage/homeNav";
import { admissionQuery } from "../../../apis/Login/AuthService";
import NotifyMsg from "../../Academics/NotifyMsg";
import { ToastContainer, toast } from "react-toastify";
import ThankYouPage from "../thankYouPage";
import { FaWhatsapp, FaPhone, FaEnvelope, FaClock } from "react-icons/fa";

const Registration_query = () => {
  const [notificationMsg, setMsg] = useState(null);
  const [typeOfMsg, setMsgType] = useState();
  const [formSubmitted, setFormSubmitted] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    mobileNumber: "",
    class: "",
    queryType: "",
    query: "",
  });

  // Predefined query types for parents
  const queryTypes = [
    { value: "", label: "📋 Select Query Type", icon: "📋" },
    { value: "admission", label: "🎓 Admission Related", icon: "🎓" },
    { value: "fees", label: "💰 Fees & Payment", icon: "💰" },
    { value: "timings", label: "⏰ School Timings", icon: "⏰" },
    { value: "transport", label: "🚌 Transport Facility", icon: "🚌" },
    { value: "curriculum", label: "📚 Curriculum", icon: "📚" },
    { value: "facilities", label: "🏫 School Facilities", icon: "🏫" },
    { value: "other", label: "💬 Other Query", icon: "💬" },
  ];

  const classOptions = [
    { value: "", label: "🎒 Select Class" },
    { value: "Play Group", label: "👶 Play Group (Age 2-3)" },
    { value: "Nursery", label: "🧸 Nursery (Age 3-4)" },
    { value: "LKG", label: "📖 LKG (Age 4-5)" },
    { value: "UKG", label: "✏️ UKG (Age 5-6)" },
    { value: "1st", label: "1st Standard" },
    { value: "2nd", label: "2nd Standard" },
    { value: "3rd", label: "3rd Standard" },
    { value: "4th", label: "4th Standard" },
    { value: "5th", label: "5th Standard" },
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate mobile number
    if (formData.mobileNumber.length !== 10) {
      toast.error("Please enter a valid 10-digit mobile number");
      return;
    }

    const data = await admissionQuery(formData);
    if (data.status === 200) {
      toast.success("Query submitted successfully! We'll contact you soon.");
      setMsg("Query submitted successfully");
      setMsgType("success");
      setFormSubmitted(true);
    } else {
      toast.error("Some error occurred. Please try again later.");
      setMsg("Some error occurred, Please try again later");
      setMsgType("error");
    }
  };

  // WhatsApp contact number
  const whatsappNumber = "+919125540611";
  const whatsappMessage = "Hello, I would like to inquire about admission at Mother's Goose Public School.";
  const whatsappLink = `https://wa.me/${whatsappNumber}?text=${encodeURIComponent(whatsappMessage)}`;

  return (
    <>
      <div className="container-fluide">
        <div className="row">
          <HomeNav />
        </div>

        {/* WhatsApp Floating Button */}
        <a 
          href={whatsappLink}
          target="_blank"
          rel="noopener noreferrer"
          className="whatsapp-float"
          title="Chat with us on WhatsApp"
        >
          <FaWhatsapp />
          <span className="whatsapp-text">Chat on WhatsApp</span>
        </a>

        <div className="row">
          {formSubmitted ? (
            <ThankYouPage />
          ) : (
            <div className="admission-container">
              <div className="admission-left">
                {/* Admission Open Banner with WhatsApp */}
                <div className="admission-banner">
                  <h1>🎉 Admission Open for Session 2026-2027 🎉</h1>
                  <a 
                    href={whatsappLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="banner-whatsapp-btn"
                    title="Contact us on WhatsApp"
                  >
                    <FaWhatsapp />
                    <span>Contact on WhatsApp</span>
                  </a>
                </div>
                
                <div className="breadcrumb">
                  <span>Home</span> &gt; <span>Admission</span>
                </div>
                <p className="admission-description">
                  At <strong>Mother's Goose Preschool</strong>, we nurture young minds with love
                  and care, providing a safe and stimulating environment for your
                  child's early education. Our dedicated team creates a supportive
                  environment where children can thrive.
                </p>

                {/* Quick Contact Info */}
                <div className="quick-contact-info">
                  <h3>📞 Quick Contact</h3>
                  <div className="contact-item">
                    <FaPhone className="contact-icon" />
                    <span>+91 91255 40611</span>
                  </div>
                  <div className="contact-item">
                    <FaWhatsapp className="contact-icon whatsapp-icon" />
                    <span>+91 91255 40611</span>
                  </div>
                  <div className="contact-item">
                    <FaEnvelope className="contact-icon" />
                    <span>admissions@mothersgoose.com</span>
                  </div>
                  <div className="contact-item">
                    <FaClock className="contact-icon" />
                    <span>Mon - Sat: 9:00 AM - 4:00 PM</span>
                  </div>
                </div>

                {/* Class Timings Info */}
                <div className="class-timings-info">
                  <h3>⏰ Class Timings</h3>
                  <div className="timing-item">
                    <span className="timing-label">Days:</span>
                    <span>Monday to Saturday</span>
                  </div>
                  <div className="timing-item">
                    <span className="timing-label">Time:</span>
                    <span>8:00 AM - 12:00 PM (with 1 hour break)</span>
                  </div>
                  <div className="timing-item">
                    <span className="timing-label">Classes:</span>
                    <span>Play Group, Nursery, LKG, UKG</span>
                  </div>
                </div>
              </div>

              <div className="admission-right">
                <div className="form-header-section">
                  <h2>📝 Admission Enquiry Form</h2>
                  <p>Fill the form below and we'll get back to you within 24 hours</p>
                </div>

                <form onSubmit={handleSubmit}>
                  <div className="form-group-row">
                    <div className="form-group">
                      <label htmlFor="fullName">
                        <span className="label-icon">👤</span> Parent's Full Name *
                      </label>
                      <input
                        type="text"
                        id="fullName"
                        name="fullName"
                        placeholder="Enter your full name"
                        value={formData.fullName}
                        onChange={handleChange}
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label htmlFor="mobileNumber">
                        <span className="label-icon">📱</span> Mobile Number *
                      </label>
                      <input
                        type="tel"
                        id="mobileNumber"
                        name="mobileNumber"
                        placeholder="Enter 10-digit mobile number"
                        value={formData.mobileNumber}
                        onChange={handleChange}
                        pattern="[0-9]{10}"
                        maxLength="10"
                        required
                      />
                    </div>
                  </div>

                  <div className="form-group-row">
                    <div className="form-group">
                      <label htmlFor="email">
                        <span className="label-icon">✉️</span> Email Address
                      </label>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="your.email@example.com"
                        value={formData.email}
                        onChange={handleChange}
                      />
                    </div>

                    <div className="form-group">
                      <label htmlFor="class">
                        <span className="label-icon">🎒</span> Select Class *
                      </label>
                      <select
                        id="class"
                        name="class"
                        value={formData.class}
                        onChange={handleChange}
                        required
                      >
                        {classOptions.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="form-group">
                    <label htmlFor="queryType">
                      <span className="label-icon">📋</span> Query Type *
                    </label>
                    <select
                      id="queryType"
                      name="queryType"
                      value={formData.queryType}
                      onChange={handleChange}
                      required
                      className="query-type-select"
                    >
                      {queryTypes.map((type) => (
                        <option key={type.value} value={type.value}>
                          {type.label}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="query">
                      <span className="label-icon">💬</span> Your Query *
                    </label>
                    <textarea
                      id="query"
                      name="query"
                      placeholder="Please describe your query in detail..."
                      rows="5"
                      value={formData.query}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="form-actions">
                    <button type="submit" className="submit-btn">
                      <span>🚀</span> Submit Enquiry
                    </button>
                    <button 
                      type="button" 
                      className="whatsapp-btn"
                      onClick={() => window.open(whatsappLink, '_blank')}
                    >
                      <FaWhatsapp /> Chat on WhatsApp
                    </button>
                  </div>

                  <p className="form-note">
                    🔒 Your information is secure. We'll never spam you.
                  </p>
                </form>
              </div>
            </div>
          )}
        </div>

        <div className="row">
          <AboutUs />
        </div>
        <div className="row">
          <MyFooter />
        </div>
      </div>
      <NotifyMsg msg={notificationMsg} type={typeOfMsg} />
      <ToastContainer position="top-right" autoClose={3000} />
    </>
  );
};

export default Registration_query;
