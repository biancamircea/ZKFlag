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

    function addFeatureTag(id){
        const newTag = tags.find((item) => item.id === id);
        const updatedTags = [...toggle.tags, newTag];
        setToggle(prevState => ({
            ...prevState,
            tags: updatedTags
        }));
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