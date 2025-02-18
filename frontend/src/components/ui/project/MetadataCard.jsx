import React from 'react';

function MetadataCard({description, instanceName}) {
    return (
        <div className={"project-overview-meta-card"}>
            {instanceName ? <h4>Instance metadata</h4> :  <h4>Project metadata</h4>}
                <p>Description:</p>
                <p className={description === "" ? "gray-text" : ""}>
                    {description === "" ? "No description." : description}
                </p>
        </div>
    );
}

export default MetadataCard;