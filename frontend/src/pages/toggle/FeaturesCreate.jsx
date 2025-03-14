import React, {Suspense, useState} from 'react';
import 'react-dropdown/style.css';
import CreateFeatureProjectForm from "../../components/forms/CreateFeatureProjectForm.jsx";
import {toast} from "react-toastify";
import {createToggle, getTogglesFromProject} from "../../api/featureToggleApi.js";
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }) {
    return defer({ toggles: getTogglesFromProject(params.projectId) });
}


function FeaturesCreate() {
    const loaderDataPromise = useLoaderData();
    const [disableSubmit, setDisableSubmit] = useState(false);
    const [selectedType, setSelectedType] = useState(0);
    const navigate = useNavigate();
    const { projectId } = useParams();
    let toggleNames = [];

    function handleNameInput(event) {
        const name = event.target.value;
        setDisableSubmit(toggleNames.includes(name));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const description = formData.get("description");

        let sanitizedName = name ? name.trim() : null;

        if (!sanitizedName) {
            toast.error("Please check empty fields!");
            return;
        }

        if (disableSubmit) {
            toast.error("Please check invalid fields.");
            return;
        }

        if (selectedType === null || selectedType === undefined) {
            toast.error("Please select a valid toggle type.");
            return;
        }

        try {
            await createToggle(projectId, { name, description, toggle_type: selectedType });
            toast.success("Feature toggle created.");
            navigate(-1);
        } catch (err) {
            toast.error("Error creating feature toggle.");
            console.error(err);
        }
    }

    function handleTypeInput(selectedOption) {
        setSelectedType(selectedOption.value);
    }

    function render(response) {
        toggleNames = response.toggles.map(el => el.name);
        return (
            <CreateFeatureProjectForm
                handleSubmit={handleSubmit}
                handleNameInput={handleNameInput}
                disableSubmit={disableSubmit}
                handleTypeInput={handleTypeInput}
                selectedType={selectedType}
            />
        );
    }

    return (
        <Suspense fallback={<LoadingBanner />}>
            <Await resolve={loaderDataPromise.toggles}>
                {render}
            </Await>
        </Suspense>
    );
}

export default FeaturesCreate;
