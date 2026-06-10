import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import DashBoardCard from "../../../Dashboard/DashBoardCard/DashBoardCard";
import "./style.css";

const StudentFeeDetails = () => {
  const navigate = useNavigate();
  const [feeItems, setFeeItems] = useState([
    {
      id: 1,
      name: "ENRICHMENT FEE",
      dueDate: "01-11-2023",
      feeAmount: 9500,
      fine: 0,
      scholarship: 0,
      remainingAmount: 9500,
      payingNow: 9500,
      selected: false,
    },
    {
      id: 2,
      name: "Term 1",
      dueDate: "30-11-2023",
      feeAmount: 8500,
      fine: 1630,
      scholarship: 0,
      remainingAmount: 5130,
      payingNow: 5130,
      selected: false,
    },
    {
      id: 3,
      name: "Term 2",
      dueDate: "30-12-2023",
      feeAmount: 8500,
      fine: 1330,
      scholarship: 0,
      remainingAmount: 4830,
      payingNow: 4830,
      selected: false,
    },
    {
      id: 4,
      name: "Term 3",
      dueDate: "31-01-2024",
      feeAmount: 8500,
      fine: 1010,
      scholarship: 0,
      remainingAmount: 9510,
      payingNow: 9510,
      selected: false,
    },
    {
      id: 5,
      name: "Admission fee",
      dueDate: "01-11-2023",
      feeAmount: 100,
      fine: 0,
      scholarship: 0,
      remainingAmount: 100,
      payingNow: 100,
      selected: false,
    },
  ]);

  const toggleSelection = (id) => {
    setFeeItems((prevItems) =>
      prevItems.map((item) =>
        item.id === id ? { ...item, selected: !item.selected } : item
      )
    );
  };

  const handlePayingNowChange = (id, value) => {
    setFeeItems((prevItems) =>
      prevItems.map((item) =>
        item.id === id ? { ...item, payingNow: Number(value) } : item
      )
    );
  };
  const handleSubmitClick = () => {

const feeSummary = {
    totalFee,
    totalFine,
    totalScholarship,
    totalRemaining,
    totalPayingNow,
  };

  // Navigating to the FeeSubmission route with the feeSummary object
  navigate("/Accounting/feeDetail/Students/Student/FeeSubmission", { state: feeSummary });
  };

  const selectedItems = feeItems.filter((item) => item.selected);
  const totalFee = selectedItems.reduce((sum, item) => sum + item.feeAmount, 0);
  const totalFine = selectedItems.reduce((sum, item) => sum + item.fine, 0);
  const totalScholarship = selectedItems.reduce(
    (sum, item) => sum + item.scholarship,
    0
  );
  const totalRemaining = selectedItems.reduce(
    (sum, item) => sum + item.remainingAmount,
    0
  );
  const totalPayingNow = selectedItems.reduce(
    (sum, item) => sum + item.payingNow,
    0
  );

  return (
    <div className="container-fluid">
      <DashBoardCard title="Fee Summary" />
      <div className="fee-container my-4">
        <div className="fee-card p-4 shadow-sm">
          <div className="fee-header d-flex justify-content-between align-items-center mb-3">
            <div className="fee-left d-flex align-items-center">
              <div
                className="fee-avatar rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3"
                style={{ width: "60px", height: "60px", fontSize: "2.5rem" }}
              >
                S
              </div>
              <div>
                <h5 className="fee-name mb-1">Samuel</h5>
                <p className="fee-details mb-0 text-muted">
                  <span>
                    Class: <strong>Class - 3</strong>
                  </span>{" "}
                  |
                  <span>
                    Primary Contact: <strong>7654329087</strong>
                  </span>{" "}
                  |
                  <span>
                    Parent: <strong>Parent</strong>
                  </span>
                </p>
                <p className="fee-details mb-0 text-muted">
                  <span>
                    Fee Category: <strong>New Students</strong>
                  </span>{" "}
                  |
                  <span>
                    Fee Session: <strong>23-24 new</strong>
                  </span>
                </p>
              </div>
            </div>
            <div className="fee-right">
              <p className="fee-dues text-danger fw-bold mb-0">
                Previous Session Dues: ₹ 0.00
              </p>
            </div>
          </div>

          <div className="row text-center">
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">Total Fee (A)</p>
              <h6 className="fee-value text-dark">₹ 35,100.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">
                Allocated Concession (B)
              </p>
              <h6 className="fee-value text-dark">₹ 0.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">
                Payable Fee (C = A - B)
              </p>
              <h6 className="fee-value text-danger">₹ 35,100.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">Fee Due</p>
              <h6 className="fee-value text-danger">₹ 100.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">Paid Till Date (D)</p>
              <h6 className="fee-value text-dark">₹ 38,760.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6 mb-3">
              <p className="fee-label text-muted mb-0">
                Fee Due (Annual) (E = C - D)
              </p>
              <h6 className="fee-value text-dark">₹ 100.00</h6>
            </div>
            <div className="fee-column col-md-3 col-sm-6">
              <p className="fee-label text-muted mb-0">Fee Paid</p>
              <h6 className="fee-value text-dark">₹ 38,760.00</h6>
            </div>
          </div>
        </div>
      </div>

      {/* Fee Items Table */}
      <div className="my-4">
        <div className="table-responsive" style={{ overflowY: "auto" }}>
          <table className="table table-hover">
            <thead className="table-light">
              <tr>
                <th>
                  <input
                    type="checkbox"
                    onChange={(e) =>
                      setFeeItems((prevItems) =>
                        prevItems.map((item) => ({
                          ...item,
                          selected: e.target.checked,
                        }))
                      )
                    }
                  />
                </th>
                <th>Name</th>
                <th>Due Date</th>
                <th>Fee Amount</th>
                <th>Fine</th>
                <th>Scholarship/Concession</th>
                <th>Remaining Amount</th>
                <th>Paying Now</th>
              </tr>
            </thead>
            <tbody>
              {feeItems.map((item) => (
                <tr key={item.id}>
                  <td>
                    <input
                      type="checkbox"
                      checked={item.selected}
                      onChange={() => toggleSelection(item.id)}
                    />
                  </td>
                  <td>{item.name}</td>
                  <td>{item.dueDate}</td>
                  <td>{item.feeAmount}</td>
                  <td>{item.fine}</td>
                  <td>{item.scholarship}</td>
                  <td>{item.remainingAmount}</td>
                  <td>
                    <input
                      type="number"
                      value={item.payingNow}
                      onChange={(e) =>
                        handlePayingNowChange(item.id, e.target.value)
                      }
                      disabled={!item.selected}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Conditional Fee Summary */}
      {selectedItems.length > 0 && (
        <div className="d-flex justify-content-between align-items-center mt-4 summary-section">
          <div className="text-start ms-auto fee-summary-text">
            <p>
              <strong>Total: </strong> ₹ {totalFee.toFixed(2)}{" "}
              <span style={{ margin: "0 20px" }}>
                {totalFine.toFixed(2)} Fine{" "}
              </span>{" "}
              ₹ {totalScholarship.toFixed(2)} Scholarship
              <span style={{ margin: "0 20px" }}>
                ₹ {totalRemaining.toFixed(2)} Remaining{" "}
              </span>{" "}
              ₹ {totalPayingNow.toFixed(2)}
            </p>
          </div>
          <div>
            <button
              className="btn btn-outline-danger me-2"
              onClick={() => navigate(-1)}
            >
              Cancel
            </button>
            <button
              className="btn btn-primary"
              onClick={() =>
                handleSubmitClick()
              }
            >
              Proceed &gt;&gt;
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentFeeDetails;
