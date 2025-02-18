import React, {useState} from 'react';
import {toast} from "react-toastify";

function ConstraintValuesList({values, setValues}) {
    const [inputValue, setInputValue] = useState('');

    const handleAdd = (event) => {
        event.preventDefault()
        event.stopPropagation()
        if(inputValue === ''){
            toast.error('Value cannot be empty!')
        } else {
            if (values.includes(inputValue)) {
                // if the value is already in the list, show an error toast
                toast.error('Value already exists in the list!');
            } else {
                // otherwise, add the value to the list and clear the input field
                setValues([...values, inputValue]);
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
            handleAdd();
        }
    };

    const valuesEl = values.map((value, index) => (
        <div key={index} className="constraint-value-container">
            <span className="value">{value}</span>
            <button className="delete-button" onClick={(event) => {
                event.preventDefault()
                event.stopPropagation()
                handleDelete(index)
            }}>
                x
            </button>
        </div>
    ))

    return (
        <div className={"constraint-values-wrapper"}>
            <div className={"constraint-values-input-wrapper"}>
                <input
                    type="text"
                    placeholder={"Insert value"}
                    value={inputValue}
                    onChange={e => setInputValue(e.target.value)}
                    onKeyDown={handleKeyDown}
                />
                <button onClick={handleAdd}>
                    Add
                </button>
            </div>
            <div className="constraint-values-list">
                {
                    valuesEl.length === 0 ?
                        (<span className={"gray-text"} style={{padding: '0 1em'}}>No item.</span>) :
                        valuesEl
                }
            </div>
        </div>
    );
}

export default ConstraintValuesList;