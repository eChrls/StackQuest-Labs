const path = require('node:path');
const express = require('express');
const jsonServer = require('json-server');

const PORT = process.env.PORT || 4000;

const app = express();
const router = jsonServer.router(path.join(__dirname, 'db.json'));

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  next();
});

app.use('/media', express.static(path.join(__dirname, 'media'), { maxAge: '1h' }));
app.use('/api', router);

app.get('/healthz', (req, res) => res.json({ status: 'ok' }));

app.listen(PORT, () => {
  console.log(`Lab 09 CMS API listening on http://0.0.0.0:${PORT}`);
  console.log('Routes: GET /api/home, GET /api/products, GET /api/products/:id, GET /api/categories');
});
