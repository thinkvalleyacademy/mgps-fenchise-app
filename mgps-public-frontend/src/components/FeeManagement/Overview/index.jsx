import React from 'react';
import DashBoardCard from '../../Dashboard/DashBoardCard/DashBoardCard';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'
import OverViewCard from './OverviewCard';

ChartJS.register(ArcElement, Tooltip, Legend)

const OverView = () => {
    const chartData = {
        labels: [
          'Uniform Fee Boys',
          'Sports Fee',
          'ENRICHMENT FEE',
          'Term 1',
          'Term 2',
          'Term 3',
          'Admission fee',
          'January Transport Instalment',
          'February Transport Instalment',
        ],
        datasets: [
          {
            data: [12, 11, 10, 9, 8, 7, 6, 5, 4],
            backgroundColor: [
              '#8B5CF6',
              '#7C3AED',
              '#6D28D9',
              '#5B21B6',
              '#4C1D95',
              '#6D28D9',
              '#7C3AED',
              '#8B5CF6',
              '#9333EA',
            ],
            borderWidth: 0,
          },
        ],
      }
    
      return (
        <div
        className="container-fluid p-3"
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          entity={"Invoice"}
          title={"Fee Summary"}
        />
        <div style={{marginLeft:"10px"}}>
        <div className="row g-3 mb-3">
            
            <OverViewCard heading={"Total Payments Collected"} data={"787,150.00"} />
            <OverViewCard heading={"Total's collection"} data={"0.00"} />

            <OverViewCard heading={"Total due pending "} data={"550,439.00"} />

            <OverViewCard heading={"Total Receivables"} data={"1,550,439.00"} />
          </div>
    
          <div className="row g-3 mb-3">

          <OverViewCard heading={"Total fine"} data={"12,439.00"} />

          <OverViewCard heading={"Total Concession"} data={"126,439.00"} />

          </div>
    
          <div className="row g-3">
            <div className="col-md-6">
              <div className="card shadow-sm">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <h5 className="card-title mb-0">Current Summary</h5>
                    <select className="form-select w-auto">
                      <option>Dues</option>
                    </select>
                  </div>
                  <div style={{ height: '300px' }}>
                    <Doughnut
                      data={chartData}
                      options={{
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                          legend: {
                            position: 'bottom',
                            labels: {
                              boxWidth: 12,
                            },
                          },
                        },
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>
            <div className="col-md-6">
              <div className="card shadow-sm">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <h5 className="card-title mb-0">Today's Summary</h5>
                    <select className="form-select w-auto">
                      <option>Collected By Fee Type</option>
                    </select>
                  </div>
                  <div className="d-flex justify-content-center align-items-center" style={{ height: '300px' }}>
                    <p className="text-muted mb-0">No Data Found</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
         
        </div>
      )
    }

export default OverView
