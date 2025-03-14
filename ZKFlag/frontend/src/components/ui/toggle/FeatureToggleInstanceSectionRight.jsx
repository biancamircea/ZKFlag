// import React, { useEffect, useState } from "react";
// import { isToggleEnabled, toggleFeature } from "../../../api/featureToggleApi";
// import ProjectEnvironmentSwitch from "../project/ProjectEnvironmentSwitch.jsx";
//
// function FeatureToggleInstanceSectionRight({ projectId, featureId, instanceId, environments }) {
//     const [toggleStates, setToggleStates] = useState({});
//     const [isLoading, setIsLoading] = useState(true); // Pentru a evita randarea prematură
//
//     useEffect(() => {
//         async function fetchToggleStates() {
//             try {
//                 setIsLoading(true); // Blocăm interfața până preluăm datele
//
//                 const statesArray = await Promise.all(
//                     environments.map(async (env) => ({
//                         id: env.id,
//                         isEnabled: await isToggleEnabled(featureId, env.id, instanceId),
//                     }))
//                 );
//
//                 const states = Object.fromEntries(statesArray.map(({ id, isEnabled }) => [id, isEnabled]));
//                 setToggleStates(states);
//             } catch (error) {
//                 console.error("Failed to fetch toggle states:", error);
//             } finally {
//                 setIsLoading(false); // Permitem randarea
//             }
//         }
//
//         fetchToggleStates();
//     }, [featureId, instanceId, environments]);
//
//
//     const handleToggle = async (envId, envName) => {
//         const currentState = toggleStates[envId] ?? false;
//         const newState = !currentState;
//
//         try {
//             await toggleFeature(projectId, featureId, instanceId, envName, newState);
//             setToggleStates((prevState) => ({
//                 ...prevState,
//                 [envId]: newState,
//             }));
//         } catch (error) {
//             console.error("Failed to toggle feature:", error);
//         }
//     };
//
//     return (
//         <div className="feature-toggle-instance-section-right">
//             <div className="list-container">
//                 <div className="project-environment list-item item-header" style={{ display: "flex" }}>
//                     <p>Name</p>
//                     <p style={{ marginLeft: "300px" }}>Active in environment</p>
//                 </div>
//                 {isLoading ? (
//                     <p>Loading...</p>
//                 ) : (
//                     environments.map((env) => (
//                         <div
//                             className="project-environment list-item"
//                             key={env.id}
//                             style={{ flexGrow: 1, width: "100%", display: "flex" }}
//                         >
//                             <span>{env.name}</span>
//                             <div className="list-item-actions" style={{ marginLeft: `${300 - 7.5 * env.name.length}px` }}>
//                                 <ProjectEnvironmentSwitch
//                                     checked={toggleStates[env.id] ?? false} // Asigurăm că nu e undefined
//                                     handleEnable={() => handleToggle(env.id, env.name)}
//                                     handleDisable={() => handleToggle(env.id, env.name)}
//                                     resourceName={env.name}
//                                 />
//                             </div>
//                         </div>
//                     ))
//                 )}
//             </div>
//         </div>
//     );
// }
//
// export default FeatureToggleInstanceSectionRight;
import React, { useEffect, useState } from "react";
import { isToggleEnabled, toggleFeature, getAllConstraintsForInstanceEnvironment } from "../../../api/featureToggleApi";
import ProjectEnvironmentSwitch from "../project/ProjectEnvironmentSwitch.jsx";
import ConstraintsList from "./ConstraintsList.jsx";

function FeatureToggleInstanceSectionRight({ projectId, featureId, instanceId, environments }) {
    const [toggleStates, setToggleStates] = useState({});
    const [constraints, setConstraints] = useState({});
    const [isLoading, setIsLoading] = useState(true);

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
                ) : (
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
