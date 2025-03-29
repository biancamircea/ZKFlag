

import React from 'react';

function FeaturesListHeader({environmentsEl}) {
    return (
        <div className={"feature-list-item item-header"}  style={{
            display: "flex",
            justifyContent: "space-between",
            width: "100%"
        }}>
            <div style={{  flex: "1.3",display: "flex",justifyContent: "space-between" }}>
                <span style={{marginLeft:"30px" }}>Name</span>
                <span>Created</span>
            </div>

            <div style={{ flex: "1", display: "flex", gap: "20px", justifyContent: "flex-end", marginRight: "20px" }}>
                <span style={{ marginRight: "40px" }}>Actions</span>
            </div>
        </div>
    );
}

export default FeaturesListHeader;