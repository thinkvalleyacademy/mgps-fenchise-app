import React, { useState, useEffect } from 'react';
import LoadingBar from 'react-top-loading-bar';

const CustomLoadingBar = ({ isLoading }) => {
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        if (isLoading) {
            setProgress(20); 
        } else {
            setProgress(100); 
        }
    }, [isLoading]);

    return (
        <LoadingBar color="#4377E6" width="90%"  progress={progress} />
    );
};

export default CustomLoadingBar;
