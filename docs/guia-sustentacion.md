# Guía de estudio para la sustentación (Servlets/JSP, Ejercicio 13 - Edificio)

## 1. ¿Cómo está organizada la arquitectura?

Por capas, cada una en su paquete:

| Capa | Paquete / carpeta | Ejemplo |
|---|---|---|
| Vista | `src/main/webapp/WEB-INF/views` y `jspf` | `edificios/lista.jsp` |
| Controlador | `src/main/webapp/*.jsp` | `EdificioController.jsp` |
| Negocio | `Business.Services`, `Business.Exceptions` | `EdificioService`, `NegocioException` |
| Acceso a datos | `Infrastructure.Persistence` | `EdificioDAO` |
| Conexión | `Infrastructure.Database` | `Conexion` |
| Modelo | `Domain.Model` | `Edificio`, `Usuario` |

## 2. ¿Qué patrones usa?

- **MVC**: el controlador JSP recibe la petición, el modelo son las entidades y los servicios, y las vistas JSP solo presentan.
- **DAO**: `UsuarioDAO` y `EdificioDAO` esconden el SQL del resto de la aplicación.
- **Front controller por acción**: cada controlador recibe `action` y usa un `switch`.
- **Filtro (Intercepting Filter)**: `AuthFilter` revisa sesión y rol antes de cada página.
- **Post/Redirect/Get**: tras guardar o eliminar se hace `sendRedirect`, para que recargar la página no repita la operación.

## 3. ¿Cuál es el flujo de una petición?

Ejemplo: guardar un edificio.

1. El navegador envía un `POST` a `EdificioController.jsp` con `action=save`.
2. `AuthFilter` comprueba que hay sesión (y el rol, si aplica) y deja pasar.
3. El controlador lee los parámetros con `leerEdificio` y llama a `EdificioService.guardar`.
4. El servicio valida las reglas y llama a `EdificioDAO.insertar` o `actualizar`.
5. El DAO ejecuta un `PreparedStatement` sobre PostgreSQL.
6. Si todo sale bien, el controlador guarda un mensaje en la sesión y redirige al listado.
7. Si falla, hace `forward` a `form.jsp` con el error, y el formulario conserva lo digitado.

## 4. ¿Cómo funciona la sesión?

En `AuthController.jsp` (`action=login`) `AuthService.autenticar` compara la clave con BCrypt. Si es correcta se invalida la sesión anterior, se crea una nueva y se guarda el `Usuario` (sin clave) en `HttpSession`. `logout` invalida la sesión. Pregunta frecuente: *¿por qué se crea una sesión nueva?* Para evitar la fijación de sesión.

## 5. ¿Cómo se controla el acceso?

- `AuthFilter` (`@WebFilter("/*")`) deja pasar solo `login.jsp`, `recuperar.jsp`, `AuthController.jsp`, `error.jsp` y `/css/*`. Sin sesión redirige al login.
- `UsuarioController.jsp` solo lo abre el ADMIN (filtro y comprobación propia en el controlador).
- CONSULTA no puede crear, editar ni eliminar edificios (`puedeEscribir()`).
- Las vistas están en `WEB-INF`, así que nadie puede abrirlas escribiendo su URL.

## 6. ¿Cómo evita la inyección SQL?

Todas las consultas usan `PreparedStatement` con `?`; nunca se concatenan valores del usuario. Los reportes también usan parámetros. Incluso los filtros opcionales (ascensor/zona social) se resuelven con condiciones parametrizadas.

## 7. ¿Cómo se guardan las claves y cómo funciona la recuperación?

Con BCrypt (`BCrypt.hashpw`), nunca en texto plano. En la recuperación `AuthService.solicitarRecuperacion` genera un token de 256 bits con `SecureRandom`, guarda solo su hash SHA-256 con vencimiento de 30 minutos (`TokenDAO`, tabla `token_recuperacion`) y `CorreoService` envía por correo (API de Brevo o SMTP) el enlace de restablecimiento. `AuthService.restablecer` valida el token, guarda la nueva clave con BCrypt y borra el token: solo sirve una vez. La respuesta es igual exista o no el correo, para no revelar qué usuarios existen.

## 8. ¿Por qué el `id` del Usuario es el correo?

El enunciado limita Usuario a `id`, `clave`, `nombre` y `rol`. Usar el correo como `id` permite iniciar sesión y recuperar la clave sin agregar columnas.

## 9. ¿Por qué el controlador es un JSP?

Es la estructura de la guía de respaldo (JSP puro con `action` y `switch`). Los métodos auxiliares se declaran en un bloque `<%! %>` y el resto del código es un scriptlet. Las vistas no tienen lógica: usan JSTL y EL.

## 10. ¿Qué reportes hay y con qué parámetros?

- Edificio 1: ciudad (parcial) + pisos mínimo y máximo (`reportePorCiudadYPisos`).
- Edificio 2: valor mínimo y máximo de administración + ascensor + zona social (`reportePorAdministracion`).
- Usuario 1: rol. Usuario 2: texto en nombre o correo/dominio.

## 11. ¿Qué pasa si falla la base de datos o hay un error?

El DAO lanza `SQLException`, el servicio la convierte en `NegocioException` y el controlador muestra el mensaje. Los errores no controlados van a `error.jsp` (configurado en `web.xml`).

## 12. ¿Cómo se ejecuta y despliega?

Local: `mvn package` y `mvn cargo:run` (Tomcat 10.1). Despliegue: el `Dockerfile` construye el WAR con Maven y lo copia a Tomcat; la conexión sale de variables de entorno.
