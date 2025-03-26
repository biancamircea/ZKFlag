import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import EditIcon from "../common/EditIcon.jsx";
import { getToggleFromProject } from "../../../api/featureToggleApi.js";

function FeatureToggleMetadataCard({ description, projectName, instanceName }) {
    const { projectId, featureId } = useParams();
    const [toggle, setToggle] = useState(null);

    useEffect(() => {
        async function fetchToggle() {
            try {
                const resp = await getToggleFromProject(projectId, featureId);
                if (resp) {
                    setToggle(resp);
                }
            } catch (error) {
                console.error("Error fetching toggle:", error);
            }
        }

        fetchToggle();
    }, [projectId, featureId]);

    return (
        <div className={"feature-toggle-overview-meta-card"}>
            <img src={"/images/description.png"}
                 alt={"Add tag"}
                 className={"description-icon"}
            />
            <h4>Project: {projectName}</h4>
            {instanceName && <h4>Instance: {instanceName}</h4>}

            {instanceName == null &&
                <div className={"metadata-edit-icon"}>
                    <EditIcon directLink={`/projects/${projectId}/features/edit/${featureId}`} className={"metadata-edit-icon"} />
                </div>
            }

            <div className={"feature-toggle-overview-meta-card-description"}>
                <p>
                    {"Description: "}
                    <span className={description === "" ? "gray-text" : ""}>
                        {description === "" ? "No description." : description}
                    </span>
                </p>


                {toggle && (
                    <p>
                        {"Toggle type:"}
                        <span>
                             {toggle.toggle_type === 0 ? " Frontend" : toggle.toggle_type === 1 ? " Backend" : toggle.toggle_type===2? " Frontend and Backend" : " Unknown"}
                        </span>
                    </p>
                )}

            </div>
        </div>
    );
}

export default FeatureToggleMetadataCard;
