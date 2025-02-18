import React, {Suspense} from 'react';
import {Await, defer, Outlet, useLoaderData, useNavigate} from "react-router-dom";
import {deleteApplication, getApplicationDetails} from "../../api/applicationApi.js";
import LoadingBanner from "../ui/common/LoadingBanner.jsx";
import ApplicationPath from "../ui/application/ApplicationPath.jsx";
import ApplicationHeader from "../ui/application/ApplicationHeader.jsx";
import ApplicationBody from "../ui/application/ApplicationBody.jsx";
import {deleteAllConstraintsFromToggleEnv} from "../../api/featureToggleApi.js";
import {toast} from "react-toastify";

export function loader({ params }){
    return defer({ application: getApplicationDetails(params.appId) })
}

function ApplicationsLayout(props) {
    const loaderDataPromise = useLoaderData()
    const navigate = useNavigate()

    async function deleteHandler(appId) {
        const res = await deleteApplication(appId);
        if (res) {
            toast.success("Application deleted.");
            navigate(-1)
        } else {
            toast.error("Operation failed.");
        }
    }

    function render(response){
        return (
            <>
                <ApplicationPath
                    name={response.name}
                />
                <ApplicationHeader
                    name={response.name}
                    deleteHandler={() => deleteHandler(response.id)}
                />
                <ApplicationBody
                    applicationProp={response}
                />
            </>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.application}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default ApplicationsLayout;