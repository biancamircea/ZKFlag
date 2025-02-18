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
    updateConstraintInToggleEnv
} from "../../../api/featureToggleApi.js";
import { useOutletContext, useParams } from "react-router-dom";

function ConstraintsList({ toggleId, constraints }) {
    const { projectId } = useParams();
    const {
        addConstraintHandler,
        updateConstraintHandler,
        deleteConstraintHandler,
        deleteAllConstraintsHandler
    } = useOutletContext();

    const disabledStyle = {
        opacity: '0.5'
    };

    async function addConstraint(data) {
        const resData = await addConstraintInToggleEnv(projectId, toggleId, data);
        if (resData) {
            addConstraintHandler(toggleId, resData);
            toast.success("Constraint added.");
        } else {
            toast.error("Operation failed.");
        }
    }

    async function modifyConstraint(constraintId, data) {
        const resData = await updateConstraintInToggleEnv(projectId, toggleId, constraintId, data);
        if (resData) {
            updateConstraintHandler(toggleId, constraintId, resData);
            toast.success("Constraint updated.");
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteConstraint(id) {
        const res = await deleteConstraintFromToggleEnv(projectId, toggleId, id);
        if (res) {
            deleteConstraintHandler(toggleId, id);
            toast.success("Constraint deleted.");
        } else {
            toast.error("Operation failed.");
        }
    }

    async function deleteAllConstraints() {
        const res = await deleteAllConstraintsFromToggleEnv(projectId, toggleId);
        if (res) {
            deleteAllConstraintsHandler(toggleId);
            toast.success("Entire list of constraints deleted.");
        } else {
            toast.error("Operation failed.");
        }
    }

    // Generăm elementele pentru lista de constrângeri
    const constraintsItemsEl = constraints.map(el => (
        <ConstraintListItem
            key={el.id}
            constraintId={el.id}
            contextName={el.contextName}
            operator={el.operator}
            values={el.values}
            remove={() => deleteConstraint(el.id)}
            update={(data) => modifyConstraint(el.id, data)}
        />
    ));

    return (
        <div
            className="toggle-environment-constraints-container"
            style={constraints.length === 0 ? disabledStyle : null}
        >
            <div className="header">
                <img
                    src={"/src/assets/images/environment.png"}
                    alt={"Environment"}
                    className="environment-icon"
                />
                <h3>{toggleId}</h3>
                {
                    constraints.length > 0 &&
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
            <AddConstraintButton submitHandler={addConstraint} />
        </div>
    );
}

export default ConstraintsList;
