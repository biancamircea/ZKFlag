import React from 'react';
import CancelButton from "../ui/common/CancelButton.jsx";
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";

function CreateContextFieldForm({handleSubmit, handleNameInput, disableSubmit}) {
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Create context field</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your context name?</label>
                    {
                        disableSubmit
                        &&
                        <WarningField
                            message={"*context field with that name already exists"}
                        />
                    }
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Context name"}
                        onInput={handleNameInput}
                    />
                </div>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>What is this context used for?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Context description"}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Create context
                </button>
            </div>
        </Form>
    );
}

export default CreateContextFieldForm;