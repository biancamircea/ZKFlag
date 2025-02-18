import React from 'react';
import {useNavigate} from "react-router-dom";

function CancelButton(props) {
    const navigate = useNavigate()

    function handleClick(){
        navigate(-1)
    }

    return (
        <button className={"reverse-btn"} onClick={event => {
            event.stopPropagation()
            event.preventDefault()
            handleClick()
        }
        }>
            Cancel
        </button>
    );
}

export default CancelButton;