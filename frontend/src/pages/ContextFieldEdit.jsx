import React, {Suspense} from 'react';
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {getContextField, updateContextField} from "../api/contextFieldApi.js";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import UpdateContextFieldForm from "../components/forms/UpdateContextFieldForm.jsx";
import {toast} from "react-toastify";

export function loader({ params }){
    return defer({ contextField: getContextField(params.projectId, params.contextFieldId) })
}

function ContextFieldEdit(props) {
    const loaderDataPromise = useLoaderData()
    const { projectId, contextFieldId } = useParams();
    const navigate = useNavigate();
    let receivedDescription = ""
    let receivedName = ""
    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const description = formData.get("description");
        if(receivedDescription === description){
            // nothing changed
            toast.success("Context field updated.")
            navigate(-1)
        } else {
            // PUT request
            const requestSuccessful = await updateContextField(projectId, contextFieldId , {name: receivedName, description})
            toast.success("Context field updated.")
            navigate(-1)
        }

    }

    function render(response){
        receivedDescription=response.description
        receivedName=response.name
        return (
            <UpdateContextFieldForm
                handleSubmit={handleSubmit}
                name={response.name}
                description={response.description}
            />
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.contextField}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default ContextFieldEdit;