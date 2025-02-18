import React from 'react';
import Tooltip from "@mui/material/Tooltip";

function DeleteIconNoDialog({deleteHandler}) {
    return (
        <div
            onClick={(event) => {
                event.stopPropagation()
                deleteHandler()
            }}
            className={"action-icon-wrapper"}>
            <Tooltip title={"Delete"} arrow>
                <img src={"/src/assets/images/delete.png"}
                     alt={"Delete"}
                     className={"action-icon"}

                />
            </Tooltip>
        </div>
    );
}

export default DeleteIconNoDialog;