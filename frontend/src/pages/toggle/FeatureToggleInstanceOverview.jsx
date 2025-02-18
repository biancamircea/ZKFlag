import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, useLoaderData, useOutletContext, useParams} from "react-router-dom";
import {getToggleFromProject, removeTagFromToggle} from "../../api/featureToggleApi.js";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import FeatureToggleMetadataCard from "../../components/ui/toggle/FeatureToggleMetadataCard.jsx";
import FeatureToggleDetailsCard from "../../components/ui/toggle/FeatureToggleDetailsCard.jsx";
import {toast} from "react-toastify";
import FeatureToggleInstanceSectionRight from "../../components/ui/toggle/FeatureToggleInstanceSectionRight.jsx";
import { getAllConstraintsInToggle } from "../../api/projectApi";
import {getAllEnvironmentsFromInstance, getInstanceOverview} from "../../api/instanceApi.js";

export async function loader({ params }) {
    const { projectId, featureId, instanceId } = params;

    try {
        const constraints = await getAllConstraintsInToggle(projectId, featureId);
        return defer({ constraints }); // Încărcăm constrângerile
    } catch (error) {
        console.error("Error loading constraints:", error);
        throw error; // Poți adăuga o gestionare mai bună a erorilor aici
    }
}

async function getEnvironment( instanceId){
   try {
       const res = await getAllEnvironmentsFromInstance(instanceId);
       return res;
   }catch (error) {
       console.error("Error loading environments:", error);
       throw error;
    }
}


function FeatureToggleInstanceOverview(props) {
    const {projectId, featureId,instanceId} = useParams()
    const {toggle, removeFeatureTag} = useOutletContext()
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

        async function getInstanceName(){
            try {
                const res = await getInstanceOverview(instanceId);
                setInstanceName(res.name);
            } catch (error) {
                console.error("Error loading environments:", error);
                throw error;
            }
        }
        getInstanceName()
    }, [instanceId]);

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
                        instanceName={instanceName}
                    />
                    <FeatureToggleDetailsCard
                        createdAt={toggle.createdAt}
                        tags={toggle.tags}
                        removeTag={removeTag}
                    />
                </div>
                <div className={"feature-toggle-overview-section-right"}>
                    <FeatureToggleInstanceSectionRight
                        projectId={projectId}
                        featureId={featureId}
                        instanceId={instanceId}
                        environments={environments}
                    />
                </div>
            </div>
        </>
    );
}

export default FeatureToggleInstanceOverview;