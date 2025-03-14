import React, { Suspense, useState, useEffect } from 'react';
import { Await, defer, useLoaderData } from "react-router-dom";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import { getEvents } from "../api/eventsApi.js";
import ListPageHeader from "../components/ui/common/ListPageHeader.jsx";
import EventsList from "../components/ui/events/EventsList.jsx";
import { fetchUser, getProjectsForProjectAdmin, getInstancesForInstanceAdmin } from "../api/userApi.js";

export async function loader() {
    try {
        const user = await fetchUser();

        if (user.role === "SystemAdmin") {
            return defer({ user });
        }

        if (user.role === "ProjectAdmin") {
            const projects = await getProjectsForProjectAdmin(user.id);
            return defer({ user, projects });
        }

        if (user.role === "InstanceAdmin") {
            const instances = await getInstancesForInstanceAdmin(user.id);
            return defer({ user, instances });
        }

        return defer({ user });
    } catch (error) {
        console.error("Error loading user data:", error);
        return defer({ user: null });
    }
}

function Events() {
    const loaderData = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const [events, setEvents] = useState([]);

    useEffect(() => {
        async function fetchEvents() {
            try {
                if (!loaderData.user) return;

                let allEvents = [];

                if (loaderData.user.role === "ProjectAdmin" && loaderData.projects) {
                    for (const proj of loaderData.projects) {
                        const response = await getEvents(proj.id, null);
                        if (response && Array.isArray(response.events)) {
                            allEvents = allEvents.concat(response.events);
                        }
                    }
                }

                if (loaderData.user.role === "InstanceAdmin" && loaderData.instances) {
                    for (const inst of loaderData.instances) {
                        const response = await getEvents(null, inst.id);
                        if (response && Array.isArray(response.events)) {
                            allEvents = allEvents.concat(response.events);
                        }
                    }
                }

                if (loaderData.user.role === "SystemAdmin") {
                    const response = await getEvents();
                    if (response && Array.isArray(response.events)) {
                        allEvents = response.events;
                    }
                }

                setEvents(allEvents);
            } catch (error) {
                console.error("Error fetching events:", error);
            }
        }

        fetchEvents();
    }, [loaderData]);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    return (
        <Suspense fallback={<LoadingBanner />}>
            <Await resolve={loaderData.user}>
                {() => (
                    <>
                        <br />
                        <ListPageHeader
                            title={"Event logs"}
                            buttonText={""}
                            hasButton={false}
                            searchQuery={searchQuery}
                            handleSearch={handleSearch}
                        />
                        <div className={"list-container"}>
                            {events.length > 0 ? (
                                <EventsList events={events} search={searchQuery} />
                            ) : (
                                <p style={{ textAlign: "center", marginTop: "20px" }}>No events found.</p>
                            )}
                        </div>
                    </>
                )}
            </Await>
        </Suspense>
    );
}

export default Events;
