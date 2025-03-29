import React from 'react';
import Tooltip from "@mui/material/Tooltip";

function AddConstraintIcon({ onClick }) {
    return (
        <div
            onClick={(event) => {
                event.stopPropagation();
                onClick();
            }}
            className={"action-icon-wrapper"}>
            <Tooltip title={"Add constraint"} arrow>
                <img src={"/images/add-constraint.webp"}
                     alt={"Add"}
                     className={"action-icon"} />
            </Tooltip>
        </div>
    );
}

export default AddConstraintIcon;
