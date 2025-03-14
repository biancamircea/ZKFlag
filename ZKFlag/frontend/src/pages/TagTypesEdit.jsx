import React, {Suspense} from 'react';
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {getTag, updateTag} from "../api/tagApi.js";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import UpdateTagForm from "../components/forms/UpdateTagForm.jsx";
import {toast} from "react-toastify";

export function loader({ params }){
    return defer({ tag: getTag(params.projectId, params.tagId) })
}

function TagTypesEdit(props) {
    const loaderDataPromise = useLoaderData()
    const { projectId, tagId } = useParams();
    const navigate = useNavigate();

    let receivedDescription = ""
    let receivedName = ""
    let receivedColor = ""

    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const description = formData.get("description");
        const color = formData.get("color")

        if(receivedDescription === description && receivedColor === color){
            // nothing changed
            toast.success("Tag updated.")
            navigate(-1)
        } else {
            // PUT request
            const requestSuccessful = await updateTag(projectId, tagId , {labelName: receivedName, description, color})
            toast.success("Tag updated.")
            navigate(-1)
        }
    }

    function render(response){
        receivedDescription=response.description
        receivedName=response.labelName
        receivedColor=response.color
        return (
            <UpdateTagForm
                handleSubmit={handleSubmit}
                name={response.labelName}
                description={response.description}
                colorProp={response.color}
            />
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.tag}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default TagTypesEdit;