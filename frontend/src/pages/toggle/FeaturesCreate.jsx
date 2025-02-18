import React, {Suspense, useState} from 'react';
import 'react-dropdown/style.css';
import CreateFeatureProjectForm from "../../components/forms/CreateFeatureProjectForm.jsx";
import {toast} from "react-toastify";
import {createToggle, getTogglesFromProject} from "../../api/featureToggleApi.js";
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }){
    return defer({ toggles: getTogglesFromProject(params.projectId) })
}

function FeaturesCreate() {
    const loaderDataPromise = useLoaderData()
    const [disableSubmit, setDisableSubmit] = useState(false)
    const navigate = useNavigate();
    const { projectId } = useParams();
    let toggleNames = []
    function handleNameInput(event) {
        const name = event.target.value;
        if (toggleNames.includes(name)) {
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
                    const requestSuccessful = await createToggle(projectId,{name, description})
                    toast.success("Feature toggle created.");
                    navigate(-1)
                } catch(err) {
                    return err.message
                }
            }
        }
    }


    function render(response){
        toggleNames = response.toggles.map(el => el.name)
        return (
            <CreateFeatureProjectForm
                handleSubmit={handleSubmit}
                handleNameInput={handleNameInput}
                disableSubmit={disableSubmit}
            />
        );
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.toggles}>
                {
                    render
                }
            </Await>
        </Suspense>
    );
}

export default FeaturesCreate;