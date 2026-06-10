import React, { useState } from 'react';
import DashBoardCard from "../../../Dashboard/DashBoardCard/DashBoardCard";
import './style.css';
import ReceiptFormat from './receiptFormat';
import { useLocation } from "react-router-dom";


const FeeSubmission = () => {
  const location = useLocation();
  const feeSummary = location.state;


  const [isSubmitted, setIsSubmitted] = useState(false); // New state for submission status
  const [formData, setFormData] = useState({
    selectedFees: ['monthly'],
    remarks: '',
    paymentMethod: '',
    totalAmount: 0,
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Submitted form data:', formData);
    setIsSubmitted(true); // Set submission status to true
  };

  const handlePrint = () => {
    const printContents = document.getElementById("receipt").innerHTML;
    const printWindow = window.open("", "_blank");
    printWindow.document.open();
    printWindow.document.write(`
      <html>
        <head>
          <title>Print Receipt</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            .receipt-container {
              width: 600px;
              margin: auto;
              border: 1px solid #ddd;
              padding: 20px;
              border-radius: 5px;
              box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            .receipt-header {
              text-align: center;
              font-size: 1.5rem;
              font-weight: bold;
              color: #4caf50;
            }
            .receipt-details {
              margin-top: 20px;
            }
            .receipt-details p {
              margin: 5px 0;
              font-size: 1rem;
            }
            .footer {
              text-align: center;
              margin-top: 20px;
              font-size: 0.9rem;
              color: #777;
            }
          </style>
        </head>
        <body>
          ${printContents}
        </body>
      </html>
    `);
    printWindow.document.close();
    printWindow.print();
  };

  if (isSubmitted) {
    return (
      <div className="container-fluid">
         <DashBoardCard title="Fee Payment Completed" />
      {/* <div className="success-message">
      <div className="success-icon">
          <i className="bi bi-check-circle-fill text-success" style={{ fontSize: "5rem" }}></i>
        </div>
        <h1 className="text-success">Success</h1>
        <p>Your transaction has been successfully completed.</p>
        <div className="buttonsSty">
        <button
className="btn professional-btn"          
style={{marginRight:"20px"}}
          onClick={() => setIsSubmitted(false)} // Reset form for new submission
        >
          Submit Another Fee
        </button>

        <button
 className="btn print-btn"          
 onClick={() => setIsSubmitted(false)} // Reset form for new submission
        >
          Print Receipt
        </button>
        </div>
       
      </div> */}
      <div className="success-container">
  <div className="checkmark-circle">
    <div className="checkmark"></div>
  </div>
  <h2 className="success-message">Success</h2>
  <h4>Your transaction has been successfully completed.</h4>
  <div className="button-container">
    <button
      className="btn professional-btn"
      onClick={() => setIsSubmitted(false)}
    >
      Submit Another Fee
    </button>
    <button
      className="btn print-btn"
      onClick={handlePrint}>
    
      Print Receipt
    </button>
  </div>
  <div id="receipt" style={{ display: "none" }}>
 <ReceiptFormat/>
      </div>
</div>

    </div>
    );
  }

  return (
    <div className="container-fluid">
      <DashBoardCard title="Fee Payment" />
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
                  Roll number: <strong>120251</strong>
                </p>
              </div>
            </div>
            <div className="fee-right">
              <p className="fee-dues text-danger fw-bold mb-0">
                Remaining Amount: ₹ {feeSummary.totalRemaining}
              </p>
            </div>
          </div>
        </div>
        <div style={{ marginLeft: "20px" }}>
          <form className="row g-3 feeForm needs-validation" onSubmit={handleSubmit}>
           
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Remark</label>
                <input
                  type="text"
                  className="form-control"
                  id="remarks"
                  value={formData.remarks}
                  onChange={(e) => setFormData(prev => ({ ...prev, remarks: e.target.value }))}
                  placeholder="Enter any additional notes or remarks..."
                  required
                />
              </div>
            </div>
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Method</label>
                <select
                  id="dropdown"
                  name="paymentMethod"
                  className="form-control"
                  value={formData.paymentMethod}
                  onChange={(e) => setFormData(prev => ({ ...prev, paymentMethod: e.target.value }))}
                  required
                >
                  <option value>Select</option>
                  <option value="cash">Cash</option>
                  <option value="check">Check</option>
                  <option value="online">Online Payment Systems</option>
                </select>
              </div>
            </div>
            <div className="dynamic-total-fee">
              <h4>Total Fee: ₹{feeSummary.totalFee}</h4>
            </div>
            <div className="col-12 d-flex justify-content-center">
              <button type="submit" className="btn btn-outline-primary btn-lg custom-button">
                Submit
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default FeeSubmission;
