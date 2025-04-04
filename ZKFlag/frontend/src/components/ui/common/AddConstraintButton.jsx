import React from 'react';
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";
import Tooltip from "@mui/material/Tooltip";

function AddConstraintButton({submitHandler}) {
    const { contextFields } = useOutletContext()
    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    const handleClose = (event, reason) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    return (
        <>
            <Tooltip title="Add a group of constraints" arrow>
                <button
                    className={"center-aligned-button"}
                    onClick={handleClickOpen}>
                    + Add constraint
                </button>
            </Tooltip>
            <FeatureToggleAddConstraintDialog
                open={open}
                onClose={handleClose}
                contextFields={contextFields}
                submitHandler={submitHandler}
            />
        </>
    );
}

export default AddConstraintButton;