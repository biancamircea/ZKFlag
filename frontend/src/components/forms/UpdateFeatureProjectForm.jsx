import React from 'react';
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";
import Dropdown from "react-dropdown";
import CancelButton from "../ui/common/CancelButton.jsx";

function UpdateFeatureProjectForm({handleSubmit, name, description}) {
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
                <span className={"title"}>Edit feature toggle</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your toggle name?</label>
                    <input
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Feature toggle name"}
                        value={name}
                        readOnly
                        disabled
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
                        defaultValue={description}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Update feature
                </button>
            </div>
        </Form>
    );
}

export default UpdateFeatureProjectForm;