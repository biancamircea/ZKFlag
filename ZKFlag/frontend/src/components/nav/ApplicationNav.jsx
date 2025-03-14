import React from 'react';
import {NavLink} from "react-router-dom";

function ApplicationNav(props) {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        fontWeight: "bold"
    }
    return (
        <nav className={"nav-container application"}>
            <NavLink
                to={"."}
                end
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Overview
            </NavLink>
            <NavLink
                to={"all-admins"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Admins
            </NavLink>
            <NavLink
                to={"all-projects"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Projects
            </NavLink>
            <NavLink
                to={"all-instances"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Instances
            </NavLink>
        </nav>
    );
}

export default ApplicationNav;