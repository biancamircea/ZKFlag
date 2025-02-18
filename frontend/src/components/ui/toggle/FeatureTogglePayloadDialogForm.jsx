import React from 'react';
import {toast} from "react-toastify";
import {
    Dialog, DialogActions,
    DialogContent,
    DialogTitle,
} from "@mui/material";
import {Form} from "react-router-dom";

function FeatureTogglePayloadDialogForm({onClose, open, defaultValueOn, defaultValueOff,  submitHandler, edit}) {
    const handleClose = () => {
        onClose();
    };

    function handleSubmit(event) {
        event.preventDefault()
        const formData = new FormData(event.target);
        const enabledValue = formData.get("valueOn");
        const disabledValue = formData.get("valueOff");

        if(!enabledValue || !disabledValue){
            toast.error("Fields can't be empty!")
        } else {
            submitHandler({enabledValue, disabledValue})
            handleClose()
        }
    }
    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>{edit ? "Edit payload" : "Add payload"}</DialogTitle>
            <Form
                method={"post"}
                onSubmit={handleSubmit}
            >
                <DialogContent>
                    <div className={"create-form-fields"}>
                        <div className={"create-form-field-item"}>
                            <label htmlFor={"valueOn"}>Value when toggle is enabled:</label>
                            <input
                                id={"valueOn"}
                                name={"valueOn"}
                                type={"text"}
                                placeholder={"ON value"}
                                defaultValue={defaultValueOn}
                            />
                        </div>
                        <div className={"create-form-field-item"}>
                            <label htmlFor={"valueOff"}>Value when toggle is disabled:</label>
                            <input
                                id={"valueOff"}
                                name={"valueOff"}
                                type={"text"}
                                placeholder={"OFF value"}
                                defaultValue={defaultValueOff}
                            />
                        </div>
                    </div>
                </DialogContent>
                <DialogActions>
                    <button
                        className={"reverse-btn"}
                        onClick={event => {
                            event.preventDefault()
                            handleClose()
                        }}>
                        Cancel
                    </button>
                    <button
                        type={"submit"}>
                        Save
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default FeatureTogglePayloadDialogForm;