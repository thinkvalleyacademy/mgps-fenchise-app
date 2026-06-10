import React, { useCallback, useContext, useEffect, useState } from "react";
import "./style.css";
import DashBoardCard from "../../DashBoardCard/DashBoardCard";
import AuthContext from "../../../../context/student/AuthContext";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD";
import { fetchListData } from "../../../../apis/CRUD_operation/operation";
import { RegisterCollection } from "../../../../apis/Fee_operation/fee_operation";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Bounce } from "react-toastify";
import { useNavigate } from 'react-router-dom';


const FeeCollection = () => {
  const [formData, setFormData] = useState({
    studentUsername: "",
    feeStructureId: 23456,
    amountPaid: null,
    paymentMethod: "",
    paymentDate: "",
  });
  const navigate = useNavigate();
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: name === "amountPaid" ? parseInt(value, 10) : value,
    }));
  };

  const [data, setData] = useState([]);
  const [sectionList, setSectionList] = useState([]);
  const { token } = useContext(AuthContext);
  const [studentList, SetStudentList] = useState([]);
  const [class_name, setClassName] = useState(null);
  const [loadingStudents, setLoadingStudents] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      setData(responseData.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [token]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

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
      transition: Bounce,
    });
  };

  const [selectedClass, setSelectedClass] = useState("");
  const [selectedSection, setSelectedSection] = useState("");

  const fetchStudentData = useCallback(async () => {
    if (!selectedSection) return;
    setLoadingStudents(true);
    try {
      const newData = await fetchListData(token, "student");
      const filteredData = newData.filter(
        (student) => student.grade === class_name && student.section === selectedSection
      );
      SetStudentList(filteredData);
    } catch (error) {
      console.error("Error fetching student data:", error);
    } finally {
      setLoadingStudents(false);
    }
  }, [class_name, selectedSection, token]);

  useEffect(() => {
    fetchStudentData();
  }, [fetchStudentData]);

  const handleClassChange = (event) => {
    event.preventDefault();
    const selectedClassIndex = event.target.value;
    setSelectedClass(selectedClassIndex);
    if (selectedClassIndex) {
      const sections = data[selectedClassIndex].sections.map(
        (section) => section.sectionName
      );
      setClassName(data[selectedClassIndex].className);

      setSelectedSection("");
      setSectionList(sections);
    } else {
      setClassName(null);
      setSelectedSection("");
      setSectionList([]);
      SetStudentList([]);
    }
  };

  const handleSectionChange = (event) => {
    setSelectedSection(event.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(formData);

    if (
      !selectedClass ||
      !selectedSection ||
      !formData.studentUsername ||
      !formData.amountPaid ||
      !formData.paymentMethod ||
      !formData.paymentDate
    ) {
      alert("Please fill all required fields.");
      return;
    } else {
      const data = await RegisterCollection(token, formData);
      if (data.status === 200) {
        notify("success", "Collection saved Successfully");
        setTimeout(() => {
          navigate('/Accounting/invoice');
        }, 3000);
      } else {
        notify("error", "Something wrong");
      }
    }
  };

  return (
    <>
      <div
        className="container dashboard-form-page"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          entity={"Fee Collection"}
          title={"Fee Collection"}
          visibility="hidden"
        />
        <div className="structForm">
          <div className="dashboard-form-header">
            <span className="dashboard-form-eyebrow">Fee Workflow</span>
            <h3 className="dashboard-form-title">Fee Collection</h3>
            <p className="dashboard-form-subtitle">
              Capture payment details using the same visual system as the other dashboard forms.
            </p>
          </div>
          <form
            className="row g-3 feeForm needs-validation"
            onSubmit={handleSubmit}
          >
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Class</label>
                <select
                  className="form-control"
                  id="dropdown"
                  aria-label="Class select"
	                  onChange={handleClassChange}
	                  value={selectedClass}
	                  required
	                >
	                  <option disabled value="">
	                    Select class...
	                  </option>
	                  {data.map((result, index) => (
	                    <option key={result?.id ?? result?.className ?? index} value={index}>{result.className}</option>
	                  ))}
	                </select>
                <div className="invalid-feedback">
                  Please choose a username.
                </div>
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Section</label>
                <select
                  className="form-control"
                  id="dropdown"
                  aria-label="Section select"
                  onChange={handleSectionChange}
	                  disabled={!selectedClass}
	                  value={selectedSection}
	                  required
	                >
	                  <option value="">
	                    Select section...
	                  </option>
	                  {sectionList.map((result, index) => (
	                    <option key={result ?? index} value={result}>{result}</option>
	                  ))}
	                </select>
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Student</label>
                <select
                  id="dropdown"
                  name="studentUsername"
                  disabled={loadingStudents || !selectedSection}
                  className="form-control"
                  onChange={(e) => handleChange(e)}
	                  value={formData.studentUsername}
	                  required
	                >
	                  <option value="">
	                    Select Student
	                  </option>
	                  {studentList.length !== 0 &&
	                    studentList.map((student, index) => (
	                      <option key={student?.id ?? student?.username ?? index} value={student.username}>
	                        {student.firstName} {student.lastName}
	                      </option>
	                    ))}
	                </select>
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Amount Paid</label>
                <input
	                  type="number"
	                  className="form-control"
	                  name="amountPaid"
	                  value={formData.amountPaid ?? ""}
	                  onChange={(e) => handleChange(e)}
	                />
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Method</label>
                <select
                  id="dropdown"
                  name="paymentMethod"
                  className="form-control"
                  value={formData.paymentMethod}
	                  onChange={(e) => handleChange(e)}
	                  required
	                >
	                  <option value="">Select</option>
	                  <option value="cash">Cash</option>
	                  <option value="check">Check</option>
	                  <option value="online">Online Payment Systems</option>
	                </select>
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Date</label>
                <input
                  type="date"
                  className="form-control"
                  name="paymentDate"
                  value={formData.paymentDate}
                  onChange={(e) => handleChange(e)}
                />
              </div>
            </div>

            <div className="col-12 d-flex justify-content-center">
              <button
                type="submit"
                className="btn btn-outline-primary btn-lg custom-button"
              >
                Submit
              </button>
            </div>
          </form>
        </div>
      </div>
      <ToastContainer />
    </>
  );
};

export default FeeCollection;
