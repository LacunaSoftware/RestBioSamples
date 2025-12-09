const express = require('express');
const path = require('path'); // Required for path manipulation
const { createProxyMiddleware } = require('http-proxy-middleware');
const app = express();
const port = 4200;

app.get('/', (req, res) => {
	res.sendFile(path.join(__dirname, 'index.html'));
});

app.use('/sample-api', createProxyMiddleware({
	target: 'http://localhost:5078/sample-api',
	changeOrigin: true,
}));

app.listen(port, () => {
	console.log(`Demo application running at http://localhost:${port}`);
});