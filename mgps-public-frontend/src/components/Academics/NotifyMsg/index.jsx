import { useEffect } from 'react';
import { toast } from 'react-toastify';

import 'react-toastify/dist/ReactToastify.css';
import { Bounce } from 'react-toastify';

const NotifyMsg = (props) => {
    useEffect(() => {
        const notify = () => {
            toast[props.type](props.msg, {
                position: "top-right",
                autoClose: props.type === "danger" ? 5000 : 3000, 
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
                transition: Bounce
            });
        };

        notify(); 
    }, [props.msg, props.type]); 

    return null; 
};

NotifyMsg.defaultProps = {
    type: "success",
  };
  

export default NotifyMsg;
