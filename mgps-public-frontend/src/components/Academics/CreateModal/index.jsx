import React, { useContext, useEffect, useState } from "react";
import { Modal } from "react-bootstrap";
import "./style.css";
import { CreateEntity } from "../../../apis/Academic CRUD/AC_CRUD";
import AuthContext from "../../../context/student/AuthContext";
import ClassDropDown from "../DropDown/ClassDropDown";
import { FaBook, FaGraduationCap, FaUsers, FaChalkboardTeacher, FaDoorOpen, FaClock, FaDollarSign } from "react-icons/fa";

const CreateAcademics = ({ show, onHide, title, details, inputConfigs, includeClassDropdown = false }) => {
  const { token } = useContext(AuthContext);

  const [isButtonDisabled, setIsSubmitDisabled] = useState(false);

  useEffect(() => {
    const hasEmptyField = inputConfigs.some(
      (config) => config.value.trim() === ""
    );
    setIsSubmitDisabled(hasEmptyField);
  }, [inputConfigs]);

  const onSubmit = async () => {
    setIsSubmitDisabled(true);
    await CreateEntity(
      details,
      title.charAt(0).toLowerCase() + title.slice(1),
      token
    );
    onHide();
  };

  // Get icon based on title
  const getTitleIcon = () => {
    const titleLower = title.toLowerCase();
    if (titleLower.includes("subject")) return <FaBook />;
    if (titleLower.includes("class")) return <FaGraduationCap />;
    if (titleLower.includes("department")) return <FaUsers />;
    if (titleLower.includes("teacher")) return <FaChalkboardTeacher />;
    if (titleLower.includes("room")) return <FaDoorOpen />;
    if (titleLower.includes("routine")) return <FaClock />;
    if (titleLower.includes("fee")) return <FaDollarSign />;
    return <FaGraduationCap />;
  };

  return (
    <>
      <Modal show={show} onHide={onHide} backdrop="static" keyboard={false} centered>
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            {getTitleIcon()} Add {title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {includeClassDropdown && (
            <div className="form-group">
              <label htmlFor="classDropdown">
                <FaGraduationCap /> Select Class
              </label>
              <ClassDropDown
                id="classDropdown"
                name="className"
                onChange={(e) => details.className = e.target.value}
                value={details.className || ""}
              />
            </div>
          )}
          {inputConfigs.map((config, index) =>
            config.id !== "className" ? (
              <div key={index} className="form-group">
                <label htmlFor={config.id}>{config.label}</label>
                <input
                  className="form-control"
                  name={config.name}
                  type={config.type}
                  id={config.id}
                  value={config.value}
                  onChange={config.onChange}
                  placeholder={`Enter ${config.label.toLowerCase()}`}
                />
              </div>
            ) : (
              <ClassDropDown
                key={index}
                index={index}
                id={config.id}
                label={config.label}
                name={config.name}
                onChange={config.onChange}
                value={config.value}
              />
            )
          )}
        </Modal.Body>
        <Modal.Footer>
          <button
            className="btn btn-secondary"
            type="button"
            onClick={onHide}
          >
            Cancel
          </button>
          <button
            className="btn btn-primary"
            disabled={isButtonDisabled}
            type="button"
            onClick={onSubmit}
          >
            Add {title}
          </button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default CreateAcademics;
