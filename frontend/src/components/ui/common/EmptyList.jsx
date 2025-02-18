import React from 'react';

function EmptyList({resource, recommend}) {
    return (
        <div className={"empty-search-result"}>
            {`No ${resource} available. ${recommend !== undefined ? recommend : "Get started by creating one."}`}
        </div>
    );
}

export default EmptyList;