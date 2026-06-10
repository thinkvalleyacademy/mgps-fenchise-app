import React from 'react';
import DashBoardCard from '../../../Dashboard/DashBoardCard/DashBoardCard';
import { ArrowDownUp, Filter} from 'lucide-react'
import { useNavigate } from 'react-router-dom';


const FeeDetailStudent = () => {
  const navigate = useNavigate();
  const students = [
      {
        id: 1,
        name: "Samuel",
        className: "Class - 3",
        feesCollected: 38760.00,
        feesReceivable: 100.00,
        dueAmount: 0.00
      },
      {
        id: 2,
        name: "Senthil Kumar Saravanan Annamalai",
        className: "Class - 3", 
        feesCollected: 18000.00,
        feesReceivable: 19340.00,
        dueAmount: 0.00
      },
      {
        id: 3,
        name: "SHIBAN MOHAMMED V T",
        className: "Class - 3",
        feesCollected: 10000.00,
        feesReceivable: 29070.00,
        dueAmount: 0.00
      },
      {
        id: 4,
        name: "Shiva",
        className: "Class - 3",
        feesCollected: 10130.00,
        feesReceivable: 28840.00,
        dueAmount: 0.00
      },
      {
        id: 5,
        name: "SOBIYA M",
        className: "Class - 3",
        feesCollected: 0.00,
        feesReceivable: 38970.00,
        dueAmount: 0.00
      }
    ]

    const handleOnClick = ()=>{
      navigate(`/Accounting/feeDetail/Students/Student`);
    }


  return (
    <div className="container-fluid p-4">      
    <DashBoardCard
          title="Fee Details"
        />
      
      <div className="card">
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-center mb-4">
             <div className="d-flex align-items-center">
               <h5 className="mb-0 me-2">Fee Details</h5>
             </div>
             <div className="d-flex gap-2">
               <select className="form-select" style={{ width: '200px' }}>
                 <option>5 items selected</option>
               </select>
               <button className="btn btn-primary">
                <Filter size={18} />
                Filter
              </button>
            </div>
          </div>

          <div className="table-responsive">
  <table className="table table-hover">
    <thead>
      <tr>
        <th>
          {/* Header checkbox for "Select All" */}
          <input
            type="checkbox"
            className="form-check-input"
            onChange={(e) => {
              const checkboxes = document.querySelectorAll('.row-checkbox');
              checkboxes.forEach((checkbox) => (checkbox.checked = e.target.checked));
            }}
          />
        </th>
        <th>Sl.No</th>
        <th>Student Name</th>
        <th>List Name</th>
        <th>
          Fees Collected
          <ArrowDownUp size={14} className="ms-1" />
        </th>
        <th>
          Fees Receivable
          <ArrowDownUp size={14} className="ms-1" />
        </th>
        <th>
          Due Amount
          <ArrowDownUp size={14} className="ms-1" />
        </th>
      </tr>
    </thead>
    <tbody>
      {students.map((student) => (
        <tr key={student.id}>
          <td>
            {/* Row-specific checkbox */}
            <input type="checkbox" className="form-check-input row-checkbox" />
          </td>
          <td>{student.id}</td>
          <td>
            <div
              className="d-flex align-items-center"
              onClick={() => handleOnClick()}
              style={{ cursor: "pointer" }}
            >
              <div
                className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-2"
                style={{ width: "32px", height: "32px" }}
              >
                S
              </div>
              <div>
                <div>{student.name}</div>
                <small className="text-muted">{student.className}</small>
              </div>
            </div>
          </td>
          <td>{student.className}</td>
          <td>{student.feesCollected.toFixed(2)}</td>
          <td>{student.feesReceivable.toFixed(2)}</td>
          <td>{student.dueAmount.toFixed(2)}</td>
        </tr>
      ))}
    </tbody>
  </table>
</div>

        </div>
      </div>
    </div>
  )
}

export default FeeDetailStudent
