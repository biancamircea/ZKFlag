import React, { useState, useEffect } from 'react';
import ScheduleActivationDialog from './ScheduleActivationDialog';
import ScheduleDeactivationDialog from './ScheduleDeactivationDialog';
import ScheduleIntervalDialog from './ScheduleIntervalDialog';
import { setToggleSchedule, getStrategyForEnvironment } from '../../../api/featureToggleApi';
import { toast } from 'react-toastify';

function FeatureToggleScheduleItem({ environmentName, featureId, environmentId,instanceId }) {
    const [isActivationOpen, setIsActivationOpen] = useState(false);
    const [isDeactivationOpen, setIsDeactivationOpen] = useState(false);
    const [isIntervalOpen, setIsIntervalOpen] = useState(false);
    const [schedulingStrategy, setSchedulingStrategy] = useState(null);

    useEffect(() => {
        const fetchSchedulingStrategy = async () => {
            try {
                const schedule = await getStrategyForEnvironment(featureId, environmentId,instanceId);
                if (schedule && (schedule.startDate || schedule.startOn || schedule.endDate || schedule.startOff)) {
                    const strategyDetails = {
                        startDate: schedule.startDate || null,
                        startTime: schedule.startOn || null,
                        endDate: schedule.endDate || null,
                        endTime: schedule.startOff || null,
                    };

                    let strategyType = null;
                    if ((schedule.startDate || schedule.startOn) && !(schedule.endDate || schedule.startOff)) {
                        strategyType = "Activation";
                    } else if (!(schedule.startDate || schedule.startOn) && (schedule.endDate || schedule.startOff)) {
                        strategyType = "Deactivation";
                    } else if (schedule.startDate && schedule.endDate) {
                        strategyType = "Interval";
                    }

                    setSchedulingStrategy({ type: strategyType, details: strategyDetails });
                } else {
                    setSchedulingStrategy(null);
                }
            } catch (error) {
                toast.error(`Failed to fetch scheduling strategy: ${error.message}`);
            }
        };

        fetchSchedulingStrategy();
    }, [ featureId, environmentId]);

    const handleSaveStrategy = async (strategy, details) => {
        const formatDate = (date) => {
            const [day, month, year] = date.split('/');
            return `${year}-${month}-${day}`;
        };

        const formatTime = (time) => {
            return `${time}:00`;
        };

        const scheduleData = {
            startOn: details.startTime ? `${formatTime(details.startTime)}` : null,
            startOff: details.endTime ? `${formatTime(details.endTime)}` : null,
            startDate: details.startDate ? formatDate(details.startDate) : null,
            endDate: details.endDate ? formatDate(details.endDate) : null,
        };

        try {
            await setToggleSchedule(featureId, environmentId,instanceId, scheduleData)
            setSchedulingStrategy({ type: strategy, details });
            toast.success('Scheduling strategy saved successfully!');
        } catch (error) {
            toast.error(`Failed to save strategy: ${error.message}`);
        }
    };

    const handleDeleteStrategy = async () => {
        try {
            const resetScheduleData = {
                startOn: null,
                startOff: null,
                startDate: null,
                endDate: null,
            };

            await setToggleSchedule(featureId, environmentId,instanceId, resetScheduleData)
            setSchedulingStrategy(null);
            toast.success('Scheduling strategy deleted successfully!');
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
                {schedulingStrategy ? (
                    <div>
                        <p className="no-strategy-message">
                            Scheduled Strategy: <strong>{schedulingStrategy?.type || "No strategy"}</strong>
                        </p>
                        {schedulingStrategy?.details?.startDate && schedulingStrategy?.details?.startTime && (
                            <p className="no-strategy-message">
                                Start Date:{" "}
                                <strong>
                                    {schedulingStrategy.details.startDate} at {schedulingStrategy.details.startTime}
                                </strong>
                            </p>
                        )}
                        {schedulingStrategy?.details?.startDate && !schedulingStrategy?.details?.startTime && (
                            <p className="no-strategy-message">
                                Start Date:{" "}
                                <strong>
                                    {schedulingStrategy.details.startDate}
                                </strong>
                            </p>
                        )}
                        {!schedulingStrategy?.details?.startDate && schedulingStrategy?.details?.startTime && (
                            <p className="no-strategy-message">
                                Start Hour:{" "}
                                <strong>
                                    {schedulingStrategy.details.startTime}
                                </strong>
                            </p>
                        )}
                        {schedulingStrategy?.details?.endDate && (
                            <p className="no-strategy-message">
                                End Date:{" "}
                                <strong>
                                    {schedulingStrategy.details.endDate} at {schedulingStrategy.details.endTime}
                                </strong>
                            </p>
                        )}
                        <div className="schedule-buttons">
                            <button onClick={handleDeleteStrategy}>
                                Delete scheduling strategy
                            </button>
                        </div>
                    </div>
                ) : (
                    <div>
                        <p className="no-strategy-message">No scheduling strategy</p>
                        <div className="schedule-buttons">
                            <button onClick={() => setIsActivationOpen(true)}>
                                Schedule activation
                            </button>
                            <button onClick={() => setIsDeactivationOpen(true)}>
                                Schedule deactivation
                            </button>
                            <button onClick={() => setIsIntervalOpen(true)}>
                                Schedule interval of activation
                            </button>
                        </div>
                    </div>
                )}
            </div>

            <ScheduleActivationDialog
                open={isActivationOpen}
                onClose={() => setIsActivationOpen(false)}
                onSave={(details) => {
                    handleSaveStrategy("Activation", details);
                    setIsActivationOpen(false);
                }}
            />
            <ScheduleDeactivationDialog
                open={isDeactivationOpen}
                onClose={() => setIsDeactivationOpen(false)}
                onSave={(details) => {
                    handleSaveStrategy("Deactivation", details);
                    setIsDeactivationOpen(false);
                }}
            />
            <ScheduleIntervalDialog
                open={isIntervalOpen}
                onClose={() => setIsIntervalOpen(false)}
                onSave={(details) => {
                    handleSaveStrategy("Interval", details);
                    setIsIntervalOpen(false);
                }}
            />
        </div>
    );
}

export default FeatureToggleScheduleItem;
