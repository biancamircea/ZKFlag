import React from 'react';
import Tooltip from "@mui/material/Tooltip";
import DialogDelete from "./DialogDelete.jsx";

function DeleteIcon({deleteHandler, resource, resourceName}) {
    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    return (
        <>
            <div
                onClick={(event) => {
                    event.stopPropagation()
                    handleClickOpen()
                }}
                className={"action-icon-wrapper"}>
                <Tooltip title={"Delete"} arrow>
                    <img src={"/src/assets/images/delete.png"}
                         alt={"Delete"}
                         className={"action-icon"}
                    />
                </Tooltip>
            </div>
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

export default DeleteIcon;