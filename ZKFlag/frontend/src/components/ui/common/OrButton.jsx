import React from "react";
import Tooltip from "@mui/material/Tooltip";

function OrButton(){
    return (
    <Tooltip arrow>
        <div className="or-button"
             style={{
                 backgroundColor: "#c4cfe8",
                 height: "30px",
                 width: "50px",
                 display: "flex",
                 justifyContent: "center",
                 alignItems: "center",
                 borderRadius: "8px",
             }}
        >
            OR
        </div>
    </Tooltip>);
}

export default OrButton;