import React from 'react';
import DialogDelete from "./DialogDelete.jsx";

function DeleteAllConstraintsBtn({deleteHandler}) {
    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    return (
        <>
            <button
                className={"delete-all-constraint"}
                onClick={event => {
                    event.stopPropagation()
                    handleClickOpen()
                }}
            >
                Delete all
            </button>
            <DialogDelete
                deleteHandler={deleteHandler}
                resource={"ALL Constraints!"}
                resourceName={"the entire list of constraints for this toggle"}
                open={open}
                setOpen={setOpen}
            />
        </>
    );
}

export default DeleteAllConstraintsBtn;