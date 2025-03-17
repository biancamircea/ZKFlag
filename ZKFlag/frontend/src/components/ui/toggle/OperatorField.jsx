import React, { useState } from 'react';

function OperatorField({ defaultOperator }) {
    // Setăm operatorul implicit la 'IN' dacă nu există un operator prestabilit
    const [operator, setOperator] = useState(defaultOperator ? defaultOperator : "IN");

    function handleOperatorChange(e) {
        setOperator(e.target.value);
    }

    return (
        <div className="constraint-form-operator">
            <label className={`constraint-form-operator-label ${operator === "IN" ? "selected" : ""}`}>
                <input
                    type="radio"
                    name="operator"
                    value="IN"
                    className="constraint-form-operator-input"
                    checked={operator === "IN"}
                    onChange={handleOperatorChange}
                />
                IN
            </label>
            <label className={`constraint-form-operator-label ${operator === "NOT_IN" ? "selected" : ""}`}>
                <input
                    type="radio"
                    name="operator"
                    value="NOT_IN"
                    className="constraint-form-operator-input"
                    checked={operator === "NOT_IN"}
                    onChange={handleOperatorChange}
                />
                NOT IN
            </label>
            <label className={`constraint-form-operator-label ${operator === "GREATER_THAN" ? "selected" : ""}`}>
                <input
                    type="radio"
                    name="operator"
                    value="GREATER_THAN"
                    className="constraint-form-operator-input"
                    checked={operator === "GREATER_THAN"}
                    onChange={handleOperatorChange}
                />
                GREATER THAN
            </label>
            <label className={`constraint-form-operator-label ${operator === "LESS_THAN" ? "selected" : ""}`}>
                <input
                    type="radio"
                    name="operator"
                    value="LESS_THAN"
                    className="constraint-form-operator-input"
                    checked={operator === "LESS_THAN"}
                    onChange={handleOperatorChange}
                />
                LESS THAN
            </label>
        </div>
    );
}

export default OperatorField;
