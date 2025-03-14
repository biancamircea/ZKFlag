import React from 'react';
import ProfileNav from "../nav/ProfileNav.jsx";
import {Outlet} from "react-router-dom";
import ProjectSettingsNav from "../nav/ProjectSettingsNav.jsx";

function ProjectSettingsLayout(props) {
    return (
        <div className={"project-settings-layout-container"}>
            <ProjectSettingsNav/>
            <main className={"project-settings-layout-main"}>
                <Outlet/>
            </main>
        </div>
    );
}

export default ProjectSettingsLayout;