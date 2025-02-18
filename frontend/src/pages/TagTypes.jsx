import ListPageHeader from "../components/ui/common/ListPageHeader.jsx";
import React, {Suspense, useEffect, useState} from "react";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import EditIcon from "../components/ui/common/EditIcon.jsx";
import DeleteIcon from "../components/ui/common/DeleteIcon.jsx";
import EmptyList from "../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../components/ui/common/EmptySearchResult.jsx";
import {deleteTag, getTags} from "../api/tagApi.js";
import {toast} from "react-toastify";
import Tooltip from "@mui/material/Tooltip";

export async function loader({ params }){
    return defer({ tags: getTags(params.projectId) })
}

function TagTypes() {
    const navigate = useNavigate();
    const loaderDataPromise = useLoaderData()
    const [allTags, setAllTags] = useState([])
    const [searchQuery, setSearchQuery] = useState('');
    const { projectId } = useParams();

    async function deleteHandler(id){
        const res = await deleteTag(projectId, id);
        if (res) {
            toast.success("Tag deleted.");
            setAllTags(prevElements => prevElements.filter(el => el.id !== id));
        } else {
            toast.error("Operation failed.");
        }
    }

    useEffect(() => {
        loaderDataPromise.tags.then((res) =>  setAllTags(res.tags))
    }, [])

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function handleItemClick(id){
        navigate(`edit/${id}`)
    }

    function render(response){
        const filteredTags = allTags?.filter((el) =>
            el.labelName.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sortedTags = filteredTags?.sort((a, b) => {
            if(a.id < b.id) return -1;
            if(a.id > b.id) return 1;
            return 0;
        })

        const tagsEl = sortedTags.map(el => (
            <div
                className={"tags-list list-item item-body"}
                key={el.id}
                onClick={() => handleItemClick(el.id)}

            >
                <div className={"tags-list list-item item-body name-wrapper"}>
                    <Tooltip title={el.color} arrow>
                        <div className={"tags-list list-item item-body name-wrapper name"}
                            style={{backgroundColor: `${el.color}`}}
                        >
                            {el.labelName}
                        </div>

                    </Tooltip>
                </div>
                <div className={"tags-list list-item item-body description gray-text"} >
                    {el.description === "" ? "No description" : el.description}
                </div>
                <div className={"context-fields list-item actions"}>
                    <EditIcon
                        id={el.id}
                    />
                    <DeleteIcon
                        resource={"Tag"}
                        resourceName={el.labelName}
                        deleteHandler={() => deleteHandler(el.id)}
                    />
                </div>
            </div>
        ))
        if(allTags.length === 0){
            return (
                <EmptyList
                    resource={"tags"}
                />
            )
        } else {
            if(tagsEl?.length === 0){
                return <EmptySearchResult
                    resource={"tag"}
                    searchValue={searchQuery}
                />
            } else {
                return tagsEl
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={"Tags"}
                buttonText={"New tag"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"list-container"}>
                <div className={"context-fields list-item item-header"}>
                    <div className={"tags-list-item"}>
                        <p>Name</p>
                        <p>Description</p>
                    </div>
                    <p>Actions</p>
                </div>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={loaderDataPromise.tags}>
                        {
                            render
                        }
                    </Await>
                </Suspense>
            </div>
        </>
    )
}

export default TagTypes