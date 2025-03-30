import React from "react";
import Tooltip from "@mui/material/Tooltip";

function AndButton(){
    return ( <Tooltip arrow>
        <div className="and-button"
             style={{
                 backgroundColor: "#c4cfe8",
                 display: "flex",
                 justifyContent: "center",
                 alignItems: "center",
                 height: "30px",
                 width: "60px",
                 borderRadius: "8px",
                 textAlign: "center",
                 marginLeft: "0px",
                 alignSelf: "flex-start"
             }}
        >
            AND
        </div>


    </Tooltip>);
}

export default AndButton;