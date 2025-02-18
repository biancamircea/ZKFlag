import React from 'react';
import {NavLink} from "react-router-dom";

function FeatureToggleInstanceNav(props) {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        // backgroundColor: "#eceaea",
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
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Overview
            </NavLink>
            <NavLink
                to={"payload"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Payload
            </NavLink>
            <NavLink
                to={"events"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Event log
            </NavLink>
            <NavLink
                to={"schedule"}
                className={"feature-toggle-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Schedule flag
            </NavLink>
        </nav>
    );
}

export default FeatureToggleInstanceNav;