import React, { useCallback, useEffect, useState, useContext } from 'react'
import AuthContext from '../../../context/student/AuthContext';
import demoUser from './user.png'
import DeleteConfirmation from '../content/deleteModal';
import StudentProfile from '../content/studentProfile';
import StudentUpdate from '../content/Student_update';
import { fetchListData } from '../../../apis/CRUD_operation/operation';
import EmptyImage from '../../../Images/Empty.png';
import SearchBox from '../SearchBox';
import CustomLoadingBar from '../../Academics/LoadingBar/Index';
import Pagination from '../content/Pagination';
import { FaUserCircle,FaEdit, FaTrashAlt } from 'react-icons/fa';
import { useNavigate } from "react-router-dom";

const UserList = ({ filter, grade, section }) => { // Accept grade and section as props
  const [isLoading, setIsLoading] = useState(true);
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectName, setSelectName] = useState(null);
  const [modalShow, setModalShow] = useState(false);
  const [studentList, SetStudentList] = useState(null);
  const [dataList, setDataList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [studentPerPage] = useState(5);

  const { user, token } = useContext(AuthContext);
  const navigate = useNavigate(); // Get navigate function

  const fetchData = useCallback(async () => {
    try {
      console.log("Grade passed to API:", grade); // Log the grade value
      console.log("Section passed to API:", section); // Log the section value

      const newData = await fetchListData(token, "student", grade, section, navigate); // Pass navigate
      SetStudentList(newData);
      setDataList(newData);
    } catch (error) {
      if (error.message.includes("401")) {
        alert("Token expired. Please log in again.");
        navigate("/login");
      } else {
        console.error("Error fetching student data:", error);
      }
    } finally {
      setIsLoading(false);
    }
  }, [grade, navigate, section, token]);

  useEffect(() => {
    fetchData();
  }, [fetchData, filter]);

  const [show, setShow] = useState(false);
  const handleClose = () => {
    fetchData();
    setShow(false);
  }

  const capitalizeFirstLetter = (string) => {
    return string.charAt(0).toUpperCase() + string.slice(1);
  };

  const handleItemClick = async (action, index) => {
    if (action === "Profile") {
      setShow(true);
      setSelectedItem(currentStudents[index]);
      console.log("Profile selected:", currentStudents[index]);
      setSelectName("Profile");
    } else if (action === "Delete") {
      setModalShow(true);
      setSelectedItem(currentStudents[index]);
      console.log("Delete selected:", currentStudents[index]);
      setSelectName("Delete");
    } else if (action === "Update") {
      setShow(true);
      setSelectedItem(currentStudents[index]);
      console.log("Update selected:", currentStudents[index]);
      setSelectName("Update");
    } else {
      setSelectedItem(null);
      console.log("No action selected");
    }
  };

  // For search list 
  const setResults = (searchStudent) => {

    if (searchStudent === false)
      setDataList(studentList)
    else
      setDataList(searchStudent)
  }

  // For Pagination 
  // Get Current students 
  const indexOfLastStudent = currentPage * studentPerPage;
  const indexOfFirstStudent = indexOfLastStudent - studentPerPage;
  const currentStudents = dataList.slice(indexOfFirstStudent, indexOfLastStudent);

  // Change page
  const paginate = pageNumber => setCurrentPage(pageNumber);

  return (
    <>
      <CustomLoadingBar isLoading={isLoading} />

      {!isLoading ? (
        
            <> 
            <SearchBox listUser={studentList} setResults={setResults} />
            {currentStudents.length !== 0 ? (
              <table
                id="basic-datatable"
                className="table table-striped dt-responsive nowrap dataTable no-footer dtr-inline"
                width="90%"
                role="grid"
                aria-describedby="basic-datatable_info"
                style={{ position: "relative", marginTop: "20px" }}
              >
                <thead>
                  <tr role="row">
                    <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "73.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-label="Photo: activate to sort column ascending">Photo</th>
                    <th className="sorting sorting_asc" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "158.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-sort="ascending" aria-label="Code: activate to sort column descending">Email</th>
                    <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "199.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-label="Name: activate to sort column ascending">Name</th>
                    <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "91.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-label="Options: activate to sort column ascending">Options</th>
                  </tr>
                </thead>
                <tbody>
                  {currentStudents.map((student, index) => (
                    <tr key={index} className="odd">
                      <td>
                        <img className="rounded-circle" width="50" height="50" src={demoUser} alt="User" />
                      </td>
                      <td className="dtr-control sorting_1" tabIndex="0">
                        <div style={{ position: 'absolute', height: '1px', width: '0px', overflow: 'hidden' }}>
                          <input type="text" tabIndex="0" />
                        </div>
                        {student.email}
                      </td>
                      <td>{capitalizeFirstLetter(student.firstName)} {" "} {capitalizeFirstLetter(student.lastName)}</td>
                      <td>
                        <FaUserCircle
                        color='#343a40'
                          size={28}
                          style={{ marginRight: '20px', cursor: 'pointer' }}
                          onClick={() => handleItemClick("Profile", index)}
                        />

                        {
                          user !== "Student" && <>

                            <FaTrashAlt
                              size={22}
                              color='red'
                              style={{ cursor: 'pointer', marginRight: '20px' }}
                              onClick={() => handleItemClick("Delete", index)}
                            />
                            <FaEdit
                              size={24}
                              color='blue'
                              style={{ cursor: 'pointer' }}
                              onClick={() => handleItemClick("Update", index)}
                            /> </>

                        }

                      </td>
                    </tr>
                  ))}
                </tbody>
                <Pagination
                  postsPerPage={studentPerPage}
                  totalPosts={studentList.length}
                  paginate={paginate}
                />
              </table>
              ) : (
                <div className="text-center">
                  <img src={EmptyImage} height={"20px"} style={{ marginTop: "10px" }} className="img-fluid" alt="..." />
                </div>
              )}
            </>
          
        
      ) : (
        <div>Loading...</div>
      )}




      {
        selectName === "Profile" && (

          <StudentProfile
            student={selectedItem.registrationNumber}
            show={show}
            handleClose={handleClose} />
        )
      }

      {
        selectName === "Delete" && (
          <DeleteConfirmation
            registrationNumber={selectedItem?.registrationNumber}
            moduleName="student"
            accountType="student"
            show={modalShow}
            onHide={() => {
                console.log("Delete modal closed");
                setModalShow(false);
                fetchData(navigate);
            }}
        />
        )
      }

      {
        selectName === "Update" && (
          <StudentUpdate student={selectedItem.registrationNumber}
          show={show}
          onHide={handleClose} />
        )
      }

    </>
  )
}

export default UserList
