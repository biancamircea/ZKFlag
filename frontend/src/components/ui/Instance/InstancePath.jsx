import React from 'react';
import {NavLink} from "react-router-dom";

function InstancePath({name, projectId}) {
    const activeStyles = {
        color: "black"
    }
    return (
        <div className={"project-overview-path"}>
            <NavLink
                to={`/instances`}
                style={({isActive}) => isActive ? activeStyles : null}
                end
            >
               Instances
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

export default InstancePath;