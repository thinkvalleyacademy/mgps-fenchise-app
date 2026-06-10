
import React, { useCallback, useEffect, useMemo, useState, useContext } from 'react'
import './style.css'
import { Modal } from 'react-bootstrap';
import { getInfoByRegistrationNumber, updateUser } from '../../../../apis/CRUD_operation/operation';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Bounce } from 'react-toastify';
import AuthContext from '../../../../context/student/AuthContext';
import { IoIosCloseCircleOutline } from "react-icons/io";
import InputComponent from '../admission/inputComponent';
import { GetAll } from '../../../../apis/Academic CRUD/AC_CRUD';


const Student_update = ({
  student,
  show,
  onHide,
}) => {


  const { token } = useContext(AuthContext);
  const [classList, setClassList] = useState([]);
  const [sectionList, setSectionList] = useState([]);
  const [selectedClass, setSelectedClass] = useState("");

  const initialFormData = useMemo(() => ({
    userType: { value: "student", type: "text" },
    registrationNumber: { value: student, type: "text" },
    username: { value: "", type: "text" },
    firstName: { value: "", type: "text", size: 6 },
    lastName: { value: "", type: "text", size: 6 },
    email: { value: "", type: "email", size: 6 },
    dateOfBirth: { value: "", type: "date", size: 2 },
    bloodGroup: { value: "", type: "text", size: 2 },
    gender: { value: "", type: "select", size: 2 },
    studentMob: { value: "", type: "number", size: 6 },
    fatherName: { value: "", type: "text", size: 6 },
    motherName: { value: "", type: "text", size: 6 },
    otherParents: { value: "", type: "text", size: 6 },
    grade: { value: "", type: "select", size: 4 },
    section: { value: "", type: "select", size: 4 },
    address: { value: "", type: "textarea", size: 12 },
  }), [student]);

  const [formData, setFormData] = useState(initialFormData);

  const resetForm = useCallback(() => {
    setFormData(initialFormData);
    setSelectedClass("");
    setSectionList([]);
  }, [initialFormData]);

  const updateFormData = useCallback((data) => {
    if (!data) return; 

    setFormData(prevState => ({
      ...prevState,
      firstName: { ...prevState.firstName, value: data.firstName || "" },
      lastName: { ...prevState.lastName, value: data.lastName || "" },
      email: { ...prevState.email, value: data.email || "" },
      dateOfBirth: { ...prevState.dateOfBirth, value: data.dateOfBirth || "" },
      bloodGroup: { ...prevState.bloodGroup, value: data.bloodGroup || "" },
      gender: { ...prevState.gender, value: data.gender || "" },
      studentMob: { ...prevState.studentMob, value: data.studentMob ? data.studentMob.toString() : "" },
      fatherName: { ...prevState.fatherName, value: data.fatherName || "" },
      motherName: { ...prevState.motherName, value: data.motherName || "" },
      otherParents: { ...prevState.otherParents, value: data.otherParents || "" },
      grade: { ...prevState.grade, value: data.grade || "" },
      section: { ...prevState.section, value: data.section || "" },
      address: { ...prevState.address, value: data.address || "" },
      userType: { ...prevState.userType, value: data.userType || "" },
      username: { ...prevState.username, value: data.username || "" },
    }));
  }, []);

  const fetchData = useCallback(async () => {
    try {
      const response = await GetAll("academic", token);
      setClassList(response.data);
      const responseData = await getInfoByRegistrationNumber(student, token);
      updateFormData(responseData)

    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [student, token, updateFormData]);

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
    setFormData((prevStringData) => ({
      ...prevStringData,
      [name]: { ...prevStringData[name], value },
    }));


  };

  const changeDateFormat = () => {
    const updatedDetails = {};
    for (const key in formData) {
      const field = formData[key];
      if (field.type === "date" && field.value) {
        const parts = field.value.split("-");
        const formattedDate = `${parts[0]}-${parts[1]}-${parts[2]}`;
        updatedDetails[key] = { ...field, value: formattedDate };
      } else {
        updatedDetails[key] = field;
      }
    }
    setFormData(updatedDetails);
  };


  const handleClassChange = (event) => {
    event.preventDefault();

    const selectedClassIndex = event.target.value;
    setSelectedClass(selectedClassIndex);
    if (selectedClassIndex !== "Select class...") {
      const sections = classList[selectedClassIndex].sections.map(
        (section) => section.sectionName
      );
      setFormData((prevStringData) => ({
        ...prevStringData,
        grade: {
          value: classList[selectedClassIndex].className,
          type: prevStringData.grade.type,
        },
      }));
      setSectionList(sections);
    } else {
      setSectionList([]);
    }
  };


  const onSubmit = async (e) => {
    e.preventDefault();

    changeDateFormat();
    const values = Object.values(formData);

    const postData = values.reduce((acc, { value }, index) => {
      const key = Object.keys(formData)[index];
      acc[key] = value;
      return acc;
    }, {});

    const data = await updateUser(postData, token);


    if (data.status === 200) {
      if (data.message === "Student successfully updated") {
        onHide();
        notify("success", "User Updated Successfully");

      }
      else {
        onHide();
        notify("error", "Something wrong")
      }
    }


  }




  return (
    <>
      <Modal show={show} onHide={onHide} animation={true} backdrop="static"
        keyboard={false} centered className="custom-modal-width custom-modal-rounded">
        <Modal.Header className="d-flex justify-content-between align-items-center">
          <Modal.Title>Update Student Details</Modal.Title>
          <IoIosCloseCircleOutline color='red' size={40} style={{ cursor: 'pointer' }} onClick={onHide} />
        </Modal.Header>
        <Modal.Body>
          <form onSubmit={onSubmit}>
            <div className="form-row">
              {Object.entries(formData)
                .slice(2, 12)
                .map(([menuItem, component]) => (
                  <InputComponent
                    label={formatLabel(menuItem)}
                    type={component.type}
                    size={component.size}
                    name={menuItem}
                    value={component.value}
                    onChange={(e) => handleInputChange(e)}
                    placeholder={`Enter your ${formatLabel(
                      menuItem
                    )}`}
                  />
                ))}


              <div className="form-group col-md-4">
                <h5 className="text-left">Grade</h5>
                <select
                  className="form-control"
                  id="inputDado"
                  name="grade"
                  value={selectedClass}
                  onChange={handleClassChange}
                  required
                >
                  <option selected>Select class...</option>
                  {classList.map((result, index) => (
                    <option value={index}>{result.className}</option>
                  ))}
                </select>
              </div>
              <div className="form-group col-md-4">
                <h5 className="text-left">Section</h5>
                <select
                  className="form-control"
                  id="inputDado"
                  name="section"
                  value={formData.section.value}
                  disabled={!selectedClass}
                  onChange={(e) => handleInputChange(e)}
                  required
                >
                  <option selected>Select section...</option>
                  {sectionList.map((result, index) => (
                    <option value={result}>{result}</option>
                  ))}
                </select>
              </div>
              <div className="form-group col-md-12">
                <label for="inputAddress">Address</label>
                <input
                  required
                  type="textarea"
                  className="form-control"
                  id="inputDado"
                  name="address"
                  placeholder="Enter your address"
                  value={formData.address.value}
                  onChange={(e) => handleInputChange(e)}
                />
              </div>


            </div>
            <div className="button-container">
              <button type="button" className="btn btn-lg btn-outline-secondary" onClick={onHide}>
                Close
              </button>
              <button type="submit" className="btn btn-lg  btn-secondary">
                Update
              </button>
            </div>

          </form>
        </Modal.Body>
        <Modal.Footer>


        </Modal.Footer>
      </Modal>

      <ToastContainer
      />
    </>
  )
}

export default Student_update
