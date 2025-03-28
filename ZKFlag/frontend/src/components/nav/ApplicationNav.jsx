import React from 'react';
import {NavLink} from "react-router-dom";

function ApplicationNav(props) {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        fontWeight: "bold"
    }
    return (
        <nav className={"project-nav-container"} style={{
            display: "flex",
            justifyContent: "space-between",
            width: "100%",
        }}>
            <NavLink
                to={"."}
                end
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Overview
            </NavLink>
            <NavLink
                to={"all-admins"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Admins
            </NavLink>
            <NavLink
                to={"all-projects"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Projects
            </NavLink>
            <NavLink
                to={"all-instances"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Instances
            </NavLink>
        </nav>
    );
}

export default ApplicationNav;