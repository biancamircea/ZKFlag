import React, {useState} from 'react';
import ProjectApiTokensListItem from "./ProjectApiTokensListItem.jsx";
import {toast} from "react-toastify";
import EmptyList from "../common/EmptyList.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import {createInstanceApiToken, deleteInstanceApiToken} from "../../../api/instanceApi.js";
import {useParams} from "react-router-dom";
import ProjectApiTokenAddBtn from "./ProjectApiTokenAddBtn.jsx";

function ProjectApiTokensList({apiTokens, searchQuery, environments}) {
    const { projectId,instanceId } = useParams();
    const [allTokens, setAllTokens] = useState([...apiTokens])

    async function addToken(data) {
        const resData = await createInstanceApiToken(projectId,instanceId, data);
        if (resData) {
            setAllTokens(prevElements => [...prevElements, resData]);
            toast.success("Api Token generated.");
        } else {
            toast.error("Operation failed.");
        }
    }

    function removeToken(tokenId){
        const response = deleteInstanceApiToken(instanceId, tokenId)
        if(response){
            setAllTokens(prevElements => prevElements.filter(el => el.id !== tokenId));
            toast.success("Api Token deleted.");
        }
        else {
            toast.error("Operation failed.");
        }
    }

    const filteredTokens = allTokens.filter((el) =>
        el.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const allTokensEl = filteredTokens.map((el) => {
        return (
            <ProjectApiTokensListItem
                key={el.id}
                name={el.name}
                environment={el.environmentName}
                createdAt={el.createdAt}
                secret={el.secret}
                remove={() => removeToken(el.id)}
                projectId={projectId}
                instanceId={instanceId}
                type={el.type}
            />
        )
    })

    function renderResult(){
        if(allTokens.length > 0){
            if(allTokensEl.length > 0){
                return allTokensEl
            } else {
                return <EmptySearchResult resource={"api token"} searchValue={searchQuery}/>
            }
        } else {
            return (<EmptyList resource={"api token"} recommend={"Get started by generating one."}/>)
        }
    }

    return (
        <>
            {
                renderResult()
            }
            <ProjectApiTokenAddBtn
                tokensName={allTokens.map(el => el.name)}
                environments={environments}
                submitHandler={addToken}
            />
        </>
    );
}

export default ProjectApiTokensList;