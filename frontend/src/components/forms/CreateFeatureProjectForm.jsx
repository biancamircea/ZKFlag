import React from 'react';
import Dropdown from "react-dropdown";
import CancelButton from "../ui/common/CancelButton.jsx";
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";

function CreateFeatureProjectForm({handleSubmit, handleNameInput, disableSubmit}) {
    const types = [
        { value: 'standard', label: 'Standard' },
        { value: 'experimental', label: 'Experimental' }
    ]

    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}
        >
            <div className={"create-form-fields"}>
                <span className={"title"}>Create feature toggle</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your toggle name?</label>
                    {
                        disableSubmit
                        &&
                        <WarningField
                            message={"*feature toggle with that name already exists"}
                        />
                    }
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Feature toggle name"}
                        onInput={handleNameInput}
                    />
                </div>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"type"}>What type is your toggle?</label>
                    <Dropdown
                        options={types }
                        controlClassName={"form-dropdown"}
                        menuClassName={"menu-dropdown"}
                        placeholder={"Select an option"}
                    />
                </div>



                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>How would you describe your feature toggle?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Feature toggle description"}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Create feature
                </button>
            </div>
        </Form>
    );
}

export default CreateFeatureProjectForm;