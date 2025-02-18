import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, Outlet, useLoaderData, useParams} from "react-router-dom";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";
import {getToggleFromProject} from "../../api/featureToggleApi.js";
import FeatureToggleHeader from "../ui/toggle/FeatureToggleHeader.jsx";
import {getTags} from "../../api/tagApi.js";
import {getContextFields} from "../../api/contextFieldApi.js";
import {getInstanceOverview} from "../../api/instanceApi.js";
export function loader({ params }){
    return defer({
        toggle: getToggleFromProject(params.projectId, params.featureId),
        tags: getTags(params.projectId),
        contextFields: getContextFields(params.projectId)
    })
}
function FeatureToggleInstanceLayout(props) {
   const {instanceId}= useParams()
    const loaderDataPromise = useLoaderData()
    const [tags, setTags] = useState([])
    const [contextFields, setContextFields] = useState([])
    const [toggle, setToggle] = useState(null)
    const [instanceName, setInstanceName] = useState("")

    useEffect(() => {
        async function getInstanceName() {
            try {
                const data = await getInstanceOverview(instanceId);
                setInstanceName(data.name);
            } catch (error) {
                console.error("Error fetching environments:", error);
            }
        }

        getInstanceName();
    },[instanceId]);

    function enableDisableToggleEnvironment(environmentId, enabled){
        // Find the target environment in the state's 'environments' array
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            // Update the 'enabled' property of the environment
            const updatedEnvironment = { ...targetEnvironment, enabled };

            // Create a new array of environments with the updated environment
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? updatedEnvironment : env
            );

            // Update the state with the updated environments array
            setToggle(prevState => ({
                ...prevState,
                environments: updatedEnvironments
            }));
        }
    };
    function addFeatureTag(id){
        const newTag = tags.find((item) => item.id === id);
        const updatedTags = [...toggle.tags, newTag];
        setToggle(prevState => ({
            ...prevState,
            tags: updatedTags
        }));
    }
    function removeFeatureTag(id){
        const updatedTags = toggle.tags.filter(tag => tag.id !== id)
        setToggle(prevState => ({
            ...prevState,
            tags: updatedTags
        }));
    }
    function addConstraint(environmentId, newConstraint){
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if(targetEnvironment){
            const updatedConstraints = [...targetEnvironment.constraints, newConstraint];

            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? { ...env, constraints: updatedConstraints } : env
            );

            setToggle(prevState => ({
                ...prevState,
                environments: updatedEnvironments
            }));

        }
    }
    function updateConstraint(environmentId, constraintId, updatedConstraint){
        // Find the target environment in the state's 'environments' array
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            // Find the index of the constraint within the environment's 'constraints' array
            const constraintIndex = targetEnvironment.constraints.findIndex(constraint => constraint.id === constraintId);

            if (constraintIndex !== -1) {
                // Create a new array of constraints with the updated constraint at the specified index
                const updatedConstraints = [
                    ...targetEnvironment.constraints.slice(0, constraintIndex),
                    updatedConstraint,
                    ...targetEnvironment.constraints.slice(constraintIndex + 1)
                ];

                // Create a new array of environments with the updated constraints
                const updatedEnvironments = toggle.environments.map(env =>
                    env.id === environmentId ? { ...env, constraints: updatedConstraints } : env
                );

                // Update the state with the updated environments array
                setToggle(prevState => ({
                    ...prevState,
                    environments: updatedEnvironments
                }));
            }
        }
    }
    function deleteConstraint(environmentId, constraintId){
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            // Filter out the constraint to be deleted
            const updatedConstraints = targetEnvironment.constraints.filter(constraint => constraint.id !== constraintId);

            // Create a new array of environments with the updated constraints
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? { ...env, constraints: updatedConstraints } : env
            );

            // Update the state with the updated environments array
            setToggle(prevState => ({
                ...prevState,
                environments: updatedEnvironments
            }));
        }
    }
    function deleteAllConstraints(environmentId){
        // Find the target environment in the state's 'environments' array
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            // Remove all constraints by assigning an empty array to the 'constraints' property
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? { ...env, constraints: [] } : env
            );

            // Update the state with the updated environments array
            setToggle(prevState => ({
                ...prevState,
                environments: updatedEnvironments
            }));
        }
    }

    function addPayload(updatedToggleEnvironment){
        const newToggle = { ...toggle };
        const environmentIndex = newToggle.environments.findIndex(env => env.id === updatedToggleEnvironment.id);

        if (environmentIndex !== -1) {
            newToggle.environments[environmentIndex] = updatedToggleEnvironment;
            setToggle(newToggle);
        }
    }
    function removePayload(environmentId){
        const newState = { ...toggle };
        const environmentIndex = newState.environments.findIndex(env => env.id === environmentId);

        if (environmentIndex !== -1) {
            newState.environments[environmentIndex].enabledValue = null;
            newState.environments[environmentIndex].disabledValue = null;
            setToggle(newState);
        }
    }

    useEffect(() => {
        loaderDataPromise.contextFields.then((res) =>  setContextFields(res['context-fields']))
        loaderDataPromise.tags.then((res) =>  setTags(res.tags))
        loaderDataPromise.toggle.then((res) => {setToggle(res)})
    }, [])
    function render(response){
        return (
            <>
                <FeatureToggleHeader
                    projectName={toggle.project}
                    toggleName={toggle.name}
                    tags={tags}
                    featureTags={toggle.tags}
                    addFeatureTag={addFeatureTag}
                    instanceName={instanceName}
                />
                <main className={"feature-toggle-main-wrapper"}>
                    <Outlet context={{
                        toggle,
                        removeFeatureTag,
                        enableDisableToggleEnvironment,
                        addConstraintHandler: addConstraint,
                        updateConstraintHandler: updateConstraint,
                        deleteConstraintHandler: deleteConstraint,
                        deleteAllConstraintsHandler: deleteAllConstraints,
                        addPayload,
                        removePayload,
                        contextFields
                    }}/>
                </main>
            </>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.toggle}>
                {
                    render
                }
            </Await>
        </Suspense>

    );
}

export default FeatureToggleInstanceLayout;