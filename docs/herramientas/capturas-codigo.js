// Genera imagenes con resaltado de sintaxis de las piezas clave. Uso: node capturas-codigo.js [raiz-del-proyecto]
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');
const ROOT = process.argv[2] || path.join(__dirname, '..', '..');
const OUT = path.join(__dirname, '..', 'capturas-codigo');
const piezas = require('./piezas.json');   // [{archivo, nombre, lang, desde, hasta}]

const esc = (s) => s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1100, height: 400 }, deviceScaleFactor: 2 });
  for (const it of piezas) {
    const lineas = fs.readFileSync(path.join(ROOT, it.archivo), 'utf8').split(/\r?\n/);
    const desde = it.desde || 1, hasta = it.hasta || lineas.length;
    const trozo = lineas.slice(desde - 1, hasta).join('\n');
    const html = `<html><head><meta charset="utf-8">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github-dark.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<style>body{margin:0;background:#0d1117;font-family:Consolas,monospace}
.t{background:#161b22;color:#c9d1d9;padding:10px 18px;font:600 14px 'Segoe UI',sans-serif;border-bottom:1px solid #30363d}
pre{margin:0;padding:14px 18px}code{font-size:14px;line-height:1.5;background:none!important;padding:0!important}</style></head>
<body><div class="t">${esc(it.archivo)} &nbsp;·&nbsp; líneas ${desde}-${hasta}</div>
<pre><code class="language-${it.lang}">${esc(trozo)}</code></pre>
<script>hljs.highlightAll();</script></body></html>`;
    await p.setContent(html, { waitUntil: 'networkidle' });
    await p.screenshot({ path: path.join(OUT, it.nombre + '.png'), fullPage: true });
    console.log('ok', it.nombre);
  }
  await b.close();
})();
