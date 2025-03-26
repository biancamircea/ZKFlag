import React, { Suspense, useEffect, useState } from 'react';
import { Await, defer, useLoaderData, useOutletContext, useParams } from "react-router-dom";
import FeatureToggleMetadataCard from "../../components/ui/toggle/FeatureToggleMetadataCard.jsx";
import FeatureToggleDetailsCard from "../../components/ui/toggle/FeatureToggleDetailsCard.jsx";
import FeatureToggleInstanceSectionRight from "../../components/ui/toggle/FeatureToggleInstanceSectionRight.jsx";
import { getAllEnvironmentsFromInstance, getInstanceOverview } from "../../api/instanceApi.js";
import FeatureToggleSectionRight from "../../components/ui/toggle/FeatureToggleSectionRight.jsx";
import { getAllConstraintsInToggle } from "../../api/projectApi.js";
import EmptyList from "../../components/ui/common/EmptyList.jsx";

export async function loader({ params }) {
    return defer({ constraints: getAllConstraintsInToggle(params.projectId, params.featureId) });
}

function FeatureToggleInstanceOverview(props) {
    const loaderDataPromise = useLoaderData();
    const { projectId, featureId, instanceId } = useParams();
    const { toggle, removeFeatureTag } = useOutletContext();
    const [environments, setEnvironments] = useState([]);
    const [instanceName, setInstanceName] = useState("");

    useEffect(() => {
        async function fetchEnvironments() {
            try {
                const data = await getAllEnvironmentsFromInstance(instanceId);
                setEnvironments(data);
            } catch (error) {
                console.error("Error fetching environments:", error);
            }
        }
        fetchEnvironments();
    }, [instanceId]);

    useEffect(() => {
        async function getInstanceName() {
            try {
                const res = await getInstanceOverview(instanceId);
                setInstanceName(res.name);
            } catch (error) {
                console.error("Error loading instance name:", error);
            }
        }
        getInstanceName();
    }, [instanceId]);


    return (
        <div className="project-overview-wrapper" key={toggle.id}>
            <div className="feature-toggle-overview-section-left">
                <FeatureToggleMetadataCard
                    projectName={toggle.project}
                    description={toggle.description}
                    instanceName={instanceName}
                />
                <FeatureToggleDetailsCard
                    createdAt={toggle.createdAt}
                    tags={toggle.tags}
                    instanceId={instanceId}
                />
            </div>
            <div className="feature-toggle-overview-section-right">
                <FeatureToggleInstanceSectionRight
                    projectId={projectId}
                    featureId={featureId}
                    instanceId={instanceId}
                    environments={environments}
                />
            </div>
        </div>
    );
}

export default FeatureToggleInstanceOverview;
