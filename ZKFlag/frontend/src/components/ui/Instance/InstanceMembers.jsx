import React, {Suspense, useEffect, useState} from 'react';
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {getUsersWithInstanceAdminRole} from "../../../api/instanceApi.js";
import EmptyList from "../common/EmptyList.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import LoadingBanner from "../common/LoadingBanner.jsx";
import ListPageHeader from "../common/ListPageHeader.jsx";

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
                <div className="tags-list-item" style={{
                    display: "flex",
                    justifyContent: "space-between",
                    width: "100%"
                }}>
                    <div style={{flex: "1.5", display: "flex", justifyContent: "flex-start"}}>
                        <p>{admin.name}</p>
                    </div>
                    <div style={{flex: "1.5",display:"flex",justifyContent: "center"}}>
                        <p>{admin.email}</p>
                    </div>
                </div>
            </div>
        ));
    }

    return (
        <>
            <ListPageHeader
                title={"Members"}
                buttonText={""}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
        <div className="list-container">
            <div className="context-fields list-item item-header">
                <div className="tags-list-item" style={{
                    display: "flex",
                    justifyContent: "space-between",
                    width: "100%"
                }}>
                    <div style={{flex: "1.5", display: "flex", justifyContent: "flex-start"}}>
                        <p>Name</p>
                    </div>
                    <div style={{flex: "1.5",display:"flex",justifyContent: "center"}}>
                        <p>Email</p>
                    </div>
                </div>
            </div>
            <Suspense fallback={<LoadingBanner />}>
                <Await resolve={loaderDataPromise.instanceAdmins}>
                    {(admins) => renderAdmins(admins)}
                </Await>
            </Suspense>
        </div>
        </>
    );
}

export default InstanceMembers;
