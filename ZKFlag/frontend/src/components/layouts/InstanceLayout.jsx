import React, {Suspense} from 'react';
import {Await, defer, Outlet, useLoaderData} from "react-router-dom";
import ProjectHeader from "../ui/project/ProjectHeader.jsx";
import {getInstanceOverview} from "../../api/instanceApi.js";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";
import InstanceHeader from "../ui/Instance/InstanceHeader.jsx";

export function loader({ params }){
    return defer({ instance: getInstanceOverview(params.instanceId) })
}
function InstanceLayout(props) {
    const loaderDataPromise = useLoaderData()

    function render(response){
        return (
            <>
                <InstanceHeader
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
            <Await resolve={loaderDataPromise.instance}>
                {
                    render
                }
            </Await>
        </Suspense>

    );
}

export default InstanceLayout;