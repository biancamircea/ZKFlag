import React from 'react';
import {Form} from "react-router-dom";
import CancelButton from "../ui/common/CancelButton.jsx";

function UpdateContextFieldForm({handleSubmit, name, description}) {
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Edit context field</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your context name?</label>
                    <input
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Context name"}
                        value={name}
                        readOnly
                        disabled
                    />
                </div>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>What is this context used for?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Context description"}
                        defaultValue={description}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Update context
                </button>
            </div>
        </Form>
    );
}

export default UpdateContextFieldForm;