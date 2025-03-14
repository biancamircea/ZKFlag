import React, {Suspense} from 'react';
import {Await, defer, Form, Link, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {getProjectById, updateProject} from "../../api/projectApi.js";
import {toast} from "react-toastify";
import CancelButton from "../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }){
    return defer({ project: getProjectById(params.projectId) })
}

function ProjectEdit() {
    const loaderDataPromise = useLoaderData()
    const navigate = useNavigate();
    let receivedName = null
    let receivedDescription = null
    const { projectId } = useParams();
    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const description = formData.get("description");
        if(receivedDescription === description){
            // nothing changed
            toast.success("Project updated.")
            navigate(-1)
        } else {
            // PUT request
            const requestSuccessful = await updateProject(projectId,{name: receivedName, description})
            toast.success("Project updated.")
            navigate(-1)
        }
    }

    function renderForm(response){
        receivedDescription = response.description
        receivedName=response.name
        return (
            <Form
                onSubmit={handleSubmit}
                method={"put"}
                className={"create-form-container"}
            >
                <div className={"create-form-fields"}>
                    <span className={"title"}>Edit project</span>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"name"}>What is your project name?</label>
                        <input
                            id={"name"}
                            name={"name"}
                            type={"text"}
                            placeholder={"Project name"}
                            value={response.name}
                            readOnly
                            disabled
                        />
                    </div>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"description"}>What is your project description?</label>
                        <textarea
                            id={"description"}
                            name={"description"}
                            placeholder={"Project description"}
                            defaultValue={response.description}
                        />
                    </div>
                </div>
                <div className={"create-form-buttons"}>
                    <CancelButton/>
                    <button
                        type={"submit"}
                    >
                        Update project
                    </button>
                </div>
            </Form>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.project}>
                {
                    renderForm
                }
            </Await>
        </Suspense>
    );
}

export default ProjectEdit;