import React from 'react';
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import {Button} from "semantic-ui-react";

function DialogDisableEnvironment({deleteHandler, resourceName, open, setOpen, setIsActive, enabledToggleCount}) {
    const handleClose = () => {
        setOpen(false);
    };

    const handleOk = () => {
        deleteHandler()
        setIsActive(prevState => !prevState)
        handleClose()
    }

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">
                Warning: Disable environment!
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    There are currently {enabledToggleCount} active toggles in specified environment.
                    <br/>Are you sure you want to disable {resourceName}?
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} className={"reverse-btn"}>Cancel</Button>
                <Button onClick={handleOk}>Disable</Button>
            </DialogActions>
        </Dialog>
    );
}

export default DialogDisableEnvironment;