// src/Components/AdminNavbar/AdminNavbar.js
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../Router/Router";
import "../Navbar/Navbar.css"

const AdminNavbar = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const navigate = useNavigate();
    const auth = useAuth();

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };

    const closeSidebar = () => {
        setIsSidebarOpen(false);
    };

    return (
        <div>
            <nav className="navbar">
                <div className="navbar-logo">
                    <img src="../../../logo.png" alt="Logo" className="logo-image" />
                </div>
                <div className="navbar-logo">
                    <img src="../../../titlu2.png" alt="Logo" className="title" />
                </div>
                <div className="navbar-icons">
                    <div className="navbar-hamburger" onClick={toggleSidebar}>
                        ☰
                    </div>
                </div>
            </nav>

            <div className={`sidebar ${isSidebarOpen ? "open" : ""}`}>
                <div className="sidebar-header">
                    <button className="close-btn" onClick={closeSidebar}>
                        ✖
                    </button>
                </div>
                <div className="ul-nav">
                    <ul>
                        <li className="side-bar-options" onClick={() => {
                            closeSidebar();
                            navigate("/pgAdmin");
                        }}>Home
                        </li>
                        <br />
                        <li
                            className="side-bar-options"
                            onClick={() => {
                                closeSidebar();
                                auth.logout();
                                navigate("/login");
                            }}
                        >
                            Log out
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default AdminNavbar;
