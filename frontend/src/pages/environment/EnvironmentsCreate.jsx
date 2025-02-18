import React, {Suspense, useState} from 'react';
import {Await, defer, Form, Link, useLoaderData, useNavigate} from "react-router-dom";
import {createEnvironment, getEnvironments} from "../../api/environmentApi.js";
import {toast} from "react-toastify";
import WarningField from "../../components/ui/common/WarningField.jsx";
import CancelButton from "../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader(){
    return defer({ env: getEnvironments() })
}

function EnvironmentsCreate() {
    const envDataPromise = useLoaderData()
    const navigate = useNavigate();
    const [disableSubmit, setDisableSubmit] = useState(false)
    let envNames = []

    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const type = formData.get("type");
        let sanitizedName = null;
        if(name){
            sanitizedName = name.trim()
        }

        if (!sanitizedName || !type) {
            // One or both fields are empty
            toast.error("Please check empty fields!");
        } else {
            if(disableSubmit){
                toast.error("Please check invalid fields.");
            } else {
                try {
                    const requestSuccessful = await createEnvironment({name, type})
                    toast.success("Environment created.");
                    navigate(-1)
                } catch(err) {
                    return err.message
                }
            }
        }
    }

    function handleNameInput(event) {
        // console.log(envNames)
        const name = event.target.value;
        if (envNames.includes(name)) {
            setDisableSubmit(true);
        } else {
            setDisableSubmit(false);
        }
    }

    function renderForm(response){
        envNames = response.environments.map(el => el.name)

        return (
            <Form
                onSubmit={handleSubmit}
                method={"post"}
                className={"create-form-container"}
            >
                <div className={"create-form-fields"}>
                    <span className={"title"}>Create environment</span>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"name"}>What is your environment name?</label>
                        {
                            disableSubmit
                            &&
                            <WarningField
                                message={"*environment with that name already exists"}
                            />
                        }
                        <input
                            className={disableSubmit ? "invalid" : ""}
                            id={"name"}
                            name={"name"}
                            type={"text"}
                            placeholder={"Environment name"}
                            onInput={handleNameInput}
                        />
                    </div>
                    <div className={"create-form-field-item"}>
                        <fieldset>
                            <legend>What type is your environment?</legend>
                            <div className={"field-item-select"}>
                                <input
                                    id={"development"}
                                    name={"type"}
                                    value={"DEVELOPMENT"}
                                    type={"radio"}
                                />
                                <label htmlFor={"development"}>Development</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"test"}
                                    name={"type"}
                                    value={"TEST"}
                                    type={"radio"}
                                />
                                <label htmlFor={"test"}>Test</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"pre-production"}
                                    name={"type"}
                                    value={"PRE_PRODUCTION"}
                                    type={"radio"}
                                />
                                <label htmlFor={"pre-production"}>Pre-Production</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"production"}
                                    name={"type"}
                                    value={"PRODUCTION"}
                                    type={"radio"}
                                />
                                <label htmlFor={"production"}>Production</label>
                            </div>

                        </fieldset>
                    </div>
                    <div className={"field-item-select"}>
                        <input
                            id={"apikey-checkbox"}
                            name={"apikey-checkbox"}
                            type={"checkbox"}/>
                        <label htmlFor={"apikey-checkbox"}>Generate API Key for environment now?</label>
                    </div>
                </div>
                <div className={"create-form-buttons"}>
                    <CancelButton/>
                    <button
                        type={"submit"}
                        // disabled={disableSubmit}
                    >
                        Create environment
                    </button>
                </div>
            </Form>

        );

    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={envDataPromise.env}>
                {
                    renderForm
                }
            </Await>
        </Suspense>
    );
}

export default EnvironmentsCreate;