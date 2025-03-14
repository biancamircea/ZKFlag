import React from "react";
import { useNavigate } from "react-router-dom";
import  "../Home/Home.css"

const PgAdmin = () => {
    const navigate = useNavigate();

    return (
            <div className="home-container">
                <h1 style={{color:"#3f3f3f"}}>Welcome, admin!</h1>
                <div className="button-group">
                    <button className="custom-button" onClick={() => navigate("/user_managemnt")}>
                        User Management
                    </button>
                    <button className="custom-button" onClick={() => navigate("/mod_commander")}>
                        Modify commander
                    </button>
                    <button className="custom-button" onClick={() => navigate("/mod_head dep")}>
                        Modify head of department
                    </button>
                    <button className="custom-button" onClick={() => navigate("/mod_user_dep")}>
                        Modify the user's department
                    </button>
                </div>
        </div>
    );
};

export default PgAdmin;
