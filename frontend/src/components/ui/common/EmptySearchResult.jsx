import React from 'react';

function EmptySearchResult({resource, searchValue}) {
    return (
        <div className={"empty-search-result"}>
            {`No ${resource} found matching "${searchValue}"`}
        </div>
    );
}

export default EmptySearchResult;