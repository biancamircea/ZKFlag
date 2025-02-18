import React from 'react';
import SearchBar from "../common/SearchBar.jsx";
import FeatureTogglesFilterTag from "./FeatureTogglesFilterTag.jsx";

function FeatureTogglesListPageHeader({ title, handleSearch, searchQuery, tags, tag, handleFilter}) {
    return (
        <div className={"list-page-header-container"}>
            <h2>{title}</h2>
            <FeatureTogglesFilterTag
                tags={tags}
                tag={tag}
                handleFilter={handleFilter}
            />
            <div className={"list-page-header-functions"}>

                <SearchBar onSearch={handleSearch} value={searchQuery}/>
            </div>
        </div>
    )
}
export default FeatureTogglesListPageHeader;