import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../apis/Login/AuthService";
import AuthContext from "../context/student/AuthContext";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Bounce } from "react-toastify";
import LoginImage from "../Images/login.webp"
import HomeNav from "./public_page/HomePage/homeNav";

const Login = () => {
  const [credentials, setcredentials] = useState({
    username: "",
    password: "",
    userType: "",
  });
  const { setToken, setUserName, setUser, setName } = useContext(AuthContext);

  const [showPassword, setShowPassword] = useState(false);

  const navigate = useNavigate();

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
      alert("Please select the userType");
    } else if (!credentials.username && !credentials.password) {
      alert("Please enter username and password.");
      return;
    } else {
      const { data_set, error } = await loginUser(credentials);
      if (error) {
        console.log("Error is ", error);
        if (error.includes("Invalid Token")) {
          notify("Token expired. Please log in again.");
          navigate("/login");
        } else {
          notify(error);
        }
      } else {
        setToken(data_set.accessToken);
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

  const myStyle = {
    margin: "6px",
  };

  const onChangee = (e) => {
    setcredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const changeUser = (param) => {
    setcredentials((prevState) => ({
      ...prevState,
      userType: param,
    }));
  };

  return (
    <>
     <div className="row">
          <HomeNav />
        </div>
      
      <div className="card text-black" style={{ borderRadius: 100 + "px" }}>
        <div className="card-body p-md-5">
          <div className="row justify-content-center">
            <div className="col-md-10 col-lg-6 col-xl-5 order-2 order-lg-1">
              <p className="text-left h3 mb-5 mx-1 mx-md-4 mt-4">Sign in</p>

              <form className="mx-1 mx-md-4" onSubmit={hadleSubmit}>
                <div className="d-flex flex-row align-items-center mb-4">
                  <i className="fas fa-user fa-lg me-3 fa-fw"></i>
                  <div className="form-outline flex-fill mb-0">
                    <input
                      type="text"
                      id="username"
                      value={credentials.username}
                      onChange={onChangee}
                      name="username"
                      className="form-control"
                    />
                    <label className="form-label" htmlFor="form3Example1c">
                      UserName
                    </label>
                  </div>
                </div>

                <div className="d-flex flex-row align-items-center mb-4">
                  <i className="fas fa-lock fa-lg me-3 fa-fw"></i>
                  <div className="form-outline flex-fill mb-0 position-relative">
                    <input
                      type={showPassword ? "text" : "password"}
                      value={credentials.password}
                      onChange={onChangee}
                      id="password"
                      name="password"
                      className="form-control"
                    />
                    <label className="form-label" htmlFor="password">
                      Password
                    </label>
                    <i
                      className={`fas ${
                        showPassword ? "fa-eye-slash" : "fa-eye"
                      } position-absolute`}
                      style={{
                        right: "10px",
                        top: "30%",
                        transform: "translateY(-50%)",
                        cursor: "pointer",
                      }}
                      onClick={togglePasswordVisibility}
                    ></i>
                  </div>
                </div>

                <div className="d-flex justify-content-center mx-4 mb-3 mb-lg-4">
                  <button
                    type="button"
                    onClick={hadleSubmit}
                    className="btn btn-primary btn-lg"
                  >
                    Login
                  </button>
                </div>
                <div className="col-md-12 text-center">
                  <h4 className="text-muted">Login As </h4>
                </div>
              </form>
              <div className="col-md-12 text-center">
                <button
                  onClick={() => changeUser("superadmin")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Superadmin
                </button>
                <button
                  onClick={() => changeUser("admin")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Admin
                </button>
                <button
                  onClick={() => changeUser("teacher")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Teacher
                </button>
                <button
                  onClick={() => changeUser("parent")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Parent
                </button>
                <button
                  onClick={() => changeUser("student")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Student
                </button>
                <button
                  onClick={() => changeUser("accountant")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Accountant
                </button>
                <button
                  onClick={() => changeUser("librarian")}
                  className="btn btn-outline-success rounded-pill btn-sm mt-1"
                  style={myStyle}
                >
                  Librarian
                </button>
              </div>
            </div>
            <div className="col-md-10 col-lg-6 col-xl-7 d-flex align-items-center order-1 order-lg-2">
              <img src={LoginImage} className="img-fluid" alt="Sample image" />
            </div>
          </div>
        </div>
      </div>
      <ToastContainer />
    </>
  );
};

export default Login;
