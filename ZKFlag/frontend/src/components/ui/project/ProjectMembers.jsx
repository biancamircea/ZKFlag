import React, { useEffect, useState, Suspense } from "react";
import { Await, defer, useLoaderData, useParams } from "react-router-dom";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../../components/ui/common/EmptySearchResult.jsx";
import { getUsersWithProjectAdminRole } from "../../../api/projectApi.js";

export async function loader({ params }) {
    return defer({
        projectAdmins: getUsersWithProjectAdminRole(params.projectId),
    });
}

function ProjectMembers() {
    const { projectId } = useParams();
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState("");
    const [projectAdmins, setProjectAdmins] = useState([]);

    useEffect(() => {
        loaderDataPromise.projectAdmins.then(setProjectAdmins);
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
            return <EmptyList resource="Project Admins" />;
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
                <Await resolve={loaderDataPromise.projectAdmins}>
                    {(admins) => renderAdmins(admins)}
                </Await>
            </Suspense>
        </div>
    );
}

export default ProjectMembers;
