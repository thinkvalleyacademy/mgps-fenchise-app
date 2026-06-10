import React, { useState } from 'react';
import TableComponent from '../Table Component';

const Subjects = () => {
  const [details, setDetails] = useState({ subjectName: "", className: "" });
  const onChangee = (e) => {
    setDetails({ ...details, [e.target.name]: e.target.value });
  };

  const inputConfigs = [
    { label: 'Subject Name', name: 'subjectName', type: 'text', id: 'subjectName', value: details.subjectName, onChange: onChangee },
    { label: 'Class name', name: 'className', type: 'text', id: 'className', value: details.className, onChange: onChangee },
  ];

  return (
    <>
           <TableComponent
            entityName={"subject"}
            isClass={true}
            title={"Subject"}
            valueSet={details}
            idName={"id"}
            inputConfigs={inputConfigs}
            titleList={["Subject name"]}
            valueList={["subjectName"]}

          />
    </>
  )
}

export default Subjects
