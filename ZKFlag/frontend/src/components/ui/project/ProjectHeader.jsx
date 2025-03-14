import React from 'react';
import EditIcon from "../common/EditIcon.jsx";
import ProjectNav from "../../nav/ProjectNav.jsx";
import { useParams} from "react-router-dom";
import ProjectPath from "./ProjectPath.jsx";

function ProjectHeader({name}) {
    const { projectId } = useParams();

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
                </div>
            </div>
            <ProjectNav/>
        </>
    );
}

export default ProjectHeader;