import React from 'react';
import {NavLink} from "react-router-dom";

function ApplicationPath({name}) {
    const activeStyles = {
        color: "black"
    }
    return (
        <div className={"project-overview-path"}>
            <NavLink
                to={"/applications"}
                style={({isActive}) => isActive ? activeStyles : null}
                end
            >
                Applications
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

export default ApplicationPath;