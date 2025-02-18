import React from 'react';
import FeatureToggleDetailsEnvironments from "./FeatureToggleDetailsEnvironments.jsx";
import FeatureToggleDetailsInfo from "./FeatureToggleDetailsInfo.jsx";
import FeatureToggleDetailsTag from "./FeatureToggleDetailsTag.jsx";

function FeatureToggleDetailsCard({environments, createdAt, tags, removeTag}) {
    return (
        <div className={"feature-toggle-details-card-wrapper"}>
            {/*<FeatureToggleDetailsEnvironments*/}
            {/*    environments={environments}*/}
            {/*/>*/}
            <FeatureToggleDetailsInfo
                createdAt={createdAt}
            />
            <FeatureToggleDetailsTag
                tags={tags}
                removeTag={removeTag}
            />
        </div>
    );
}

export default FeatureToggleDetailsCard;