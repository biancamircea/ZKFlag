import React from 'react';
import {NavLink} from "react-router-dom";

function ProjectSettingsNav(props) {
    const activeStyles = {
        borderLeft: "0.5em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        backgroundColor: "whitesmoke",
        fontStyle: "italic"
    }
    return (
        <nav className={"project-settings-nav-container"}>
            <NavLink
                to={"."}
                end
                className={"project-settings-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Environments
            </NavLink>
            <NavLink
                to={"members"}
                className={"project-settings-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Members
            </NavLink>
            <NavLink
                to={"api-tokens"}
                className={"project-settings-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                API Tokens
            </NavLink>
        </nav>
    );
}

export default ProjectSettingsNav;