import React, { useEffect, useState, Suspense } from "react";
import { Await, defer, useLoaderData, useNavigate, useParams } from "react-router-dom";
import { getAllInstancesFromProject} from "../../api/instanceApi.js";
import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";

export function loader({ params }) {
    return defer({ instances: getAllInstancesFromProject(params.projectId) });
}

function Instances() {
    const navigate = useNavigate();
    const loaderDataPromise = useLoaderData();
    const [allInstances, setAllInstances] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const { projectId } = useParams();


    useEffect(() => {
        loaderDataPromise.instances.then((res) => setAllInstances(res));
    }, [loaderDataPromise]);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function renderInstances(response) {
        const filteredInstances = allInstances.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sortedInstances = filteredInstances.sort((a, b) => {
            if (a.id < b.id) return -1;
            if (a.id > b.id) return 1;
            return 0;
        });

        const instancesEl = sortedInstances.map((el) => (
            <div className={"instances-list list-item item-body"} key={el.id} style={{display:"flex", justifyContent:"space-between"}}>
                <div
                    className={"instances-list-item-name"}
                    style={{height:"60px",textAlign:"center",display:"flex",alignItems:"center"}}
                >
                    {el.name}
                </div>
            </div>
        ));

        if (allInstances.length === 0) {
            return <EmptyList resource={"instances"} />;
        } else {
            if (instancesEl.length === 0) {
                return <EmptySearchResult resource={"instance"} searchValue={searchQuery} />;
            } else {
                return instancesEl;
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={"Instances"}
                buttonText={""}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"list-container"}>
                <div className={"context-fields list-item item-header"}>
                    <p>Name</p>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.instances}>
                        {renderInstances}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default Instances;
