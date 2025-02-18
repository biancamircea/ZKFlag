import React from 'react';
import FeatureTogglesListItem from "./FeatureTogglesListItem.jsx";

function FeatureTogglesList({toggles}) {

    const togglesListEl = toggles.map(el => {
        return (
            <FeatureTogglesListItem
                key={el.id}
                id={el.id}
                name={el.name}
                description={el.description}
                tags={el.tags}
                createdAt={el.createdAt}
                projectName={el.project}
                projectId={el.projectId}
            />
        )
    })


    return (
        <>
            {
                togglesListEl
            }
        </>
    );
}

export default FeatureTogglesList;