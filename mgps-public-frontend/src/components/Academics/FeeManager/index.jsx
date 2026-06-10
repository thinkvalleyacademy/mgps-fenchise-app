import React, { useState } from "react";
import DashBoardCard from "../../Dashboard/DashBoardCard/DashBoardCard";
import "./style.css";
import InvoiceFilter from "./invoiceFilter";
import { GetInvoice } from "../../../apis/Fee_operation/fee_operation";

const FeeManager = () => {
  const [invoiceList, setList] = useState(null);
  const [tableVisibility, setVisibility]= useState(false);

  const fetchInvoice = async (userName, quarter, year) => {
    
    const data = await GetInvoice(userName, quarter, year); // Replace with actual parameters
    if (data.status === 200) {
      setList(data.data);
      setVisibility(true)
    }
    console.log(data)
  };

  return (
    <>
      <div
        className="container TopContainer"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          entity={"Invoice"}
          title={"Student Fee Manager"}
          buttonValue={`Add a single invoice`}
          visibility="visible"
        />
        <div className="container mt-4">
         {
         !tableVisibility ? <InvoiceFilter fetchInvoice={fetchInvoice}/> :
      //   
      <div className="table-container-custom">
      <div className="container-fluid table-responsive py-5">
        <table className="table-custom">
          <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Amount</th>
              <th scope="col">Date</th>
            </tr>
          </thead>
          <tbody>
            {invoiceList.feeRecord.map((record, index) => (
              <tr key={index}>
                <th scope="row">{index + 1}</th>
                <td>{record.amount}</td>
                <td>{record.date}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="summary-custom">
            <p style={{color:"green"}}><strong>Total Submitted:</strong> {invoiceList.totalSubmitted}</p>
            <p style={{color:"red"}}><strong>Total Due:</strong> {invoiceList.totalDue}</p>
            <p><strong>Due Date:</strong> {invoiceList.dueDate}</p>
            <p style={{color:"blue"}}><strong>Total Fee:</strong> {invoiceList.totalFee}</p>
          </div>
        </div>
     
         </div>
         } 
        </div>
      </div>
    </>
  );
};

export default FeeManager;
