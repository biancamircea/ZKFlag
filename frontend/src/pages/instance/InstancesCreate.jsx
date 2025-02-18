import React, {Suspense, useState} from 'react';
import {Await, defer, Form, Link, useLoaderData, useNavigate, useParams} from "react-router-dom";
import CreateInstanceForm from "../../components/forms/CreateInstanceForm.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import {toast} from "react-toastify";
import {getAllInstancesFromProject} from "../../api/instanceApi.js";
import {createInstance} from "../../api/instanceApi.js";

export async function loader({ params }){
    return defer({ instances: getAllInstancesFromProject(params.projectId) })
}

function InstancesCreate() {
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
            toast.error("Please check empty fields!");
        } else {
            if(disableSubmit){
                toast.error("Please check invalid fields.");
            } else {
                try {
                    const requestSuccessful = await createInstance(projectId, { name: sanitizedName });
                    toast.success("Instance created.");
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
        existingNames = response.map(el => el.name)
        return (
            <CreateInstanceForm
                handleSubmit={handleSubmit}
                handleNameInput={handleNameInput}
                disableSubmit={disableSubmit}
            />
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.instances}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default InstancesCreate;