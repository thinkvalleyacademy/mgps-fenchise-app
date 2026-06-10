import React, { useCallback, useContext, useEffect, useState } from "react";
import AuthContext from "../../../../context/student/AuthContext";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD";
import { fetchListData } from "../../../../apis/CRUD_operation/operation";

const InvoiceFilter = ({fetchInvoice}) => {
    const [formData, setFormData] = useState({
        studentUsername: "",
        quarter:"",
        financialYear:""
        
      });

      const currentYear = new Date().getFullYear();

      const [data, setData] = useState([]);
      const [sectionList, setSectionList] = useState([]);
      const { token } = useContext(AuthContext);
	      const [studentList, SetStudentList] = useState([]);
	      const [class_name, setClassName] = useState(null);

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

	  const [selectedClass, setSelectedClass] = useState("");
	  const [selectedSection, setSelectedSection] = useState("");

	  const fetchStudentData = useCallback(async () => {
	    if (!selectedSection) return;
	    try {
	      const newData = await fetchListData(token, "student");
	      const filteredData = newData.filter(student => 
	        student.grade === class_name && student.section === selectedSection
	      );
	      SetStudentList(filteredData);
	    } catch (error) {
	      console.error("Error fetching student data:", error);
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

	  const handleChange = (e) => {
	    const { name, value } = e.target;
	    setFormData((prevState) => ({
      ...prevState,
      [name]: name === "amountPaid" ? parseInt(value, 10) : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await fetchInvoice(formData.studentUsername, formData.quarter, formData.financialYear);
  };

  return (
    <>
      <div className="structForm"> 
          <div className="dashboard-form-header">
            <span className="dashboard-form-eyebrow">Invoice Setup</span>
            <h3 className="dashboard-form-title">Invoice Filter</h3>
            <p className="dashboard-form-subtitle">
              Filter invoices by class, section, student, quarter, and financial year.
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
                  id="inputDado"
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
                  id="inputDado"
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
                  id="inputDado"
                  name="studentUsername"
                  className="form-control"
                  disabled={!selectedSection}
	                  onChange={(e) => handleChange(e)}
	                  value={formData.studentUsername}
	                  required
	                >
	                  <option value="">
	                    Select 
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
            <div className="col-md-3">
              <div className="form-group">
                <label className="labelStyle">Select Quarter</label>
                <select
                  id="inputDado"
                  name="quarter"
                  className="form-control"
	                  onChange={(e) => handleChange(e)}
	                  value={formData.quarter}
	                  required
	                >
	                  <option value="">
	                    Select 
	                  </option>
	                  <option value="Q1">Q1</option>
	            <option value="Q2">Q2</option>
                </select>
              </div>
            </div>

            <div className="col-md-3">
              <div className="form-group">
                <label className="labelStyle">Financial Year</label>
          <select
            className="form-select"
             id="inputDado"
            name="financialYear"
            value={formData.financialYear}
	            onChange={handleChange}
	            required
	          >
	             <option value="">Select year</option>
	            {Array.from({ length: currentYear - 2023 + 1 }, (_, i) => 2023 + i).map(year => (
	              <option key={year} value={year}>{year}</option>
	            ))}
	          </select>
        </div></div>

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
    </>
  )
}

export default InvoiceFilter
