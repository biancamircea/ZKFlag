import React, {Suspense} from 'react';
import {Await, defer, Outlet, useLoaderData} from "react-router-dom";
import ProjectHeader from "../ui/project/ProjectHeader.jsx";
import {getProjectOverview} from "../../api/projectApi.js";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";

export function loader({ params }){
    return defer({ project: getProjectOverview(params.projectId) })
}
function ProjectLayout(props) {
    const loaderDataPromise = useLoaderData()

    function render(response){
        return (
            <>
                <ProjectHeader
                    name={response.name}
                />
                <main className={"project-main-wrapper"}>
                    <Outlet/>
                </main>
            </>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.project}>
                {
                    render
                }
            </Await>
        </Suspense>

    );
}

export default ProjectLayout;