import React from 'react';
import {Link} from "react-router-dom";

function DropDownProfile(props) {
    const {id} = props
    return (
        <div className={"drop-down-menu profile"} id={id}>
            <h3 className={"drop-down-profile-name"}>Rosu Cosmin</h3>
            <Link to={"profile"} className={"view-profile-link"}>
                View profile settings
            </Link>
            <div className={"normal-line"}></div>
            <button>Log out</button>
        </div>
    );
}

export default DropDownProfile;