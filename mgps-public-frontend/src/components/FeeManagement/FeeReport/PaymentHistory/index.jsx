import React from "react";
import DashBoardCard from "../../../Dashboard/DashBoardCard/DashBoardCard";
import './style.css'

const PaymentHistory = () => {

    const studentFeeRecords = [
        {
          slNo: 1,
          studentName: "Aryan Sharma",
          classSection: "10A",
          feeType: "Tuition Fee",
          receiptNo: "REC001",
          amount: "₹1500",
          paymentDate: "2024-12-01",
          collectedBy: "Mr. Rajesh",
          statusRemark: "Paid on time",
          status: "Paid",
        },
        {
          slNo: 2,
          studentName: "Neha Gupta",
          classSection: "9B",
          feeType: "Lab Fee",
          receiptNo: "REC002",
          amount: "₹800",
          paymentDate: "2024-12-03",
          collectedBy: "Ms. Kavita",
          statusRemark: "Paid late",
          status: "Paid",
        },
        {
          slNo: 3,
          studentName: "Rohan Mehta",
          classSection: "8C",
          feeType: "Sports Fee",
          receiptNo: "REC003",
          amount: "₹500",
          paymentDate: "2024-11-28",
          collectedBy: "Mr. Rajesh",
          statusRemark: "Pending clearance",
          status: "Cancelled",
        },
        {
          slNo: 4,
          studentName: "Simran Kaur",
          classSection: "7D",
          feeType: "Library Fee",
          receiptNo: "REC004",
          amount: "₹300",
          paymentDate: "2024-12-02",
          collectedBy: "Ms. Kavita",
          statusRemark: "Paid on time",
          status: "Cancelled",
        },
        {
          slNo: 5,
          studentName: "Aditya Verma",
          classSection: "6A",
          feeType: "Transport Fee",
          receiptNo: "REC005",
          amount: "₹1200",
          paymentDate: "2024-12-05",
          collectedBy: "Mr. Rajesh",
          statusRemark: "Partially Paid",
          status: "Paid",
        },
      ];
      

  return (
    <div className="container-fluid my-4">
      <DashBoardCard title="Payment History" />

      <div className="table-responsive">
        <table className="table table-striped table-bordered table-hover text-center align-middle">
          <thead className="table-light">
            <tr>
              <th>Sl No</th>
              <th>Student Name</th>
              <th> Class Section </th>
              <th> Fee Type  </th>
              <th> Receipt No  </th>
              <th> Amount </th>
              <th> Payment Date </th>
              <th> Collected by  </th>
              <th> Status remark  </th>
              <th> Status </th>
            </tr>
          </thead>
          <tbody>
          {studentFeeRecords.map((record) => (
          <tr key={record.slNo}>
            <td>{record.slNo}</td>
            <td>{record.studentName}</td>
            <td>{record.classSection}</td>
            <td>{record.feeType}</td>
            <td>{record.receiptNo}</td>
            <td>{record.amount}</td>
            <td>{record.paymentDate}</td>
            <td>{record.collectedBy}</td>
            <td>{record.statusRemark}</td>
            <td>
              <span className={`status ${record.status.toLowerCase()}`}>
                {record.status}
              </span>
            </td>
          </tr>
        ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default PaymentHistory
