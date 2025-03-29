import React from 'react';

function ProjectApiTokensHeader(props) {
    return (
        <div className={"tokens list-item item-header"}>
            <p>Name</p>
            <p>Environment</p>
            <p>Type</p>
            <p>Created</p>
            <p className={"align-end"}>Actions</p>
        </div>
    );
}

export default ProjectApiTokensHeader;