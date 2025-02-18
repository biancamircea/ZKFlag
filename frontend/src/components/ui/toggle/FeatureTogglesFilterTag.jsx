import React, {useState} from 'react';
import {FormControl, InputLabel, MenuItem, OutlinedInput, Select} from "@mui/material";

function FeatureTogglesFilterTag({tags, tag, handleFilter}) {
    const tagsDropDown = [
        <MenuItem value="" key="noTag" style={{ color: "grey" }}>
            No tag
        </MenuItem>,
        ...tags.map((el) => {
            return (
                <MenuItem value={el.id} key={el.id}>
                    {el.labelName}
                    {el.description ? ` - ${el.description}` : ''}
                </MenuItem>
            )
        })
    ]

    return (
        <div className={"row-flex-container"}>
            <h4>Filter by tag: </h4>
            <FormControl sx={{ m: 1, width: '25%' }}>
                <InputLabel id="demo-dialog-select-label">Tag</InputLabel>
                <Select
                    labelId="demo-dialog-select-label"
                    id="demo-dialog-select"
                    name={"tag"}
                    value={tag}
                    onChange={handleFilter}
                    input={<OutlinedInput label="Tag" />}
                >{
                    tagsDropDown?.length === 0
                        ? <h4>No tag available.</h4>
                        : tagsDropDown
                }</Select>
            </FormControl>
        </div>
    );
}

export default FeatureTogglesFilterTag;