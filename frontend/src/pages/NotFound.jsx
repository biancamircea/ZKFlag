import React from 'react';
import {Link, useNavigate} from "react-router-dom";

function NotFound(props) {
    const navigate = useNavigate()
    console.log(props)
    return (
        <div className={"error-page-container"}>
            <Link to={"/"} className={"error-home"}>Go home</Link>
            <h1>{"Sorry, the page was not found."}</h1>
            <button
                className={"error-button"}
                onClick={() => navigate(-1)}
            >{"<< Go back"}</button>
        </div>
    );
}

export default NotFound;