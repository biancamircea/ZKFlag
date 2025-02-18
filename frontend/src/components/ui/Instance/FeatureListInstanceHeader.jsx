import React from 'react';

function FeatureListInstanceHeader() {
    return (
        <div className={"context-fields list-item item-header"}>
            <div className={"instances-list-item-name"}>
                <span>Name</span>
            </div>

            <div className={"context-fields list-item actions"}>
                <span >Created</span>
            </div>

        </div>


    );
}

export default FeatureListInstanceHeader;