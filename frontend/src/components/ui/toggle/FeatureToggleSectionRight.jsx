import React from "react";
import ConstraintsList from "./ConstraintsList";

function FeatureToggleSectionRight({ featureId, constraints ,instanceId}) {
    return (
        <div className="toggle-environment-constraints-container">
            <ConstraintsList
                toggleId={featureId}
                constraints={constraints}
                instanceId={instanceId}
            />
        </div>
    );
}

export default FeatureToggleSectionRight;

