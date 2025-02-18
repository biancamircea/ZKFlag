import React from 'react';
import {NavLink} from "react-router-dom";

function ApplicationNav(props) {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        // backgroundColor: "#eceaea",
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
                to={"edit"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Edit Application
            </NavLink>
        </nav>
    );
}

export default ApplicationNav;