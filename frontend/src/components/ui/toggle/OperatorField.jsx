import React, {useState} from 'react';

function OperatorField({defaultOperator}) {
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
        </div>
    );
}

export default OperatorField;