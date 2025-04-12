import React from 'react';
import {Form} from "react-router-dom";
import Select from 'react-select';
import CancelButton from "../ui/common/CancelButton.jsx";

function UpdateFeatureProjectForm({handleSubmit, name, description, handleTypeInput, selectedType, type}) {
    const types = [
        { value: 0, label: 'Frontend' },
        { value: 1, label: 'Backend' },
        {value: 2, label:'Both'}
    ];

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
                <div className={"create-form-field-item"} style={{width: "500px"}}>
                    <label htmlFor={"type"}>What type is your toggle?</label>
                    <Select
                        options={types}
                        menuClassName={"menu-dropdown"}
                        onChange={handleTypeInput}
                        defaultValue={types.find(type2 => type2.value === type)}
                        placeholder={"Select an option"}
                        isDisabled={true}
                        sx={{
                            cursor: "not-allowed",
                            backgroundColor: "#f5f5f5",
                            color: "rgba(0, 0, 0, 0.6)",
                        }}
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