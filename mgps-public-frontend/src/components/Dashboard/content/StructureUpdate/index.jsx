import React, { useCallback, useContext, useEffect, useState } from "react";
import AuthContext from "../../../../context/student/AuthContext";
import { GetAll } from "../../../../apis/Academic CRUD/AC_CRUD";
import { Modal } from "react-bootstrap";
import { IoIosCloseCircleOutline } from "react-icons/io";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Bounce } from 'react-toastify';
import { UpdateStructureByID } from "../../../../apis/Fee_operation/fee_operation";

const UpdateStructure = ({ structureID, show, onHide }) => {  // Destructure props here
  const initialFeeStructure = {
    className: "",
    category: "",
    feeAmount: "",
    remark: "",
    effectiveDate: "",
    expiryDate: "",
    paymentFrequency: "",
  };

  const [formData, setFormData] = useState(initialFeeStructure);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: name === "feeAmount" ? parseInt(value, 10) : value,
    }));
  };

  const [classList, setClassList] = useState([]);
  const { token } = useContext(AuthContext);

  const notify = (msgtype, msg) => {
    toast[msgtype](msg, {
      position: "top-right",
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
      transition: Bounce
    });
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

  const onSubmit = async (e) => {
    e.preventDefault();
    console.log(formData);

    const data = await UpdateStructureByID(structureID, formData);
    if (data.status === 200) {
          onHide();
          notify("success", "Structure Updated Successfully");
  
    }
        else {
          onHide();
          notify("error", "Something wrong")
        }
  };

  return (
    <>
      <Modal
        show={show}
        onHide={onHide}
        animation={true}
        backdrop="static"
        keyboard={false}
        centered
        className="custom-modal-width custom-modal-rounded"
      >
        <Modal.Header className="d-flex justify-content-between align-items-center">
          <Modal.Title>Update Student Details</Modal.Title>
          <IoIosCloseCircleOutline
            color="red"
            size={40}
            style={{ cursor: "pointer" }}
            onClick={onHide}
          />
        </Modal.Header>
        <Modal.Body>
          <form onSubmit={onSubmit}>
            <div className="form-row">
              <div className="form-group col-md-6">
                <h5 className="text-left">Class</h5>
	                <select
	                  className="form-control"
	                  id="inputDado"
	                  name="className"
	                  value={formData.className}
	                  onChange={handleChange}
	                  required
	                >
	                  <option value="">Select class...</option>
	                  {classList.map((result, index) => (
	                    <option key={index} value={result.className}>{result.className}</option>
	                  ))}
	                </select>
              </div>

              <div className="form-group col-md-6">
                <label htmlFor="inputAddress">Category</label>
                <input
                  required
                  type="text"
	                  className="form-control"
	                  id="inputDado"
	                  name="category"
	                  placeholder="Enter your address"
	                  value={formData.category}
	                  onChange={handleChange}
	                />
              </div>

              <div className="form-group col-md-6">
                <label htmlFor="inputAddress">Fee Amount</label>
                <input
                  required
                  type="number"
	                  className="form-control"
	                  id="inputDado"
	                  name="feeAmount"
	                  placeholder="Enter your fee Amount"
	                  value={formData.feeAmount}
	                  onChange={handleChange}
	                />
              </div>

              <div className="form-group col-md-6">
                <label htmlFor="inputAddress">Remark</label>
                <input
                  required
                  type="text"
	                  className="form-control"
	                  id="inputDado"
	                  name="remark"
	                  placeholder="Enter remark"
	                  value={formData.remark}
	                  onChange={handleChange}
	                />
              </div>

              <div className="form-group col-md-6">
                <label htmlFor="inputAddress">Effective date</label>
                <input
                  required
                  type="date"
                  className="form-control"
	                  id="inputDado"
	                  name="effectiveDate"
	                  placeholder="Enter effective date"
	                  value={formData.effectiveDate}
	                  onChange={handleChange}
	                />
              </div>

              <div className="form-group col-md-6">
                <label htmlFor="inputAddress">Expire Date</label>
                <input
                  required
                  type="date"
                  className="form-control"
	                  id="inputDado"
	                  name="expiryDate"
	                  placeholder="Enter expiry date"
	                  value={formData.expiryDate}
	                  onChange={handleChange}
	                />
              </div>

              <div className="col-md-6">
                <div className="form-group">
                  <label htmlFor="inputAddress">Payment Frequency</label>
                  <select
                    className="form-control"
                    id="inputDado"
                    name="paymentFrequency"
	                    value={formData.paymentFrequency || ""}
	                    onChange={handleChange}
	                    required
	                  >
	                    <option value="">Select payment frequency...</option>
	                    <option value="monthly">Monthly</option>
	                    <option value="quarterly">Quarterly</option>
	                    <option value="yearly">Yearly</option>
	                  </select>
                </div>
              </div>
            </div>
            <div className="button-container">
              <button
                className="btn btn-lg btn-outline-secondary"
                onClick={onHide}
              >
                Close
              </button>
              <button type="submit" className="btn btn-lg btn-secondary">
                Update
              </button>
            </div>
          </form>
        </Modal.Body>
        <Modal.Footer></Modal.Footer>
      </Modal>

      <ToastContainer />
    </>
  );
};

export default UpdateStructure;
