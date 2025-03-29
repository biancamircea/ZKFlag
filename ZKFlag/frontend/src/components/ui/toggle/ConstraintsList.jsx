import React from "react";
import AddConstraintButton from "../common/AddConstraintButton.jsx";
import ConstraintListItem from "./ConstraintListItem.jsx";
import EmptyList from "../common/EmptyList.jsx";
import DeleteAllConstraintsBtn from "../common/DeleteAllConstraintsBtn.jsx";
import { toast } from "react-toastify";
import {
    addConstraintInToggleEnv,
    deleteAllConstraintsFromToggleEnv,
    deleteConstraintFromToggleEnv,
    updateConstraintInToggleEnv, updateConstraintValuesForToggleEnvironment
} from "../../../api/featureToggleApi.js";
import {  useParams } from "react-router-dom";
import AddConstraintToGroupButton from "../common/AddConstraintToGroupButton.jsx";

function ConstraintsList({ toggleId, constraints,instanceId,environmentId,refreshConstraints }) {
    const { projectId } = useParams();

    const groupedConstraints = constraints.reduce((acc, constraint) => {
        const groupId = constraint.constrGroupId || 0;
        if (!acc[groupId]) {
            acc[groupId] = [];
        }
        acc[groupId].push(constraint);
        return acc;
    }, {});

    const disabledStyle = {
        opacity: '0.5'
    };

    async function addConstraint(data, groupId = null) {
        if (constraints.some(el => el.contextName === data.contextName && el.isConfidential === 1)) {
            toast.error("You can add only one ZKP constraint with the same context name.");
            return;
        }

        if (data.values.length > 1) {
            toast.error("Only one value is allowed for this operator.");
            return;
        }

        let newGroupId = groupId;
        if (groupId === null) {
            const maxConstrGroupId = constraints.reduce((max, constraint) => Math.max(max, constraint.constrGroupId || 0), 0);
            newGroupId = maxConstrGroupId + 1;
        }

        const requestData = {
            ...data,
            constrGroupId: newGroupId
        };

        const resData = await addConstraintInToggleEnv(projectId, toggleId, requestData);
        if (resData) {
            toast.success("Constraint added.");
            refreshConstraints();
        } else {
            toast.error("Operation failed.");
        }
    }


    async function modifyConstraint(constraintId, data) {
        if (constraints.some(el => el.contextName === data.contextName && el.id!==constraintId)) {
            toast.error("Constraint with same context name already exists.");
            return;
        }

        if((data.operator==="GREATER_THAN" || data.operator==="LESS_THAN") && data.values.length>1){
            toast.error("Only one value is allowed for this operator.");
            return;
        }

        let resData;

        if(instanceId==null) {
           resData = await updateConstraintInToggleEnv(projectId, toggleId, constraintId, data);
        }else{
            resData=await updateConstraintValuesForToggleEnvironment(projectId,toggleId,instanceId,environmentId,constraintId,data)
        }
        if (resData) {
            toast.success("Constraint updated.");
            refreshConstraints();
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteConstraint(id) {
           const res = await deleteConstraintFromToggleEnv(projectId, toggleId, id);
        if (res) {
            toast.success("Constraint deleted.");
            refreshConstraints();
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteAllConstraints() {
        const res = await deleteAllConstraintsFromToggleEnv(projectId, toggleId);
        if (res) {
            toast.success("Entire list of constraints deleted.");
            refreshConstraints();
        } else {
            toast.error("Operation failed.");
        }
    }

    const constraintsItemsEl = constraints.map(el => (
        <ConstraintListItem
            key={el.id}
            constraintId={el.id}
            contextName={el}
            operator={el.operator}
            values={el.values}
            remove={() => deleteConstraint(el.id)}
            update={(data) => modifyConstraint(el.id, data)}
            instanceId={instanceId}
            environmentId={environmentId}
            toggleId={toggleId}
        />
    ));


    return (
        <>
        <div
            className="toggle-environment-constraints-container"
            style={constraints.length === 0 ? disabledStyle : null}
        >
            <div className="header">
                <img
                    src={"/images/environment.png"}
                    alt={"Environment"}
                    className="environment-icon"
                />
                {
                    constraints.length > 0 && instanceId == null &&
                    <DeleteAllConstraintsBtn deleteHandler={deleteAllConstraints} />
                }
            </div>
            {constraints.length === 0 ? (
                <EmptyList resource={"constraint"} recommend={"Add constraints to manage feature toggles effectively."} />
            ) : (
                Object.entries(groupedConstraints).map(([groupId, groupConstraints]) => (
                    <div key={groupId} className="constraint-group group-container">
                        <div className="constraint-group-header">
                            <span>Group {groupId}</span>
                            <AddConstraintToGroupButton submitHandler={addConstraint} groupId={groupId} />

                        </div>
                        {groupConstraints.map(el => (
                            <ConstraintListItem
                                key={el.id}
                                constraintId={el.id}
                                contextName={el}
                                operator={el.operator}
                                values={el.values}
                                remove={() => deleteConstraint(el.id)}
                                update={(data) => modifyConstraint(el.id, data)}
                                instanceId={instanceId}
                                environmentId={environmentId}
                                toggleId={toggleId}
                            />
                        ))}
                    </div>
                ))
            )}
        </div>
    {instanceId == null && <AddConstraintButton submitHandler={addConstraint} />}
        </>
    );
}

export default ConstraintsList;
