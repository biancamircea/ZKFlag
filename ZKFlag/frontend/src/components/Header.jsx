import { Link, NavLink } from "react-router-dom";
import { useEffect, useState } from "react";
import HeaderDropDown from "./ui/common/HeaderDropDown.jsx";
import Tooltip from "@mui/material/Tooltip";
import {fetchUser} from "../api/userApi.js";

function Header() {
    const [user, setUser] = useState(null);

    useEffect(() => {
        fetchUser().then(setUser);
    }, []);

    if (!user) {
        return null;
    }

    return (
        <header>
            <div className="header-navlinks-container">
                <Link to="/" className="header-container">
                    <img src="/images/favicon.png" className="header-logo" alt="favorite-icon" />
                    <span className="header-title">ZKFlag</span>
                </Link>

                {user.role === "ProjectAdmin" && (
                    <>
                        <NavLink to="projects">Projects</NavLink>
                        <NavLink to="projects/features">Feature Toggles</NavLink>
                    </>
                )}

                {user.role === "SystemAdmin" && (
                    <NavLink to="system-admin/environments">Environments</NavLink>
                )}

                {user.role === "InstanceAdmin" && (
                    <NavLink to="instances">Instances</NavLink>

                )}

                <NavLink to={"/events"}>Events</NavLink>
            </div>

            <div className="header-icons-container">
                <Tooltip title="Check documentation" arrow>
                    <Link to="/documentation" className="icon-with-arrow" target="_blank">
                        <img src="/images/document.png" alt="document" className="small-icon" />
                    </Link>
                </Tooltip>

                <HeaderDropDown
                    title={<img src="/images/user.png" alt="user" className="profile-icon" />}
                    items={[]}
                    profile={true}
                    user={user}
                />
            </div>
        </header>
    );
}

export default Header;
