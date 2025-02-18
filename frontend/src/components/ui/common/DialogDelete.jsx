import React from 'react';
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import {Button} from "semantic-ui-react";

function DialogDelete({deleteHandler, resource, resourceName, open, setOpen}) {
    const handleClose = () => {
        setOpen(false);
    };

    const handleOk = () => {
        deleteHandler()
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
                Delete {resource}
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    Are you sure you want to delete {resourceName}?
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} className={"reverse-btn"}>Cancel</Button>
                <Button onClick={handleOk}>Ok</Button>
            </DialogActions>
        </Dialog>
    );
}

export default DialogDelete;