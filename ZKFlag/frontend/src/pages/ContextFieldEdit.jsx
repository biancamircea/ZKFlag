import React, {Suspense, useState} from 'react';
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {getContextField, updateContextField} from "../api/contextFieldApi.js";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import UpdateContextFieldForm from "../components/forms/UpdateContextFieldForm.jsx";
import {toast} from "react-toastify";

export function loader({ params }) {
    return defer({ contextField: getContextField(params.projectId, params.contextFieldId) })
}

function ContextFieldEdit() {
    const loaderDataPromise = useLoaderData();
    const { projectId, contextFieldId } = useParams();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        isConfidential: 0
    });

    async function handleSubmit(event) {
        event.preventDefault();
        try {
            await updateContextField(projectId, contextFieldId, {
                name: formData.name,
                description: formData.description,
                isConfidential: Number(formData.isConfidential)
            });
            toast.success("Context field updated.");
            navigate(-1);
        } catch (error) {
            toast.error("Failed to update context field");
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    }

    function render(response) {
        // Initialize form data only once
        if (formData.name === '') {
            setFormData({
                name: response.name,
                description: response.description,
                isConfidential: response.isConfidential || 0
            });
        }

        return (
            <UpdateContextFieldForm
                handleSubmit={handleSubmit}
                formData={formData}
                handleChange={handleChange}
            />
        );
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.contextField}>
                {render}
            </Await>
        </Suspense>
    );
}

export default ContextFieldEdit;