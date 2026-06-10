import { Calculator, HandCoins, Receipt, FileX, Coins, BarChart3 } from 'lucide-react';
import React from 'react';
import './style.css'
import { useNavigate } from 'react-router-dom';

import DashBoardCard from '../../../Dashboard/DashBoardCard/DashBoardCard';
const FeeCollectionHome = () => {
  const navigate = useNavigate();

    const reports = [
        {
          title: "Collection Report",
          icon: <Calculator className="h-8 w-8" />,
          color: "purple",
          route:"CollectionReport"
        },
        {
          title: "Payment History",
          icon: <HandCoins className="h-8 w-8" />,
          color: "#f79009",
          route:"PaymentHistory"

        },
        {
          title: "Paid and Due Report",
          icon: <Receipt className="h-8 w-8" />,
          color: "skyblue",
          route:"CollectionReport"

        },
        {
          title: "Cancellation Report",
          icon: <FileX className="h-8 w-8" />,
          color: "red",
          route:"CollectionReport"

        },
        {
          title: "Concession Report",
          icon: <Coins className="h-8 w-8" />,
          color: "green",
          route:"CollectionReport"

        },
        {
          title: "Fine Report",
          icon: <BarChart3 className="h-8 w-8" />,
          color: "orange",
          route:"CollectionReport"

        },
      ];

      const handleRowClick = (routeName) => {
        navigate(`/Accounting/${routeName}`);
      };
    
  return (
    <>
      <div className="container-fluid  my-5">
      <DashBoardCard title="Fee Reports" />

     
      <div className="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
        {reports.map((report) => (
          <div key={report.title} className="col">
            <div className="card shadow-sm border-0">
              <div className="card-body d-flex flex-column align-items-center text-center">
                <div className={`p-4 rounded-circle ${report.color}`} style={{backgroundColor:`${report.color}`}}>
                {React.cloneElement(report.icon, { style: { width: '64px', height: '64px', color:'white' } })}
                </div>
                <h5 className="mt-3 mb-2">{report.title}</h5>
                <button
                type="submit"
                onClick={()=>handleRowClick(report.route)}
                className="btn btn-outline-primary btn-md custom-button"
              >
                View
              </button>

              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
    </>
  )
}

export default FeeCollectionHome
