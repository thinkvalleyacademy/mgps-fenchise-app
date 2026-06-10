import React, { useCallback, useContext, useEffect, useState } from "react";
import InputComponent from "./inputComponent";
import { BulkImportStudents, RegisterUser } from "../../../../apis/CRUD_operation/operation";
import DashBoardCard from "../../DashBoardCard/DashBoardCard";
import { useNavigate } from "react-router-dom";
import AuthContext from "../../../../context/student/AuthContext";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD";
import { ToastContainer } from "react-toastify";
import NotifyMsg from "../../../Academics/NotifyMsg";
import "./style.css";
import { FaUser, FaEnvelope, FaCalendarAlt, FaVenusMars, FaTint, FaPhone, FaUserGraduate, FaMapMarkerAlt, FaCamera, FaDownload, FaFileUpload, FaFileCsv } from "react-icons/fa";

const Admission = () => {
  const { token } = useContext(AuthContext);
  const navigate = useNavigate();
  const [classList, setClassList] = useState([]);
  const [sectionList, setSectionList] = useState([]);
  const [selectedClassIndex, setSelectedClassIndex] = useState("");
  const [notificationMsg, setMsg] = useState(null);
  const [typeOfMsg, setMsgType] = useState();
  const [isRegistering, setIsRegistering] = useState(false);
  const [imageFile, setImageFile] = useState(null);
  const [imageFileName, setImageFileName] = useState("");
  const [bulkFile, setBulkFile] = useState(null);
  const [bulkFileName, setBulkFileName] = useState("");
  const [isBulkUploading, setIsBulkUploading] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      if (responseData && responseData.data) {
        setClassList(responseData.data);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [token]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const [stringData, setStringData] = useState({
    userType: { value: "student" },
    firstName: { value: "", type: "text" },
    lastName: { value: "", type: "text" },
    email: { value: "", type: "email" },
    dateOfBirth: { value: "", type: "date" },
    studentMob: { value: "", type: "number" },
    fatherName: { value: "", type: "text" },
    motherName: { value: "", type: "text" },
    otherParents: { value: "", type: "text" },
    gender: { value: "", type: "select" },
    grade: { value: "", type: "select" },
    section: { value: "", type: "select" },
    bloodGroup: { value: "", type: "select" },
    address: { value: "", type: "textarea" },
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setStringData((prevStringData) => ({
      ...prevStringData,
      [name]: { ...prevStringData[name], value },
    }));
  };

  const handleClassChange = (event) => {
    const index = event.target.value;
    setSelectedClassIndex(index);
    
    if (index !== "") {
      const sections = classList[index].sections.map(
        (section) => section.sectionName
      );
      setStringData((prev) => ({
        ...prev,
        grade: { ...prev.grade, value: classList[index].className },
      }));
      setSectionList(sections);
    } else {
      setSectionList([]);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setImageFile(file);
    if (file) {
      setImageFileName(file.name);
    }
  };

  const handleBulkFileChange = (e) => {
    const file = e.target.files?.[0] || null;
    setBulkFile(file);
    setBulkFileName(file ? file.name : "");
  };

  const downloadSampleCsv = () => {
    const sampleHeaders = [
      "firstName",
      "lastName",
      "email",
      "dateOfBirth",
      "studentMob",
      "fatherName",
      "motherName",
      "otherParents",
      "gender",
      "grade",
      "section",
      "bloodGroup",
      "address",
    ];
    const sampleRows = [
      [
        "Aarav",
        "Sharma",
        "aarav.sharma@example.com",
        "2012-05-14",
        "9876543210",
        "Rajesh Sharma",
        "Pooja Sharma",
        "",
        "Male",
        "Class 5",
        "A",
        "B+",
        "12 Lake View Road",
      ],
      [
        "Anaya",
        "Reddy",
        "anaya.reddy@example.com",
        "2011-11-02",
        "9123456780",
        "Suresh Reddy",
        "Lakshmi Reddy",
        "Grandmother",
        "Female",
        "Class 6",
        "B",
        "O+",
        "\"44 Park Street, Block 2\"",
      ],
    ];
    const csvContent = [sampleHeaders.join(","), ...sampleRows.map((row) => row.join(","))].join("\n");
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = downloadUrl;
    link.setAttribute("download", "student-bulk-import-sample.csv");
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);
  };

  const handleBulkUpload = async () => {
    if (!bulkFile) {
      setMsg("Please choose a CSV file for bulk upload");
      setMsgType("warning");
      return;
    }

    setIsBulkUploading(true);
    try {
      const result = await BulkImportStudents(bulkFile);
      const importedCount = result?.importedCount ?? 0;
      const failedCount = result?.failedCount ?? 0;
      setMsg(`Bulk import completed. Imported ${importedCount} students${failedCount ? `, failed ${failedCount} rows` : ""}.`);
      setMsgType(failedCount ? "warning" : "success");
      setBulkFile(null);
      setBulkFileName("");
    } catch (error) {
      setMsg(error.message || "Bulk student import failed");
      setMsgType("error");
    } finally {
      setIsBulkUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const values = Object.values(stringData);

    const postData = values.reduce((acc, { value }, index) => {
      const key = Object.keys(stringData)[index];
      acc[key] = value?.trim();
      return acc;
    }, {});

    // Image upload is optional for JSON-based registration; remove mandatory check

    const isEmpty = checkEmptyValues(postData);
    if (isEmpty) {
      setIsRegistering(true);
        try {
        const data = await RegisterUser(postData.userType || "student", postData, navigate);
        if (data === "User already registered.") {
          setMsg("Student already registered", "error");
          setMsgType("error");
        } else {
          setMsg("Student registered successfully", "success");
          setMsgType("success");
          setTimeout(() => {
            navigate("/users/student");
          }, 2000);
        }
      } catch (error) {
        console.error("Registration failed:", error);
        setMsg("Registration failed. Please try again.", "error");
        setMsgType("error");
      } finally {
        setIsRegistering(false);
      }
    } else {
      setMsg("Please fill all required fields", "warning");
      setMsgType("warning");
    }
  };

  function checkEmptyValues(studentInfo) {
    for (const key in studentInfo) {
      if (!studentInfo[key]) {
        return false;
      }
    }
    return true;
  }

  return (
    <div className="container dashboard-form-page" style={{ backgroundColor: "#f8f9fa", maxWidth: "1400px", paddingBottom: "30px" }}>
      <DashBoardCard title="Add a student" visibility="hidden" />

      {isRegistering && (
        <div className="loading-overlay">
          <div className="loading-content">
            <div className="loading-spinner"></div>
            <p className="loading-text">Registering student...</p>
          </div>
        </div>
      )}

      <div className="form-container">
        <div className="form-header">
          <span className="dashboard-form-eyebrow">Student Setup</span>
          <h3 className="form-title">New Student Admission</h3>
          <p className="form-subtitle">Fill in the details below to register a new student</p>
        </div>

        <div className="bulk-import-panel">
          <div className="bulk-import-copy">
            <span className="bulk-import-badge"><FaFileCsv /> Bulk Import</span>
            <h4>Upload students from an Excel-friendly CSV file</h4>
            <p>Download the sample file, fill it in with Excel, then upload the CSV to create multiple students at once.</p>
          </div>
          <div className="bulk-import-actions">
            <button type="button" className="btn btn-secondary" onClick={downloadSampleCsv}>
              <FaDownload /> Download Sample
            </button>
            <label className="bulk-file-picker" htmlFor="bulk-student-file">
              <FaFileUpload /> {bulkFileName || "Choose CSV File"}
            </label>
            <input
              id="bulk-student-file"
              type="file"
              accept=".csv"
              onChange={handleBulkFileChange}
              className="bulk-file-input"
            />
            <button type="button" className="btn btn-primary" onClick={handleBulkUpload} disabled={isBulkUploading}>
              <FaFileUpload /> {isBulkUploading ? "Uploading..." : "Upload Bulk Students"}
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Personal Information Section */}
          <div className="form-section">
            <h4 className="form-section-title">
              <FaUser /> Personal Information
            </h4>
            <div className="form-row">
              <InputComponent
                label="FirstName"
                type="text"
                name="firstName"
                value={stringData.firstName.value}
                onChange={handleInputChange}
                placeholder="First name"
                size={4}
                icon={<FaUser />}
              />

              <InputComponent
                label="LastName"
                type="text"
                name="lastName"
                value={stringData.lastName.value}
                onChange={handleInputChange}
                placeholder="Last name"
                size={4}
                icon={<FaUser />}
              />

              <InputComponent
                label="Email"
                type="email"
                name="email"
                value={stringData.email.value}
                onChange={handleInputChange}
                placeholder="Email address"
                size={4}
                icon={<FaEnvelope />}
              />

              <InputComponent
                label="DateOfBirth"
                type="date"
                name="dateOfBirth"
                value={stringData.dateOfBirth.value}
                onChange={handleInputChange}
                size={4}
                icon={<FaCalendarAlt />}
              />

              <InputComponent
                label="Gender"
                type="select"
                name="gender"
                value={stringData.gender.value}
                onChange={handleInputChange}
                size={4}
                icon={<FaVenusMars />}
              />

              <InputComponent
                label="BloodGroup"
                type="select"
                name="bloodGroup"
                value={stringData.bloodGroup.value}
                onChange={handleInputChange}
                size={4}
                icon={<FaTint />}
              />
            </div>
          </div>

          {/* Contact Information Section */}
          <div className="form-section">
            <h4 className="form-section-title">
              <FaPhone /> Contact & Family Information
            </h4>
            <div className="form-row">
              <InputComponent
                label="StudentMobile"
                type="number"
                name="studentMob"
                value={stringData.studentMob.value}
                onChange={handleInputChange}
                placeholder="Mobile number"
                size={4}
                icon={<FaPhone />}
                helperText="10-digit mobile number"
              />

              <InputComponent
                label="FatherName"
                type="text"
                name="fatherName"
                value={stringData.fatherName.value}
                onChange={handleInputChange}
                placeholder="Father's name"
                size={4}
                icon={<FaUserGraduate />}
              />

              <InputComponent
                label="MotherName"
                type="text"
                name="motherName"
                value={stringData.motherName.value}
                onChange={handleInputChange}
                placeholder="Mother's name"
                size={4}
                icon={<FaUserGraduate />}
              />

              <InputComponent
                label="OtherParents"
                type="text"
                name="otherParents"
                value={stringData.otherParents.value}
                onChange={handleInputChange}
                placeholder="Guardian name (if applicable)"
                size={6}
                icon={<FaUserGraduate />}
                required={false}
              />

              <div className="form-group col-6">
                <div className="form-label-wrapper">
                  <label className="form-label" htmlFor="address">
                    <FaMapMarkerAlt /> Address <span className="required-mark">*</span>
                  </label>
                </div>
                <textarea
                  className="form-control"
                  id="address"
                  name="address"
                  placeholder="Enter complete address"
                  value={stringData.address.value}
                  onChange={handleInputChange}
                  required
                  rows={3}
                />
              </div>
            </div>
          </div>

          {/* Academic Information Section */}
          <div className="form-section">
            <h4 className="form-section-title">
              <FaUserGraduate /> Academic Information
            </h4>
            <div className="form-row">
              <div className="form-group col-6">
                <div className="form-label-wrapper">
                  <label className="form-label" htmlFor="grade">
                    <FaUserGraduate /> Grade <span className="required-mark">*</span>
                  </label>
                </div>
                <select
                  className="form-control"
                  id="grade"
                  name="grade"
                  value={selectedClassIndex}
                  onChange={handleClassChange}
                  required
                >
                  <option value="">Select class</option>
                  {classList.map((result, index) => (
                    <option key={index} value={index}>{result.className}</option>
                  ))}
                </select>
              </div>

              <div className="form-group col-6">
                <div className="form-label-wrapper">
                  <label className="form-label" htmlFor="section">
                    <FaUserGraduate /> Section <span className="required-mark">*</span>
                  </label>
                </div>
                <select
                  className="form-control"
                  id="section"
                  name="section"
                  value={stringData.section.value}
                  onChange={handleInputChange}
                  disabled={!selectedClassIndex}
                  required
                >
                  <option value="">Select section</option>
                  {sectionList.map((result, index) => (
                    <option key={index} value={result}>{result}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Photo Upload Section */}
          <div className="form-section" style={{ borderBottom: 'none' }}>
            <h4 className="form-section-title">
              <FaCamera /> Student Photograph
            </h4>
            <div className="form-row">
              <div className="form-group col-12">
                <InputComponent
                  label="StudentPhoto"
                  type="file"
                  name="studentImage"
                  value={imageFileName}
                  onChange={handleFileChange}
                  size={12}
                  icon={<FaCamera />}
                  helperText="Upload recent passport-size photo (JPG/PNG)"
                />
              </div>
            </div>
          </div>

          {/* Form Actions */}
          <div className="form-actions">
            <button type="button" className="btn btn-secondary" onClick={() => navigate("/users/student")}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              <FaUserGraduate /> Register Student
            </button>
          </div>
        </form>
      </div>

      <NotifyMsg msg={notificationMsg} type={typeOfMsg} />
      <ToastContainer autoClose={2000} position="top-right" />
    </div>
  );
};

export default Admission;
