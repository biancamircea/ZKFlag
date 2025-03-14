import React, {Suspense, useState} from 'react';
import {Await, defer, Form, Link, useLoaderData, useNavigate} from "react-router-dom";
import {createProject, getProjects} from "../../api/projectApi.js";
import {toast} from "react-toastify";
import WarningField from "../../components/ui/common/WarningField.jsx";
import CancelButton from "../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader(){
    return defer({ projects: getProjects() })
}

function ProjectsCreate() {
    const loaderDataPromise = useLoaderData()
    let projectNames = []
    const [disableSubmit, setDisableSubmit] = useState(false)
    const navigate = useNavigate();
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
                    const requestSuccessful = await createProject({name, description})
                    toast.success("Project created.");
                    navigate(-1)
                } catch(err) {
                    return err.message
                }
            }
        }
    }

    function handleNameInput(event) {
        const name = event.target.value;
        if (projectNames.includes(name)) {
            setDisableSubmit(true);
        } else {
            setDisableSubmit(false);
        }
    }

    function renderForm(response) {
        projectNames = response.projects.map(el => el.name)
        return (
            <div>
                <br/>
            <Form
                className={"create-form-container"}
                onSubmit={handleSubmit}
                method={"post"}
            >
                <div className={"create-form-fields"}>
                    <span className={"title"}>Create project</span>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"name"}>What is your project name?</label>
                        {
                            disableSubmit
                            &&
                            <WarningField
                                message={"*project with that name already exists"}
                            />
                        }
                        <input
                            className={disableSubmit ? "invalid" : ""}
                            id={"name"}
                            name={"name"}
                            type={"text"}
                            placeholder={"Project name"}
                            onInput={handleNameInput}
                        />
                    </div>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"description"}>What is your project description?</label>
                        <textarea
                            id={"description"}
                            name={"description"}
                            placeholder={"Project description"}
                        />
                    </div>
                </div>
                <div className={"create-form-buttons"}>
                    <CancelButton/>
                    <button
                        type={"submit"}
                    >
                        Create project
                    </button>
                </div>
            </Form>
            </div>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.projects}>
                {
                    renderForm
                }
            </Await>
        </Suspense>
    );
}

export default ProjectsCreate;