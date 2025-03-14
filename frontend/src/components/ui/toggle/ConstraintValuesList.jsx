import React, { useState } from 'react';
import { toast } from "react-toastify";

function ConstraintValuesList({ values, setValues, instanceId }) {
    const [inputValue, setInputValue] = useState('');

    const handleAdd = (event) => {
        event.preventDefault();
        event.stopPropagation();
        if (inputValue === '') {
            toast.error('Value cannot be empty!');
        } else {
            // Dacă values este o listă de obiecte, verificăm dacă deja există un obiect cu același name
            const valueExists = values.some((val) => val === inputValue);

            if (valueExists) {
                toast.error('Value already exists in the list!');
            } else {
                const newValue =  inputValue; // Adaugă un obiect doar dacă există instanceId
                setValues([...values, newValue]);
                setInputValue('');
            }
        }
    };

    const handleDelete = (index) => {
        setValues((prevValues) => {
            const newValues = [...prevValues];
            newValues.splice(index, 1); // Șterge obiectul pe index
            return newValues;
        });
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            handleAdd(event);  // Trebuie să transmitem event-ul
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
