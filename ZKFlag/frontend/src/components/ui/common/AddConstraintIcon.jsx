import React from 'react';
import Tooltip from "@mui/material/Tooltip";

function AddConstraintIcon({ onClick,text }) {
    return (
        <div
            onClick={(event) => {
                event.stopPropagation();
                onClick();
            }}
            style={{display:"flex",justifyContent:"center"}}>
            <Tooltip title={text || "Add an AND constraint"} arrow>
                <img src={"/images/add_constraint.png"}
                     alt={"Add"}
                     className={"small-icon"} />
            </Tooltip>
        </div>
    );
}

export default AddConstraintIcon;
