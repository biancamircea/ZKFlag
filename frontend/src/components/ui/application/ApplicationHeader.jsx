import React from 'react';
import DeleteBtnDialog from "../common/DeleteBtnDialog.jsx";

function ApplicationHeader({name, deleteHandler}) {
    return (
        <div className={"page-header-container application"}>
            <div className={"name-and-icon"}>
                <img src={"/images/application.png"}
                     alt={"Application"}
                     className={"px45-icon"}
                />
                <h2>{name}</h2>
            </div>
            <DeleteBtnDialog
                deleteHandler={deleteHandler}
                resource={"application"}
                resourceName={name}
            />
        </div>
    );
}

export default ApplicationHeader;