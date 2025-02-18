import React from 'react';

function FeatureTogglePayloadContainer({enabledValue, disabledValue}) {
    return (
        <div className={"payload-container"}>
            <div>
                <span className={"bold-text large-text"}>ON</span>: {enabledValue}
            </div>
            <div>
                <span className={"bold-text large-text"}>OFF</span>: {disabledValue}
            </div>
        </div>
    );
}

export default FeatureTogglePayloadContainer;