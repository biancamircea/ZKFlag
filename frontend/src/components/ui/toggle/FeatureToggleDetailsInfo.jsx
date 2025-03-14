import React from 'react';

function FeatureToggleDetailsInfo({createdAt}) {
    return (
        <div className={"feature-toggle-details-section middle"}>
            <h4>Feature toggle details</h4>
            <p>{"Created at: "}
                <span>{new Date(createdAt).toLocaleDateString("ro")}</span>
            </p>
        </div>
    );
}

export default FeatureToggleDetailsInfo;