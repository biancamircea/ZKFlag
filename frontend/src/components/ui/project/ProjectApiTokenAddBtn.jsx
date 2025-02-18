import React from 'react';
import ProjectApiTokenAddDialog from "./ProjectApiTokenAddDialog.jsx";

function ProjectApiTokenAddBtn({submitHandler, tokensName, environments, projectId, instanceId}) {
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
                className={"center-aligned-button top-margin"}
                onClick={handleClickOpen}
            >
                + Generate token
            </button>
            <ProjectApiTokenAddDialog
                open={open}
                onClose={handleClose}
                tokensName={tokensName}
                submitHandler={submitHandler}
                environments={environments}
                projectId={projectId}
                instanceId={instanceId}
            />
        </>
    );
}

export default ProjectApiTokenAddBtn;