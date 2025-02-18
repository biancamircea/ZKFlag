import React, {useState} from 'react';
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";
import CancelButton from "../ui/common/CancelButton.jsx";

function UpdateTagForm({handleSubmit, name, description, colorProp}) {
    const [color, setColor] = useState(colorProp);
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}
        >
            <div className={"create-form-fields"}>
                <span className={"title"}>Edit tag type</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your tag name?</label>
                    <input
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Tag name"}
                        value={name}
                        readOnly
                        disabled
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
                        defaultValue={description}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Update tag
                </button>
            </div>
        </Form>
    );
}

export default UpdateTagForm;