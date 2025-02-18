import React, {Suspense, useState} from 'react';
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import {createTag, getTags} from "../api/tagApi.js";
import CreateTagForm from "../components/forms/CreateTagForm.jsx";
import {toast} from "react-toastify";
import {createToggle} from "../api/featureToggleApi.js";

export function loader({ params }){
    return defer({ tags: getTags(params.projectId) })
}

function TagTypesCreate() {
    const loaderDataPromise = useLoaderData()
    const navigate = useNavigate();
    const { projectId } = useParams();
    const [disableSubmit, setDisableSubmit] = useState(false)
    let tagNames = []

    function handleNameInput(event) {

        const name = event.target.value;
        if (tagNames.includes(name)) {
            setDisableSubmit(true);
        } else {
            setDisableSubmit(false);
        }
    }

    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const description = formData.get("description");
        const color = formData.get("color")

        // console.log({name, description, color})
        let sanitizedName = null;
        if(name){
            sanitizedName = name.trim()
        }

        if (!sanitizedName || !color) {
            // One or both fields are empty
            toast.error("Please check empty fields!");
        } else {
            if(disableSubmit){
                toast.error("Please check invalid fields.");
            } else {
                try {
                    const requestSuccessful = await createTag(projectId,{labelName: name, description, color})
                    toast.success("Tag created.");
                    navigate(-1)
                } catch(err) {
                    return err.message
                }
            }
        }
    }

    function render(response){
        tagNames = response.tags.map(el => el.labelName)
        return (
            <CreateTagForm
                handleSubmit={handleSubmit}
                handleNameInput={handleNameInput}
                disableSubmit={disableSubmit}
            />
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.tags}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default TagTypesCreate;