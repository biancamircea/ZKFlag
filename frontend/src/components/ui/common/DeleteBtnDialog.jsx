import React from 'react';
import DialogDelete from "./DialogDelete.jsx";

function DeleteBtnDialog({btnLabel, deleteHandler, resource, resourceName, style}) {
    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    return (
        <>
            <button
                onClick={(event) => {
                    event.stopPropagation()
                    handleClickOpen()
                }}
                style={style}
            >
                {btnLabel ? btnLabel : "Delete"}
            </button>
            <div onClick={event => event.stopPropagation()}>
                <DialogDelete
                    deleteHandler={deleteHandler}
                    resource={resource}
                    resourceName={resourceName}
                    open={open}
                    setOpen={setOpen}
                />
            </div>
        </>
    );
}

export default DeleteBtnDialog;