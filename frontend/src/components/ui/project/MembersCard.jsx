import React from 'react';
import {Link} from "react-router-dom";

function MembersCard({membersCount}) {
    return (
        <div className={"project-overview-meta-card"}>
            <h4>Project members</h4>
            <div className={"members-number-wrapper"}>
                <span className={"members-number"}>{membersCount}</span>
                <p> members</p>
            </div>

            <Link to={"settings/members"}>
                View all members
            </Link>
        </div>
    );
}

export default MembersCard;