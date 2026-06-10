import React, { useCallback, useContext, useEffect, useState } from 'react';
import "./style.css";
import InputComponent from '../content/admission/inputComponent';
import { RegisterUser } from '../../../apis/CRUD_operation/operation';
import { useNavigate } from "react-router-dom";
import AuthContext from '../../../context/student/AuthContext';
import { GetAll } from '../../../apis/Academic CRUD/AC_CRUD';
import { ToastContainer } from "react-toastify";
import NotifyMsg from '../../Academics/NotifyMsg';
import DashBoardCard from '../DashBoardCard/DashBoardCard';

const TeacherRegistration = () => {
  // Context to get the authentication token
  const { token } = useContext(AuthContext);

  // React Router's navigate function to redirect after successful registration
  const navigate = useNavigate();

  // State variables for managing form data, notifications, and registration progress
  const [classList, setClassList] = useState([]); // List of classes fetched from the API
  const [notificationMsg, setMsg] = useState(null); // Message content for notifications
  const [typeOfMsg, setMsgType] = useState(); // Type of notification (success, error, etc.)
  const [isRegistering, setIsRegistering] = useState(false); // Flag to show/hide the registration progress
  const [registrationProgress, setRegistrationProgress] = useState(0); // Progress bar value

  // Fetch class list from the API
  const fetchData = useCallback(async () => {
    try {
      console.log("Fetching class list...");
      const responseData = await GetAll("academic", token);
      setClassList(responseData.data);
      console.log("Class list fetched successfully:", responseData.data);
    } catch (error) {
      console.error("Error fetching class list:", error);
    }
  }, [token]);

  // Fetch class data when the component mounts
  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // State to manage form input data
  const [stringData, setStringData] = useState({
    userType: { value: "teacher" }, // Default userType for teacher registration
    firstName: { value: "", type: "text", size: 6 },
    lastName: { value: "", type: "text", size: 6 },
    email: { value: "", type: "email", size: 6 },
    dateOfBirth: { value: "", type: "date", size: 3 },
    gender: { value: "", type: "select", size: 3 },
    phone_num: { value: "", type: "number", size: 6 },
    emergency_contact: { value: "", type: "number", size: 6 },
    security_number: { value: "", type: "text", size: 6 },
    qualification: { value: "", type: "text", size: 6 },
    specialization: { value: "", type: "text", size: 6 },
    grade_level: { value: "", type: "text", size: 3 },
    teaching_experience: { value: "", type: "number", size: 3 },
    joining_date: { value: "", type: "date", size: 4 },
    subjects: { value: "", type: "text", size: 4 },
    position: { value: "", type: "text", size: 4 },
    address: { value: "", type: "textarea", size: 12 },
  });

  // Helper function to format labels for form fields
  const formatLabel = (key) => {
    return key
      .replace(/_/g, ' ') // Replace underscores with spaces
      .replace(/([A-Z])/g, " $1") // Add space before uppercase letters
      .replace(/^./, (str) => str.toUpperCase()); // Capitalize the first letter
  };

  // Handle input changes in the form
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    console.log(`Updating field: ${name} with value: ${value}`);
    setStringData((prevStringData) => ({
      ...prevStringData,
      [name]: { ...prevStringData[name], value }, // Update the 'value' property within the corresponding object
    }));
  };

  // Simulate registration with progress updates
  const registerUserWithProgress = async (userType, postData, progressCallback) => {
    return new Promise(async (resolve, reject) => {
      const totalTime = 3000; // Simulate API call for 3 seconds
      const interval = 100; // Update progress every 100ms
      let currentTime = 0;

      const progressInterval = setInterval(() => {
        currentTime += interval;
        const progress = Math.min((currentTime / totalTime) * 100, 100);
        progressCallback(progress);

        if (currentTime >= totalTime) {
          clearInterval(progressInterval);
          resolve("Teacher registered successfully");
        }
      }, interval);

      try {
        console.log("Sending registration data to API:", postData);
        const data = await RegisterUser(userType, postData);
        resolve(data);
      } catch (error) {
        clearInterval(progressInterval);
        console.error("Error during registration:", error);
        reject(error);
      }
    });
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Form submitted. Preparing data...");

    // Construct the payload from form data
    const values = Object.values(stringData);
    const postData = values.reduce((acc, { value }, index) => {
      const key = Object.keys(stringData)[index];
      acc[key] = value.trim(); // Ensure proper key-value mapping
      return acc;
    }, {});

    console.log("Payload being sent to API:", postData);

    // Validate form data
    const isEmpty = checkEmptyValues(postData);
    if (isEmpty) {
      setIsRegistering(true);
      try {
        const data = await registerUserWithProgress("teacher", postData, setRegistrationProgress);
        if (data === "User already registered.") {
          setMsg("Teacher already registered");
          setMsgType("error");
        } else {
          setMsg("Teacher registered successfully");
          setMsgType("success");
          setTimeout(() => {
            navigate("/users/teacher");
          }, 3000);
        }
      } catch (error) {
        console.error("Registration failed:", error);
        setMsg("Registration failed");
        setMsgType("error");
      } finally {
        setIsRegistering(false);
      }
    } else {
      setMsg("Fill the fields");
      setMsgType("warning");
    }
  };

  // Check if any form field is empty
  function checkEmptyValues(teacherInfo) {
    for (const key in teacherInfo) {
      if (!teacherInfo[key]) {
        console.warn(`Field ${key} is empty`);
        return false;
      }
    }
    return true;
  }

  return (
    <>
      <div className="container dashboard-form-page" style={{ backgroundColor: "#F4F5F9", maxWidth: "1424px" }}>
        <DashBoardCard title="Add a teacher" visibility="hidden" />

        {isRegistering && (
          <div className="dashboard-form-progress">
            <div className="dashboard-form-progress-card">
              <progress value={registrationProgress} max="100" />
              <p>Registering teacher. Please wait...</p>
            </div>
          </div>
        )}

        <section id="form" className="dashboard-form-shell">
          <div className="dashboard-form-header">
            <span className="dashboard-form-eyebrow">Staff Setup</span>
            <h3 className="dashboard-form-title">Teacher Registration</h3>
            <p className="dashboard-form-subtitle">
              Create teacher accounts with consistent profile, grade, and subject details.
            </p>
          </div>
          <div className="row">
            <div className="col-md-12">
              <form onSubmit={handleSubmit}>
                <div className="form-row">
                  {Object.entries(stringData)
                    .slice(1, 11)
                    .map(([menuItem, component]) => (
                      <InputComponent
                        key={menuItem}
                        label={formatLabel(menuItem)}
                        size={component.size}
                        type={component.type}
                        name={menuItem}
                        value={component.value}
                        onChange={(e) => handleInputChange(e)}
                        placeholder={`Enter your ${formatLabel(menuItem)}`}
                      />
                    ))}

                  <div className="form-group col-md-3">
                    <h5 className="text-left">Grade level</h5>
                    <select
                      className="form-control"
                      id="inputDado"
                      name="grade_level"
                      value={stringData.grade_level.value}
                      onChange={(e) => handleInputChange(e)}
                      required
                    >
                      <option value="">Select class...</option>
                      {classList.map((result) => (
                        <option key={result.className} value={result.className}>
                          {result.className}
                        </option>
                      ))}
                    </select>
                  </div>

                  {Object.entries(stringData)
                    .slice(12)
                    .map(([menuItem, component]) => (
                      <InputComponent
                        key={menuItem}
                        label={formatLabel(menuItem)}
                        size={component.size}
                        type={component.type}
                        name={menuItem}
                        value={component.value}
                        onChange={(e) => handleInputChange(e)}
                        placeholder={`Enter your ${formatLabel(menuItem)}`}
                      />
                    ))}
                </div>

                <div className="dashboard-form-actions">
                  <button type="submit" className="btn btn-lg btn-secondary">
                    Register
                  </button>
                </div>
              </form>
            </div>
          </div>
        </section>
      </div>
      <NotifyMsg msg={notificationMsg} type={typeOfMsg} />
      <ToastContainer />
    </>
  );
};

export default TeacherRegistration;
