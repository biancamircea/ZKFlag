import React, { useEffect, useState} from 'react';
import { defer, useLoaderData, useParams } from 'react-router-dom';
import FeatureToggleScheduleItem from '../../components/ui/toggle/FeatureToggleScheduleItem';
import { getEnabledEnvFromInstance } from '../../api/environmentApi';
import {getToggleEnvironments} from "../../api/instanceApi.js";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import { getProjectByToggleId } from "../../api/featureToggleApi.js";

export async function loader({ params }) {
    return defer({ environments: getEnabledEnvFromInstance(params.instanceId)});
}

function FeatureToggleSchedule() {
    const loaderDataPromise = useLoaderData();
    const {  featureId,instanceId } = useParams();
    const [environments, setEnvironments] = useState([]);
    const [projectId, setProjectId] = useState("");

    useEffect(() => {
        async function fetchEnvironments() {
            try {
                const data = await getToggleEnvironments(instanceId, featureId);
                setEnvironments(data);
            } catch (error) {
                console.error("Error fetching environments:", error);
            }
        }

        fetchEnvironments();
    }, [instanceId]);

    useEffect(() => {
        async function fetchProjectByToggleId(){
            try {
                console.log("featureId", featureId)
                const data = await getProjectByToggleId(featureId);
                console.log("data projectId", data)
                setProjectId(data);
            } catch (error) {
                console.error("Error fetching projectId:", error);
            }
        }

        fetchProjectByToggleId();
    }, [featureId]);

    return (
        <div>
            {environments.length === 0 ? (
            <EmptyList
                resource={"environment"}
                recommend={"Activate environments from settings to manage feature toggles effectively."}
            />
            ) :( environments.map((env) => (
                    <FeatureToggleScheduleItem
                        key={env.id}
                        environmentName={env.name}
                        environmentId={env.environmentId}
                        featureId={featureId}
                        instanceId={instanceId}
                        projectId={projectId}
                    />
                ))
            )
            }
        </div>
    );
}

export default FeatureToggleSchedule;
