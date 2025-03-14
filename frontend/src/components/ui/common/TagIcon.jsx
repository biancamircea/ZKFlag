import React from 'react';
import Tooltip from "@mui/material/Tooltip";

function TagIcon({handleClick}) {
    return (
        <div className={"action-icon-wrapper"} onClick={handleClick}>
            <Tooltip title={"Add tag"} arrow>
                <img src={"/images/tag.png"}
                     alt={"Add tag"}
                     className={"action-icon"}
                />
            </Tooltip>
        </div>
    );
}

export default TagIcon;