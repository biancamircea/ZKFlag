import React from 'react';
import {Link, useNavigate} from "react-router-dom";
import FeatureTag from "../common/FeatureTag.jsx";

function FeatureTogglesListItem({id, name, description, tags, createdAt, projectName, projectId}) {
    const navigate = useNavigate();

    function onClickHandler(toggleId){
        navigate(`/projects/${projectId}/features/${toggleId}`)
    }

    const tagsEl = tags.map((el) => {
        return (
            <FeatureTag
                key={el.id}
                label={el.labelName}
                color={el.color}
            />
        )
    })

    return (
        <div
            className={"list-item item-body features"}
            onClick={() => onClickHandler(id)}
        >
            <div className={"name-and-description"}>
                <p className={"underlined-on-parent-hover"}>{name}</p>
                <span className={"gray-text"}>{description === "" ? "No description" : description}</span>
            </div>
            <div className={"feature-list-item-left tags"}>
                {tagsEl}
            </div>
            <p>{
                createdAt ?
                    new Date(createdAt).toLocaleDateString("ro") :
                    "null"
            }</p>
            <Link
                to={`/projects/${projectId}`}
                onClick={event => event.stopPropagation()}
                className={"underlined-on-hover centered-wrapped-content"}
            >
                {projectName}
            </Link>
        </div>
    );
}

export default FeatureTogglesListItem;