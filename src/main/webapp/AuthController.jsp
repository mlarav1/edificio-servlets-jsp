<%@ page import="Business.Services.AuthService, Business.Exceptions.NegocioException, Domain.Model.Usuario" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    // Controlador de autenticacion: recibe "action" y despacha con un switch.
    private final AuthService auth = new AuthService();

    /** Direccion publica para armar el enlace del correo: APP_BASE_URL, RENDER_EXTERNAL_URL o la de la peticion. */
    private String baseUrl(HttpServletRequest r) {
        String v = System.getenv("APP_BASE_URL");
        if (v == null || v.isBlank()) v = System.getenv("RENDER_EXTERNAL_URL");
        if (v != null && !v.isBlank()) return v;
        String puerto = (r.getServerPort() == 80 || r.getServerPort() == 443) ? "" : ":" + r.getServerPort();
        return r.getScheme() + "://" + r.getServerName() + puerto + r.getContextPath();
    }
%>
<%
    request.setCharacterEncoding("UTF-8");
    String ctx = request.getContextPath();
    String action = request.getParameter("action");
    if (action == null) action = "";

    switch (action) {
        case "login": {
            if (!"POST".equals(request.getMethod())) { response.sendRedirect(ctx + "/login.jsp"); return; }
            try {
                Usuario u = auth.autenticar(request.getParameter("correo"), request.getParameter("clave"));
                // Se crea una sesion nueva al iniciar sesion (evita fijacion de sesion).
                HttpSession vieja = request.getSession(false);
                if (vieja != null) vieja.invalidate();
                HttpSession sesion = request.getSession(true);
                u.setClave(null);                       // la clave no viaja en la sesion
                sesion.setAttribute("usuario", u);
                response.sendRedirect(ctx + "/index.jsp");
            } catch (NegocioException e) {
                session.setAttribute("loginError", e.getMessage());
                response.sendRedirect(ctx + "/login.jsp");
            }
            return;
        }
        case "logout": {
            HttpSession sesion = request.getSession(false);
            if (sesion != null) sesion.invalidate();
            response.sendRedirect(ctx + "/login.jsp?msg=salio");
            return;
        }
        case "recuperar": {
            if (!"POST".equals(request.getMethod())) { response.sendRedirect(ctx + "/recuperar.jsp"); return; }
            try {
                // Envia por correo un enlace con token (vigente 30 min, un solo uso).
                auth.solicitarRecuperacion(request.getParameter("correo"), baseUrl(request));
                // Respuesta identica exista o no el correo.
                response.sendRedirect(ctx + "/login.jsp?msg=recuperada");
            } catch (NegocioException e) {
                session.setAttribute("loginError", e.getMessage());
                response.sendRedirect(ctx + "/recuperar.jsp");
            }
            return;
        }
        case "restablecer": {
            String token = request.getParameter("token");
            if ("POST".equals(request.getMethod())) {
                try {
                    auth.restablecer(token, request.getParameter("nuevaClave"), request.getParameter("confirmarClave"));
                    response.sendRedirect(ctx + "/login.jsp?msg=clave");
                } catch (NegocioException e) {
                    session.setAttribute("loginError", e.getMessage());
                    response.sendRedirect(ctx + "/AuthController.jsp?action=restablecer&token="
                            + java.net.URLEncoder.encode(token == null ? "" : token, "UTF-8"));
                }
                return;
            }
            // GET: solo se muestra el formulario si el token del enlace es valido.
            try {
                request.setAttribute("nombre", auth.validarToken(token).getNombre());
                request.setAttribute("token", token);
                request.getRequestDispatcher("/WEB-INF/views/auth/restablecer.jsp").forward(request, response);
            } catch (NegocioException e) {
                session.setAttribute("loginError", e.getMessage());
                response.sendRedirect(ctx + "/recuperar.jsp");
            }
            return;
        }
        default:
            response.sendRedirect(ctx + "/login.jsp");
    }
%>
