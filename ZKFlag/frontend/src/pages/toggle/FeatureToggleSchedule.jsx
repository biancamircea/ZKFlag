import React, { useEffect, useState} from 'react';
import { defer, useLoaderData, useParams } from 'react-router-dom';
import FeatureToggleScheduleItem from '../../components/ui/toggle/FeatureToggleScheduleItem';
import { getEnabledEnvFromInstance } from '../../api/environmentApi';
import {getToggleEnvironments} from "../../api/instanceApi.js";
import EmptyList from "../../components/ui/common/EmptyList.jsx";

export async function loader({ params }) {
    return defer({ environments: getEnabledEnvFromInstance(params.instanceId) });
}

function FeatureToggleSchedule() {
    const loaderDataPromise = useLoaderData();
    const {  featureId,instanceId } = useParams();
    const [environments, setEnvironments] = useState([]);

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
                    />
                ))
            )
            }
        </div>
    );
}

export default FeatureToggleSchedule;
