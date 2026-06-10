import React, { useCallback, useContext, useEffect, useState } from "react";
import DashBoardCard from "../../DashBoardCard/DashBoardCard";
import AuthContext from "../../../../context/student/AuthContext";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD";
import { AddStructure } from "../../../../apis/Fee_operation/fee_operation";
import { useNavigate } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import NotifyMsg from "../../../Academics/NotifyMsg";

const RegisterStructure = () => {
  const [classList, setClassList] = useState([]);
  const { token } = useContext(AuthContext);
  const [notificationMsg, setMsg] = useState(null); //Msg content
  const [typeOfMsg, setMsgType] = useState();
  const navigate = useNavigate();

  const [feeStructure, setFeeStructure] = useState({
    className: "",
    category: "",
    feeAmount: "",
    remark: "",
    effectiveDate: "",
    expiryDate: "",
    paymentFrequency: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFeeStructure((prevState) => ({
      ...prevState,
      [name]: name === "feeAmount" ? parseInt(value, 10) : value,
    }));
  };

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      setClassList(responseData.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [token]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await AddStructure(feeStructure);
    if (data.status === 200) {
      setMsg("Structure Added successfully");
      setMsgType("success");
      setTimeout(()=>{
        navigate("/Accounting/fee-structure");
      })
    } else {
      setMsg("Internal Server Error");
      setMsgType("error");
    }
  };

  return (
    <>
      <div
        className="container dashboard-form-page"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          entity={"Add structure"}
          title={"Add Fee Structure"}
          visibility="hidden"
        />
        <div className="structForm">
          <div className="dashboard-form-header">
            <span className="dashboard-form-eyebrow">Fee Setup</span>
            <h3 className="dashboard-form-title">Add Fee Structure</h3>
            <p className="dashboard-form-subtitle">
              Define class-based fee structures with dates, category, and payment frequency.
            </p>
          </div>
          <form
            className="row g-3 feeForm needs-validation"
            onSubmit={handleSubmit}
          >
            <div className="col-md-6">
              <div className="form-group">
                <label className="labelStyle">Select Class</label>
                <select
                  className="form-control"
                  id="dropdown"
                  name="className"
                  aria-label="Class select"
                  onChange={handleChange}
	                  value={feeStructure.className}
	                  required
	                >
	                  <option disabled value="">
	                    Select class...
	                  </option>
	                  {classList.map((result, index) => (
	                    <option key={result?.id ?? result?.className ?? index} value={result.className}>{result.className}</option>
	                  ))}
	                </select>
              </div>
            </div>
            {/* Fee Amount */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Fee Amount</label>
                <input
                  type="number"
                  className="form-control"
                  name="feeAmount"
                  value={feeStructure.feeAmount || ""}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Category */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Category</label>
                <input
                  type="text"
                  className="form-control"
                  name="category"
                  value={feeStructure.category || ""}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Remark */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Remark</label>
                <input
                  type="text"
                  className="form-control"
                  name="remark"
                  value={feeStructure.remark || ""}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Effective Date */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Effective Date</label>
                <input
                  type="date"
                  className="form-control"
                  name="effectiveDate"
                  value={feeStructure.effectiveDate || ""}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Expiry Date */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">Expiry Date</label>
                <input
                  type="date"
                  className="form-control"
                  name="expiryDate"
                  value={feeStructure.expiryDate || ""}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Payment Frequency */}
            <div className="col-md-6">
              <div className="form-group">
                <label className="form-label labelStyle">
                  Payment Frequency
                </label>
                <select
                  className="form-control"
                  name="paymentFrequency"
                  value={feeStructure.paymentFrequency || ""}
                  onChange={handleChange}
                  required
                >
                  <option value="">
                    Select payment frequency...
                  </option>
                  <option value="monthly">Monthly</option>
                  <option value="quarterly">Quarterly</option>
                  <option value="yearly">Yearly</option>
                </select>
              </div>
            </div>
            <div className="col-12 d-flex justify-content-center">
              <button
                type="submit"
                className="btn btn-outline-primary btn-lg custom-button"
              >
                Submit
              </button>
            </div>
          </form>
        </div>
      </div>
      <NotifyMsg msg={notificationMsg} type={typeOfMsg} />
      <ToastContainer />
    </>
  );
};

export default RegisterStructure;
