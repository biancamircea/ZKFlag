import React from 'react';
import InstanceNav from "../../nav/InstanceNav.jsx";
import {useParams} from "react-router-dom";
import InstancePath from "./InstancePath.jsx";

function InstanceHeader({name}) {
    const { projectId } = useParams();


    return (
        <>
            <InstancePath
                name={name}
                projectId={projectId}
            />
            <div className={"project-header-container"}>
                <h2>{name}</h2>
            </div>
            <InstanceNav/>
        </>
    );
}

export default InstanceHeader;