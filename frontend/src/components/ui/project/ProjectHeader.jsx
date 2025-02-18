import React from 'react';
import EditIcon from "../common/EditIcon.jsx";
import DeleteIcon from "../common/DeleteIcon.jsx";
import ProjectNav from "../../nav/ProjectNav.jsx";
import {useNavigate, useParams} from "react-router-dom";
import {deleteProject} from "../../../api/projectApi.js";
import {toast} from "react-toastify";
import ProjectPath from "./ProjectPath.jsx";

function ProjectHeader({name}) {
    const { projectId } = useParams();
    const navigate = useNavigate();

    async function deleteHandler(id){
        const res = await deleteProject(id);
        if (res) {
            toast.success("Project deleted.");
            navigate("/projects")
        } else {
            toast.error("Operation failed.");
        }
    }

    return (
        <>
            <ProjectPath
                name={name}
            />
            <div className={"project-header-container"}>
                <h2>{name}</h2>
                <div className={"list-page-header-functions"}>
                    <EditIcon
                        directLink={`/projects/edit/${projectId}`}
                    />
                    <DeleteIcon
                        resource={"Project"}
                        resourceName={name}
                        deleteHandler={() => deleteHandler(projectId)}
                    />
                </div>
            </div>
            <ProjectNav/>
        </>
    );
}

export default ProjectHeader;