import React from 'react';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';

function SwaggerUiComponent(props) {
    return (
        <SwaggerUI
            url="/api/v3/api-docs"
            requestInterceptor={(request) => {
                request.credentials = 'include';
                return request;
            }}
        />
    );
}

export default SwaggerUiComponent;
