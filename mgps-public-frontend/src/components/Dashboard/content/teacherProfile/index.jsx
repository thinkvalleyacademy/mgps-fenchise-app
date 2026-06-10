import React, { useCallback, useState, useEffect, useContext } from "react";
import './style.css';
import { Modal } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import { MdOutlineBadge } from "react-icons/md";
import { getInfoByUserName } from "../../../../apis/CRUD_operation/operation";
import AuthContext from "../../../../context/student/AuthContext";

const TeacherProfile = ({
  teacher,
  show,
  handleClose,
}) => {

  const { token } = useContext(AuthContext);

  const [teacherData, setTeacherData] = useState(null);

  const fetchData = useCallback(async () => {
    if (!teacher || !token) return;
    console.log("Teacher is ", teacher)
    try {
      const response = await getInfoByUserName("teacher", teacher, token);
      console.log(typeof response.subjects);
      setTeacherData(response);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [teacher, token]);

  useEffect(() => {
    if (!show) return;
    fetchData();
  }, [fetchData, show]);

  if (!teacherData) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Modal show={show} onHide={handleClose} >
        <Modal.Body >
          <div className="my-container">
            <h4><b><center>Profile</center></b></h4>
            <div className="row-fluid">
              <div className="span10 offset1">

                <div className="teacher_navbar">
                  <div className="profile_div">
                    <center>
	                      <img
	
	                        src="https://i.pinimg.com/736x/08/68/4e/08684ecfc028788c2642fbea98180e45.jpg"
	                        alt="Teacher profile"
	                        width={130}
	                        height={130}
	                        style={{ borderRadius: "50%", marginTop: "20px" }}
	                      />
                      <span>
                        <span className="modal-user-name">
                          {teacherData.firstName && teacherData.firstName.charAt(0).toUpperCase() + teacherData.firstName.slice(1).toLowerCase()} {teacherData.lastName && teacherData.lastName.charAt(0).toUpperCase() + teacherData.lastName.slice(1).toLowerCase()}
                        </span>
                        <span className="modal-position">{teacherData.specialization} teacher</span>
                      </span>
                    </center>
                  </div>
                </div>
                <div className="profile-div">

                  <span className="profile-heading" >Contact</span>

                  <h5>Email</h5>
                  <span className="contact-details" >{teacherData.email}</span>
                  <br /><br />
                  <h5>Phone</h5>
                  <span className="contact-details" >+91 {teacherData.phone_num}</span>


                </div>

               

                <div className="profile-div">
                  <span className="profile-heading" >Experience</span>

                  <MdOutlineBadge size="60px" />

                  <p className="education-detail">{teacherData.teaching_experience} years teaching experience</p>
                </div>
              </div>
            </div>
          </div>
        </Modal.Body>
      </Modal>
    </>
  )
}

export default TeacherProfile
