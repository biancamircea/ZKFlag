

import React from 'react';

function FeaturesListHeader({environmentsEl}) {
    return (
        <div className={"feature-list-item item-header"} style={{display: "flex", justifyContent: "space-between",flexDirection:"row"}}>
            <span>Name</span>
            <span>Created</span>
            <span style={{marginRight:"30px"}}>Actions</span>
        </div>
    );
}

export default FeaturesListHeader;