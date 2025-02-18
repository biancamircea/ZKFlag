
function SearchBar({ onSearch, value }) {
    function handleChange(event) {
        onSearch(event.target.value); // Call the onSearch callback with the new search query
    }

    return (
        <div className={"search-bar-container"}>
            <img src={"/src/assets/images/search.png"} alt={"search"}/>
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