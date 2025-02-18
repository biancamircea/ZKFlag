import React from 'react';
import FeatureTag from "./FeatureTag.jsx";
import Tooltip from "@mui/material/Tooltip";

function DeleteFeatureTag({label, color, deleteHandler}) {
    return (
        <div
            style={{background: `${color}`}}
             className={"feature-tag delete"}
        >
            <div className={"label"}>
                {label}
            </div>
            <Tooltip title={"remove tag"} arrow>

                <div
                    className={"delete-button"}
                    onClick={deleteHandler}
                >
                    X
                </div>
            </Tooltip>
        </div>
    );
}

export default DeleteFeatureTag;