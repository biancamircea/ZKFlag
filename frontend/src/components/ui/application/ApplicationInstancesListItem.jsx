import React from 'react';
import DeleteIconNoDialog from "../common/DeleteIconNoDialog.jsx";

function ApplicationInstancesListItem({name, startedAt, deleteHandler}) {
    return (
        <div
            className={"list-item item-body instances"}
            onClick={() => onClickHandler(id)}
        >
            <div className={"list-item item-body icon"}>
                <img src={"/src/assets/images/instance.png"}
                     alt={"Application"}
                     className={"px45-icon"}
                />
            </div>
            <div className={"name-and-description"}>
                <p>{name}</p>
                <span className={"gray-text"}>Last seen at: { new Date(startedAt).toLocaleString("ro")}</span>
            </div>
            <DeleteIconNoDialog
                deleteHandler={deleteHandler}
            />
        </div>
    );
}

export default ApplicationInstancesListItem;