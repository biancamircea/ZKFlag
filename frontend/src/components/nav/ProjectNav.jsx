import React from 'react';
import {NavLink} from "react-router-dom";

function ProjectNav() {
    const activeStyles = {
        borderBottom: "0.3em solid #203983",
        boxShadow: "2px 2px 4px 0 rgba(0, 0, 0, 0.1)",
        // backgroundColor: "#eceaea",
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
            <NavLink
                to={"context"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Context fields
            </NavLink>
            <NavLink
                to={"tags"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Tags
            </NavLink>
            <NavLink
                to={"instances"}
                className={"project-nav-item"}
                style={({isActive}) => isActive ? activeStyles : null}
            >
                Instances
            </NavLink>
            {/*<NavLink*/}
            {/*    to={"settings"}*/}
            {/*    className={"project-nav-item"}*/}
            {/*    style={({isActive}) => isActive ? activeStyles : null}*/}
            {/*>*/}
            {/*    Settings*/}
            {/*</NavLink>*/}
            {/*<NavLink*/}
            {/*    to={"events"}*/}
            {/*    className={"project-nav-item"}*/}
            {/*    style={({isActive}) => isActive ? activeStyles : null}*/}
            {/*>*/}
            {/*    Event log*/}
            {/*</NavLink>*/}

        </nav>
    );
}

export default ProjectNav;