import React, {  useState } from 'react';
import 'react-toastify/dist/ReactToastify.css';
import TableComponent from '../Table Component';

const Department = () => {

    const [details, setDetails] = useState({ depName: "", hoD: "" });

    const onChangee = (e) => {
        e.preventDefault();
        setDetails({ ...details, [e.target.name]: e.target.value });
    };

    const inputConfigs = [
        { label: 'Department Name', name: 'depName', type: 'text', id: 'depName', value: details.depName, onChange: onChangee },
        { label: 'HoD', name: 'hoD', type: 'text', id: 'hoD', value: details.hoD, onChange: onChangee },
    ];

    

    return (
        <>
            <TableComponent 
            entityName={"department"}
            title={"Department"}
            valueSet={details}
            idName={"depId"}
            inputConfigs={inputConfigs}
            titleList={["Department name", "HoD"]}
            valueList={["depName", "hoD"]}
             />

        </>
    )
}

export default Department;
