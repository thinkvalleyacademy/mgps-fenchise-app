import React, { useCallback, useContext, useEffect, useState } from 'react'
import { Modal } from 'react-bootstrap';
import { CreateEntity, GetAll } from '../../../apis/Academic CRUD/AC_CRUD';
import AuthContext from '../../../context/student/AuthContext';
import { fetchListData } from '../../../apis/CRUD_operation/operation';

const CreateRoutine = ({ show, onHide, title }) => {

    const [classList, setClasses] = useState([]);
    const [sectionList, setSectionList] = useState([]);
    const { token } = useContext(AuthContext);


    const [details, setDetails] = useState({
        class_name: { value: "" },
        section_name: { value: "" },
        subject_name: { value: "" },
        teacher_id: { value: "" },
        teacher_name: { value: "" },
        classRoom_id: { value: "" },
        day: { value: "" },
        starting_hour: { value: "" },
        starting_min: { value: "" },
        ending_hour: { value: "" },
	        ending_min: { value: "" },
	    });

	    const weekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

    const [selectedClass, setSelectedClass] = useState("");
    const [subjectList, setSubjectList] = useState("");
    const [classRoomList, setRoomList] = useState("");
    const [teacherList, setTeacherList] = useState("");


    const updateDetails = (updatedFields) => {

        setDetails(prevDetails => {
            const updatedDetails = { ...prevDetails[0], ...updatedFields };
            return [updatedDetails];
        });
    };

    const handleClassChange = (event) => {
        const selectedClassIndex = event.target.value;
        setSelectedClass(selectedClassIndex);
        if (selectedClassIndex !== "") {
            const sections = classList[selectedClassIndex].sections.map(section => section.sectionName);

            updateDetails({
                class_name: { value: classList[selectedClassIndex].className },
            });
            setSectionList(sections);
        } else {
            setSectionList([]);
        }
    };


	    const fetchData = useCallback(async () => {
	        try {
	            const responseData = await GetAll("academic", token);
	            setClasses(responseData.data);
	            const subjectList = await GetAll("subject", token);
	            setSubjectList(subjectList.data);
	            const roomList = await GetAll("classRoom", token);
	            setRoomList(roomList.data);
	            const teacherData = await fetchListData(token, "teacher");
	            setTeacherList(teacherData);
	        } catch (error) {
	            console.error('Error fetching data:', error);
	        }
	    }, [token]);

	    useEffect(() => {
	        fetchData();
	    }, [fetchData]);


    const setTeacherValues = async (event) => {
        updateDetails({
            teacher_id: { value: event.target.selectedIndex },
            teacher_name: { value: event.target.value }
        });
    }


    const onSubmit = async (e) => {
        let postData = details.map(item => {
            return {
                class_name: item.class_name.value,
                section_name: item.section_name.value,
                subject_name: item.subject_name.value,
                teacher_id: item.teacher_id.value,
                teacher_name: item.teacher_name.value,
                classRoom_id: parseInt(item.classRoom_id.value),
                day: item.day.value,
                starting_hour: item.starting_hour.value.padStart(2, '0'),
                starting_min: item.starting_min.value.padStart(2, '0'),
                ending_hour: item.ending_hour.value.padStart(2, '0'),
                ending_min: item.ending_min.value.padStart(2, '0')
            };
        });

        e.preventDefault();

        await CreateEntity(postData[0], "routine", token);
        onHide();
    }

    const generateHourOptions = () => {
        const hours = [];
        for (let i = 7; i <= 15; i++) {
            hours.push(i);
        }
        return hours.map(hour => <option key={hour} value={hour}>{hour}</option>);
    };

    const generateMinutesOptions = () => {
        const minutes = [];
        for (let i = 0; i <= 60; i++) {
            if (i % 5 === 0)
                minutes.push(i);
        }
        return minutes.map(minutes => <option key={minutes} value={minutes}>{minutes}</option>);
    };

    const capitalizeFirstLetter = (string) => {
        if (!string) {
            return '';
        }
        return string.charAt(0).toUpperCase() + string.slice(1);
    };


    return (
        <>
            <Modal
                show={show}
                onHide={onHide}
                backdrop="static"
                keyboard={false}
            >

                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-vcenter">
                        Add a {title}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>

                    <form onSubmit={onSubmit} >
                        <div className="form-row" style={{ marginLeft: "20px" }}>
                            <div className="form-group" style={{ minWidth: "400px" }} >
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Class
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={handleClassChange}
	                                            value={selectedClass}
	                                            required
	                                        >
	                                            <option value="">Select class...</option>
	                                            {
	                                                classList.map((result, index) => (
	                                                    <option key={index} value={index}>{result.className}</option>
	                                                ))
	                                            }

	                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Section
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={(e) => setDetails([{ ...details[0], section_name: { value: e.target.value } }, ...details.slice(1)])}
	                                            disabled={!selectedClass}
	                                            defaultValue=""
	                                            required

	                                        >
	                                            <option value="">Select section...</option>
	                                            {
	                                                sectionList.map((result, index) => (
	                                                    <option key={index} value={result}>{result}</option>
	                                                ))
	                                            }


                                        </select>
                                    </div>

                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Subject
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={(e) => setDetails([{ ...details[0], subject_name: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select Subject...</option>
	                                            {Object.values(subjectList).map((subject, index) => (
	                                                <option key={index} value={subject.subjectName}>
	                                                    {subject.subjectName}
	                                                </option>
	                                            ))}
	                                        </select>

                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Teacher
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={setTeacherValues}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select teacher...</option>
	                                            {Object.values(teacherList).map((teacher, index) => (
	                                                <option key={index} value={teacher.firstName + teacher.lastName}>
	                                                    {capitalizeFirstLetter(teacher.firstName)} {" "} {teacher.lastName}
	                                                </option>
	                                            ))}



                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Class room
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={(e) => setDetails([{ ...details[0], classRoom_id: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select class room...</option>
	                                            {Object.values(classRoomList).map((room, index) => (
	                                                <option key={index} value={room.roomID}>
	                                                    {room.roomName}
	                                                </option>
	                                            ))}


                                        </select>
                                    </div>
                                </div>


                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Day
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            defaultValue=""
	                                            required
	                                            onChange={(e) => setDetails([{ ...details[0], day: { value: e.target.value } }, ...details.slice(1)])}
	                                        >
	                                            <option value="">Select a day...</option>
	                                            {weekdays.map((weekday, i) => (
	                                                <option key={i} value={weekday}>{weekday}</option>
	                                            ))}

	                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Starting hour
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="starting_hour"
	                                            aria-label="Starting Hour select"
	                                            onChange={(e) => setDetails([{ ...details[0], starting_hour: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select starting hour...</option>
	                                            {generateHourOptions()}
	                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Starting minute
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={(e) => setDetails([{ ...details[0], starting_min: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select starting minute...</option>
	                                            {generateMinutesOptions()}
	                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Ending hour
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="starting_hour"
	                                            aria-label="Starting Hour select"
	                                            onChange={(e) => setDetails([{ ...details[0], ending_hour: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select ending hour...</option>
	                                            {generateHourOptions()}
	                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label className="col-md-3 col-form-label" >
                                        Ending minute
                                    </label>
                                    <div className="col-md-9">

	                                        <select
	                                            className="form-select"
	                                            id="librarySectionDropdown"
	                                            aria-label="Section select"
	                                            onChange={(e) => setDetails([{ ...details[0], ending_min: { value: e.target.value } }, ...details.slice(1)])}
	                                            defaultValue=""
	                                            required
	                                        >
	                                            <option value="">Select Ending minute...</option>
	                                            {generateMinutesOptions()}
	                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="form-group mt-2 col-md-12">
                            <button className="btn btn-block btn-primary"
                                type="submit">
                                Add a routine
                            </button>
                        </div>
                    </form>

                </Modal.Body>

            </Modal>
        </>
    )
}

export default CreateRoutine
