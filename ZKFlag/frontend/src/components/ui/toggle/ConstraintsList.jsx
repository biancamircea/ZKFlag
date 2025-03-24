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

function ConstraintsList({ toggleId, constraints,instanceId,environmentId }) {
    console.log("constraint list constraints: ",constraints)
    const { projectId } = useParams();

    const disabledStyle = {
        opacity: '0.5'
    };

    async function addConstraint(data) {
        if (constraints.some(el => el.contextName === data.contextName)) {
            toast.error("Constraint with same context name already exists.");
            return;
        }

        if((data.operator==="GREATER_THAN" || data.operator==="LESS_THAN") && data.values.length>1){
            toast.error("Only one value is allowed for this operator.");
            return;
        }

        const resData = await addConstraintInToggleEnv(projectId, toggleId, data);
        if (resData) {
            toast.success("Constraint added.");
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
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteConstraint(id) {
           const res = await deleteConstraintFromToggleEnv(projectId, toggleId, id);
        if (res) {
            toast.success("Constraint deleted.");
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteAllConstraints() {
        const res = await deleteAllConstraintsFromToggleEnv(projectId, toggleId);
        if (res) {
            //deleteAllConstraintsHandler(toggleId);
            toast.success("Entire list of constraints deleted.");
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
            pIsConfidential={el.isConfidential}
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
            {
                constraints.length === 0 ? (
                    <EmptyList
                        resource={"constraint"}
                        recommend={"Add constraints to manage feature toggles effectively."}
                    />
                ) : (
                    constraintsItemsEl
                )
            }
        </div>
    {instanceId == null && <AddConstraintButton submitHandler={addConstraint} />}
        </>
    );
}

export default ConstraintsList;
