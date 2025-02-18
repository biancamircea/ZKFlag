import React from 'react';
import DeleteIconNoDialog from "../common/DeleteIconNoDialog.jsx";
import EditConstraintDialog from "../common/EditConstraintDialog.jsx";

function ConstraintListItem({contextName, operator, values, remove, update}) {
    let valuesString = ""
    if(values){
        valuesString = values.join(', ')
    }

    return (
        <div className={"constraint-item"}>
            <img src={"/src/assets/images/target.png"}
                 alt={"Target"}
                 className={"target-icon"}
            />
            <span className={"context-name"}>
                    {contextName}
                </span>
            <div className={"operator"}>
                <span className={"title"}>{operator}</span>
                <span className={"description gray-text"}>is {operator === "IN" ? "" : "not"} one of</span>
            </div>
            <div className={"values"}>
                {valuesString}
            </div>
            <div className={"list-page-header-functions"}>
                <EditConstraintDialog
                    context={contextName}
                    operator={operator}
                    values={values}
                    submitHandler={update}
                />
                <DeleteIconNoDialog
                    deleteHandler={remove}
                />
            </div>
        </div>
    );
}

export default ConstraintListItem;