import React, { useState } from "react";
import PropTypes from "prop-types";
import "./style.css";
import { useNavigate } from "react-router-dom";
import CreateAcademics from "../../Academics/CreateModal";
import CreateRoutine from "../../Academics/Routine/CreateRoutine";

const DashBoardCard = (props) => {
  const navigate = useNavigate();
  const isVisible = props.visibility;


  const [openSubject, setSubject] = useState(false);
  const [openDepatmt, setDepartment] = useState(false);
  const [openClassRoom, setClassRoon] = useState(false);
  const [openRoutine, setRoutine] = useState(false);
  const [openAcademic, setAcademic] = useState(false);

  const modalConfig = {
    
    Subject: {
      component: CreateAcademics,
      props: {
        show: openSubject,
        onHide: () => {
          setSubject(false);
          props.fetchData();
        },
        title: "Subject",
        details: props.details,
        inputConfigs: props.inputConfigs,
      },
    },

    ClassRoom: {
      component: CreateAcademics,
      props: {
        show: openClassRoom,
        onHide: () => {
          setClassRoon(false);
          props.fetchData();
        },
        title: "ClassRoom",
        details: props.details,
        inputConfigs: props.inputConfigs,
      },
    },

    Department: {
      component: CreateAcademics,
      props: {
        show: openDepatmt,
        onHide: () => {
          setDepartment(false);
          props.fetchData();
        },
        title: "Department",
        details: props.details,
        inputConfigs: props.inputConfigs,
      },
    },
    Routine: {
      component: CreateRoutine,
      props: {
        show: openRoutine,
        onHide: () => {
          setRoutine(false);
          props.fetchData();
        },
        title: "Routine",
      },
    },
    Class: {
      component: CreateAcademics,
      props: {
        show: openAcademic,
        onHide: () => {
          setAcademic(false);
          props.fetchData();
        },
        title: "Class",
        details: props.details,
        inputConfigs: props.inputConfigs,
      },
    },
  };

  const modalInfo = modalConfig[props.title];
  const ModalComponent = modalInfo && modalInfo.component;
  const modalProps = modalInfo && modalInfo.props;

  const registerButton = () => {
    switch (props.title) {
      case "Student":
        navigate("/registration/student");
        break;
      case "Teacher":
        navigate("/registration/teacher");
        break;

      case "Subject":
        setSubject(true);
        break;

      case "Department":
        setDepartment(true);
        break;

      case "ClassRoom":
        setClassRoon(true);
        break;

      case "Fee Structure":
        navigate("/Accounting/fee-structure/create")
        break;

      case "Routine":
        setRoutine(true);
        break;

      case "Class":
        setAcademic(true);
        break;

      default:
        break;
    }
  };

  return (
    <>
      {ModalComponent && <ModalComponent {...modalProps} />}

      <div className="content">
        <div className="horizontal-card">
          <h2 style={{ marginTop: "20px" }}>
            {props.title.charAt(0).toUpperCase() + props.title.slice(1)}
            <button
              className="btn btn-outline-primary rounded-pill alignToTitle float-end mt-0"
              onClick={registerButton}
              style={{ visibility: isVisible }}
            >
              <i className="mdi mdi-plus"></i>+ {props.buttonValue}
            </button>
          </h2>
        </div>
      </div>
    </>
  );
};

DashBoardCard.defaultProps = {
  visibility: "hidden",
  buttonValue: "None",
};

DashBoardCard.prototype = {
  title: PropTypes.string.isRequired,
  visibility: PropTypes.string,
  buttonValue: PropTypes.string,
};

export default DashBoardCard;
