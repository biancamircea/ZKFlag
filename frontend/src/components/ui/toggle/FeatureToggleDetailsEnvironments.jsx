import React, { useEffect, useState } from 'react';
import Tooltip from "@mui/material/Tooltip";
import TooltipSwitch from "./TooltipSwitch.jsx";
import { toggleFeature, getStrategyForEnvironment } from "../../../api/featureToggleApi.js";
import { toast } from "react-toastify";
import { useNavigate, useOutletContext, useParams } from "react-router-dom";
import EmptyList from "../common/EmptyList.jsx";

function FeatureToggleDetailsEnvironments({ environments }) {
    const navigate = useNavigate();
    const { projectId, featureId } = useParams();
    const { enableDisableToggleEnvironment } = useOutletContext();
    const [strategies, setStrategies] = useState({});

    async function fetchStrategies() {
        try {
            const fetchedStrategies = {};
            for (const env of environments) {
                const schedule = await getStrategyForEnvironment(projectId, featureId, env.id);

                // Inițializează obiectul pentru acest environment ID
                fetchedStrategies[env.id] = {};

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

                    fetchedStrategies[env.id].details = strategyDetails;
                    fetchedStrategies[env.id].type = strategyType;
                } else {
                    fetchedStrategies[env.id].type = "No strategy";
                    fetchedStrategies[env.id].details = null;
                }
            }
            setStrategies(fetchedStrategies);
        } catch (error) {
            toast.error("Failed to fetch strategies.");
        }
    }


    useEffect(() => {
        if (environments.length > 0) {
            fetchStrategies();
        }
    }, [environments]);

    async function enableToggle(envName, envId) {
        const res = await toggleFeature(projectId, featureId, envName, true);
        if (res) {
            enableDisableToggleEnvironment(envId, true);
            toast.success("Feature toggle enabled.");
        } else {
            toast.error("Operation failed.");
        }
    }

    async function disableToggle(envName, envId) {
        const res = await toggleFeature(projectId, featureId, envName, false);
        if (res) {
            enableDisableToggleEnvironment(envId, false);
            toast.success("Feature toggle disabled");
        } else {
            toast.error("Operation failed.");
        }
    }

    function renderPayload(el) {
        const hasPayload = el.enabledValue !== null && el.disabledValue !== null;
        if (hasPayload) {
            const payload = el.enabled ? el.enabledValue : el.disabledValue;
            return (
                <Tooltip
                    title={"This is the value of the toggle for the current configuration. In order to get this, you must use getPayload() function in SDK."}
                    arrow
                    placement={"top"}
                >
                    <span
                        onClick={() => navigate("payload")}
                        className={"bold-text env-payload"}
                    >
                        {payload}
                    </span>
                </Tooltip>
            );
        } else {
            return (
                <Tooltip title={"Get started by adding a payload."} arrow placement={"top"}>
                    <span
                        onClick={() => navigate("payload")}
                        className={"gray-text env-payload"}
                    >
                        No payload
                    </span>
                </Tooltip>
            );
        }
    }

    function renderStrategy(env) {
        const strategy = strategies[env.id];
        if (!strategy || !strategy.details) {
            return (
                <Tooltip title={"No active strategies. Click to add one."} arrow placement={"top"}>
                <span
                    onClick={() => navigate("schedule")}
                    className={"gray-text env-strategy"}
                >
                    No strategy
                </span>
                </Tooltip>
            );
        }
        return (
            <Tooltip title={"Active strategies for this environment. Click to view or edit them."} arrow placement={"top"}>
            <span
                onClick={() => navigate("schedule")}
                className={"bold-text env-strategy"}
            >
                Schedule strategy: {strategy.type}
            </span>
            </Tooltip>
        );
    }


    const envEl = environments.map(el => {
        return (
            <div key={el.id}>
            <div className={"switch"} key={el.name}>
                <TooltipSwitch
                    checked={el.enabled}
                    handleEnable={() => enableToggle(el.name, el.id)}
                    handleDisable={() => disableToggle(el.name, el.id)}
                    environmentName={el.name}
                />
                <div className={"name"}>
                    <p>{el.name}</p>
                    <span>{el.constraints.length === 0 ? "No" : el.constraints.length} constraint{el.constraints.length > 1 ? "s" : ""}
                    </span>
                </div>

                {renderPayload(el)}
            </div>
                <div className={"render_strategy_div"}>
                {renderStrategy(el)}
                </div>
            </div>
        );
    });

    return (
        <div className={"feature-toggle-details-section top"}>
            <div className={"info"}>
                <h4>Enabled in environments</h4>
                <Tooltip
                    title={"When a toggle is disabled, it will always return false. When it is enabled, it will return true or false based on its list of constraints"}
                    arrow
                    placement={"top"}
                >
                    <img
                        src={"/src/assets/images/info.png"}
                        alt={"Add tag"}
                        className={"info-icon"}
                    />
                </Tooltip>
            </div>
            {envEl.length === 0 ? (
                <EmptyList resource={"environment"} recommend={""} />
            ) : (
                envEl
            )}
        </div>
    );
}

export default FeatureToggleDetailsEnvironments;
