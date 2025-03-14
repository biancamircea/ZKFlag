import React from 'react';
import CancelButton from "../ui/common/CancelButton.jsx";
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";

function CreateInstanceForm({handleSubmit, handleNameInput, disableSubmit}) {
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Create instance</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your instance name?</label>
                    {
                        disableSubmit
                        &&
                        <WarningField
                            message={"*instance with that name already exists in project"}
                        />
                    }
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Instance name"}
                        onInput={handleNameInput}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Create instance
                </button>
            </div>
        </Form>
    );
}

export default CreateInstanceForm;