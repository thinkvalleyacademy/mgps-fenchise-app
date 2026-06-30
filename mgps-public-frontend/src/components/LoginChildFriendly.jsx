import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchActiveSchools, loginUser } from "../apis/Login/AuthService";
import AuthContext from "../context/student/AuthContext";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Bounce } from "react-toastify";
import { FaSchool, FaChalkboardTeacher, FaChild, FaBook } from "react-icons/fa";
import { FaRegSmile } from "react-icons/fa";
import HomeNav from "./public_page/HomePage/homeNav/HomeNavChildFriendly";
import MyFooter from "./public_page/HomePage/footerComponent/MyFooterChildFriendly";

const Login = () => {
  const [credentials, setcredentials] = useState({
    username: "",
    password: "",
    userType: "",
    schoolCode: "MGPS",
  });
  const { setToken, setUserName, setUser, setName } = useContext(AuthContext);

  const [showPassword, setShowPassword] = useState(false);
  const [schools, setSchools] = useState([]);
  const isSuperadminLogin = credentials.userType === "superadmin";

  const navigate = useNavigate();

  useEffect(() => {
    let mounted = true;
    fetchActiveSchools().then(({ data_set }) => {
      if (!mounted) return;
      setSchools(data_set);
      if (data_set.length > 0) {
        setcredentials((prev) => ({
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

  const notify = (msg) => {
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

  const hadleSubmit = async (e) => {
    e.preventDefault();

    if (credentials.userType === "") {
      alert("Please select who you are! 🎒");
    } else if (!credentials.username && !credentials.password) {
      alert("Please enter username and password. 📝");
      return;
    } else {
      const { data_set, error } = await loginUser(credentials);
      if (error) {
        console.log("Error is ", error);
        if (error.includes("Invalid Token")) {
          notify("Token expired. Please log in again. 🔄");
          navigate("/login");
        } else {
          notify(error);
        }
      } else {
        setToken(data_set.accessToken ?? data_set.token);
        setName(data_set.firstName + " " + data_set.lastName);
        setUserName(data_set.username);
        setUser(credentials.userType);
        navigate("/Dashboard"); // Redirect to /Dashboard after login
      }
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const changeUser = (param) => {
    setcredentials((prevState) => ({
      ...prevState,
      userType: param,
    }));
  };

  // Child-friendly styles
  const styles = {
    loginContainer: {
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '40px 20px',
      position: 'relative',
      overflow: 'hidden',
    },
    floatingShape: {
      position: 'absolute',
      fontSize: '60px',
      animation: 'float 6s ease-in-out infinite',
      opacity: 0.3,
    },
    card: {
      background: 'white',
      borderRadius: '30px',
      boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
      maxWidth: '900px',
      width: '100%',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'row',
    },
    leftPanel: {
      flex: 1,
      background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      padding: '40px',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      color: 'white',
      textAlign: 'center',
    },
    rightPanel: {
      flex: 1,
      padding: '40px',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
    },
    welcomeText: {
      fontSize: '2.5rem',
      fontWeight: 'bold',
      marginBottom: '20px',
      textShadow: '2px 2px 4px rgba(0,0,0,0.2)',
    },
    featureItem: {
      display: 'flex',
      alignItems: 'center',
      gap: '15px',
      marginBottom: '20px',
      fontSize: '1.1rem',
    },
    featureIcon: {
      fontSize: '2.5rem',
      filter: 'drop-shadow(0 2px 4px rgba(0,0,0,0.2))',
    },
    formTitle: {
      fontSize: '2rem',
      color: '#667eea',
      marginBottom: '30px',
      fontWeight: 'bold',
      textAlign: 'center',
    },
    inputGroup: {
      marginBottom: '25px',
      position: 'relative',
    },
    input: {
      width: '100%',
      padding: '15px 20px',
      borderRadius: '50px',
      border: '3px solid #e0e0e0',
      fontSize: '1rem',
      transition: 'all 0.3s ease',
      outline: 'none',
    },
    userSelectContainer: {
      display: 'flex',
      gap: '10px',
      marginBottom: '25px',
      flexWrap: 'wrap',
    },
    userButton: {
      flex: 1,
      padding: '12px 20px',
      borderRadius: '50px',
      border: 'none',
      background: '#f0f0f0',
      color: '#666',
      cursor: 'pointer',
      transition: 'all 0.3s ease',
      fontWeight: '600',
      fontSize: '0.9rem',
    },
    submitButton: {
      width: '100%',
      padding: '15px',
      borderRadius: '50px',
      border: 'none',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      fontSize: '1.1rem',
      fontWeight: 'bold',
      cursor: 'pointer',
      transition: 'all 0.3s ease',
      boxShadow: '0 5px 15px rgba(102, 126, 234, 0.4)',
    },
    select: {
      width: '100%',
      padding: '15px 20px',
      borderRadius: '50px',
      border: '3px solid #e0e0e0',
      fontSize: '1rem',
      outline: 'none',
      background: 'white',
      marginBottom: '20px',
    },
  };

  return (
    <>
      <HomeNav isOtherOptions={false} />
      
      <style>
        {`
          @keyframes float {
            0%, 100% { transform: translateY(0px) rotate(0deg); }
            50% { transform: translateY(-20px) rotate(10deg); }
          }
          input:focus {
            border-color: #667eea !important;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
          }
          button:hover {
            transform: translateY(-2px);
            box-shadow: 0 7px 20px rgba(102, 126, 234, 0.6) !important;
          }
          .user-btn-active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
            color: white !important;
            transform: scale(1.05);
          }
        `}
      </style>

      <div style={styles.loginContainer}>
        {/* Floating decorative elements */}
        <div style={{...styles.floatingShape, top: '10%', left: '5%'}}>🎈</div>
        <div style={{...styles.floatingShape, top: '20%', right: '10%', animationDelay: '1s'}}>⭐</div>
        <div style={{...styles.floatingShape, bottom: '15%', left: '10%', animationDelay: '2s'}}>🌈</div>
        <div style={{...styles.floatingShape, bottom: '25%', right: '5%', animationDelay: '3s'}}>🎨</div>

        <div style={styles.card}>
          {/* Left Panel - Welcome */}
          <div style={styles.leftPanel}>
            <FaSchool style={{fontSize: '80px', marginBottom: '20px'}} />
            <h2 style={styles.welcomeText}>
              Welcome to<br/>Mother's Goose! 🎉
            </h2>
            
            <div style={{marginTop: '30px'}}>
              <div style={styles.featureItem}>
                <FaChalkboardTeacher style={styles.featureIcon} />
                <span>Loving Teachers</span>
              </div>
              <div style={styles.featureItem}>
                <FaChild style={styles.featureIcon} />
                <span>Fun Friends</span>
              </div>
              <div style={styles.featureItem}>
                <FaBook style={styles.featureIcon} />
                <span>Exciting Learning</span>
              </div>
              <div style={styles.featureItem}>
                <FaRegSmile style={styles.featureIcon} />
                <span>Happy Days</span>
              </div>
            </div>
            
            {/* Back to Home Button */}
            <button 
              onClick={() => navigate('/')}
              style={{
                marginTop: '30px',
                padding: '12px 30px',
                background: 'rgba(255,255,255,0.2)',
                border: '2px solid white',
                borderRadius: '50px',
                color: 'white',
                fontWeight: '600',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                fontSize: '1rem',
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                alignSelf: 'center'
              }}
              onMouseOver={(e) => {
                e.target.style.background = 'white';
                e.target.style.color = '#667eea';
                e.target.style.transform = 'scale(1.05)';
              }}
              onMouseOut={(e) => {
                e.target.style.background = 'rgba(255,255,255,0.2)';
                e.target.style.color = 'white';
                e.target.style.transform = 'scale(1)';
              }}
            >
              🏠 Back to Home
            </button>
          </div>

          {/* Right Panel - Login Form */}
          <div style={styles.rightPanel}>
            <h3 style={styles.formTitle}>Sign In 🚀</h3>
            
            <form onSubmit={hadleSubmit}>
              {/* User Type Selection */}
              <div style={styles.userSelectContainer}>
                {['superadmin', 'teacher', 'admin', 'parent', 'student'].map((type) => (
                  <button
                    key={type}
                    type="button"
                    className={credentials.userType === type ? 'user-btn-active' : ''}
                    style={{
                      ...styles.userButton,
                      ...(credentials.userType === type ? {
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        color: 'white',
                        transform: 'scale(1.05)',
                      } : {})
                    }}
                    onClick={() => changeUser(type)}
                  >
                    {type === 'superadmin' && '🛡️ '}
                    {type === 'teacher' && '👨‍🏫 '}
                    {type === 'admin' && '👔 '}
                    {type === 'parent' && '👪 '}
                    {type === 'student' && '🎒 '}
                    {type === 'superadmin' ? 'Superadmin' : (type.charAt(0).toUpperCase() + type.slice(1))}
                  </button>
                ))}
              </div>

              {isSuperadminLogin && (
                <select
                  name="schoolCode"
                  value={credentials.schoolCode}
                  onChange={(e) => setcredentials({...credentials, schoolCode: e.target.value})}
                  style={styles.select}
                  required
                >
                  {schools.length > 0 ? (
                    schools.map((school) => (
                      <option key={school.schoolCode} value={school.schoolCode}>
                        {school.schoolCode} - {school.schoolName}
                      </option>
                    ))
                  ) : (
                    <option value="MGPS">MGPS</option>
                  )}
                </select>
              )}

              {/* Username Input */}
              <div style={styles.inputGroup}>
                <input
                  type="text"
                  name="username"
                  placeholder="👤 Username"
                  value={credentials.username}
                  onChange={(e) => setcredentials({...credentials, username: e.target.value})}
                  style={styles.input}
                  required
                />
              </div>

              {/* Password Input */}
              <div style={styles.inputGroup}>
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="🔒 Password"
                  value={credentials.password}
                  onChange={(e) => setcredentials({...credentials, password: e.target.value})}
                  style={styles.input}
                  required
                />
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  style={{
                    position: 'absolute',
                    right: '15px',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '1.2rem',
                  }}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                style={styles.submitButton}
              >
                Let's Go! 🎯
              </button>

              {/* Signup Link */}
              <div style={{ textAlign: 'center', marginTop: '20px', color: '#666' }}>
                Don't have an account?{' '}
                <a
                  href="/signup"
                  style={{
                    color: '#667eea',
                    textDecoration: 'none',
                    fontWeight: '600',
                    cursor: 'pointer'
                  }}
                  onMouseEnter={(e) => e.target.style.textDecoration = 'underline'}
                  onMouseLeave={(e) => e.target.style.textDecoration = 'none'}
                >
                  Create Account
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>

      <MyFooter />
      <ToastContainer />
    </>
  );
};

export default Login;
