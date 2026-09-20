# Guion de sustentación (Servlets/JSP, Ejercicio 13 - Edificio)

Duración aproximada: 10 a 12 minutos. Para leer en cámara.

## 1. Presentación (0:00 - 0:45)
"Hola, soy Miguel Lara, código 7502510046, estudiante de cuarto semestre de Ingeniería de Software en la Universidad de Cartagena. Este es mi trabajo de la asignatura Desarrollo Web, Unidad 1: una aplicación con Servlets y JSP para el ejercicio 13, Edificio. Permite administrar edificios y usuarios, consultar reportes y controlar el acceso por roles."

## 2. Arquitectura y patrones (0:45 - 2:30)
"Usé la estructura de la guía CRUD JSP, por capas. En `Domain.Model` están las entidades `Usuario` y `Edificio`. En `Infrastructure.Persistence` están los DAO, que ejecutan el SQL con JDBC. En `Business.Services` está la lógica de negocio y las validaciones. En `src/main/webapp` están los controladores `EdificioController.jsp` y `UsuarioController.jsp`, y en `WEB-INF/views` las vistas. Los patrones son MVC, DAO y un filtro de seguridad, `AuthFilter`. Cada controlador recibe un parámetro `action` y usa un `switch`."

## 3. Recorrido de una operación completa (2:30 - 4:00)
"Voy a crear un edificio. El navegador envía un POST al controlador. El filtro comprueba mi sesión. El controlador lee el formulario y llama a `EdificioService.guardar`, que valida los datos. Luego `EdificioDAO.insertar` ejecuta un `PreparedStatement`. Al terminar, el controlador redirige al listado; esto se llama Post/Redirect/Get y evita duplicar el registro si recargo la página."

## 4. Login y sesión (4:00 - 5:00)
"Ingreso con el correo y la clave. `AuthService` compara la clave con BCrypt y guarda el usuario en `HttpSession`. Si intento abrir una página sin sesión, el filtro me redirige al login. Al cerrar sesión la sesión se invalida y el botón atrás ya no muestra páginas privadas."

## 5. CRUD de Edificio y de Usuario (5:00 - 7:00)
"Aquí veo el listado de edificios, creo uno nuevo, lo edito y lo elimino. Si escribo metros cuadrados negativos, la validación del servicio muestra un mensaje y conserva lo digitado. Los usuarios solo los gestiona el administrador: si entro como consulta, recibo acceso denegado. El id del usuario es su correo."

## 6. Reportes (7:00 - 8:30)
"Hay cuatro reportes. Para edificios: por ciudad y rango de pisos, y por rango de valor de administración con filtros de ascensor y zona social. Para usuarios: por rol y por texto en el nombre o el dominio del correo. Todos usan consultas parametrizadas."

## 7. Recuperación de clave (8:30 - 9:30)
"En 'Olvidaste tu clave' escribo mi correo. El sistema genera un token aleatorio, guarda solo su hash y envía por correo un enlace que vence en 30 minutos y se usa una sola vez; con ese enlace creo una clave nueva, que se guarda con BCrypt. Responde igual aunque el correo no exista, para no revelar usuarios."

## 8. Aplicación desplegada (9:30 - 10:15)
"Esta es la aplicación publicada en Internet: [URL de la aplicación desplegada]. Ingreso, hago una consulta y compruebo que funciona igual que en local."

## 9. Historial de commits (10:15 - 11:00)
"En el repositorio de GitHub, [URL del repositorio], se ve el historial: configuración inicial, base de datos, modelo, acceso a datos, servicios, login, CRUD, reportes, recuperación de clave, README y despliegue. Todos los commits son míos, hechos a medida que avanzaba el trabajo. Muchas gracias."
