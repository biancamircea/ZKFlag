import React from 'react';
import CircularProgress from "@mui/material/CircularProgress";

function LoadingBanner() {
    return (
        <div className={"loading-element"}>
            <h2>Loading...</h2>
            <CircularProgress/>
        </div>
    );
}

export default LoadingBanner;