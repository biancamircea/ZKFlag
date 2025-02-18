import React, {useState} from 'react';
import {Form} from "react-router-dom";
import CancelButton from "../ui/common/CancelButton.jsx";
import WarningField from "../ui/common/WarningField.jsx";

function CreateTagForm({handleSubmit, handleNameInput, disableSubmit}) {
    const [color, setColor] = useState("#5e72e4");
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}
        >
            <div className={"create-form-fields"}>
                <span className={"title"}>Create tag type</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your tag name?</label>
                    {
                        disableSubmit
                        &&
                        <WarningField
                            message={"*tag with that name already exists"}
                        />
                    }
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Tag name"}
                        onInput={handleNameInput}
                    />
                </div>
                <div className={"create-form-field-item"}>
                    <div className={"create-form-field-item color-field"}>
                        <label htmlFor={"name"}>Pick a color:</label>
                        <input
                            id={"color-input"}
                            name={"color"}
                            type={"color"}
                            value={color}
                            onChange={e => setColor(e.target.value)}
                        />
                        <div
                            style={{
                                width: 100,
                                height: 30,
                                marginTop: 0,
                                backgroundColor: color,
                                border: "1px solid black",
                                borderRadius: 10
                            }}
                        />
                    </div>
                </div>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>What is your tag description?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Tag description"}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Create tag
                </button>
            </div>
        </Form>
    );
}

export default CreateTagForm;