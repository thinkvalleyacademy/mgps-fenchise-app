import React from "react";
import DashBoardCard from "../../../Dashboard/DashBoardCard/DashBoardCard";

const tableData = [
  {
    id: 1,
    receiptNo: "TF/24-0043",
    admNo: "123",
    studentName: "SHIBAN MOHAMMED V",
    studentStatus: "Active",
    feeCategory: "New Students",
    academicDuration: "23-24 new",
    classSection: "Class - 3",
   
    collectedBy: "Admin",
    paymentDate: "08-08-2024",
    collectedDate: "08-08-2024",
    paymentMode: "Cash",
  },
  {
    id: 2,
    receiptNo: "TF/24-0045",
    admNo: "123",
    studentName: "SHIBAN MOHAMMED V",
    studentStatus: "Active",
    feeCategory: "New Students",
    academicDuration: "23-24 new",
    classSection: "Class - 3",
   
    collectedBy: "Admin",
    paymentDate: "08-08-2024",
    collectedDate: "08-08-2024",
    paymentMode: "Cash",
  },
  {
    id: 3,
    receiptNo: "ENF/24-0035",
    admNo: "123",
    studentName: "SHIBAN MOHAMMED V",
    studentStatus: "Active",
    feeCategory: "New Students",
    academicDuration: "23-24 new",
    classSection: "Class - 3",
    collectedBy: "Admin",
    paymentDate: "08-08-2024",
    collectedDate: "08-08-2024",
    paymentMode: "Cash",
  },
  {
    id: 4,
    receiptNo: "TF/24-0032",
    admNo: "456",
    studentName: "Test Student",
    studentStatus: "Active",
    feeCategory: "Regular",
    academicDuration: "23-24 new",
    classSection: "Class - 7",
    collectedBy: "Admin",
    paymentDate: "07-08-2024",
    collectedDate: "07-08-2024",
    paymentMode: "Cash",
  },
];

const CollectionReport = () => {
  return (
    <div className="container-fluid my-4">
      {/* Page Header */}
      <DashBoardCard title="Fee Summary" />

      {/* Filter Section */}
      <div className="card p-3 mb-4 shadow-sm">
        <form>
          <div className="row">
            <div className="col-md-3 mb-3">
              <label htmlFor="academicSession" className="form-label">
                Academic Session
              </label>
              <select id="academicSession" className="form-select">
                <option>23-24 new</option>
                <option>22-23 old</option>
              </select>
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="fromDate" className="form-label">
                From Date
              </label>
              <input type="date" id="fromDate" className="form-control" />
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="toDate" className="form-label">
                To Date
              </label>
              <input type="date" id="toDate" className="form-control" />
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="listName" className="form-label">
                List Name
              </label>
              <select id="listName" className="form-select">
                <option>All</option>
              </select>
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="paymentMode" className="form-label">
                Mode of Payment
              </label>
              <select id="paymentMode" className="form-select">
                <option>All</option>
              </select>
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="feeType" className="form-label">
                Fee Type
              </label>
              <select id="feeType" className="form-select">
                <option>All</option>
              </select>
            </div>
            <div className="col-md-3 mb-3">
              <label htmlFor="collectedBy" className="form-label">
                Collected By
              </label>
              <select id="collectedBy" className="form-select">
                <option>All</option>
              </select>
            </div>
            <div className="col-md-3 mb-3 d-flex align-items-end">
              <button type="button" className="btn btn-primary w-100">
                Filter
              </button>
            </div>
          </div>
        </form>
      </div>

      {/* Search Bar */}
      <div className="mb-3">
        <input
          type="text"
          placeholder="Search by name/Receipt No"
          className="form-control"
        />
      </div>

      {/* Table Section */}
      <div className="table-responsive">
        <table className="table table-striped table-bordered table-hover text-center align-middle">
          <thead className="table-light">
            <tr>
              <th>Sl No</th>
              <th>Receipt No</th>
              <th>Adm No</th>
              <th>Student Name</th>
              <th>Student Status</th>
              <th>Fee Category</th>
              <th>Academic Duration</th>
              <th>Class Section</th>
              <th>Collected By</th>
              <th>Payment Date</th>
              <th>Collected Date</th>
              <th>Payment Mode</th>
            </tr>
          </thead>
          <tbody>
            {tableData.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.receiptNo}</td>
                <td>{item.admNo}</td>
                <td>{item.studentName}</td>
                <td>{item.studentStatus}</td>
                <td>{item.feeCategory}</td>
                <td>{item.academicDuration}</td>
                <td>{item.classSection}</td>
                <td>{item.collectedBy}</td>
                <td>{item.paymentDate}</td>
                <td>{item.collectedDate}</td>
                <td>{item.paymentMode}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CollectionReport;
