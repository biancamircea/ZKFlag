import React from "react";
import ConstraintsList from "./ConstraintsList";

function FeatureToggleSectionRight({ featureId, constraints }) {
    return (
        <div className="toggle-environment-constraints-container">
            <ConstraintsList
                toggleId={featureId}
                constraints={constraints}
            />
        </div>
    );
}

export default FeatureToggleSectionRight;

