import React from 'react';
import ProjectEnvironmentSwitch from "./ProjectEnvironmentSwitch.jsx";
import CopyIcon from "../common/CopyIcon.jsx";
import DeleteIconNoDialog from "../common/DeleteIconNoDialog.jsx";
import {toast} from "react-toastify";

function ProjectApiTokensListItem({name, environment, createdAt, secret, remove,type}) {

    const copyToClipboard = () => {
        navigator.clipboard.writeText(secret)
            .then(() => {
                toast.success("Api Token copied to clipboard.")
            })
            .catch((error) => {
                toast.error("Operation failed.")
            });
    };

    return (
        <div className={"tokens list-item"}>
            <p>{name}</p>
            <p>{environment}</p>
            <p>
                {type === 0 ? 'Frontend' :
                    type === 1 ? 'Backend' :
                        type === 2 ? 'Front and Back' :
                            'Unknown'}
            </p>
            <p>{
                createdAt
                    ? new Date(createdAt).toLocaleDateString("ro")
                    : "null"
            }</p>
            <div className={"tokens list-item-actions"}>
                <CopyIcon
                    onClickHandler={copyToClipboard}
                />
                <DeleteIconNoDialog
                    deleteHandler={remove}
                />
            </div>
        </div>
    );
}

export default ProjectApiTokensListItem;