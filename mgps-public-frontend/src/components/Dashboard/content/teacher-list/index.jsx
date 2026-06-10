import React, { useState, useEffect, useContext, useCallback } from 'react'
import EmptyImage from '../../../../Images/Empty.png';
import './style.css'
import userImage from './user.png';
import DeleteConfirmation from '../deleteModal';
import { FaUserCircle, FaPen } from 'react-icons/fa';
import { MdDelete } from "react-icons/md";
import AuthContext from '../../../../context/student/AuthContext';
import TeacherProfile from '../teacherProfile';
import TeacherUpdate from '../update_Modal';
import { fetchListData } from '../../../../apis/CRUD_operation/operation';
import DashBoardCard from '../../DashBoardCard/DashBoardCard';
import SearchBox from '../../SearchBox';
import { GetAll } from '../../../../apis/Academic CRUD/AC_CRUD';
import Pagination from '../Pagination';

const TeacherList = () => {
    const [subjectList, setSubjectList] = useState([]);
    const [selectedItem, setSelectedItem] = useState(null);
    const [selectName, setSelectName] = useState(null);
    const [modalShow, setModalShow] = useState(false);
    const [teachers, setTeachers] = useState([]);
    const { user, token } = useContext(AuthContext);
    const [dataList, setDataList] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [teacherPerPage] = useState(5);


    const fetchData = useCallback(async () => {
        try {
            setDataList([]);
            const teacherData = await fetchListData(token, "teacher");
            setTeachers(teacherData);
            setDataList(teacherData);
        
            const subjectData = await GetAll("subject", token);
            setSubjectList(subjectData.data);
          } catch (error) {
            console.error('Error fetching data:', error);
          }
    }, [token]);



    useEffect(() => {
        fetchData();
    }, [fetchData]);


    const [show, setShow] = useState(false);

    const handleCloseWithoutCall = () =>{
        setShow(false);
    }

    const handleClose = () => {
        fetchData();
        setShow(false);
    }


    const handleItemClick = async (action, index) => {
        if (action === "Profile") {
            setShow(true);
            setSelectedItem(currentTeachers[index]);
            setSelectName("Profile");
        } else if (action === "Delete") {
            setModalShow(true);
            setSelectedItem(currentTeachers[index]);
            setSelectName("Delete");
        } else if (action === "Update") {
            setShow(true);
            setSelectedItem(currentTeachers[index]);
            setSelectName("Update");
        } else {
            setSelectedItem(null);
        }
    };

    const capitalizeFirstLetter = (string) => {
        if (!string) {
            return '';
        }
        return string.charAt(0).toUpperCase() + string.slice(1);
    };


    const indexOfLastTeacher = currentPage * teacherPerPage;
    const indexOfFirstTeacher = indexOfLastTeacher - teacherPerPage;
    const currentTeachers = dataList.slice(indexOfFirstTeacher, indexOfLastTeacher);

    // Change page
    const paginate = pageNumber => setCurrentPage(pageNumber);

    const setResults = (searchTeacher) => {

        if (searchTeacher === false)
            setDataList(teachers)
        else
            setDataList(searchTeacher)
    }

    const handleInputChange = (e) => {
        const name = e.target.value;
        if (name === "All") {
            setDataList(teachers)

        } else {
            const filtered = dataList.filter(teacher => teacher.subjects === name);
            setDataList(filtered)
        }
    }

    return (
        <>

            <div className="container" style={{ backgroundColor: "#F4F5F9", maxWidth: "1424px" }}>
                <DashBoardCard
                    title="Teacher"
                    buttonValue="Add a teacher"
                    visibility="visible"
                />
                <div className="card bg-white shadow rounded-lg overflow-hidden listContainer" >

                    
                            <div style={{ marginTop: "30px" }}>
                                <div style={{ display: "flex", alignItems: "center", marginBottom: "10px" }}>
                                    <div style={{ flex: "0 0 80%" }}>
                                        <SearchBox listUser={teachers} setResults={setResults} />
                                    </div>
                                    <div style={{ flex: "0 0 10%" }}>
                                        <select
                                            className="form-control"
                                            id="subject-select"

                                            name="subjectName"
                                            onChange={(e) => handleInputChange(e)}

                                        >
                                            <option selected>All</option>
                                            {subjectList.map((result) => (
                                                <option value={result.subjectName}>{result.subjectName}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                {
                        currentTeachers.length !== 0 ? (
                                <table
                                    id="basic-datatable"
                                    className="table table-striped dt-responsive nowrap dataTable no-footer dtr-inline"
                                    width="90%"
                                    role="grid"
                                    aria-describedby="basic-datatable_info"
                                    style={{ position: "relative", marginTop: "30px" }}
                                >

                                    <thead>
                                        <tr role="row">
                                            <th className="sorting sorting_asc" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1"
                                                style={{ width: "120.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-sort="ascending" aria-label="Code: activate to sort column descending">Image</th>

                                            <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "570px", backgroundColor: "#313a46", color: "#ababab" }}
                                                aria-label="Photo: activate to sort column ascending">Name</th>


                                            <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1"
                                                style={{ width: "990.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-label="Name: activate to sort column ascending">Email</th>


                                            <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1"
                                                style={{ width: "790.8px", backgroundColor: "#313a46", color: "#ababab" }} aria-label="Name: activate to sort column ascending">Date of Birth</th>


                                            <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1"
                                                style={{ width: "191.8px", backgroundColor: "#313a46", color: "#ababab" }}
                                                aria-label="Options: activate to sort column ascending">Options</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {dataList.map((result, index) => (

                                            <tr className="odd" key={index} >
                                                <td>
                                                    <img className="rounded-circle" width="50" height="50" src={userImage} alt="User" />

                                                </td>

                                                <td className="dtr-control sorting_1" tabIndex="0">
                                                    {capitalizeFirstLetter(result.firstName)} {" "} {result.lastName}
                                                </td>
                                                <td className="dtr-control sorting_1" tabIndex="0">
                                                    {capitalizeFirstLetter(result.email)}
                                                </td>

                                                <td>{capitalizeFirstLetter(result.dateOfBirth)}</td>
                                                <td>
                                                    <FaUserCircle
                                                        size={28}
                                                        style={{ marginRight: '5px', cursor: 'pointer' }}
                                                        onClick={() => handleItemClick("Profile", index)}
                                                    />
                                                    {user !== "Student" && <>

                                                        <MdDelete
                                                            size={30}
                                                            style={{ marginRight: '5px', cursor: 'pointer', }}
                                                            onClick={() => handleItemClick("Delete", index)}
                                                        />
                                                        <FaPen
                                                            size={24}
                                                            style={{ cursor: 'pointer' }}
                                                            onClick={() => handleItemClick("Update", index)}
                                                        /> </>
                                                    }
                                                </td>
                                            </tr>

                                        ))}
                                    </tbody>
                                    <Pagination
                                        postsPerPage={teacherPerPage}
                                        totalPosts={teachers.length}
                                        paginate={paginate}
                                    />
                                </table>
                           

                        ) : (
                            <div className="text-center">
                                <img src={EmptyImage} height={"20px"} className="img-fluid" alt="..." />
                            </div>)
                    }
 </div>

                    {
                        selectName === "Profile" && (

                            <TeacherProfile

                                teacher={selectedItem.username}
                                show={show}
                                handleClose={handleCloseWithoutCall} />
                        )
                    }

                    {
                        selectName === "Delete" && (
                            <DeleteConfirmation 
                            userName={selectedItem.username}
                             accountType={"teacher"} 
                             show={modalShow} onHide={() => {
                                setModalShow(false);
                                fetchData();
                            }} />
                        )
                    }
                    {
                        selectName === "Update" && (

                            <TeacherUpdate
                                teacher={selectedItem.username}
                                show={show}
                                handleClose={handleClose}

                            />
                        )
                    }

                </div>
            </div>
        </>
    )
}

export default TeacherList
