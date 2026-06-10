import React, { useContext, useState } from 'react';
import { Modal, Button } from "react-bootstrap";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Bounce } from 'react-toastify';
import { DeleteUser } from '../../../../apis/CRUD_operation/operation';
import AuthContext from '../../../../context/student/AuthContext';
import { Delete } from '../../../../apis/Academic CRUD/AC_CRUD';
import { DeleteStructure } from '../../../../apis/Fee_operation/fee_operation';

const DeleteConfirmation = ({ moduleName, registrationNumber, accountType, show, onHide, idName }) => {

    const { token } = useContext(AuthContext);
    const [isDeleting, setIsDeleting] = useState(false);

    // Debug log to verify props
    console.log("DeleteConfirmation Props:", { moduleName, registrationNumber, accountType, idName });

    const capitalizeFirstLetter = (string) => {
        if (typeof string !== 'string') {
            
            return null;
        }
    
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    const handleDelete = async () => {
        console.log("Data object:", { registrationNumber, moduleName }); // Debug log
        if (!registrationNumber || !moduleName) {
            console.error("Missing registrationNumber or moduleName");
            return;
        }

        try {
            setIsDeleting(true);
            if (moduleName === "academics") {
                await Delete(registrationNumber, accountType, token);
                notify("success", `${accountType} Deleted Successfully`);
            } else if (moduleName === "fee") {
                const response = await DeleteStructure(idName);
                if (response.status === 200) {
                    notify("success", `Structure Deleted Successfully`);
                } else {
                    notify("error", `Internal Server error`);
                }
            } else {
                const data = await DeleteUser(accountType, registrationNumber, token);
                if (data.data === `${capitalizeFirstLetter(accountType)} not found`) {
                    notify("error", "User not found");
                } else {
                    notify("success", "User Deleted Successfully");
                }
            }
        } catch (error) {
            console.error("Error during deletion:", error);
        } finally {
            setIsDeleting(false);
        }
    };

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

    return (
        <>
            <Modal
                show={show}
                onHide={onHide}
                size="lg"
                aria-labelledby="contained-modal-title-vcenter"
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-vcenter">
                        Delete Confirmation
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>
                        Are you sure you want to delete ?
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>
                        Close
                    </Button>
                    <Button variant="danger" 
                    disabled={isDeleting}
                    onClick={async () => {
                        await handleDelete();
                        onHide();
                    }}
                    >
                        {isDeleting ? 'Deleting...' : 'Delete'}
                    </Button>
                </Modal.Footer>
            </Modal>
            <ToastContainer
            />
        </>
    );
}

export default DeleteConfirmation;
