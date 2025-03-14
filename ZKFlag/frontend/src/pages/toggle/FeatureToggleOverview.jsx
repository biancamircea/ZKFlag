import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, useLoaderData, useOutletContext, useParams} from "react-router-dom";
import {getToggleFromProject, removeTagFromToggle} from "../../api/featureToggleApi.js";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import FeatureToggleMetadataCard from "../../components/ui/toggle/FeatureToggleMetadataCard.jsx";
import FeatureToggleDetailsCard from "../../components/ui/toggle/FeatureToggleDetailsCard.jsx";
import {toast} from "react-toastify";
import FeatureToggleSectionRight from "../../components/ui/toggle/FeatureToggleSectionRight.jsx";
import { getAllConstraintsInToggle } from "../../api/projectApi";

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
                    />
                </div>
            </div>
        </>
    );
}

export default FeatureToggleOverview;