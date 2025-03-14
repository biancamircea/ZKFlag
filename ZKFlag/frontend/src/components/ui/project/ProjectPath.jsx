import React from 'react';
import {NavLink} from "react-router-dom";

function ProjectPath({name}) {
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
                to={"."}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                {name}
            </NavLink>
        </div>
    );
}

export default ProjectPath;