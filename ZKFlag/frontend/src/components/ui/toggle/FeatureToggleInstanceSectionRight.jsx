import React, { useEffect, useState } from "react";
import { isToggleEnabled, toggleFeature, getAllConstraintsForInstanceEnvironment } from "../../../api/featureToggleApi";
import ProjectEnvironmentSwitch from "../project/ProjectEnvironmentSwitch.jsx";
import ConstraintsList from "./ConstraintsList.jsx";
import EmptyList from "../common/EmptyList.jsx";

function FeatureToggleInstanceSectionRight({ projectId, featureId, instanceId, environments }) {
    const [toggleStates, setToggleStates] = useState({});
    const [constraints, setConstraints] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    const refreshConstraints = async (envId) => {
        try {
            const updatedConstraints = await getAllConstraintsForInstanceEnvironment(
                projectId,
                featureId,
                instanceId,
                envId
            );
            setConstraints(prev => ({
                ...prev,
                [envId]: updatedConstraints
            }));
        } catch (error) {
            console.error("Failed to refresh constraints:", error);
            toast.error("Failed to refresh constraints");
        }
    };

    useEffect(() => {
        async function fetchData() {
            try {
                setIsLoading(true);
                const statesArray = await Promise.all(
                    environments.map(async (env) => {
                        const isEnabled = await isToggleEnabled(featureId, env.id, instanceId);
                        const constraints = await getAllConstraintsForInstanceEnvironment(projectId,featureId, instanceId, env.id);
                        return { id: env.id, isEnabled, constraints };
                    })
                );

                const states = Object.fromEntries(statesArray.map(({ id, isEnabled }) => [id, isEnabled]));
                const constraintsData = Object.fromEntries(statesArray.map(({ id, constraints }) => [id, constraints]));

                setToggleStates(states);
                setConstraints(constraintsData);
            } catch (error) {
                console.error("Failed to fetch toggle states or constraints:", error);
            } finally {
                setIsLoading(false);
            }
        }
        fetchData();
    }, [featureId, instanceId, environments]);

    const handleToggle = async (envId, envName) => {
        const currentState = toggleStates[envId] ?? false;
        const newState = !currentState;
        try {
            await toggleFeature(projectId, featureId, instanceId, envName, newState);
            setToggleStates((prevState) => ({ ...prevState, [envId]: newState }));
        } catch (error) {
            console.error("Failed to toggle feature:", error);
        }
    };

    return (
        <div className="feature-toggle-instance-section-right">
            <div className="list-container">
                <div className="project-environment list-item item-header" style={{ display: "flex" }}>
                    <p>Name</p>
                    <p style={{ marginLeft: "300px" }}>Active in environment</p>
                </div>
                <br/>
                {isLoading ? (
                    <p>Loading...</p>
                )  : environments.length === 0 ? (
                    <EmptyList
                        resource={"environment"}
                        recommend={"Activate environments from settings to manage feature toggles effectively."}
                    />
                ): (
                    environments.map((env) => (
                        <div key={env.id} className="environment-section">
                            <div
                                className="project-environment list-item"
                                style={{ flexGrow: 1, width: "100%", display: "flex" }}
                            >
                                <span>{env.name}</span>
                                <div className="list-item-actions" style={{ marginLeft: `${300 - 7.5 * env.name.length}px` }}>
                                    <ProjectEnvironmentSwitch
                                        checked={toggleStates[env.id] ?? false}
                                        handleEnable={() => handleToggle(env.id, env.name)}
                                        handleDisable={() => handleToggle(env.id, env.name)}
                                        resourceName={env.name}
                                    />
                                </div>
                                <br/>
                            </div>
                                <ConstraintsList
                                    toggleId={featureId}
                                    constraints={constraints[env.id] || []}
                                    instanceId={instanceId}
                                    environmentId={env.id}
                                    refreshConstraints={() => refreshConstraints(env.id)}
                                />
                            <br/>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default FeatureToggleInstanceSectionRight;
