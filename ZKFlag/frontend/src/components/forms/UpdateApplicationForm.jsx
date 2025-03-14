import React from 'react';
import {Form} from "react-router-dom";

function UpdateApplicationForm({handleSubmit, appUrl, description}) {
    return (
        <Form
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"appUrl"}>What is your application URL?</label>
                    <input
                        id={"appUrl"}
                        name={"appUrl"}
                        type={"text"}
                        placeholder={"Application URL"}
                        defaultValue={appUrl}
                    />
                </div>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>What is this application used for?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Application description"}
                        defaultValue={description}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <button type={"submit"}>
                    Save
                </button>
            </div>
        </Form>
    );
}

export default UpdateApplicationForm;