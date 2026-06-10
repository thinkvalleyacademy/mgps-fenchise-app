import React, { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import HomeNav from "../HomePage/homeNav/HomeNavChildFriendly";
import { fetchActiveSchools, registerEmployee } from "../../../apis/Login/AuthService";

const Signup = () => {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    userType: "teacher",
    schoolCode: "MGPS",
    firstName: "",
    lastName: "",
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    gender: "",
    address: "",
  });
  const [schools, setSchools] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const isSuperadminSignup = form.userType === "superadmin";

  useEffect(() => {
    let mounted = true;
    fetchActiveSchools().then(({ data_set }) => {
      if (!mounted) return;
      setSchools(data_set);
      if (data_set.length > 0) {
        setForm((prev) => ({
          ...prev,
          schoolCode: data_set.some((school) => school.schoolCode === prev.schoolCode)
            ? prev.schoolCode
            : data_set[0].schoolCode,
        }));
      }
    });
    return () => {
      mounted = false;
    };
  }, []);

  const notifyError = (msg) => {
    toast["error"](msg, {
      position: "top-right",
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
      transition: Bounce,
    });
  };

  const notifySuccess = (msg) => {
    toast["success"](msg, {
      position: "top-right",
      autoClose: 5000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
      transition: Bounce,
    });
  };

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "schoolCode" ? value.toUpperCase() : value,
    }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!form.firstName || !form.lastName || !form.email || !form.username || !form.password) {
      notifyError("Please fill all required fields.");
      return;
    }

    if (isSuperadminSignup && !form.schoolCode) {
      notifyError("Please choose a school code for the superadmin.");
      return;
    }

    if (form.schoolCode && !/^[A-Z0-9_-]{2,50}$/.test(form.schoolCode)) {
      notifyError("School code must be 2-50 characters and can use letters, numbers, underscore, or hyphen.");
      return;
    }

    if (isSuperadminSignup && schools.length > 0 && !schools.some((school) => school.schoolCode === form.schoolCode)) {
      notifyError("Please choose a valid active school code.");
      return;
    }

    if (form.password !== form.confirmPassword) {
      notifyError("Passwords do not match!");
      return;
    }

    if (form.password.length < 6) {
      notifyError("Password must be at least 6 characters long.");
      return;
    }

    if (!/^[a-zA-Z0-9_]+$/.test(form.username)) {
      notifyError("Username can only contain letters, numbers, and underscores.");
      return;
    }

    setSubmitting(true);
    
    // Remove confirmPassword before sending to API
    const { confirmPassword, ...formData } = form;
    const { error, data_set } = await registerEmployee(formData);
    setSubmitting(false);

    if (error) {
      notifyError(error);
      return;
    }

    // Check if approval is needed
    const message = data_set?.message || "Signup successful!";
    notifySuccess(message);
    
    // Redirect to login after delay
    setTimeout(() => navigate("/login"), 2000);
  };

  // Get approval message based on user type
  const getApprovalMessage = () => {
    if (form.userType === "superadmin" || form.userType === "student") {
      return "✅ You can login immediately after registration";
    }
    return "⏳ Your account will require admin approval before you can login";
  };

  const styles = {
    container: {
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '80px 20px 40px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center'
    },
    card: {
      background: 'white',
      borderRadius: '20px',
      boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
      maxWidth: '800px',
      width: '100%',
      overflow: 'hidden'
    },
    header: {
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      padding: '30px',
      textAlign: 'center'
    },
    formContainer: {
      padding: '30px'
    },
    input: {
      width: '100%',
      padding: '12px 15px',
      border: '2px solid #e0e0e0',
      borderRadius: '10px',
      fontSize: '14px',
      transition: 'all 0.3s ease',
      outline: 'none'
    },
    inputFocus: {
      borderColor: '#667eea',
      boxShadow: '0 0 0 3px rgba(102, 126, 234, 0.1)'
    },
    select: {
      width: '100%',
      padding: '12px 15px',
      border: '2px solid #e0e0e0',
      borderRadius: '10px',
      fontSize: '14px',
      backgroundColor: 'white',
      cursor: 'pointer'
    },
    button: {
      width: '100%',
      padding: '14px',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      border: 'none',
      borderRadius: '10px',
      fontSize: '16px',
      fontWeight: '600',
      cursor: 'pointer',
      transition: 'all 0.3s ease',
      marginTop: '20px'
    },
    buttonDisabled: {
      opacity: 0.6,
      cursor: 'not-allowed'
    },
    approvalBox: {
      background: form.userType === 'superadmin' || form.userType === 'student' 
        ? 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)'
        : 'linear-gradient(135deg, #f2994a 0%, #f2c94c 100%)',
      color: 'white',
      padding: '15px',
      borderRadius: '10px',
      marginTop: '20px',
      textAlign: 'center',
      fontWeight: '500'
    },
    link: {
      color: '#667eea',
      textDecoration: 'none',
      fontWeight: '600'
    }
  };

  return (
    <>
      <HomeNav />
      <div style={styles.container}>
        <div style={styles.card}>
          <div style={styles.header}>
            <h2 style={{ margin: 0, fontSize: '28px' }}>🎉 Create Your Account</h2>
            <p style={{ margin: '10px 0 0', opacity: 0.9 }}>Join our school management system</p>
          </div>

          <div style={styles.formContainer}>
            <form onSubmit={onSubmit}>
              {/* User Type Selection */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                  User Type *
                </label>
                <select
                  style={styles.select}
                  name="userType"
                  value={form.userType}
                  onChange={onChange}
                  required
                >
                  <option value="teacher">👨‍🏫 Teacher</option>
                  <option value="student">🎓 Student</option>
                  <option value="admin">👔 Admin</option>
                  <option value="superadmin">👑 Super Admin</option>
                  <option value="parent">👪 Parent</option>
                  <option value="accountant">💰 Accountant</option>
                  <option value="librarian">📚 Librarian</option>
                </select>
              </div>

              {isSuperadminSignup && (
                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                    School Code *
                  </label>
                  {schools.length > 0 ? (
                    <select
                      style={styles.select}
                      name="schoolCode"
                      value={form.schoolCode}
                      onChange={onChange}
                      required
                    >
                      {schools.map((school) => (
                        <option key={school.schoolCode} value={school.schoolCode}>
                          {school.schoolCode} - {school.schoolName}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <input
                      type="text"
                      style={styles.input}
                      name="schoolCode"
                      value={form.schoolCode}
                      onChange={onChange}
                      placeholder="MGPS"
                      required
                    />
                  )}
                  <small style={{ color: '#666', marginTop: '5px', display: 'block' }}>
                    This superadmin will be created for the selected school.
                  </small>
                </div>
              )}

              {/* Name Fields */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                <div>
                  <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                    First Name *
                  </label>
                  <input
                    type="text"
                    style={styles.input}
                    name="firstName"
                    value={form.firstName}
                    onChange={onChange}
                    placeholder="John"
                    required
                  />
                </div>
                <div>
                  <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                    Last Name *
                  </label>
                  <input
                    type="text"
                    style={styles.input}
                    name="lastName"
                    value={form.lastName}
                    onChange={onChange}
                    placeholder="Doe"
                    required
                  />
                </div>
              </div>

              {/* Email */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                  Email Address *
                </label>
                <input
                  type="email"
                  style={styles.input}
                  name="email"
                  value={form.email}
                  onChange={onChange}
                  placeholder="john.doe@example.com"
                  required
                />
              </div>

              {/* Username */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                  Username *
                </label>
                <input
                  type="text"
                  style={styles.input}
                  name="username"
                  value={form.username}
                  onChange={onChange}
                  placeholder="Choose a unique username (letters, numbers, underscores only)"
                  required
                />
                <small style={{ color: '#666', marginTop: '5px', display: 'block' }}>
                  This will be your login username. Use only letters, numbers, and underscores.
                </small>
              </div>

              {/* Password Fields */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                <div>
                  <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                    Password *
                  </label>
                  <input
                    type={showPassword ? "text" : "password"}
                    style={styles.input}
                    name="password"
                    value={form.password}
                    onChange={onChange}
                    placeholder="Min. 6 characters"
                    required
                  />
                </div>
                <div>
                  <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                    Confirm Password *
                  </label>
                  <input
                    type={showPassword ? "text" : "password"}
                    style={styles.input}
                    name="confirmPassword"
                    value={form.confirmPassword}
                    onChange={onChange}
                    placeholder="Re-enter password"
                    required
                  />
                </div>
              </div>

              {/* Show Password Checkbox */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <input
                    type="checkbox"
                    checked={showPassword}
                    onChange={(e) => setShowPassword(e.target.checked)}
                  />
                  <span>Show Password</span>
                </label>
              </div>

              {/* Gender */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                  Gender
                </label>
                <select
                  style={styles.select}
                  name="gender"
                  value={form.gender}
                  onChange={onChange}
                >
                  <option value="">Select Gender</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              {/* Address */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ fontWeight: '600', marginBottom: '8px', display: 'block' }}>
                  Address
                </label>
                <textarea
                  style={{ ...styles.input, minHeight: '80px', resize: 'vertical' }}
                  name="address"
                  value={form.address}
                  onChange={onChange}
                  placeholder="Your address (optional)"
                  rows="3"
                />
              </div>

              {/* Approval Status Message */}
              <div style={styles.approvalBox}>
                {getApprovalMessage()}
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                style={{
                  ...styles.button,
                  ...(submitting ? styles.buttonDisabled : {})
                }}
                disabled={submitting}
              >
                {submitting ? "Creating Account..." : "Create Account"}
              </button>

              {/* Login Link */}
              <div style={{ textAlign: 'center', marginTop: '20px', color: '#666' }}>
                Already have an account?{' '}
                <Link to="/login" style={styles.link}>
                  Login here
                </Link>
              </div>
            </form>
          </div>
        </div>
      </div>

      <ToastContainer />
    </>
  );
};

export default Signup;
