import SearchBar from "./SearchBar.jsx";
import {Link} from "react-router-dom";

// function ListPageHeader({ title, buttonText, hasButton, handleSearch, searchQuery, auxiliaryPath }) {
//     return (
//         <div className={"list-page-header-container"}>
//             <h2>{title}</h2>
//             <div className={"list-page-header-functions"}>
//                 <SearchBar onSearch={handleSearch} value={searchQuery}/>
//                 {
//                     hasButton &&
//                     <Link to={auxiliaryPath ? `${auxiliaryPath}/create` : `create`}>
//                         <button>
//                             {buttonText}
//                         </button>
//                     </Link>
//
//                 }
//             </div>
//         </div>
//     )
// }

import { Info } from "react-feather";
import Tooltip from "@mui/material/Tooltip";

function ListPageHeader({ title, buttonText, hasButton, handleSearch, searchQuery, auxiliaryPath, hasInfo, infoText }) {
    return (
        <div className={"list-page-header-container"}>
            <div className="header-title" style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                gap: "8px"
            }}>
                <h2 style={{ margin: 0 }}>{title}</h2>
                {hasInfo && (
                    <Tooltip title={infoText} arrow>
            <span>
                <Info className="info-icon" size={18} />
            </span>
                    </Tooltip>
                )}
            </div>
            <div className={"list-page-header-functions"}>
                <SearchBar onSearch={handleSearch} value={searchQuery}/>
                {
                    hasButton &&
                    <Link to={auxiliaryPath ? `${auxiliaryPath}/create` : `create`}>
                        <button>
                            {buttonText}
                        </button>
                    </Link>
                }
            </div>
        </div>
    )
}


export default ListPageHeader