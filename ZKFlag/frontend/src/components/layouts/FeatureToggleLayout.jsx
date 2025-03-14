import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, Outlet, useLoaderData} from "react-router-dom";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";
import {getToggleFromProject, getTogglesFromProject} from "../../api/featureToggleApi.js";
import FeatureToggleHeader from "../ui/toggle/FeatureToggleHeader.jsx";
import {getTags} from "../../api/tagApi.js";
import {getContextFields} from "../../api/contextFieldApi.js";
import {getProjectById} from "../../api/projectApi.js";
import {MenuItem} from "@mui/material";

export function loader({ params }){
    return defer({
        toggle: getToggleFromProject(params.projectId, params.featureId),
        tags: getTags(params.projectId),
        contextFields: getContextFields(params.projectId),
        project: getProjectById(params.projectId)
    })
}

function FeatureToggleLayout(props) {
    const loaderDataPromise = useLoaderData()
    const [tags, setTags] = useState([])
    const [contextFields, setContextFields] = useState([])
    const [toggle, setToggle] = useState(null)
    const [projectName, setProjectName] = useState("")

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
                loaderDataPromise.contextFields.then(resolvedContextFields => {
                    setContextFields(resolvedContextFields || []);
                });

                const resolvedTags = await loaderDataPromise.tags;

                setTags(resolvedTags.tags);
                setToggle(await loaderDataPromise.toggle);

                loaderDataPromise.project.then(resolvedproject => {
                    setProjectName(resolvedproject.name || "");
                })

            } catch (error) {
                console.error("Error loading data:", error);
            }
        }

        fetchToggles();
    }, [loaderDataPromise]);



    function render({ toggle, tags, contextFields, project }) {
        setToggle(toggle);
        setTags(tags);
        setContextFields(contextFields);
        setProjectName(project?.name ?? "");


        return (
            <>
                <FeatureToggleHeader
                    projectName={project?.name ?? ""}
                    toggleName={toggle.name}
                    tags={tags}
                    featureTags={toggle.tags}
                    addFeatureTag={addFeatureTag}
                />
                <main className="feature-toggle-main-wrapper">
                    <Outlet context={{ toggle, tags, contextFields }} />
                </main>
            </>
        );
    }

    return (
        <Suspense fallback={<LoadingBanner />}>
            <Await resolve={loaderDataPromise.tags}>
                {(resolvedTags) => (
                    <Await resolve={loaderDataPromise.toggle}>
                        {(resolvedToggle) => (
                            <Await resolve={loaderDataPromise.contextFields}>
                                {(resolvedContextFields) => (
                                    <Await resolve={loaderDataPromise.project}>
                                        {(resolvedProject) =>
                                            render({
                                                toggle: resolvedToggle,
                                                tags: resolvedTags.tags,
                                                contextFields: resolvedContextFields["context-fields"],
                                                project: resolvedProject
                                            })
                                        }
                                    </Await>
                                )}
                            </Await>
                        )}
                    </Await>
                )}
            </Await>
        </Suspense>
    );

}

export default FeatureToggleLayout;