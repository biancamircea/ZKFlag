import React from 'react';
import EventsListItem from "./EventsListItem.jsx";

function EventsList({events, search}) {
    const applicationsListItemEl = events.map(el => {
        return (
            <EventsListItem
                key={el.id}
                event={el}
                search={search}
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

export default EventsList;