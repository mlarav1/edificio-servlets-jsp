<%@ page import="Business.Services.AuthService, Business.Exceptions.NegocioException, Domain.Model.Usuario" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    // Controlador de autenticacion: recibe "action" y despacha con un switch.
    private final AuthService auth = new AuthService();
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
                auth.recuperarClave(request.getParameter("correo"));
                // Respuesta identica exista o no el correo.
                response.sendRedirect(ctx + "/login.jsp?msg=recuperada");
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
