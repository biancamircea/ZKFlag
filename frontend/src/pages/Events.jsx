import React, {Suspense, useState} from 'react';
import {Await, defer, useLoaderData} from "react-router-dom";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import {getEvents} from "../api/eventsApi.js";
import ListPageHeader from "../components/ui/common/ListPageHeader.jsx";
import EventsList from "../components/ui/events/EventsList.jsx";

export function loader({ params }){
    return defer({ events: getEvents(params.projectId, params.featureId) })
}

function Events() {
    const loaderDataPromise = useLoaderData()
    const [searchQuery, setSearchQuery] = useState('');

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function render(response){
        return (
            <>
                <ListPageHeader
                    title={"Event logs"}
                    buttonText={""}
                    hasButton={false}
                    searchQuery={searchQuery}
                    handleSearch={handleSearch}
                />
                <div className={"list-container"}>
                    <EventsList
                        events={response.events}
                        search={searchQuery}/>
                </div>
            </>
        )
    }

    return (
        <>
            <Suspense fallback={<LoadingBanner/>}>
                <Await resolve={loaderDataPromise.events}>
                    {
                        render
                    }
                </Await>
            </Suspense>
        </>
    )
}

export default Events;