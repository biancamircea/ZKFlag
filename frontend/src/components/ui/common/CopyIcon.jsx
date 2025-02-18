import React from 'react';
import Tooltip from "@mui/material/Tooltip";

function CopyIcon({onClickHandler}) {
    return (
        <div
            onClick={(event) => {
                event.stopPropagation()
                onClickHandler()
            }}
            className={"action-icon-wrapper"}>
            <Tooltip title={"Copy token"} arrow>
                <img src={"/src/assets/images/copy.png"}
                     alt={"Copy"}
                     className={"action-icon"}
                />
            </Tooltip>
        </div>
    );
}

export default CopyIcon;