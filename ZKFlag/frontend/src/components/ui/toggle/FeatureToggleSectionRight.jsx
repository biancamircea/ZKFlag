import React from "react";
import ConstraintsList from "./ConstraintsList";

function FeatureToggleSectionRight({ featureId, constraints ,instanceId, refreshConstraints}) {
    return (
        <div className="toggle-environment-constraints-container">
            <ConstraintsList
                toggleId={featureId}
                constraints={constraints}
                instanceId={instanceId}
                refreshConstraints={refreshConstraints}
            />
        </div>
    );
}

export default FeatureToggleSectionRight;

