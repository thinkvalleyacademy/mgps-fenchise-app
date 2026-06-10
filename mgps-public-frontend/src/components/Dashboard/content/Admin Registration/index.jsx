import React, { useCallback, useContext, useEffect, useState } from 'react'
import InputComponent from '../admission/inputComponent'
import { RegisterUser } from '../../../../apis/CRUD_operation/operation'
import { useNavigate } from 'react-router-dom'
import AuthContext from '../../../../context/student/AuthContext'
import { GetAll } from '../../../../apis/Academic CRUD/AC_CRUD'
import DashBoardCard from '../../DashBoardCard/DashBoardCard'
import NotifyMsg from '../../../Academics/NotifyMsg'
import { ToastContainer } from 'react-toastify'

const AdminRegisteration = () => {

    const { token } = useContext(AuthContext);
    const navigate = useNavigate();
    const [deptList, setDeptList] = useState([]);
    const [notificationMsg, setMsg] = useState(null); //Msg content
    const [typeOfMsg, setMsgType] = useState(); //Type of msg
    const [isRegistering, setIsRegistering] = useState(false);
    const [registrationProgress, setRegistrationProgress] = useState(0);

    const [stringData, setStringData] = useState({
        userType: { value: "admin" },
        firstName: { value: "", type: "text", size: 6 },
        lastName: { value: "", type: "text", size: 6 },
        dateOfBirth: { value: "", type: "date", size: 6 },
        gender: { value: "", type: "select", size: 6 },
        email: { value: "", type: "email", size: 6 },
        phone_num: { value: "", type: "number", size: 6 },
        department: { value: "", type: "select", size: 6 },
        position: { value: "", type: "text", size: 6 },
        address: { value: "", type: "textarea", size: 12 },
      });


      const fetchData = useCallback(async () => {
        try {
          const responseData = await GetAll("department", token);
          setDeptList(responseData.data);
        } catch (error) {
          console.error("Error fetching data:", error);
        }
      }, [token]);

    useEffect(() => {
        fetchData();
      }, [fetchData]); // Make sure to include dependencies array to avoid infinite loop

      const handleInputChange = (e) => {
        const { name, value } = e.target;
        setStringData((prevStringData) => ({
          ...prevStringData,
          [name]: { ...prevStringData[name], value }, // Update the 'value' property within the corresponding object
        }));
      };

      const registerUserWithProgress = async (
        userType,
        postData,
        progressCallback
      ) => {
        return new Promise(async (resolve, reject) => {
          // Simulate slow API call with progress updates
          const totalTime = 3000; // Simulate slow API call for 3 seconds
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
    
          // Simulate registration API call
          try {
            const data = await RegisterUser(userType, postData);
            resolve(data);
          } catch (error) {
            clearInterval(progressInterval);
            reject(error);
          }
        });
      };

      const formatLabel = (key) => {
        return key
          .replace(/_/g, ' ')
          .replace(/([A-Z])/g, " $1")
          .replace(/^./, (str) => str.toUpperCase());
      };
    
    
      const handleSubmit = async (e) => {
        e.preventDefault();
        const values = Object.values(stringData);
    
        const postData = values.reduce((acc, { value }, index) => {
          const key = Object.keys(stringData)[index];
          acc[key] = value.trim();
          return acc;
        }, {});
        e.preventDefault();
        const isEmpty = checkEmptyValues(postData);
        console.log(postData);
        if (isEmpty) {
          setIsRegistering(true);
          const data = await registerUserWithProgress(
            "admin",
            postData,
            setRegistrationProgress
          );
          if (data === "User already registered.") {
            setMsg("Admin already registerd");
            setMsgType("error");
            setIsRegistering(false);
          } else {
            setMsg("Admin register successfully");
            setMsgType("success");
            setIsRegistering(false);
            setTimeout(() => {
              navigate("/users/admin");
            }, 3000);
          }
        } else {
          console.log("Fileds are empty  ", isEmpty);
          setMsg("Fill the fields");
          setMsgType("warning");
        }
      };
      function checkEmptyValues(adminInfo) {
        for (const key in adminInfo) {
          if (!adminInfo[key]) {
            return false;
          }
        }
        return true;
      }

  return (
    <>
     <div
        className="container dashboard-form-page"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "1424px" }}
      >
        <DashBoardCard title="Add a admin" visibility="hidden" />

        {isRegistering && (
          <div className="dashboard-form-progress">
            <div className="dashboard-form-progress-card">
              <progress value={registrationProgress} max="100" />
              <p>Registering admin. Please wait...</p>
            </div>
          </div>
        )}

        <section id="form" className="dashboard-form-shell">
          <div className="dashboard-form-header">
            <span className="dashboard-form-eyebrow">Admin Setup</span>
            <h3 className="dashboard-form-title">Admin Registration</h3>
            <p className="dashboard-form-subtitle">
              Register admin users with aligned profile and department details.
            </p>
          </div>
          <div className="row">
            <div className="col-md-12">
              <form onSubmit={handleSubmit}>
                <div className="form-row">
                  {Object.entries(stringData)
                    .slice(1, 7)
                    .map(([menuItem, component]) => (
                      <InputComponent
                        key={menuItem}
                        label={formatLabel(menuItem)}
                        lable={formatLabel(menuItem)}
                        size={component.size}
                        type={component.type}
                        name={menuItem}
                        value={component.value}
                        onChange={(e) => handleInputChange(e)}
                        placeholder={`Enter your ${formatLabel(menuItem)}`}
                      />
                    ))}

                  <div className="form-group col-md-6">
                    <h5 className="text-left">Department</h5>
                    <select
                      className="form-control"
                      id="inputDado"
                      name="department"
                      value={stringData.department.value}
                      onChange={(e) => handleInputChange(e)}
                      required
                    >
                      <option value="">Select Department...</option>
                      {deptList.map((result) => (
                        <option key={result.depName} value={result.depName}>
                          {result.depName}
                        </option>
                      ))}
                    </select>
                  </div>

                  {Object.entries(stringData)
                    .slice(8)
                    .map(([menuItem, component]) => (
                      <InputComponent
                        key={menuItem}
                        label={formatLabel(menuItem)}
                        lable={formatLabel(menuItem)}
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
  )
}

export default AdminRegisteration
