import React, { useCallback, useContext, useEffect, useMemo, useState } from 'react'
import './style.css';
import { IoIosCloseCircleOutline } from "react-icons/io";

import { Modal } from 'react-bootstrap';
import { getInfoByUserName, updateUser } from '../../../../apis/CRUD_operation/operation';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Bounce } from 'react-toastify';
import AuthContext from '../../../../context/student/AuthContext';
import InputComponent from '../admission/inputComponent';
import { GetAll } from '../../../../apis/Academic CRUD/AC_CRUD';
const TeacherUpdate = ({
  teacher,
  show,
  handleClose,
}) => {

  const { token } = useContext(AuthContext);
  const [classList, setClassList] = useState([]);

  const initialDetailsState = useMemo(() => ({
    username: { value: teacher, type: "text" },
    userType: { value: "teacher", type: "text" },
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
  }), [teacher]);



  const [details, setDetails] = useState(initialDetailsState);

  const resetForm = useCallback(() => {
    setDetails(initialDetailsState);
  }, [initialDetailsState]);

  const updateFormData = useCallback((data) => {
    if (!data) return;

    setDetails(prevState => ({
      ...prevState,
      firstName: { ...prevState.firstName, value: data.firstName || "" },
      lastName: { ...prevState.lastName, value: data.lastName || "" },
      email: { ...prevState.email, value: data.email || "" },
      dateOfBirth: { ...prevState.dateOfBirth, value: data.dateOfBirth || "" },
      gender: { ...prevState.gender, value: data.gender || "" },
      phone_num: { ...prevState.phone_num, value: data.phone_num ? data.phone_num.toString() : "" },
      emergency_contact: { ...prevState.emergency_contact, value: data.emergency_contact ? data.emergency_contact.toString() : "" },
      security_number: { ...prevState.security_number, value: data.security_number || "" },
      qualification: { ...prevState.qualification, value: data.qualification || "" },
      specialization: { ...prevState.specialization, value: data.specialization || "" },
      grade_level: { ...prevState.grade_level, value: data.grade_level || "" },
      teaching_experience: { ...prevState.teaching_experience, value: data.teaching_experience || "" },
      joining_date: { ...prevState.joining_date, value: data.joining_date || "" },
      subjects: { ...prevState.subjects, value: data.subjects || "" },
      position: { ...prevState.position, value: data.position || "" },
      address: { ...prevState.address, value: data.address || "" },
      userType: { ...prevState.userType, value: data.userType || "" },
      username: { ...prevState.username, value: data.username || "" },
    }));
  }, []);

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      setClassList(responseData.data);

      const response = await getInfoByUserName("teacher", teacher, token);
      updateFormData(response)
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [teacher, token, updateFormData]);

  useEffect(() => {
    if (show) {
      resetForm();
      fetchData();
    }
  }, [fetchData, resetForm, show]);

  const notify = (msgtype, msg) => {
    toast[msgtype](msg, {
      position: "top-right",
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
      transition: Bounce
    });
  };


  const formatLabel = (key) => {
    return key
      .replace(/([A-Z])/g, " $1")
      .replace(/_/g, " ")
      .replace(/^./, (str) => str.toUpperCase());
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setDetails((prevStringData) => ({
      ...prevStringData,
      [name]: { ...prevStringData[name], value },
    }));


  };

  const changeDateFormat = () => {
    const updatedDetails = {};
    for (const key in details) {
      const field = details[key];
      if (field.type === "date" && field.value) {
        const parts = field.value.split("-");
        const formattedDate = `${parts[0]}-${parts[1]}-${parts[2]}`;
        updatedDetails[key] = { ...field, value: formattedDate };
      } else {
        updatedDetails[key] = field;
      }
    }
    setDetails(updatedDetails);
  };



  const onSubmit = async (e) => {
    e.preventDefault();

    changeDateFormat();
    const values = Object.values(details);

    const postData = values.reduce((acc, { value }, index) => {
      const key = Object.keys(details)[index];
      acc[key] = value;
      return acc;
    }, {});

      const data = await updateUser(postData, token);


      if (data.status === 200) {
        if (data.message === "Teacher successfully updated") {
          handleClose();
          notify("success", "User Updated Successfully");

        }
        else {
          handleClose();
          notify("error", "Something wrong")
        }
      }


  }


  return (
    <>
      <Modal show={show} onHide={handleClose} b animation={true} backdrop="static"
        keyboard={false} centered className="custom-modal-width custom-modal-rounded">
        <Modal.Header className="d-flex justify-content-between align-items-center">
          <Modal.Title>Update Teacher Details</Modal.Title>
          <IoIosCloseCircleOutline color='red' size={40} style={{ cursor: 'pointer' }} onClick={handleClose} />
        </Modal.Header>
        <Modal.Body >
          <div className="container">
            <form
              onSubmit={onSubmit}
            >
              <div className="form-row">
                {Object.entries(details)
                  .slice(2, 12)
                  .map(([menuItem, component]) => (
                    <InputComponent
                      label={formatLabel(menuItem)}
                      type={component.type}
                      size={component.size}
                      name={menuItem}
                      value={component.value}
                      onChange={(e) => handleInputChange(e)}
                      placeholder={`Enter ${formatLabel(
                        menuItem
                      )}`}
                    />
                  ))}


                <div className="form-group col-md-3">
                  <h5 className="text-left">Grade level</h5>
                  <select
                    className="form-control"
                    id="inputDado"
                    name="grade_level"
                    value={details.grade_level.value}
                    onChange={(e) => handleInputChange(e)}
                    required
                  >
                    <option selected>Select class...</option>
                    {classList.map((result) => (
                      <option value={result.className}>{result.className}</option>
                    ))}
                  </select>
                </div>

                {Object.entries(details)
                  .slice(13)
                  .map(([menuItem, component]) => (
                    <InputComponent
                      label={formatLabel(menuItem)}
                      // key={menuItem}
                      lable={formatLabel(menuItem)}
                      size={component.size}
                      type={component.type}
                      name={menuItem}
                      value={component.value}
                      onChange={(e) => handleInputChange(e)}
                      placeholder={`Enter ${formatLabel(menuItem)}`}
                    />
                  ))}


              </div>
              <div className="button-container">
                <button type="button" className="btn btn-lg btn-outline-secondary" onClick={handleClose}>
                  Close
                </button>
                <button type="submit" className="btn btn-lg  btn-secondary">
                  Update
                </button>
              </div>

            </form>
          </div>
        </Modal.Body>
      </Modal>

      <ToastContainer
      />
    </>
  )
}

export default TeacherUpdate
