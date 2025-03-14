import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {getUsersWithInstanceAdminRole} from "../../../api/instanceApi.js";
import EmptyList from "../common/EmptyList.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import LoadingBanner from "../common/LoadingBanner.jsx";

export async function loader({ params }) {
    return defer({
        instanceAdmins: getUsersWithInstanceAdminRole(params.instanceId),
    });
}

function InstanceMembers() {
    const { instanceId } = useParams();
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState("");
    const [instanceAdmins, setInstanceAdmins] = useState([]);

    useEffect(() => {
        loaderDataPromise.instanceAdmins.then(setInstanceAdmins);
    }, []);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function renderAdmins(admins) {
        const filteredAdmins = admins.filter((admin) =>
            admin.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            admin.email.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (admins.length === 0) {
            return <EmptyList resource="Instance Admins" />;
        }

        if (filteredAdmins.length === 0) {
            return <EmptySearchResult resource="admin" searchValue={searchQuery} />;
        }

        return filteredAdmins.map((admin) => (
            <div className="context-fields list-item item-body" key={admin.id}>
                <div className="tags-list-item">
                    <p>{admin.name}</p>
                    <p>{admin.email}</p>
                </div>
            </div>
        ));
    }

    return (
        <div className="list-container">
            <div className="context-fields list-item item-header">
                <div className="tags-list-item">
                    <p>Name</p>
                    <p>Email</p>
                </div>
            </div>
            <Suspense fallback={<LoadingBanner />}>
                <Await resolve={loaderDataPromise.instanceAdmins}>
                    {(admins) => renderAdmins(admins)}
                </Await>
            </Suspense>
        </div>
    );
}

export default InstanceMembers;
