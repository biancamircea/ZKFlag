import React from 'react';

function WarningField({message}) {
    return (
        <p className={"warning-text"}>
            {message}
        </p>
    );
}

export default WarningField;