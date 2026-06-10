import React, { useCallback, useState, useEffect, useContext } from "react";
import { Modal } from "react-bootstrap";
import "./style.css";
import "bootstrap/dist/css/bootstrap.css";
import { getInfoByRegistrationNumber } from "../../../../apis/CRUD_operation/operation";
import AuthContext from "../../../../context/student/AuthContext";
const StudentProfile = ({
  student,
  show,
  handleClose,
}) => {
  const { token } = useContext(AuthContext);

  const [studentData, setStudentData] = useState(null);

  const fetchData = useCallback(async () => {
    if (!student || !token) return;
    console.log("Registration Number is ", student)
    try {
      const response = await getInfoByRegistrationNumber(student, token);
      setStudentData(response);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [student, token]);

  useEffect(() => {
    if (!show) return;
    fetchData();
  }, [fetchData, show]);

  if (!studentData) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Modal show={show} onHide={handleClose}>
        <Modal.Body>
          <div className="container">
            <h2>
              <b>Student Profile</b>
            </h2>
            <div className="row-fluid">
              <div className="span10 offset1">
                <div className="modal_navbar">
                  <div className="modal_nav">
	                    <img
	                      src="data:image/png;base64,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"
	                      alt="Student profile"
	                      width={130}
	                      height={130}
	                      style={{ borderRadius: "50%", marginTop: "20px" }}
	                    />
                    <span>
                      <span className="modal-user-name">
                        {studentData.firstName} {studentData.lastName}{" "}
                      </span>
                      <span className="modal-position">
                        {studentData.grade} "{studentData.section}"
                      </span>
                    </span>
                  </div>
                </div>
                <br />
                <br />
                <br />
                <br />

                <span
                  className="modal-user-name"
                  style={{ fontSize: "20px", margin: "20px" }}
                >
                  Contact Infromation
                </span>

                <span>
                  <span className="modal-information">Phone</span>
                  <span style={{ fontSize: "17px", marginLeft: "98px" }}>
                    +91 {studentData.studentMob}
                  </span>
                </span>

                <hr />

                <span>
                  <span className="modal-information">Email</span>
                  <span style={{ fontSize: "17px", marginLeft: "100px" }}>
                    {studentData.email}
                  </span>
                </span>
                <hr />

                <span>
                  <span className="modal-information">Address</span>
                  <span style={{ fontSize: "17px", marginLeft: "80px" }}>
                    {studentData.address}
                  </span>
                </span>
                <hr />
                <span
                  className="modal-user-name"
                  style={{ fontSize: "20px", marginTop: "20px" }}
                >
                  Guadians
                </span>

                <br />

                <div className="modal_content">
                  <div className="modal-card">
                    <div
                      className="modal_nav"
                      style={{ margin: "10px", position: "relative" }}
                    >
	                      <img
	                        src="data:image/png;base64,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"
	                        alt="Student profile"
	                        className="img-guadians"
	                      />
                      <span>
                        <span className="modal-guadian-name">
                          {studentData.fatherName}
                          <button
                            className="btn btn-outline-primary rounded-pill alignToTitle float-end mt-0"
                            style={{ position: "absolute", left: "300px" }}
                          >
                            Contact
                          </button>
                        </span>

                        <span className="modal-relation">Father</span>
                      </span>
                    </div>

                  </div>
                </div>
                <div className="modal_content">
                  <div className="modal-card">
                    <div
                      className="modal_nav"
                      style={{ margin: "10px", position: "relative" }}
                    >
	                      <img
	                        src="data:image/png;base64,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"
	                        alt="Guardian profile"
	                        className="img-guadians"
	                      />
                      <span>
                        <span className="modal-guadian-name">
                          {studentData.motherName}
                          <button
                            className="btn btn-outline-primary rounded-pill alignToTitle float-end mt-0"
                            style={{ position: "absolute", left: "300px" }}
                          >
                            Contact
                          </button>
                        </span>

                        <span className="modal-relation">Mother</span>
                      </span>
                    </div>
                  </div>
                </div>
                <div className="modal_content">
                  <div className="modal-card">
                    <div
                      className="modal_nav"
                      style={{ margin: "10px", position: "relative" }}
                    >
	                      <img
	                        src="data:image/png;base64,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"
	                        alt="Guardian profile"
	                        className="img-guadians"
	                      />
                      <span>
                        <span className="modal-guadian-name">
                          {studentData.otherParents}
                          <button
                            className="btn btn-outline-primary rounded-pill alignToTitle float-end mt-0"
                            style={{ position: "absolute", left: "300px" }}
                          >
                            Contact
                          </button>
                        </span>

                        <span className="modal-relation">Relation</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Modal.Body>
      </Modal>
    </>
  );
};

export default StudentProfile;
