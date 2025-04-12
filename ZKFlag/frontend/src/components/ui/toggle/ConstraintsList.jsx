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
import OrButton from "../common/OrButton.jsx";
import AndButton from "../common/AndButton.jsx";

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

    async function addConstraint(data,isConfidential = 0,groupId = null) {
        if (constraints.some(el => el.contextName === data.contextName && el.isConfidential !== 0)) {
            toast.error("You can add only one ZKP constraint with the same context name.");
            return;
        }

        if (data.values.length > 1 && isConfidential!==2) {
            toast.error("Only one value is allowed.");
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


    async function modifyConstraint(constraintId, data, isConfidential = 0) {
        if (constraints.some(el => el.contextName === data.contextName && el.id!==constraintId && el.isConfidential !==0)) {
            toast.error("Constraint with same context name already exists.");
            return;
        }

        if( data.values.length>1 && isConfidential!==2){
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
                    {constraints.length > 0 && instanceId == null && (
                        <DeleteAllConstraintsBtn deleteHandler={deleteAllConstraints} />
                    )}
                </div>

                {constraints.length === 0 ? (
                    <EmptyList resource={"constraint"} recommend={"Add constraints to manage feature toggles effectively."} />
                ) : (
                    Object.entries(groupedConstraints).map(([groupId, groupConstraints], index) => (
                        <div key={groupId} className="constraint-group group-container">
                            <br />
                            <div className="constraint-group-header" style={{ display: "flex", justifyContent: "space-between", width: "100%" }}>
                                <div style={{ flex: 1, display: "flex", alignItems: "flex-start" }}>
                                    <span>{index + 1}.</span>
                                </div>

                                <div className="or-button-container"
                                     style={{
                                         display: "flex",
                                         justifyContent: "center",
                                         alignItems: "center",
                                         flex: 1
                                     }}
                                >
                                {index > 0 && (
                                        <OrButton/>
                                )}
                                </div>

                                <div style={{ flex: 1, display: "flex", justifyContent: "center" }}>
                                {instanceId == null &&
                                    <AddConstraintToGroupButton submitHandler={addConstraint} groupId={groupId} toggleId={toggleId} />
                                }
                                </div>
                            </div>

                            {groupConstraints.map((el, i) => (
                                <React.Fragment key={el.id}>
                                    <ConstraintListItem
                                        constraintId={el.id}
                                        contextName={el}
                                        operator={el.operator}
                                        values={el.values}
                                        remove={() => deleteConstraint(el.id)}
                                        update={(data,isConfidential) => modifyConstraint(el.id, data, isConfidential)}
                                        instanceId={instanceId}
                                        environmentId={environmentId}
                                        toggleId={toggleId}
                                        isConfidential={el.isConfidential}
                                    />

                                    {i < groupConstraints.length - 1 && (
                                        <div className={"or-button"}    style={{ display: "flex", justifyContent: "flex-start", alignItems: "center"}}>
                                           <AndButton/>
                                        </div>
                                    )}
                                </React.Fragment>
                            ))}

                        </div>
                    ))
                )}
            </div>
            {instanceId == null && <AddConstraintButton submitHandler={addConstraint}  toggleId={toggleId}/>}
        </>
    );}

    export default ConstraintsList;
