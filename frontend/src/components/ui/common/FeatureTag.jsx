import React from 'react';

function FeatureTag({label, color}) {
    return (
        <div style={{background: `${color}`}} className={"feature-tag"}>
            {label}
        </div>
    );
}

export default FeatureTag;