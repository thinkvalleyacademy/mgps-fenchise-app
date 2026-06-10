import React, { useCallback, useContext, useEffect, useState } from 'react'
import { GetAll } from '../../../apis/Academic CRUD/AC_CRUD';
import AuthContext from '../../../context/student/AuthContext';
import { FaSearch } from "react-icons/fa";


const DropDownComponent = ({ isSection, handleOnClick }) => {

  const [data, setData] = useState([]);
  const [sectionList, setSectionList] = useState([]);
  const { token } = useContext(AuthContext);
  const [class_name, setClassName] = useState(null);

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      setData(responseData.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }, [token]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const [selectedClass, setSelectedClass] = useState("");
  const [selectedSection, setSelectedSection] = useState("");

  const handleClassChange = (event) => {
    event.preventDefault();
    const selectedClassIndex = event.target.value;
    setSelectedClass(selectedClassIndex);
    if (selectedClassIndex === "") {
      setSelectedSection("");
      setSectionList([]);
      setClassName(null);
      return;
    }

    const sections = data[selectedClassIndex]?.sections?.map(section => section.sectionName) ?? [];
    setClassName(data[selectedClassIndex]?.className ?? null); // Set the class name (grade)

    setSelectedSection("");
    setSectionList(sections);
  };

  const handleSectionChange = (event) => {
    setSelectedSection(event.target.value); // Set the selected section
  };

  return (
    <>
      <div className="card-body p-4 text-center">
        <div className="d-flex flex-column gap-3 align-items-center"> {/* Changed to vertical layout */}
          <div className="w-100">
            <label className="form-label" htmlFor="libraryClassDropdown">
              Class
            </label>
            <select
              className="form-select"
              id="libraryClassDropdown"
              aria-label="Class select"
              onChange={handleClassChange}
              value={selectedClass}
            >
              <option value="">Select student class...</option>
              {
                data.map((result, index) => (
                  <option key={result?.id ?? result?.className ?? index} value={index}>
                    {result.className}
                  </option>
                ))
              }
            </select>
          </div>

          {isSection && (
            <div className="w-100">
              <label className="form-label" htmlFor="librarySectionDropdown">
                Section
              </label>
              <select
                className="form-select"
                id="librarySectionDropdown"
                aria-label="Section select"
                onChange={handleSectionChange}
                disabled={!selectedClass}
                value={selectedSection}
	              >
	                <option value="">Select section...</option>
	                {
	                  sectionList.map((result, index) => (
	                    <option key={result ?? index} value={result}>{result}</option>
	                  ))
	                }
	              </select>
            </div>
          )}

          <div>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => {
                console.log("Grade selected:", class_name);
                console.log("Section selected:", selectedSection);
                handleOnClick(class_name, selectedSection);
              }}
              disabled={isSection ? !selectedSection : !selectedClass}
            >
              <FaSearch />
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

export default DropDownComponent;
