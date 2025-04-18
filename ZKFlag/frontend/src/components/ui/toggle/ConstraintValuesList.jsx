import React, { useState } from 'react';
import { toast } from "react-toastify";
import {Info} from "react-feather";
import Tooltip from "@mui/material/Tooltip";

function ConstraintValuesList({ values, setValues, instanceId }) {
    const [inputValue, setInputValue] = useState('');

    const handleAdd = (event) => {
        event.preventDefault();
        event.stopPropagation();
        if (inputValue === '') {
            toast.error('Value cannot be empty!');
        } else {
            const valueExists = values.some((val) => val === inputValue);

            if (valueExists) {
                toast.error('Value already exists in the list!');
            } else {
                const newValue =  inputValue;
                setValues([...values, newValue]);
                setInputValue('');
            }
        }
    };

    const handleDelete = (index) => {
        setValues((prevValues) => {
            const newValues = [...prevValues];
            newValues.splice(index, 1);
            return newValues;
        });
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            handleAdd(event);
        }
    };

    const valuesEl = values.map((value, index) => (
        <div key={index} className="constraint-value-container">
                <span className="value">{value}</span>


            <button
                className="delete-button"
                onClick={(event) => {
                    event.preventDefault();
                    event.stopPropagation();
                    handleDelete(index);
                }}
            >
                x
            </button>
        </div>
    ));

    return (
        <div className={"constraint-values-wrapper"}>
            <div className={"constraint-values-input-wrapper"}>
                <input
                    type="text"
                    placeholder={"Insert value"}
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyDown={handleKeyDown}
                />
                <button onClick={handleAdd}>
                    Add
                </button>

                <Tooltip
                    title={"You can add only one value at a time (string or number). For the \"Greater Than\" and \"Less Than\" operators, only integers are allowed."}
                    arrow style={{display:"flex", alignItems:"center"}}
                >
                            <span style={{display:"flex", alignItems:"center"}}>
                                <Info className="info-icon" size={25} />
                            </span>
                </Tooltip>
            </div>
            <div className="constraint-values-list">
                {valuesEl.length === 0 ? (
                    <span className={"gray-text"} style={{ padding: '0 1em' }}>No item.</span>
                ) : (
                    valuesEl
                )}
            </div>
        </div>
    );
}

export default ConstraintValuesList;
