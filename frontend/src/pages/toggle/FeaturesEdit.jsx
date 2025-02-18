import React, {Suspense} from 'react';
import UpdateFeatureProjectForm from "../../components/forms/UpdateFeatureProjectForm.jsx";
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {getToggleFromProject, updateToggleFromProject} from "../../api/featureToggleApi.js";
import {toast} from "react-toastify";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }){
    return defer({ toggle: getToggleFromProject(params.projectId, params.featureId) })
}

function FeaturesEdit(props) {
    const navigate = useNavigate();
    const loaderDataPromise = useLoaderData()
    const { projectId, featureId} = useParams();
    let receivedName = null
    let receivedDescription = null
    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const description = formData.get("description");
        if(receivedDescription === description){
            // nothing changed
            toast.success("Feature toggle updated.")
            navigate(-1)
        } else {
            // PUT request
            const requestSuccessful = await updateToggleFromProject(projectId, featureId, {name: receivedName, description})
            toast.success("Feature toggle updated.")
            navigate(-1)
        }

    }

    function render(response){
        receivedName=response.name
        receivedDescription=response.description
        return (
            <UpdateFeatureProjectForm
                handleSubmit={handleSubmit}
                name={response.name}
                description={response.description}
            />
        );
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

export default FeaturesEdit;