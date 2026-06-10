import React, { useEffect, useState } from "react";
import Modal from "react-bootstrap/Modal";
import ClassDropDown from "../DropDown/ClassDropDown";

const AcademicUpdate = ({
  title,
  show,
  onHide,
  onSubmit,
  isupdating,
  inputConfigs,
}) => {
  const [isSubmitDisabled, setIsSubmitDisabled] = useState(false);
  useEffect(() => {
    const hasEmptyField = inputConfigs.some(
      (config) => config.value.trim() === ""
    );
    setIsSubmitDisabled(hasEmptyField);
  }, [inputConfigs]);

  return (
    <>
      <Modal show={show} onHide={onHide} backdrop="static" keyboard={false}>
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">{title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {inputConfigs.map((config, index) =>
            config.id !== "className" ? (
              <div key={index}>
                <label htmlFor={config.id}>{config.label}:</label>
                {console.log(config)}
                <input
                  className="form-control"
                  name={config.name}
                  type={config.type}
                  id={config.id}
                  value={config.value}
                  onChange={config.onChange}
                />
              </div>
            ) : (
             <ClassDropDown index={index} id={config.id} 
             label={config.label} name={config.name} 
             onChange={config.onChange} value={config.value}/>
            )
          )}

        </Modal.Body>
        <Modal.Footer>
          <div className="form-group mt-2 col-md-12">
            <button
              className="btn btn-block btn-primary"
              disabled={isSubmitDisabled}
              type="submit"
              onClick={onSubmit}
            >
              {isupdating ? "Update....." : `Update ${title}`}
            </button>
          </div>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default AcademicUpdate;
