import React from 'react';
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";
import AddConstraintIcon from "./AddConstraintIcon.jsx";

function AddConstraintToGroupButton({ submitHandler, groupId }) {
    const { contextFields } = useOutletContext();
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
            <AddConstraintIcon onClick={() => setOpen(true)} />

            <FeatureToggleAddConstraintDialog
                open={open}
                onClose={handleClose}
                contextFields={contextFields}
                submitHandler={(data) => submitHandler(data, groupId)}
            />
        </>
    );
}

export default AddConstraintToGroupButton;