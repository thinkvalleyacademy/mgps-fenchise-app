import React, { useEffect } from 'react';
import { toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const MsgToast = ({message,msgType}) => {

    useEffect(() => {
        console.log("Toast call");
        toast.success(message, {
            position: "top-right",
            autoClose: 3000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "light",
            transition: Bounce,
            });
      }, [message, msgType]);
    
      return ;

 
  
}

export default MsgToast
