import React, { useState, useContext, useEffect, useCallback } from "react";
import "./style.css";
import UserList from "../../UserList";
import DashBoardCard from "../../DashBoardCard/DashBoardCard";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD"; // Corrected path
import AuthContext from "../../../../context/student/AuthContext"; // Corrected path

const StudentContent = () => {
  const [isVisibility, setVisibility] = useState(false);
  const [filterData, setFilter] = useState(null);
  const [data, setData] = useState([]);
  const [sectionList, setSectionList] = useState([]);
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedSection, setSelectedSection] = useState("");
  const { token } = useContext(AuthContext);

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

  const handleClassChange = (event) => {
    const selectedClassIndex = event.target.value;
    setSelectedClass(selectedClassIndex);
    const sections = data[selectedClassIndex]?.sections.map(
      (section) => section.sectionName
    );
    setSectionList(sections || []);
    setSelectedSection("");
  };

  const handleSectionChange = (event) => {
    setSelectedSection(event.target.value);
  };

  const handleSearchClick = () => {
    if (selectedClass && selectedSection) {
      console.log("Grade selected:", data[selectedClass]?.className); // Log grade
      console.log("Section selected:", selectedSection); // Log section
      setVisibility(true);
      setFilter({
        grade: data[selectedClass]?.className,
        section: selectedSection,
      });
    } else {
      console.error("Grade or Section is not selected."); // Log error if not selected
    }
  };

  return (
    <>
      <div
        className="container"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          title="Student"
          buttonValue="Add a student"
          visibility="visible"
          itemClick=""
        />
        <div className="card bg-white shadow rounded-lg overflow-hidden listContainer">
          <div className="p-4">
            <div className="d-flex flex-row gap-3 align-items-center"> {/* Changed to horizontal layout */}
              <div className="flex-grow-1">
                <label className="form-label" htmlFor="classDropdown">
                  Class
                </label>
                <select
                  className="form-select"
                  id="classDropdown"
                  onChange={handleClassChange}
                  value={selectedClass}
                >
                  <option value="">Select class...</option>
                  {data.map((result, index) => (
                    <option key={index} value={index}>
                      {result.className}
                    </option>
                  ))}
                </select>
              </div>
              <div className="flex-grow-1">
                <label className="form-label" htmlFor="sectionDropdown">
                  Section
                </label>
                <select
                  className="form-select"
                  id="sectionDropdown"
                  onChange={handleSectionChange}
                  value={selectedSection}
                  disabled={!selectedClass}
                >
                  <option value="">Select section...</option>
                  {sectionList.map((section, index) => (
                    <option key={index} value={section}>
                      {section}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <button
                  className="btn btn-secondary"
                  onClick={handleSearchClick}
                  disabled={!selectedClass || !selectedSection}
                >
                  Search
                </button>
              </div>
            </div>
          </div>
          {isVisibility && (
            <UserList
              filter={filterData}
              grade={filterData?.grade} // Pass grade explicitly
              section={filterData?.section} // Pass section explicitly
            />
          )}
        </div>
      </div>
    </>
  );
};

export default StudentContent;
