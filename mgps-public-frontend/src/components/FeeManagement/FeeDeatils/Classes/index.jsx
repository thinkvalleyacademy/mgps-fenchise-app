import React from 'react';
import './style.css'
import { useNavigate } from 'react-router-dom';

import DashBoardCard from '../../../Dashboard/DashBoardCard/DashBoardCard';
const FeeDetailClass = () => {
    const navigate = useNavigate();

  const feeData = [
    { id: 1, name: 'Class - 1', collected: '86,460.00', receivable: '79,730.00', due: '79,730.00 [5]' },
    { id: 2, name: 'Class - 2', collected: '134,240.00', receivable: '56,210.00', due: '56,210.00 [4]' },
    { id: 3, name: 'Class - 3', collected: '76,890.00', receivable: '116,320.00', due: '116,320.00 [5]' },
    { id: 4, name: 'Class - 4', collected: '93,730.00', receivable: '161,380.00', due: '161,380.00 [7]' },
    { id: 5, name: 'Class - 5', collected: '19,700.00', receivable: '184,040.00', due: '184,040.00 [5]' },
    { id: 6, name: 'Class - 6', collected: '26,220.00', receivable: '219,660.00', due: '219,660.00 [6]' },
    { id: 7, name: 'Class - 7', collected: '21,900.00', receivable: '186,950.00', due: '186,950.00 [5]' },
    { id: 8, name: 'Class - 8', collected: '165,510.00', receivable: '43,920.00', due: '43,920.00 [5]' },
    { id: 9, name: 'Class - 9', collected: '83,440.00', receivable: '11,170.00', due: '11,170.00 [4]' },
    { id: 10, name: 'Class - 10', collected: '36,500.00', receivable: '237,719.00', due: '237,719.00 [5]' },
  ];

  const handleRowClick = () => {
    navigate(`/Accounting/feeDetail/Students`);
  };

  return (
    <div className="container-fluid ">
      <DashBoardCard
          title="Fee Summary"
        />
     <div className="container collectionContainer">
          <div className="row">
            <div className="col-md-12">
              <div className="table-responsive">
                <table id="mytable" className="table table-bordred table-striped table-hover">

          <thead className="bg-dark text-primary-foreground collectionTable">
            <tr>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">SI#</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">List Name</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Fees Collected</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Fees Receivable</th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Due Amount</th>
            </tr>
          </thead>
          <tbody>
            {feeData.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td><p className='page-link-my' onClick={()=>handleRowClick()}>{item.name}</p></td>
                <td>{item.collected}</td>
                <td>{item.receivable}</td>
                <td>{item.due}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
          
      </div>
            </div>
          </div>

      <nav aria-label="Page navigation" className="mt-3">
        <ul className="pagination">
          <li className="page-item active"><p className="page-link">1</p></li>
          <li className="page-item"><p className="page-link" >2</p></li>
          <li className="page-item">
            <p className="page-link" aria-label="Next">
              <span aria-hidden="true">&raquo;</span>
            </p>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default FeeDetailClass;
