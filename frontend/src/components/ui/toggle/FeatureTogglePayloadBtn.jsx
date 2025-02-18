import React from 'react';
import FeatureTogglePayloadDialogForm from "./FeatureTogglePayloadDialogForm.jsx";

function FeatureTogglePayloadBtn({submitHandler, edit, defaultValueOn, defaultValueOff}) {
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
                onClick={handleClickOpen}>
                {
                    edit ? "Edit payload" : "+ Add payload"
                }
            </button>
            <FeatureTogglePayloadDialogForm
                open={open}
                onClose={handleClose}
                submitHandler={submitHandler}
                defaultValueOn={defaultValueOn}
                defaultValueOff={defaultValueOff}
                edit={edit}
            />

        </>
    );
}

export default FeatureTogglePayloadBtn;