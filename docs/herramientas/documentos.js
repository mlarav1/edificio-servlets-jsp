// Genera docs/Evidencias-Edificio-Servlets-JSP.docx y docs/Ficha-entrega-Servlets-JSP.pdf
// Uso: node documentos.js [URL_APP_DESPLEGADA]
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const { Document, Packer, Paragraph, TextRun, ImageRun, HeadingLevel, AlignmentType, PageBreak } = require('docx');
const { chromium } = require('playwright');

const ROOT = path.join(__dirname, '..', '..');
const DOCS = path.join(ROOT, 'docs');
const REPO = 'https://github.com/mlarav1/edificio-servlets-jsp';
const APP = process.argv[2] || '';
const D = { nombre: 'Miguel Lara', codigo: '7502510046', semestre: 'Cuarto (4)', asignatura: 'Desarrollo Web',
  uni: 'Universidad de Cartagena · Ingeniería de Software', actividad: 'Servlets/JSP: introducción a la segunda generación del desarrollo de aplicaciones web (Unidad 1, individual)',
  ejercicio: '13 - Edificio', guia: 'Guía «CRUD JSP» (plan de respaldo: JSP puro con controlador por entidad, action y switch)' };

const p = (t, o = {}) => new Paragraph({ spacing: { after: 120 }, ...o, children: [new TextRun({ text: t, ...(o.run || {}) })] });
const h1 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_1, spacing: { before: 240, after: 120 }, children: [new TextRun(t)] });
const h2 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_2, spacing: { before: 160, after: 80 }, children: [new TextRun(t)] });
const li = (t) => new Paragraph({ bullet: { level: 0 }, children: [new TextRun(t)] });
const code = (t) => new Paragraph({ spacing: { after: 60 }, children: [new TextRun({ text: t, font: 'Consolas', size: 18 })] });

function pngSize(f) { const b = fs.readFileSync(f); return { w: b.readUInt32BE(16), h: b.readUInt32BE(20) }; }
let fig = 0;
function figura(archivo, pie, maxW = 580, maxH = 620) {
  const f = path.join(DOCS, archivo); const { w, h } = pngSize(f);
  const e = Math.min(maxW / w, maxH / h); fig++;
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 120 }, children: [new ImageRun({ type: 'png', data: fs.readFileSync(f), transformation: { width: Math.round(w * e), height: Math.round(h * e) } })] }),
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 200 }, children: [new TextRun({ text: `Figura ${fig}. ${pie}`, italics: true, size: 20 })] }),
  ];
}

const commits = execSync('git log --reverse --format=%h%x09%ad%x09%s --date=format:%Y-%m-%d', { cwd: ROOT }).toString('utf8').trim().split(/\r?\n/);

const hijos = [
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 1800, after: 300 }, children: [new TextRun({ text: 'Universidad de Cartagena', bold: true, size: 36 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 900 }, children: [new TextRun({ text: 'Ingeniería de Software', size: 28 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 300 }, children: [new TextRun({ text: 'Evidencias de la actividad', bold: true, size: 40 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 900 }, children: [new TextRun({ text: 'Servlets/JSP · Ejercicio 13 - Edificio', size: 30 })] }),
  ...[['Estudiante', D.nombre], ['Código', D.codigo], ['Semestre', D.semestre], ['Asignatura', D.asignatura], ['Actividad', D.actividad], ['Repositorio', REPO], ['Aplicación desplegada', APP || '(pendiente de publicar)']]
    .map(([k, v]) => new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 100 }, children: [new TextRun({ text: k + ': ', bold: true }), new TextRun(v)] })),
  new Paragraph({ children: [new PageBreak()] }),

  h1('1. Descripción del ejercicio'),
  p('El ejercicio 13 pide administrar edificios. Cada edificio tiene nombre, metros cuadrados, altura, número de pisos, apartamentos, oficinas, nombre del parqueadero, piscinas, país, departamento, ciudad, ascensor, valor de administración y zona social. Se agregó una clave técnica autoincremental (`id`).'),
  p('La aplicación tiene además la entidad Usuario (id, clave, nombre, rol), con login, control de acceso por rol, recuperación de clave por correo y cuatro reportes parametrizados. Se usó Java Servlet/JSP, JDBC directo y PostgreSQL, sin frameworks MVC externos.'),

  h1('2. Arquitectura y patrones'),
  p('Se siguió la estructura de la guía «CRUD JSP» (plan de respaldo, porque las guías de Drive no se pudieron abrir).'),
  li('Domain.Model: Usuario.java y Edificio.java (entidades).'),
  li('Infrastructure.Database: Conexion.java (JDBC con variables de entorno).'),
  li('Infrastructure.Persistence: UsuarioDAO.java y EdificioDAO.java (CRUD y reportes con PreparedStatement).'),
  li('Business.Services: UsuarioService, EdificioService, AuthService, CorreoService. Business.Exceptions: NegocioException.'),
  li('Controladores: EdificioController.jsp, UsuarioController.jsp y AuthController.jsp (parámetro action y switch).'),
  li('Vistas: WEB-INF/views (edificios y usuarios) y WEB-INF/jspf (cabecera, pie y tablas compartidas).'),
  li('Filtro de seguridad: Infrastructure.Web.AuthFilter.'),
  p('Patrones: MVC, DAO, filtro de interceptación (AuthFilter) y Post/Redirect/Get.'),
  ...figura('capturas-codigo/01-entidad-usuario.png', 'Entidad Usuario (Domain.Model.Usuario).', 520, 420),
  ...figura('capturas-codigo/02-entidad-edificio.png', 'Entidad Edificio (Domain.Model.Edificio).', 520, 420),
  ...figura('capturas-codigo/03-acceso-datos-edificio.png', 'Acceso a datos: EdificioDAO con PreparedStatement.', 560, 500),

  h1('3. Flujo de una petición completa'),
  p('Ejemplo: guardar un edificio.'),
  li('El navegador envía POST a EdificioController.jsp con action=save.'),
  li('AuthFilter verifica la sesión y el rol.'),
  li('El controlador lee el formulario (leerEdificio) y llama a EdificioService.guardar.'),
  li('El servicio valida y llama a EdificioDAO.insertar o actualizar, que ejecuta un PreparedStatement.'),
  li('El controlador guarda un mensaje en la sesión y redirige al listado (Post/Redirect/Get), que se renderiza con lista.jsp.'),
  ...figura('capturas-codigo/04-controlador-edificio.png', 'Controlador EdificioController.jsp con el switch de acciones.', 560, 640),
  ...figura('capturas-codigo/05-vista-jsp-lista.png', 'Vista JSP del listado de edificios.', 560, 400),

  h1('4. Sesión y control de acceso'),
  p('Al iniciar sesión se crea una HttpSession nueva con el usuario (sin clave). AuthFilter protege todas las páginas: sin sesión redirige al login; UsuarioController solo lo abre el ADMIN; el rol CONSULTA es de solo lectura.'),
  ...figura('capturas-codigo/06-verificacion-sesion.png', 'Verificación de sesión y rol en AuthFilter.', 560, 500),
  ...figura('capturas/01-login.png', 'Pantalla de inicio de sesión.', 460, 400),
  ...figura('capturas/03-inicio-menu.png', 'Página de inicio con el menú (rol ADMIN).', 560, 400),
  ...figura('capturas/14-acceso-denegado-consulta.png', 'Un usuario CONSULTA recibe acceso denegado en la gestión de usuarios.', 560, 400),

  h1('5. CRUD de Edificio y de Usuario'),
  ...figura('capturas/04-edificios-listado.png', 'Listado de edificios.', 580, 420),
  ...figura('capturas/05-edificio-formulario.png', 'Formulario de edificio.', 580, 460),
  ...figura('capturas/12-validacion-error.png', 'Validación: mensaje de error conservando lo digitado.', 580, 460),
  ...figura('capturas/08-usuarios-listado.png', 'Listado de usuarios (solo ADMIN).', 580, 360),
  ...figura('capturas/09-usuario-formulario.png', 'Formulario de usuario.', 580, 360),

  h1('6. Reportes parametrizados'),
  li('Edificio 1: por ciudad y rango de número de pisos.'),
  li('Edificio 2: por rango de valor de administración, con filtro de ascensor y zona social.'),
  li('Usuario 1: por rol.'),
  li('Usuario 2: por texto en el nombre o en el correo/dominio.'),
  ...figura('capturas-codigo/07-reporte-consulta.png', 'Consulta parametrizada de los reportes de Edificio.', 560, 500),
  ...figura('capturas/06-reporte-edificio-ciudad-pisos.png', 'Reporte de edificios por ciudad y pisos.', 580, 460),
  ...figura('capturas/07-reporte-edificio-administracion.png', 'Reporte de edificios por valor de administración.', 580, 460),
  ...figura('capturas/10-reporte-usuario-rol.png', 'Reporte de usuarios por rol.', 580, 400),
  ...figura('capturas/11-reporte-usuario-texto.png', 'Reporte de usuarios por texto.', 580, 400),

  h1('7. Recuperación de clave'),
  p('El usuario escribe su correo; AuthService genera una clave temporal aleatoria (SecureRandom), la envía por SMTP con Jakarta Mail y guarda su hash BCrypt. La respuesta es la misma exista o no el correo.'),
  ...figura('capturas-codigo/08-recuperacion-clave.png', 'Recuperación de clave en AuthService.', 560, 560),
  ...figura('capturas/02-recuperar-clave.png', 'Formulario de recuperación de clave.', 460, 360),
  ...figura('capturas/13-recuperar-clave-enviada.png', 'Confirmación tras solicitar la recuperación.', 460, 360),

  h1('8. Historial de commits'),
  p(`El repositorio tiene ${commits.length} commits hechos a medida que se construía el proyecto.`),
  ...commits.map((c) => { const [h, f, s] = c.split('\t'); return code(`${h}  ${f}  ${s}`); }),
];

(async () => {
  const doc = new Document({ styles: { default: { document: { run: { font: 'Calibri', size: 22 } } } }, sections: [{ children: hijos }] });
  fs.writeFileSync(path.join(DOCS, 'Evidencias-Edificio-Servlets-JSP.docx'), await Packer.toBuffer(doc));

  const a = (u) => u ? `<a href="${u}">${u}</a>` : '<span class="blanco">______________________________</span>';
  const html = `<html><head><meta charset="utf-8"><style>
body{font-family:'Segoe UI',Arial,sans-serif;margin:50px;color:#222}h1{color:#1f4e79;border-bottom:3px solid #1f4e79;padding-bottom:8px}
table{width:100%;border-collapse:collapse;margin-top:20px}td{padding:12px 10px;border-bottom:1px solid #ccc;vertical-align:top}td:first-child{width:30%;font-weight:700;color:#1f4e79}
a{color:#1f4e79}.blanco{color:#888}</style></head><body>
<h1>Ficha de entrega</h1><p>${D.uni}</p><table>
<tr><td>Estudiante</td><td>${D.nombre} · Código ${D.codigo}</td></tr>
<tr><td>Semestre</td><td>${D.semestre}</td></tr><tr><td>Asignatura</td><td>${D.asignatura}</td></tr>
<tr><td>Actividad</td><td>${D.actividad}</td></tr><tr><td>Ejercicio</td><td>${D.ejercicio}</td></tr>
<tr><td>Guía utilizada</td><td>${D.guia}</td></tr>
<tr><td>Repositorio</td><td>${a(REPO)}</td></tr>
<tr><td>Video de sustentación</td><td>${a('')}</td></tr>
<tr><td>Aplicación desplegada</td><td>${a(APP)}</td></tr></table></body></html>`;
  const b = await chromium.launch(); const pg = await b.newPage();
  await pg.setContent(html); await pg.pdf({ path: path.join(DOCS, 'Ficha-entrega-Servlets-JSP.pdf'), format: 'A4', printBackground: true });
  await b.close(); console.log('ok');
})();
