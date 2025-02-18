import React from 'react';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';

function SwaggerUiComponent(props) {
    return (
        <SwaggerUI url="http://localhost:8080/v3/api-docs" />
    );
}

export default SwaggerUiComponent;