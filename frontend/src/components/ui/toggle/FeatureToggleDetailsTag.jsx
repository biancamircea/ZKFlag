import React from 'react';
import FeatureTag from "../common/FeatureTag.jsx";
import DeleteFeatureTag from "../common/DeleteFeatureTag.jsx";

function FeatureToggleDetailsTag({tags, removeTag, instanceId}) {

    const tagsEl = tags.map(el => {
        return (
            <DeleteFeatureTag
                key={el.id}
                label={el.labelName}
                color={el.color}
                deleteHandler={() =>removeTag(el.id)}
            />
        )
    })

    return (
        <div className={"feature-toggle-details-section bottom"}>
            <h4>Tags for this feature toggle</h4>
            <div className={"tags-list"}>
                {
                    tagsEl.length === 0 ?
                        (<span className={"gray-text"}>No tag.</span>) :
                        tagsEl

                }
            </div>
        </div>
    );
}

export default FeatureToggleDetailsTag;