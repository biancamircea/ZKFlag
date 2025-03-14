
function SearchBar({ onSearch, value }) {
    function handleChange(event) {
        onSearch(event.target.value);
    }

    return (
        <div className={"search-bar-container"}>
            <img src={"/images/search.png"} alt={"search"}/>
            <input
                type={"text"}
                placeholder={"Search by name"}
                className={"search-bar"}
                onChange={handleChange}
                value={value}
            />
        </div>
    )
}

export default SearchBar