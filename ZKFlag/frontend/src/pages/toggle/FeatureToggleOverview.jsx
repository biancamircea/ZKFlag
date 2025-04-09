import React, {useEffect} from 'react';
import { defer, useLoaderData, useOutletContext, useParams} from "react-router-dom";
import {
    getOverallStatisticsByToggleId,
    removeTagFromToggle
} from "../../api/featureToggleApi.js";
import FeatureToggleMetadataCard from "../../components/ui/toggle/FeatureToggleMetadataCard.jsx";
import FeatureToggleDetailsCard from "../../components/ui/toggle/FeatureToggleDetailsCard.jsx";
import {toast} from "react-toastify";
import FeatureToggleSectionRight from "../../components/ui/toggle/FeatureToggleSectionRight.jsx";
import { getAllConstraintsInToggle } from "../../api/projectApi";
import { useRevalidator } from 'react-router-dom';
import FeatureToggleInstanceStatistics from "../../components/ui/toggle/FeatureToggleInstanceStatistics.jsx";

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
    const [stat, setStatistics] = React.useState([])

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

    useEffect(() => {
        async function fetchOverallStatistics() {
            try {
                const data = await getOverallStatisticsByToggleId(featureId)
                setStatistics(data);
                console.log("Statistics data:", data);
            } catch (error) {
                console.error("Error fetching statistics:", error);
            }
        }
        fetchOverallStatistics();
    }, [featureId]);

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
                    <div className="feature-toggle-statistics-grid">
                        <FeatureToggleInstanceStatistics statistics={stat} index={0}/>
                    </div>
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