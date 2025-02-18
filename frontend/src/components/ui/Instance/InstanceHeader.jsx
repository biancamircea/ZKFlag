import React from 'react';
import EditIcon from "../common/EditIcon.jsx";
import DeleteIcon from "../common/DeleteIcon.jsx";
import InstanceNav from "../../nav/InstanceNav.jsx";
import {useNavigate, useParams} from "react-router-dom";
import {deleteInstance} from "../../../api/instanceApi.js";
import {toast} from "react-toastify";
import InstancePath from "./InstancePath.jsx";

function InstanceHeader({name}) {
    const { projectId,instanceId } = useParams();
    const navigate = useNavigate();

    async function deleteHandler(id){
        const res = await deleteInstance(projectId,id);
        if (res) {
            toast.success("Instance deleted.");
            navigate(`/projects/${projectId}/instances`);
        } else {
            toast.error("Operation failed.");
        }
    }

    return (
        <>
            <InstancePath
                name={name}
                projectId={projectId}
            />
            <div className={"project-header-container"}>
                <h2>{name}</h2>
                <div className={"list-page-header-functions"}>
                    <DeleteIcon
                        resource={"Instance"}
                        resourceName={name}
                        deleteHandler={() => deleteHandler(instanceId)}
                    />
                </div>
            </div>
            <InstanceNav/>
        </>
    );
}

export default InstanceHeader;