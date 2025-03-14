import React from 'react';
import {NavLink} from "react-router-dom";

function ProfileNav() {
    const activeStyles = {
        borderLeft: "0.5em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        backgroundColor: "#d4dff5",
        fontStyle: "italic"
    }

    return (
        <nav className={"profile-nav-container"}>
            <NavLink
                to={"."}
                end
                className={"profile-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Profile
            </NavLink>
            <NavLink
                to={"settings"}
                className={"profile-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Settings
            </NavLink>
        </nav>
    );
}

export default ProfileNav;