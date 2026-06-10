import React, { useContext, useState } from 'react'
import { Delete, GetAll } from '../../../apis/Academic CRUD/AC_CRUD';
import CustomLoadingBar from '../LoadingBar/Index';
import DashBoardCard from '../../Dashboard/DashBoardCard/DashBoardCard';
import DropDownComponent from '../DropDown';
import './style.css'
import { MdDelete } from "react-icons/md";

import AuthContext from '../../../context/student/AuthContext';

const Routine = () => {

  const [isLoading, setIsLoading] = useState(true);

  const [routines, setEntityList] = useState(null);

  const { token } = useContext(AuthContext);

  function groupRoutinesByDay(routines,className,sectionName) {
    const daysOrder = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
  
    const filteredRoutines = routines.filter(routine => routine.class_name === className && routine.section_name === sectionName);
  
    // Group routines by day
    const grouped = filteredRoutines.reduce((acc, routine) => {
      acc[routine.day] = acc[routine.day] || [];
      acc[routine.day].push(routine);
      return acc;
    }, {});
    
    // Sort the groups by the correct day order
    const sortedGrouped = {};
    daysOrder.forEach(day => {
      if (grouped[day]) {
        sortedGrouped[day] = grouped[day];
      }
    });
  
    return sortedGrouped;
  }


  const handleOnClick = (className, sectionName) => {
    fetchData(className,sectionName);
  };

  const fetchData = async (className,sectionName) => {
    try {

      const data = await GetAll("routine", token);
      console.log(data.data)
      const sortedRoutines = groupRoutinesByDay(data.data , className,sectionName);

      setEntityList(sortedRoutines);
      if (typeof sectionName === "string")
        setIsLoading(false);

    } catch (error) {
      setIsLoading(false);

    }
  };

  const DeleteRoutine = async (routine) => {
    setIsLoading(true);
    console.log(routine)
    await Delete(routine.routine_id, "routine", token);
    fetchData();
    setIsLoading(false);
  }


  return (
    <>

      <div className="container" style={{ backgroundColor: "#F4F5F9", maxWidth: "1424px" }}>
        <CustomLoadingBar isLoading={isLoading} />

        <DashBoardCard
          title={"Routine"}
          buttonValue={`Add a routine`}
          visibility="visible"
          fetchData={fetchData}
        />


        <div className="card bg-white shadow rounded-lg overflow-hidden listContainer">

          <DropDownComponent
            handleOnClick={handleOnClick}
            isSection={"true"}
          />

          <div className="card-body class_routine_content">
            <table className="table table-striped table-bordered table-centered mb-0">
              <tbody>

                {!isLoading && (
                  Object.entries(routines).length > 0 ? (
                    Object.entries(routines).map(([day, routinesInDay]) => (
                      <tr key={day}>
                        <td style={{ fontWeight: "bold", width: "100px" }}>{day}</td>

                        <td className="m-1">
                          <div className="row">


                            {routinesInDay.map((routine, index) => (
                              <div className="col-md-4">
                                <div className="my-3">
                                  <div className="card">
                                    <div style={{
                                      display: 'flex',
                                      justifyContent: 'flex-end',
                                      position: 'absolute',
                                      right: '0'
                                    }
                                    }>
                                      <span className="badge rounded-pill bg-danger"> <MdDelete onClick={() => DeleteRoutine(routine)} style={{ cursor: "pointer" }} size={22} /> </span>
                                    </div>
                                    <div className="card-body" style={{ margin: '7px', padding: '10px' }}>
                                      <h5 className="card-title">{routine.subject_name} </h5>
                                      <p className="card-text">{routine.starting_hour}:{routine.starting_min} - {routine.ending_hour}:{routine.ending_min}</p>
                                      <p className="card-text"> {routine.teacher_name}</p>
                                      <p className="card-text"> Room-{routine.classRoom_id}</p>

                                    </div>
                                  </div>
                                </div>
                              </div>


                            ))}
                          </div>
                        </td>
                      </tr>
                    ))) : (
	                    <center>
	                      <h2 style={{ color: "#313A46" }}>No data in the list</h2>
	                      <img src='https://demo.creativeitem.com/ekattor/assets/backend/images/empty_box.png'
	                        alt="No data"
	                        width='200'
	                        height='150' />
	                    </center>
                  )
                )
                }

              </tbody>
            </table>

          </div>
        </div>



      </div >    </>
  )
}

export default Routine
