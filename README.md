# Ejercicio 13 - Edificio (Servlets/JSP)

Aplicación web con **Servlets/JSP y JDBC directo** (sin Spring, Hibernate ni JPA).
Universidad de Cartagena · Ingeniería de Software · Desarrollo Web · Unidad 1.

- Estudiante: Miguel Lara · Código 7502510046 · Cuarto semestre

## Guía utilizada

No fue posible abrir las guías del profesor (Drive pedía permisos). Se siguió el **plan de respaldo** basado en la guía "CRUD JSP" (JSP puro):

- Un controlador por entidad, `UsuarioController.jsp` y `EdificioController.jsp`, que recibe el parámetro `action` y despacha con un `switch`. Los métodos privados están en bloques de declaración del JSP.
- Paquetes: `Domain.Model`, `Infrastructure.Database`, `Infrastructure.Persistence`, `Business.Exceptions`, `Business.Services`.
- Las vistas (`WEB-INF/views`) solo presentan los datos que el controlador les entrega.

## Requisitos

Java 17, Maven 3.9, PostgreSQL 14+ y Tomcat 10.1 (Maven lo descarga con el plugin Cargo).

## Base de datos

```bash
createdb -U postgres edificios
psql -U postgres -d edificios -f db/schema.sql
psql -U postgres -d edificios -f db/data.sql
```

**Usuario:** tiene solo `id`, `clave`, `nombre`, `rol`. El `id` es el **correo electrónico**, y sirve como nombre de inicio de sesión y como destino de la recuperación de clave, sin agregar columnas extra.

## Variables de entorno (ver `.env.example`)

`DB_URL`, `DB_USER`, `DB_PASSWORD` (o `DATABASE_URL`), `BREVO_API_KEY`, `MAIL_FROM`, `MAIL_FROM_NAME`, `APP_BASE_URL` (en Render se toma de `RENDER_EXTERNAL_URL`), `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_STARTTLS`.

## Ejecución local

```bash
mvn package
mvn cargo:run        # http://localhost:8080
```

## Usuarios de prueba

| Correo | Clave | Rol |
|---|---|---|
| admin@edificios.com | Admin123 | ADMIN (gestiona usuarios y edificios) |
| operador@edificios.com | Operador123 | OPERADOR (gestiona edificios) |
| consulta@edificios.com | Consulta123 | CONSULTA (solo lectura) |

## Reportes

- Edificio: (1) por ciudad y rango de pisos; (2) por rango de valor de administración con filtro de ascensor y zona social.
- Usuario: (1) por rol; (2) por texto en nombre o correo/dominio.

## Decisiones técnicas

- **Claves con BCrypt:** nunca se guardan en texto plano; si la base se filtra no se pueden leer las claves.
- **Recuperación por token:** se genera un token aleatorio de 256 bits; en la base (tabla `token_recuperacion`, aparte para que `Usuario` conserve sus 4 atributos) solo se guarda su hash SHA-256 con vencimiento de 30 minutos. El correo lleva el enlace `AuthController.jsp?action=restablecer&token=...`; el token sirve una sola vez y la nueva clave se guarda con BCrypt. La respuesta es igual exista o no el correo. Envío: API HTTPS de Brevo (`BREVO_API_KEY`, `MAIL_FROM`; el plan gratuito de Render bloquea SMTP), o SMTP (`SMTP_HOST`), o el enlace en el log si no hay ninguno.
- **Sesión:** `HttpSession`; `AuthFilter` protege todas las páginas, redirige al login sin sesión y restringe la gestión de usuarios al rol ADMIN.
- **SQL:** solo `PreparedStatement`, con `try-with-resources`.
- **PRG:** tras crear, editar o eliminar se redirige (Post/Redirect/Get).

## Despliegue

Aplicación publicada: https://edificios-servlets-jsp.onrender.com (Render, plan gratuito: se duerme tras un tiempo sin uso y la primera petición tarda ~50 s).

`Dockerfile` con Tomcat 10.1. Configurar en el servicio las variables de entorno anteriores y cargar `db/schema.sql` y `db/data.sql` en la base de datos en la nube.
