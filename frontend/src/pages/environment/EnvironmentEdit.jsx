import React, {Suspense} from 'react';
import {getEnvironments, updateEnvironment} from "../../api/environmentApi.js";
import {Await, defer, Form, Link, useLoaderData, useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import CancelButton from "../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }) {
    return defer({ env: getEnvironments(params.envId) })
}

function EnvironmentEdit(props) {
    const envDataPromise = useLoaderData()
    const navigate = useNavigate();
    let receivedType = null
    let id = null
    let receivedName = null

    async function handleSubmit(event){
        event.preventDefault();
        const formData = new FormData(event.target);
        const type = formData.get("type");
        if(receivedType === type){
            // nothing changed
            toast.success("Environment updated.")
            navigate(-1)
        } else {
            // PUT request
            const requestSuccessful = await updateEnvironment(id,{name: receivedName, type})
            toast.success("Environment updated.")
            navigate(-1)
        }
    }

    function renderEnvironment(response){
        receivedType=response.type
        id=response.id
        receivedName=response.name
        return (
            <Form
                onSubmit={handleSubmit}
                method={"put"}
                className={"create-form-container"}
            >
                <div className={"create-form-fields"}>
                    <span className={"title"}>Edit environment</span>
                    <div className={"create-form-field-item"}>
                        <label htmlFor={"name"}>What is your environment name?</label>
                        <input
                            id={"name"}
                            name={"name"}
                            type={"text"}
                            placeholder={"Environment name"}
                            value={response.name}
                            disabled={true}
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
                                    defaultChecked={response.type === "DEVELOPMENT"}
                                />
                                <label htmlFor={"development"}>Development</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"test"}
                                    name={"type"}
                                    value={"TEST"}
                                    type={"radio"}
                                    defaultChecked={response.type === "TEST"}
                                />
                                <label htmlFor={"test"}>Test</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"pre-production"}
                                    name={"type"}
                                    value={"PRE_PRODUCTION"}
                                    type={"radio"}
                                    defaultChecked={response.type === "PRE_PRODUCTION"}
                                />
                                <label htmlFor={"pre-production"}>Pre-Production</label>
                            </div>
                            <div className={"field-item-select"}>
                                <input
                                    id={"production"}
                                    name={"type"}
                                    value={"PRODUCTION"}
                                    type={"radio"}
                                    defaultChecked={response.type === "PRODUCTION"}
                                />
                                <label htmlFor={"production"}>Production</label>
                            </div>
                        </fieldset>
                    </div>
                </div>
                <div className={"create-form-buttons"}>
                    <CancelButton/>
                    <button
                        type={"submit"}
                    >
                        Update environment
                    </button>
                </div>
            </Form>
        )
    }

    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={envDataPromise.env}>
                {
                    renderEnvironment
                }
            </Await>
        </Suspense>
    );
}

export default EnvironmentEdit;