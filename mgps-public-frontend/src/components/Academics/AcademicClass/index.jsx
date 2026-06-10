import React, { useState } from 'react'
import TableComponent from '../Table Component';

const AcademicClass = () => {

    const [details, setDetails] = useState({ className: "", sectionName: "" });
    const onChangee = (e) => {
        setDetails({ ...details, [e.target.name]: e.target.value });
    };

    const inputConfigs = [
        { label: 'Name of Class', name: 'className', type: 'text', id: 'className', value: details.className, onChange: onChangee },
        { label: 'Section name', name: 'sectionName', type: 'text', id: 'sectionName', value: details.sectionName, onChange: onChangee },
    ];




    return (
        <>

        <TableComponent
        entityName={"academic"}
        title={"Class"}
        valueSet={details}
        idName={"id"}
        inputConfigs={inputConfigs}
        titleList={["Class name"]}
        valueList={["className"]}

        
        />

        </>
    )
}

export default AcademicClass
