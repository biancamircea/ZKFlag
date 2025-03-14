import React, {useEffect, useState} from 'react';
import { Link } from "react-router-dom";
import {fetchUser, logoutUser} from "../../../api/userApi.js";
import {useNavigate} from "react-router-dom";

function DropDownProfile(props) {
    const { id,user } = props;
    const navigate = useNavigate();
    const name=user.name;


    const handleLogout = async () => {
        try {
            await logoutUser();
            navigate("/login");
        } catch (error) {
            console.error("Logout failed", error);
        }
    };

    return (
        <div className={"drop-down-menu profile"} id={id}>
            <h3 className={"drop-down-profile-name"}>{name}</h3>
            <Link to={"profile"} className={"view-profile-link"}>
                View profile
            </Link>
            <div className={"normal-line"}></div>
            <button onClick={handleLogout}>Log out</button>
        </div>
    );
}

export default DropDownProfile;