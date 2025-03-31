const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/client',
        createProxyMiddleware({
            target: 'https://localhost:8443',
            changeOrigin: true,
            secure: false,
            onProxyReq: (proxyReq) => {
                proxyReq.setHeader('Authorization', `Bearer ${process.env.REACT_APP_API_TOKEN}`);
            }
        })
    );
};