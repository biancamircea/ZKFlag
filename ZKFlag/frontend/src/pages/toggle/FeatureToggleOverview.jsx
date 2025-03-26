import React from 'react';
import { defer, useLoaderData, useOutletContext, useParams} from "react-router-dom";
import { removeTagFromToggle} from "../../api/featureToggleApi.js";
import FeatureToggleMetadataCard from "../../components/ui/toggle/FeatureToggleMetadataCard.jsx";
import FeatureToggleDetailsCard from "../../components/ui/toggle/FeatureToggleDetailsCard.jsx";
import {toast} from "react-toastify";
import FeatureToggleSectionRight from "../../components/ui/toggle/FeatureToggleSectionRight.jsx";
import { getAllConstraintsInToggle } from "../../api/projectApi";
import { useRevalidator } from 'react-router-dom';

export async function loader({ params }) {
    const { projectId, featureId } = params;

    try {
        const constraints = await getAllConstraintsInToggle(projectId, featureId);
        return defer({ constraints });
    } catch (error) {
        console.error("Error loading constraints:", error);
        throw error;
    }
}


function FeatureToggleOverview(props) {
    const {projectId, featureId} = useParams()
    const {toggle, removeFeatureTag, contextFields} = useOutletContext()
    const { constraints } = useLoaderData();
    const revalidator = useRevalidator();

    const refreshConstraints = () => {
        revalidator.revalidate();
    };

    async function removeTag(id) {
        const response = await removeTagFromToggle(projectId, featureId, id)
        if(response){
            removeFeatureTag(id);
            toast.success("Tag deleted.");
        } else {
            toast.error("Operation failed.");
        }
    }

    return (
        <>
            <div className={"project-overview-wrapper"} key={toggle.id}>
                <div className={"feature-toggle-overview-section-left"}>
                    <FeatureToggleMetadataCard
                        projectName={toggle.project}
                        description={toggle.description}
                    />
                    <FeatureToggleDetailsCard
                        createdAt={toggle.createdAt}
                        tags={toggle.tags}
                        removeTag={removeTag}
                    />
                </div>
                <div className={"feature-toggle-overview-section-right"}>
                    <FeatureToggleSectionRight
                        featureId={featureId}
                        constraints={constraints}
                        refreshConstraints={refreshConstraints}
                    />
                </div>
            </div>
        </>
    );
}

export default FeatureToggleOverview;