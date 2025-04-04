import React, { useState, useEffect } from 'react';
import ScheduleIntervalDialog from './ScheduleIntervalDialog';
import { getToggleStrategies, cancelScheduledToggle } from '../../../api/featureToggleApi';
import { toast } from 'react-toastify';

function FeatureToggleScheduleItem({ environmentName, featureId, environmentId, instanceId,projectId }) {
    const [isIntervalOpen, setIsIntervalOpen] = useState(false);
    const [strategyPairs, setStrategyPairs] = useState([]);

    const refreshStrategies = async () => {
        try {
            const strategies = await getToggleStrategies(featureId, instanceId, environmentName);
            setStrategyPairs(groupStrategies(strategies));
        } catch (error) {
            if (error.message !== "No scheduling strategies found for the specified criteria") {
                toast.error(`Failed to fetch scheduling strategies: ${error.message}`);
            }
            setStrategyPairs([]);
        }
    };
    useEffect(() => {
        refreshStrategies();
    }, [featureId, environmentId, instanceId, environmentName]);


    useEffect(() => {
        const fetchAndGroupStrategies = async () => {
            try {
                const strategies = await getToggleStrategies(featureId, instanceId, environmentName);
                setStrategyPairs(groupStrategies(strategies));
            } catch (error) {
                if (error.message !== "No scheduling strategies found for the specified criteria") {
                    toast.error(`Failed to fetch scheduling strategies: ${error.message}`);
                }
                setStrategyPairs([]);
            }
        };

        fetchAndGroupStrategies();
    }, [featureId, environmentId, instanceId, environmentName]);

    const groupStrategies = (strategies) => {
        const groups = {};

        strategies.forEach(strategy => {
            const baseInstanceId = strategy.taskInstanceId;

            if (!groups[baseInstanceId]) {
                groups[baseInstanceId] = {
                    instanceId: baseInstanceId,
                    activation: null,
                    deactivation: null,
                    recurrenceType: strategy.recurenceType
                };
            }

            if (strategy.taskType === 'activate') {
                groups[baseInstanceId].activation = strategy;
            } else {
                groups[baseInstanceId].deactivation = strategy;
            }
        });

        return Object.values(groups);
    };

    const formatDateTime = (instant) => {
        if (!instant) return 'Not scheduled';
        return new Date(instant).toLocaleString();
    };

    const getRecurrenceDisplay = (recurrenceType) => {
        switch(recurrenceType) {
            case 'ONE_TIME': return 'One-time Schedule';
            case 'DAILY': return 'Daily Schedule';
            case 'WEEKLY': return 'Weekly Schedule';
            case 'MONTHLY': return 'Monthly Schedule';
            default: return recurrenceType;
        }
    };

    const handleDeletePair = async (baseInstanceId) => {
        try {
            await cancelScheduledToggle(baseInstanceId);
            setStrategyPairs(prev => prev.filter(pair => pair.instanceId !== baseInstanceId));
            toast.success('Scheduled strategy pair deleted successfully!');
        } catch (error) {
            toast.error(`Failed to delete strategy: ${error.message}`);
        }
    };

    return (
        <div className="toggle-environment-constraints-container">
            <div className="header">
                <img src="/images/environment.png" alt="Environment" className="environment-icon" />
                <h3>{environmentName}</h3>
            </div>

            <div className="schedule-status-container">
                {strategyPairs.length > 0 ? (
                    <div className="strategy-pairs-container">
                        {strategyPairs.map((pair, index) => (
                            <div key={index} className="strategy-pair" style={{ display: "flex", justifyContent: "space-between", alignItems: "center", width: "100%" }}>
                                <div style={{ flex:"1",display: "flex", alignItems: "center" }}>
                                    <h4 className="strategy-title">{getRecurrenceDisplay(pair.recurrenceType)}</h4>
                                </div>
                                <div style={{ flex:"1",display: "flex", alignItems: "center" }}>
                                    <p className="strategy-time"><strong>Activation:</strong> {formatDateTime(pair.activation?.executionTime)}</p>
                                </div>
                                <div style={{ flex:"1",display: "flex", alignItems: "center" }}>
                                    <p className="strategy-time"><strong>Deactivation:</strong> {formatDateTime(pair.deactivation?.executionTime)}</p>
                                </div>
                                <div style={{ flex:"1",display: "flex", justifyContent:"flex-end", alignItems: "center" }}>
                                    <button onClick={() => handleDeletePair(pair.instanceId)} className="delete-pair-btn">
                                        Delete this schedule
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="no-strategy-message">No scheduling strategy</p>
                )}

                <div style={{display:"flex", justifyContent:"center", alignItems: "center"}}>
                    <button onClick={() => setIsIntervalOpen(true)}>
                        Schedule activation
                    </button>
                </div>
            </div>

            <ScheduleIntervalDialog
                open={isIntervalOpen}
                onClose={() => setIsIntervalOpen(false)}
                onSave={() => {
                    setIsIntervalOpen(false);
                    refreshStrategies();
                }}
                projectId={projectId}
                toggleId={featureId}
                instanceId={instanceId}
                environmentName={environmentName}
            />
        </div>
    );
}

export default FeatureToggleScheduleItem;
