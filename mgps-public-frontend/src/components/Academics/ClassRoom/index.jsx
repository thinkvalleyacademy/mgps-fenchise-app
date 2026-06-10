import React, { useState } from 'react';
import TableComponent from '../Table Component';


const ClassRoom = () => {

    const [details, setDetails] = useState({ roomName: "", capacity: "" });
    const onChangee = (e) => {
        setDetails({ ...details, [e.target.name]: e.target.value });
    };

    const inputConfigs = [
        { label: 'Name', name: 'roomName', type: 'text', id: 'roomName', value: details.roomName, onChange: onChangee },
        { label: 'Capacity', name: 'capacity', type: 'number', id: 'capacity', value: details.capacity, onChange: onChangee },
    ];

    return (
        <>

        <TableComponent
        entityName={"classRoom"}
        title={"ClassRoom"}
        valueSet={details}
        idName={"roomID"}
        inputConfigs={inputConfigs}
        titleList={["Room name", "Capacity"]}
        valueList={["roomName", "capacity"]}
        />

           
        </>
    )
}

export default ClassRoom
