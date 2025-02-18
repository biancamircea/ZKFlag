import React from 'react';
import ProfileNav from "../nav/ProfileNav.jsx";
import {Outlet} from "react-router-dom";

function ProfileLayout() {
    return (
        <div className={"profile-layout-container"}>
            <ProfileNav/>
            <main className={"profile-layout-main"}>
                <Outlet/>
            </main>
        </div>
    );
}

export default ProfileLayout;