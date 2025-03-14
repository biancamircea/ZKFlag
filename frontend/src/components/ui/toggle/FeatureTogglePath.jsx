import React from 'react';
import {NavLink, useParams} from "react-router-dom";

function FeatureTogglePath({projectName, toggleName}) {
    const { projectId } = useParams();
    const activeStyles = {
        color: "black"
    }
    return (
        <div className={"project-overview-path"}>
            <NavLink
                to={"/projects"}
                style={({isActive}) => isActive ? activeStyles : null}
                end
            >
                Projects
            </NavLink>
            {" > "}
            <NavLink
                to={`/projects/${projectId}`}
                style={({isActive}) => isActive ? activeStyles : null}
                end
            >
                {projectName}
            </NavLink>
            {" > "}
            <NavLink
                to={`/projects/${projectId}`}
                style={({isActive}) => isActive ? activeStyles : null}
                end
            >
                Feature Toggles
            </NavLink>
            {" > "}
            <NavLink
                to={"."}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                {toggleName}
            </NavLink>
        </div>
    );
}

export default FeatureTogglePath;