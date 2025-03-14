import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import Navbar from "../../components/nav/ApplicationNav.jsx";

function SystemAdminLayout() {
    return (
        <div className="layout-container">
            <Navbar />
            <div className="content-container">
                <Outlet />
            </div>
        </div>
    );
}

export default SystemAdminLayout;
