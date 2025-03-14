import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, Outlet, useLoaderData, useParams} from "react-router-dom";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";
import {getToggleFromProject} from "../../api/featureToggleApi.js";
import FeatureToggleHeader from "../ui/toggle/FeatureToggleHeader.jsx";
import {getTags} from "../../api/tagApi.js";
import {getContextFields} from "../../api/contextFieldApi.js";
import {getInstanceOverview} from "../../api/instanceApi.js";
import {getProjectById} from "../../api/projectApi.js";

export function loader({ params }){
    return defer({
        toggle: getToggleFromProject(params.projectId, params.featureId),
        tags: getTags(params.projectId),
        contextFields: getContextFields(params.projectId),
        project: getProjectById(params.projectId)
    })
}
function FeatureToggleInstanceLayout(props) {
   const {instanceId}= useParams()
    const loaderDataPromise = useLoaderData()
    const [tags, setTags] = useState([])
    const [contextFields, setContextFields] = useState([])
    const [toggle, setToggle] = useState(null)
    const [instanceName, setInstanceName] = useState("")
    const [projectName, setProjectName] = useState("")

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
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            const updatedEnvironment = { ...targetEnvironment, enabled };
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? updatedEnvironment : env
            );

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
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            const constraintIndex = targetEnvironment.constraints.findIndex(constraint => constraint.id === constraintId);

            if (constraintIndex !== -1) {
                const updatedConstraints = [
                    ...targetEnvironment.constraints.slice(0, constraintIndex),
                    updatedConstraint,
                    ...targetEnvironment.constraints.slice(constraintIndex + 1)
                ];

                const updatedEnvironments = toggle.environments.map(env =>
                    env.id === environmentId ? { ...env, constraints: updatedConstraints } : env
                );

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
            const updatedConstraints = targetEnvironment.constraints.filter(constraint => constraint.id !== constraintId);
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? { ...env, constraints: updatedConstraints } : env
            );

            setToggle(prevState => ({
                ...prevState,
                environments: updatedEnvironments
            }));
        }
    }
    function deleteAllConstraints(environmentId){
        const targetEnvironment = toggle.environments.find(env => env.id === environmentId);

        if (targetEnvironment) {
            const updatedEnvironments = toggle.environments.map(env =>
                env.id === environmentId ? { ...env, constraints: [] } : env
            );

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
        async function fetchToggles() {
            try {
                const contextFields = await loaderDataPromise.contextFields;
                const resolvedTags = await loaderDataPromise.tags;
                const toggle = await loaderDataPromise.toggle;

                setContextFields(contextFields || []);
                setTags(resolvedTags.tags);
                setToggle(toggle);
                setProjectName((await loaderDataPromise.project)?.name || "");


            } catch (error) {
                console.error("Error loading data:", error);
            }
        }

        fetchToggles();
    }, [loaderDataPromise]);

    function render(response){
        if (!toggle) {
            return <LoadingBanner />;
        }


        return (
            <>
                <FeatureToggleHeader
                    projectName={projectName}
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
            <Await resolve={toggle}>
                {
                    render
                }
            </Await>
        </Suspense>

    );
}

export default FeatureToggleInstanceLayout;