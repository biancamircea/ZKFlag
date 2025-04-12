import React from 'react';
import DeleteIconNoDialog from "../common/DeleteIconNoDialog.jsx";
import EditConstraintDialog from "../common/EditConstraintDialog.jsx";

function ConstraintListItem({contextName, operator, values, remove, update, instanceId, constraintId, toggleId, environmentId, isConfidential}) {
    let valuesString = "";

    if (values) {
        if (isConfidential === 2) {
            const [radius, latitude, longitude] = values;

            let radiusText = '';
            console.log("radius", radius);
            if (radius === 0) {
                radiusText = 'City';
            } else if (radius === 1) {
                radiusText = 'Country';
            } else if (radius === 2) {
                radiusText = 'Continent';
            }

            valuesString = (
                <div className="location-values">
                    <div style={{marginLeft:"30px"}}><strong>Coverage:</strong> {radiusText}</div>
                    <div style={{marginLeft:"30px"}}><strong>Latitude:</strong> {latitude}°</div>
                    <div style={{marginLeft:"30px"}}><strong>Longitude:</strong> {longitude}°</div>
                </div>
            );
        } else {
            valuesString = values.join(', ');
        }
    }


    return (
        <div className={"constraint-item"}>
            <img src={"/images/target.png"}
                 alt={"Target"}
                 className={"target-icon"}
            />
            <span className={"context-name"}>
                {contextName.contextName}
            </span>
            <div className={"operator"}>
                <span className={"title"}>{operator}</span>
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
                    instanceId={instanceId}
                    constraintId={constraintId}
                    toggleId={toggleId}
                    environmentId={environmentId}
                />
                {instanceId == null &&  <DeleteIconNoDialog
                    deleteHandler={remove}
                />}
            </div>
        </div>
    );
}

export default ConstraintListItem;
