import React from 'react';
import {Link} from "react-router-dom";
import Tooltip from "@mui/material/Tooltip";

function EditIcon({id, directLink}) {
    return (
        <Link to={directLink ? directLink : `edit/${id}`}
              className={"action-icon-wrapper"}
              onClick={event => event.stopPropagation()}
        >
            <Tooltip title={"Edit"} arrow>
                <img src={"/images/edit.png"}
                     alt={"Edit"}
                     className={"action-icon"}/>
            </Tooltip>
        </Link>
    );
}

export default EditIcon;