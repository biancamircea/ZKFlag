import React from 'react';
import EditIcon from "../common/EditIcon.jsx";
import {useParams} from "react-router-dom";

function FeatureToggleMetadataCard({description, projectName, instanceName}) {
    const {projectId, featureId} = useParams();
    return (
        <div className={"feature-toggle-overview-meta-card"}>
            <img src={"/src/assets/images/description.png"}
                 alt={"Add tag"}
                 className={"description-icon"}
            />
            <h4>Project: {projectName}</h4>
            {instanceName && <h4>Instance: {instanceName}</h4>}
            <div className={"feature-toggle-overview-meta-card-description"}>
                <p>{"Description: "}
                    <span className={description === "" ? "gray-text" : ""}>
                        {description === "" ? "No description." : description}
                    </span>
                </p>
                <div className={"metadata-edit-icon"}>
                    <EditIcon
                        directLink={`/projects/${projectId}/features/edit/${featureId}`}
                    />
                </div>
            </div>
        </div>
    );
}

export default FeatureToggleMetadataCard;