import './style.css'
import React, { useEffect, useState } from "react";
import DashBoardCard from "../../DashBoardCard/DashBoardCard";
import { FaEdit, FaTrashAlt } from 'react-icons/fa';
import { GetAllStructure } from '../../../../apis/Fee_operation/fee_operation';
import DeleteConfirmation from '../deleteModal';
import UpdateStructure from '../StructureUpdate';


const FeeStructure = () => {

    const [Listdata, setLitsData]= useState([]);
    const [selectedItem, setSelectedItem] = useState(null);
    const [selectName, setSelectName] = useState(null);
    const [modalShow, setModalShow] = useState(false);
    const [show, setShow] = useState(false);
    const handleClose = () => {
      fetchData();
      setShow(false);
    }

    useEffect(() => {
      fetchData();
    }, []);
  
    const fetchData = async () => {
      try {
        const responseData = await GetAllStructure();
        setLitsData(responseData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    const handleItemClick = async (action, index) => {
      if (action === "Delete") {
        setModalShow(true);
        setSelectedItem(index);
        
        setSelectName("Delete");
      } else if (action === "Update") {
        setShow(true);
        setSelectedItem(index);
        setSelectName("Update");
      } else {
        setSelectedItem(null);
      }
    };


  return (
    <>
      <div
        className="container-fluid "
        style={{ backgroundColor: "#F4F5F9", maxWidth: "100%" }}
      >
        <DashBoardCard
          title="Fee Structure"
          buttonValue="Add a Structure"
          visibility="visible"
          itemClick=""
        />
        <div className="container collectionContainer">
          <div className="row">
            <div className="col-md-12">
              <h4 className="FeeCollectionHeading">List of Fee Structure</h4>
              <div className="table-responsive">
                <table id="mytable" className="table table-bordred table-striped ">
                  <thead className="bg-dark text-primary-foreground collectionTable">
                  <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Class Name</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Category</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Fee Amount</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Remark</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Effective Date</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Expiry Date</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Payment Frequency</th>
                   <th scope="col" className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Options</th>
                </tr>
                  </thead> 
                  <tbody className="px-6 py-4 whitespace-nowrap">
                {Listdata.map((item) => (
                  <tr key={item.feeStructureId} className="hover:bg-secondary hover:text-secondary-foreground transition-colors duration-200">
                    <td className="px-6 py-4 whitespace-nowrap">{item.className}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.category}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.feeAmount}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.remark}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.effectiveDate}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.expiryDate}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{item.paymentFrequency}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                <button
                  className="btn btn-info btn-sm me-2"
                 onClick={() => handleItemClick("Update", item.feeStructureId)}
                >
                 <FaEdit/>
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleItemClick("Delete", item.feeStructureId)}
                >
                 <FaTrashAlt/>
                </button>
              </td>
                  </tr>
                ))}
              </tbody>
                </table>

                
              </div>
            </div>
          </div>
        </div>
      </div>
      {
        selectName === "Delete" && (
          <DeleteConfirmation 
          idName={selectedItem} moduleName={"fee"} 
          
          show={modalShow} onHide={() => {
            setModalShow(false);
            fetchData();
          }} />
        )
      }
{
        selectName === "Update" && (
          <UpdateStructure 
          
          structureID={selectedItem} 
          show={show} 
          onHide={handleClose}/>
        )
      }

    </>
  );
};

export default FeeStructure;
