import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { FaBell } from "react-icons/fa";
import "./Navbar.css";
import { useAuth } from "../Router/Router";

const Navbar = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [isNotificationListOpen, setIsNotificationListOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const navigate = useNavigate();
    const auth = useAuth();
    const employeeId = localStorage.getItem("idUser");
    const notificationRef = useRef(null);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };

    const closeSidebar = () => {
        setIsSidebarOpen(false);
    };

    const toggleNotificationList = () => {
        setIsNotificationListOpen(!isNotificationListOpen);
    };

    const closeNotificationList = () => {
        setIsNotificationListOpen(false);
    };

    useEffect(() => {
        const fetchNotifications = async () => {
            const response = await fetch(`/api/v1/notifications/${employeeId}`);
            if (response.ok) {
                const data = await response.json();
                const unreadNotifications = data.filter(notification => !notification.isRead);
                setNotifications(unreadNotifications);
            } else {
                console.error("Eroare la obținerea notificărilor:", response.status);
            }
        };
        fetchNotifications();
    }, [employeeId]);

    const handleNotificationClick = async (notification) => {
        closeNotificationList();

        try {
            const response = await fetch(`/api/v1/notifications/${notification.id}/read`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Eroare la actualizarea notificării');
            }

            navigate(notification.redirectPath.startsWith("/") ? notification.redirectPath : `/${notification.redirectPath}`);

        } catch (error) {
            console.error("Eroare:", error);
        }
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (notificationRef.current && !notificationRef.current.contains(event.target)) {
                closeNotificationList();
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    return (
        <div>
            <nav className="navbar">
                <div className="navbar-logo" onClick={() => navigate("/home")}>
                    <img src="../../../logo.png" alt="Logo" className="logo-image" />
                </div>
                <div className="navbar-logo">
                    <img src="../../../titlu2.png" alt="Title" className="title" />
                </div>
                <div className="navbar-icons">
                    <div className="notification-bell" onClick={toggleNotificationList}>
                        <FaBell className="navbar-bell-icon" />
                        <span className="notification-count">{notifications.length}</span>
                    </div>
                    <div className="navbar-hamburger" onClick={toggleSidebar}>
                        ☰
                    </div>
                </div>
            </nav>

            {isNotificationListOpen && (
                <div className="notification-dropdown" ref={notificationRef}>
                    {notifications.length > 0 ? (
                        notifications.map(notification => (
                            <div key={notification.id} className="notification-item" onClick={() => handleNotificationClick(notification)}>
                                {notification.message}
                            </div>
                        ))
                    ) : (
                        <div className="no-notifications">Nu aveți notificări noi</div>
                    )}
                </div>
            )}

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
                            navigate("/home");
                        }}>Home</li>
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

export default Navbar;
