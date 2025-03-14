import React, {Suspense, useState} from 'react';
import {Await, defer, Form, Link, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {createContextField, getContextFields} from "../api/contextFieldApi.js";
import CreateContextFieldForm from "../components/forms/CreateContextFieldForm.jsx";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import {toast} from "react-toastify";

export async function loader({ params }){
    return defer({ contextFields: getContextFields(params.projectId) })
}

function ContextFieldsCreate() {
    const loaderDataPromise = useLoaderData()
    let existingNames = []
    const { projectId } = useParams();
    const navigate = useNavigate();
    const [disableSubmit, setDisableSubmit] = useState(false)

    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const description = formData.get("description");

        let sanitizedName = null;
        if(name){
            sanitizedName = name.trim()
        }

        if (!sanitizedName) {
            // One or both fields are empty
            toast.error("Please check empty fields!");
        } else {
            if(disableSubmit){
                toast.error("Please check invalid fields.");
            } else {
                try {
                    const requestSuccessful = await createContextField(projectId,{name, description})
                    toast.success("Context field created.");
                    navigate(-1)
                } catch(err) {
                    return err.message
                }
            }
        }
    }

    function handleNameInput(event) {
        const name = event.target.value;
        if (existingNames.includes(name)) {
            setDisableSubmit(true);
        } else {
            setDisableSubmit(false);
        }
    }

    function render(response){
        existingNames = response['context-fields'].map(el => el.name)
        return (
            <CreateContextFieldForm
                handleSubmit={handleSubmit}
                handleNameInput={handleNameInput}
                disableSubmit={disableSubmit}
            />
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.contextFields}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default ContextFieldsCreate;