import React, { useCallback, useContext, useEffect, useState } from 'react';
import DashBoardCard from '../../Dashboard/DashBoardCard/DashBoardCard';
import CustomLoadingBar from '../LoadingBar/Index';
import { GetAll, UpdateEnitity } from '../../../apis/Academic CRUD/AC_CRUD';
import DeleteConfirmation from '../../Dashboard/content/deleteModal';
import ListComponent from '../ListComponent';
import AcademicUpdate from '../UpdateBody';
import AuthContext from '../../../context/student/AuthContext';
import NotifyMsg from '../NotifyMsg';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';
import PropTypes from 'prop-types';
import DropDownComponent from '../DropDown';


const TableComponent = ({
    entityName,
    isDashBoard,
    title,
    isClass,
    isSection,
    valueSet,
    inputConfigs,
    onInputChange,
    idName,
    titleList,
    valueList

}) => {

    const { token } = useContext(AuthContext);
    const [notificationMessage, setMsg] = useState(null);

    const [updating, setUpdating] = useState(false)


    const [isLoading, setIsLoading] = useState(true);
    const [selectName, setSelectName] = useState(null);
    const [modalShow, setModalShow] = useState(false);
    const [updateShow, setShow] = useState(false);


    const [entity, setEntity] = useState(null);
    const [entityList, setEntityList] = useState([]);

    const fetchData = useCallback(async () => {
        try {
            const data = await GetAll(entityName, token);
    
                setEntityList(data.data);

            setIsLoading(false);
        } catch (error) {
            console.error('Error fetching data:', error);
            setIsLoading(false);

        }
    }, [entityName, token]);

    useEffect(() => {
        if(!isClass)
            fetchData();
    }, [fetchData, isClass]);
    

    const DeleteEntity = (result) => {
        setModalShow(true);
        setEntity(result[idName]);
        setSelectName("Delete");
    }

    const UpdateEntity = async (result) => {
        setShow(true);
        setUpdating(false);
        setEntity(result[idName]);
    }

    const onSubmit = async (e) => {
        setUpdating(true)
        await UpdateEnitity(entity, valueSet, entityName, token);
        setMsg(`${title} updated Successfully `);
        setShow(false);
        fetchData();
        
        setTimeout(() => {
            setMsg(null);
        }, 3000);
    }

    return (
        <>

            <div className="container" style={{ backgroundColor: "#F4F5F9", maxWidth: "1424px" }}>
                <CustomLoadingBar isLoading={isLoading} />

                {
                    isDashBoard && <DashBoardCard
                    entity={entityName}
                    title={title}
                    buttonValue={`Add a ${title}`}
                    visibility="visible"
                    details={valueSet}
                    inputConfigs={inputConfigs}
                    fetchData={fetchData}
                />
                }
                
                <div className="card bg-white shadow rounded-lg overflow-hidden listContainer">

                    {
                        isClass &&
                        <DropDownComponent
                        handleOnClick={fetchData}
                        isSection={isSection}
                    /> 
                    
                    }

                    {!isLoading && <ListComponent
                        entityList={entityList}
                        titleList={titleList}
                        valueList={valueList}
                        DeleteEntity={DeleteEntity}
                        UpdateEntity={UpdateEntity}
                    />}
                </div>

                <AcademicUpdate
                    title={title}
                    show={updateShow}
                    onHide={() => {
                        setShow(false);
                    }}
                    inputConfigs={inputConfigs}
                    onSubmit={onSubmit}
                    isupdating={updating}
                />


                {
                    selectName === "Delete" && (
                        <DeleteConfirmation userName={entity} 
                        accountType={entityName} 
                        moduleName="academics" show={modalShow} onHide={() => {
                            setModalShow(false);
                            fetchData();
                        }} />
                    )
                }



            </div>
            <NotifyMsg msg={notificationMessage} />
            <ToastContainer
            />

        </>
    )
}

TableComponent.prototype = {

    entityName: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    isClass: PropTypes.bool.isRequired,
    isSection: PropTypes.bool.isRequired,
    valueSet: PropTypes.arrayOf(PropTypes.string).isRequired,
    inputConfigs: PropTypes.arrayOf(PropTypes.string).isRequired,
    onInputChange: PropTypes.func.isRequired,
    idName: PropTypes.string.isRequired,
    titleList: PropTypes.arrayOf(PropTypes.string).isRequired,
    valueList: PropTypes.arrayOf(PropTypes.string).isRequired,

}

TableComponent.defaultProps = {
    entityName: "Academic Entity",
    title: "Entity",
    isClass: false,
    isSection: false,
    valueSet: null,
    inputConfigs: [],
    onInputChange: null,
    idName: "id",
    titleList: [],
    isDashBoard:true,
    valueList: [],
}

export default TableComponent;
