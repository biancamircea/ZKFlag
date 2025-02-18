import React from 'react';
import ApplicationsListItem from "./ApplicationsListItem.jsx";

function ApplicationsList({applications}) {

    const applicationsListItemEl = applications.map(el => {
        return (
            <ApplicationsListItem
                key={el.id}
                id={el.id}
                name={el.name}
                description={el.description}
            />
        )
    })

    return (
        <>
            {
                applicationsListItemEl
            }
        </>
    );
}

export default ApplicationsList;