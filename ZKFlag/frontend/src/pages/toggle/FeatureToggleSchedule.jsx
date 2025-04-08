import React, { useEffect, useState} from 'react';
import { defer, useLoaderData, useParams } from 'react-router-dom';
import FeatureToggleScheduleItem from '../../components/ui/toggle/FeatureToggleScheduleItem';
import { getEnabledEnvFromInstance } from '../../api/environmentApi';
import {getToggleEnvironments} from "../../api/instanceApi.js";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import { getProjectByToggleId, getToggleScheduleHistory } from "../../api/featureToggleApi.js";
import ToggleScheduleHistory from "../../components/ui/toggle/ToggleScheduleHistory.jsx";

export async function loader({ params }) {
    return defer({ environments: getEnabledEnvFromInstance(params.instanceId)});
}

function FeatureToggleSchedule() {
    const loaderDataPromise = useLoaderData();
    const { featureId, instanceId } = useParams();
    const [environments, setEnvironments] = useState([]);
    const [projectId, setProjectId] = useState("");
    const [historyData, setHistoryData] = useState({}); // Store history by environmentId

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
        async function fetchProjectByToggleId() {
            try {
                const data = await getProjectByToggleId(featureId);
                setProjectId(data);
            } catch (error) {
                console.error("Error fetching projectId:", error);
            }
        }

        fetchProjectByToggleId();
    }, [featureId]);

    // Fetch history for each environment
    useEffect(() => {
        if (environments.length === 0) return;

        async function fetchHistoryForEnvironments() {
            const historyPromises = environments.map(async (env) => {
                try {
                    const history = await getToggleScheduleHistory(
                        featureId,
                        instanceId,
                        env.name
                    );
                    return { environmentId: env.environmentId, history };
                } catch (error) {
                    console.error(`Error fetching history for environment ${env.name}:`, error);
                    return { environmentId: env.environmentId, history: [] };
                }
            });

            const results = await Promise.all(historyPromises);
            const historyMap = results.reduce((acc, curr) => {
                acc[curr.environmentId] = curr.history;
                return acc;
            }, {});

            setHistoryData(historyMap);
        }

        fetchHistoryForEnvironments();
    }, [environments, featureId, instanceId]);

    return (
        <div>
            {environments.length === 0 ? (
                <EmptyList
                    resource={"environment"}
                    recommend={"Activate environments from settings to manage feature toggles effectively."}
                />
            ) : (
                <>
                    {/* Existing schedule items */}
                    {environments.map((env) => (
                        <FeatureToggleScheduleItem
                            key={`schedule-${env.id}`}
                            environmentName={env.name}
                            environmentId={env.environmentId}
                            featureId={featureId}
                            instanceId={instanceId}
                            projectId={projectId}
                        />
                    ))}

                    {environments.map((env) => (
                        <div key={`history-${env.id}`}>
                            {historyData[env.environmentId] ? (
                                <ToggleScheduleHistory
                                    history={historyData[env.environmentId]}
                                    environmentName={env.name}
                                />
                            ) : (
                                <div className="toggle-environment-constraints-container">
                                    <div className="header">
                                        <img src="/images/environment.png" alt="Environment" className="environment-icon" />
                                        <h3>{env.name} - History</h3>
                                    </div>
                                    <div className="schedule-status-container">
                                        <p className="no-strategy-message">Loading history...</p>
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </>
            )}
        </div>
    );
}

export default FeatureToggleSchedule;