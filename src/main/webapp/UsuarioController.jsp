<%@ page import="Business.Services.UsuarioService, Business.Exceptions.NegocioException, Domain.Model.Usuario" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    // Controlador de Usuario. Solo el ADMIN llega aqui: AuthFilter responde 403 al resto.
    private final UsuarioService servicio = new UsuarioService();
%>
<%
    request.setCharacterEncoding("UTF-8");
    String ctx = request.getContextPath();
    String action = request.getParameter("action");
    if (action == null) action = "list";
    boolean post = "POST".equals(request.getMethod());
    Usuario sesionUsuario = (Usuario) session.getAttribute("usuario");
    // Segunda barrera de seguridad, ademas del filtro.
    if (sesionUsuario == null || !sesionUsuario.esAdmin()) { response.sendError(403); return; }

    String vista = "/WEB-INF/views/usuarios/lista.jsp";
    try {
        switch (action) {
            case "list":
                request.setAttribute("usuarios", servicio.listar());
                break;
            case "new":
                request.setAttribute("usuario", new Usuario());
                request.setAttribute("nuevo", true);
                vista = "/WEB-INF/views/usuarios/form.jsp";
                break;
            case "edit": {
                Usuario u = servicio.buscar(request.getParameter("id"));
                if (u == null) throw new NegocioException("El usuario no existe.");
                u.setClave(null);
                request.setAttribute("usuario", u);
                request.setAttribute("nuevo", false);
                vista = "/WEB-INF/views/usuarios/form.jsp";
                break;
            }
            case "save": {
                if (!post) { response.sendRedirect(ctx + "/UsuarioController.jsp?action=list"); return; }
                boolean nuevo = "true".equals(request.getParameter("nuevo"));
                Usuario u = new Usuario();
                u.setId(request.getParameter("id"));
                u.setNombre(request.getParameter("nombre"));
                u.setRol(request.getParameter("rol"));
                String clave = request.getParameter("clave");
                try {
                    if (nuevo) servicio.crear(u, clave); else servicio.actualizar(u, clave);
                    session.setAttribute("flash", nuevo ? "Usuario creado correctamente." : "Usuario actualizado correctamente.");
                    session.setAttribute("flashTipo", "ok");
                    response.sendRedirect(ctx + "/UsuarioController.jsp?action=list");   // Post/Redirect/Get
                    return;
                } catch (NegocioException ex) {
                    request.setAttribute("error", ex.getMessage());
                    request.setAttribute("usuario", u);
                    request.setAttribute("nuevo", nuevo);
                    vista = "/WEB-INF/views/usuarios/form.jsp";
                }
                break;
            }
            case "delete": {
                if (!post) { response.sendRedirect(ctx + "/UsuarioController.jsp?action=list"); return; }
                try {
                    servicio.eliminar(request.getParameter("id"), sesionUsuario.getId());
                    session.setAttribute("flash", "Usuario eliminado.");
                    session.setAttribute("flashTipo", "ok");
                } catch (NegocioException ex) {
                    session.setAttribute("flash", ex.getMessage());
                    session.setAttribute("flashTipo", "error");
                }
                response.sendRedirect(ctx + "/UsuarioController.jsp?action=list");
                return;
            }
            case "reportes":
                vista = "/WEB-INF/views/usuarios/reportes.jsp";
                break;
            case "reporte1":   // usuarios por rol
                vista = "/WEB-INF/views/usuarios/reportes.jsp";
                request.setAttribute("reporte", "1");
                request.setAttribute("resultado", servicio.reportePorRol(request.getParameter("rol")));
                break;
            case "reporte2":   // texto en nombre o correo
                vista = "/WEB-INF/views/usuarios/reportes.jsp";
                request.setAttribute("reporte", "2");
                request.setAttribute("resultado", servicio.reportePorTexto(request.getParameter("texto")));
                break;
            default:
                response.sendRedirect(ctx + "/UsuarioController.jsp?action=list");
                return;
        }
    } catch (NegocioException e) {
        request.setAttribute("error", e.getMessage());
        if (action.startsWith("reporte")) {
            request.setAttribute("reporte", action.substring(7));
            vista = "/WEB-INF/views/usuarios/reportes.jsp";
        }
    }
    request.getRequestDispatcher(vista).forward(request, response);
%>
