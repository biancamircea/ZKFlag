import React, {Suspense, useEffect, useState} from 'react';
import { Await, defer, useLoaderData, useParams } from 'react-router-dom';
import FeatureToggleScheduleItem from '../../components/ui/toggle/FeatureToggleScheduleItem';
import LoadingBanner from '../../components/ui/common/LoadingBanner';
import { getEnabledEnvFromInstance } from '../../api/environmentApi';
import {toast} from "react-toastify";
import {getToggleEnvironments} from "../../api/instanceApi.js";

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
            {environments.length > 0 ? (
                environments.map((env) => (
                    <FeatureToggleScheduleItem
                        key={env.id}
                        environmentName={env.name}
                        environmentId={env.environmentId}
                        featureId={featureId}
                        instanceId={instanceId}
                    />
                ))
            ) : (
                <p>No environments available.</p>
            )}
        </div>
    );
}

export default FeatureToggleSchedule;
