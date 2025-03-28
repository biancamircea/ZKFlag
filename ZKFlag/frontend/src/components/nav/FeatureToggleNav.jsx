import React from 'react';
import {NavLink} from "react-router-dom";

function FeatureToggleNav(props) {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        fontWeight: "bold"
    }
    return (
        <nav className={"project-nav-container"}>
            <NavLink
                to={"."}
                end
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Overview
            </NavLink>
        </nav>
    );
}

export default FeatureToggleNav;