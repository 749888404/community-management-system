// 纯 node 静态文件服务器：托管前端构建产物 dist，并把 /api 反向代理到后端 8080
// 用法：node serve-frontend.js  （默认端口 9527）
const http = require('http');
const fs = require('fs');
const path = require('path');
const { URL } = require('url');

const PORT = process.env.PORT || 9527;
const DIST = path.join(__dirname, 'frontend', 'dist');
const BACKEND = 'http://localhost:8080';

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.ico': 'image/x-icon',
  '.woff2': 'font/woff2',
};

// 反向代理 /api/* 到后端（完整转发 method / headers / body）
function proxyApi(req, res) {
  const target = new URL(req.url, BACKEND);
  const headers = { ...req.headers };
  delete headers.host; // 让 node 用 target 的 host
  const options = {
    method: req.method,
    hostname: target.hostname,
    port: target.port,
    path: target.pathname + target.search,
    headers,
  };
  const preq = http.request(options, (pres) => {
    res.writeHead(pres.statusCode, pres.headers);
    pres.pipe(res);
  });
  preq.on('error', (e) => {
    res.writeHead(502, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ code: 502, message: '后端不可用: ' + e.message }));
  });
  req.pipe(preq);
}

// 托管 dist 静态文件（SPA 回退到 index.html）
function serveStatic(req, res) {
  let pathname = decodeURIComponent(new URL(req.url, 'http://x').pathname);
  let filePath = path.join(DIST, pathname);
  if (!pathname.endsWith('.') && (pathname === '/' || !fs.existsSync(filePath) || fs.statSync(filePath).isDirectory())) {
    filePath = path.join(DIST, 'index.html');
  }
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
      return;
    }
    res.writeHead(200, { 'Content-Type': MIME[path.extname(filePath)] || 'application/octet-stream' });
    res.end(data);
  });
}

http.createServer((req, res) => {
  if (req.url.startsWith('/api/')) {
    proxyApi(req, res);
  } else {
    serveStatic(req, res);
  }
}).listen(PORT, () => {
  console.log(`前端静态服务已启动: http://localhost:${PORT}  (后端代理 -> ${BACKEND})`);
});
