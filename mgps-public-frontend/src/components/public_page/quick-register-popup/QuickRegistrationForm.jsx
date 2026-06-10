import React, { useState } from "react";
import { admissionQuery } from "../../../apis/Login/AuthService";
import NotifyMsg from "../../Academics/NotifyMsg";
import { ToastContainer } from "react-toastify";
import ThankYouPage from "../thankYouPage";
import "./style.css";
import messages from "../messages.json"; // Import JSON file

const QuickRegistrationForm = () => {
  const [notificationMsg, setMsg] = useState(null);
  const [typeOfMsg, setMsgType] = useState();
  const [formSubmitted, setFormSubmitted] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    mobileNumber: "",
    class: "",
    query: "",
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await admissionQuery(formData);
    if (data.status === 200) {
      setMsg("Query submitted successfully");
      setMsgType("success");
      setFormSubmitted(true);
    } else {
      setMsg("Some error occurred, Please try again later");
      setMsgType("error");
    }
  };

  return (
    <>
      {formSubmitted ? (
        <ThankYouPage />
      ) : (
        <div className="container-fluide">
          <div className="admission-container">
          <div className="admission-right">
            <h2>🎉 {messages.registrationFormTitle} ☀️</h2>
            <form onSubmit={handleSubmit}>
              <input
                type="text"
                name="fullName"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleChange}
                required
              />
              <input
                type="email"
                name="email"
                placeholder="Email Id"
                value={formData.email}
                onChange={handleChange}
              />
              <input
                type="text"
                name="mobileNumber"
                placeholder="Mobile Number"
                value={formData.mobileNumber}
                onChange={handleChange}
                required
              />
              <select
                name="class"
                value={formData.class}
                onChange={handleChange}
                required
              >
                <option value="">Select Class</option>
                <option value="Play Ground">Play Group</option>
                <option value="Nursery">Nursery</option>
                <option value="LKG">LKG</option>
                <option value="UKG">UKG</option>
              </select>
              <textarea
                name="query"
                placeholder="Your Query (Optional)"
                rows="3"
                value={formData.query}
                onChange={handleChange}
              ></textarea>
              <button type="submit">Submit</button>
            </form>
            </div>
          </div>
        </div>
        
      )}
      <NotifyMsg msg={notificationMsg} type={typeOfMsg} />
      <ToastContainer />
    </>
  );
};

export default QuickRegistrationForm;