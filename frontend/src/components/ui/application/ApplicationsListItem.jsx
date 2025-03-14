import React from 'react';
import {useNavigate} from "react-router-dom";

function ApplicationsListItem({id, name, description}) {
    const navigate = useNavigate();
    function onClickHandler(appId){
        navigate(`${appId}`)
    }
    return (
        <div
            className={"list-item item-body applications"}
            onClick={() => onClickHandler(id)}
        >
            <div className={"list-item item-body icon"}>
                <img src={"/images/application.png"}
                     alt={"Application"}
                     className={"px45-icon"}
                />
            </div>
            <div className={"name-and-description"}>
                <p className={"underlined-on-parent-hover"}>{name}</p>
                <span className={"gray-text"}>{description ? description : "No description" }</span>
            </div>
        </div>
    );
}

export default ApplicationsListItem;