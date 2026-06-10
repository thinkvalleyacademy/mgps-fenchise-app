import React from 'react'

import { MdDelete } from "react-icons/md";
import { FaPen } from "react-icons/fa";

const ListComponent = ({
    entityList,
    titleList,
    valueList,
    DeleteEntity,
    UpdateEntity
}) => {

    return (
        <>
                {
                    Array.isArray(entityList) && entityList.length > 0 ? (
                        <table
                            id="basic-datatable"
                            className="table table-striped dt-responsive nowrap dataTable no-footer dtr-inline"
                            width="90%"
                            role="grid"
                            aria-describedby="basic-datatable_info"
                            style={{ position: "relative", marginTop: "30px", marginLeft: "20px" }}
                        >
                            <thead>
                                <tr role="row">
                                    {titleList.map((title, index) => (
                                        <th key={title ?? index} className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1" style={{ width: "570px", backgroundColor: "#313a46", color: "#ababab" }}
                                            aria-label="Photo: activate to sort column ascending">{title}</th>

                                    ))}




                                    <th className="sorting" tabIndex="0" aria-controls="basic-datatable" rowSpan="1" colSpan="1"
                                        style={{ width: "191.8px", backgroundColor: "#313a46", color: "#ababab" }}
                                        aria-label="Options: activate to sort column ascending">Options</th>
                                </tr>
                            </thead>
                            <tbody>
                                {entityList.map((result, index) => (

                                    <tr className="odd" key={index} >

	                                        {valueList.map((value, valueIndex) => (
	                                            <td key={value ?? valueIndex} className="dtr-control1 sorting_1" tabIndex="0">
	                                                {result[value]}
	                                            </td>

	                                        ))}
                                        <td>

                                            <MdDelete size={22} style={{ marginRight: '20px', cursor: 'pointer' }}
                                                onClick={() => DeleteEntity(result)}
                                            />
                                            <FaPen size={18} style={{ cursor: 'pointer' }}
                                                onClick={() => UpdateEntity(result)}
                                            />

                                        </td>
                                    </tr>

                                ))}
                            </tbody>
                        </table>
                    )
                        : (
	                            <center>
	                                <h2 style={{color:"#313A46"}}>No data in the list</h2>
	                                <img src='https://demo.creativeitem.com/ekattor/assets/backend/images/empty_box.png'
                                        alt="No data"
	                                    width='200'
	                                    height='150' />
	                            </center>)

                }


        </>
    )
}

export default ListComponent
