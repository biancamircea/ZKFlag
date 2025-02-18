import SearchBar from "./SearchBar.jsx";
import {Link} from "react-router-dom";

function ListPageHeader({ title, buttonText, hasButton, handleSearch, searchQuery, auxiliaryPath }) {
    return (
        <div className={"list-page-header-container"}>
            <h2>{title}</h2>
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