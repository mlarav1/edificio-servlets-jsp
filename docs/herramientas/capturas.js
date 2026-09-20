// Capturas de la aplicacion en ejecucion. Uso: node capturas.js [URL_BASE]
const { chromium } = require('playwright');
const path = require('path');
const BASE = process.argv[2] || 'http://localhost:8080';
const OUT = path.join(__dirname, '..', 'capturas');

(async () => {
  const b = await chromium.launch();
  const ctx = await b.newContext({ viewport: { width: 1750, height: 850 } });
  const p = await ctx.newPage();
  const shot = async (n) => p.screenshot({ path: path.join(OUT, n + '.png'), fullPage: true });

  await p.goto(BASE + '/login.jsp'); await shot('01-login');
  await p.goto(BASE + '/recuperar.jsp'); await shot('02-recuperar-clave');
  await p.goto(BASE + '/login.jsp');
  await p.fill('#correo', 'admin@edificios.com'); await p.fill('#clave', 'Admin123');
  await p.click('button[type=submit]'); await p.waitForURL('**/index.jsp'); await shot('03-inicio-menu');

  await p.goto(BASE + '/EdificioController.jsp?action=list'); await shot('04-edificios-listado');
  await p.goto(BASE + '/EdificioController.jsp?action=new'); await shot('05-edificio-formulario');
  await p.goto(BASE + '/EdificioController.jsp?action=reporte1&ciudad=Cartagena&pisosMin=10&pisosMax=40'); await shot('06-reporte-edificio-ciudad-pisos');
  await p.goto(BASE + '/EdificioController.jsp?action=reporte2&valorMin=0&valorMax=800000&ascensor=SI&zonaSocial=SI'); await shot('07-reporte-edificio-administracion');

  await p.goto(BASE + '/UsuarioController.jsp?action=list'); await shot('08-usuarios-listado');
  await p.goto(BASE + '/UsuarioController.jsp?action=new'); await shot('09-usuario-formulario');
  await p.goto(BASE + '/UsuarioController.jsp?action=reporte1&rol=OPERADOR'); await shot('10-reporte-usuario-rol');
  await p.goto(BASE + '/UsuarioController.jsp?action=reporte2&texto=edificios.com'); await shot('11-reporte-usuario-texto');

  // Validacion con error
  await p.goto(BASE + '/EdificioController.jsp?action=new');
  await p.evaluate(() => document.querySelector('form').noValidate = true);
  await p.fill('#nombre', 'Edificio sin datos'); await p.fill('#departamento', 'Bolivar'); await p.fill('#ciudad', 'Cartagena');
  await p.fill('#metrosCuadrados', '-5'); await p.fill('#altura', '20'); await p.fill('#numPisos', '4'); await p.fill('#valorAdministracion', '100000');
  await p.click('button[type=submit]'); await shot('12-validacion-error');

  // Recuperacion de clave
  const p2 = await (await b.newContext({ viewport: { width: 1400, height: 700 } })).newPage();
  await p2.goto(BASE + '/recuperar.jsp'); await p2.fill('#correo', 'estudiante@correo.com'); await p2.click('button[type=submit]');
  await p2.waitForURL('**/login.jsp**'); await p2.screenshot({ path: path.join(OUT, '13-recuperar-clave-enviada.png') });

  // Control de acceso: CONSULTA no ve usuarios
  const p3 = await (await b.newContext({ viewport: { width: 1400, height: 700 } })).newPage();
  await p3.goto(BASE + '/login.jsp'); await p3.fill('#correo', 'consulta@edificios.com'); await p3.fill('#clave', 'Consulta123');
  await p3.click('button[type=submit]'); await p3.waitForURL('**/index.jsp');
  await p3.goto(BASE + '/UsuarioController.jsp?action=list'); await p3.screenshot({ path: path.join(OUT, '14-acceso-denegado-consulta.png') });
  await b.close();
})();

