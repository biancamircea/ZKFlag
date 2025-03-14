import React from 'react';
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";

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
            <button
                className={"center-aligned-button"}
                onClick={handleClickOpen}>
                + Add constraint
            </button>
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