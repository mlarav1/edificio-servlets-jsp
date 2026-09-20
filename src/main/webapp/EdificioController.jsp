<%@ page import="Business.Services.EdificioService, Business.Exceptions.NegocioException, Domain.Model.Edificio, Domain.Model.Usuario" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    // Controlador de Edificio. Recibe "action" y usa un switch para despachar cada operacion.
    // Los metodos privados se declaran con <%! ... %> (van a la clase generada del JSP).
    private final EdificioService servicio = new EdificioService();

    /** Lee el formulario y construye la entidad (la validacion de fondo la hace el servicio). */
    private Edificio leerEdificio(HttpServletRequest r) throws NegocioException {
        Edificio e = new Edificio();
        String id = r.getParameter("id");
        e.setId(id == null || id.isBlank() ? 0 : EdificioService.entero(id, "id"));
        e.setNombre(r.getParameter("nombre"));
        e.setMetrosCuadrados(EdificioService.decimal(nz(r.getParameter("metrosCuadrados")), "metros cuadrados"));
        e.setAltura(EdificioService.decimal(nz(r.getParameter("altura")), "altura"));
        e.setNumPisos(EdificioService.entero(nz(r.getParameter("numPisos")), "número de pisos"));
        e.setNumApartamentos(EdificioService.entero(cero(r.getParameter("numApartamentos")), "apartamentos"));
        e.setNumOficinas(EdificioService.entero(cero(r.getParameter("numOficinas")), "oficinas"));
        e.setNombreParqueadero(r.getParameter("nombreParqueadero"));
        e.setNumPiscinas(EdificioService.entero(cero(r.getParameter("numPiscinas")), "piscinas"));
        e.setPais(r.getParameter("pais"));
        e.setDepartamento(r.getParameter("departamento"));
        e.setCiudad(r.getParameter("ciudad"));
        e.setTieneAscensor(r.getParameter("tieneAscensor") != null);
        e.setValorAdministracion(EdificioService.decimal(nz(r.getParameter("valorAdministracion")), "valor de administración"));
        e.setTieneZonaSocial(r.getParameter("tieneZonaSocial") != null);
        return e;
    }
    private String nz(String s) { return s == null ? "" : s; }
    private String cero(String s) { return (s == null || s.isBlank()) ? "0" : s; }
%>
<%
    request.setCharacterEncoding("UTF-8");
    String ctx = request.getContextPath();
    String action = request.getParameter("action");
    if (action == null) action = "list";
    boolean post = "POST".equals(request.getMethod());
    Usuario usuario = (Usuario) session.getAttribute("usuario");   // el filtro ya valido la sesion
    String vista = "/WEB-INF/views/edificios/lista.jsp";

    // Operaciones que modifican datos: solo ADMIN y OPERADOR, y solo por POST cuando cambian datos.
    boolean modifica = action.equals("new") || action.equals("edit") || action.equals("save") || action.equals("delete");
    if (modifica && !usuario.puedeEscribir()) { response.sendError(403); return; }

    try {
        switch (action) {
            case "list":
                request.setAttribute("edificios", servicio.listar());
                break;
            case "new":
                request.setAttribute("edificio", new Edificio());
                vista = "/WEB-INF/views/edificios/form.jsp";
                break;
            case "edit": {
                Edificio e = servicio.buscar(EdificioService.entero(request.getParameter("id"), "id"));
                if (e == null) throw new NegocioException("El edificio no existe.");
                request.setAttribute("edificio", e);
                vista = "/WEB-INF/views/edificios/form.jsp";
                break;
            }
            case "save": {
                if (!post) { response.sendRedirect(ctx + "/EdificioController.jsp?action=list"); return; }
                Edificio e = null;
                try {
                    e = leerEdificio(request);
                    boolean nuevo = e.getId() == 0;
                    servicio.guardar(e);
                    session.setAttribute("flash", nuevo ? "Edificio creado correctamente." : "Edificio actualizado correctamente.");
                    session.setAttribute("flashTipo", "ok");
                    response.sendRedirect(ctx + "/EdificioController.jsp?action=list");   // Post/Redirect/Get
                    return;
                } catch (NegocioException ex) {
                    // Vuelve al formulario conservando lo digitado
                    request.setAttribute("error", ex.getMessage());
                    request.setAttribute("edificio", e != null ? e : new Edificio());
                    vista = "/WEB-INF/views/edificios/form.jsp";
                }
                break;
            }
            case "delete": {
                if (!post) { response.sendRedirect(ctx + "/EdificioController.jsp?action=list"); return; }
                servicio.eliminar(EdificioService.entero(request.getParameter("id"), "id"));
                session.setAttribute("flash", "Edificio eliminado.");
                session.setAttribute("flashTipo", "ok");
                response.sendRedirect(ctx + "/EdificioController.jsp?action=list");
                return;
            }
            case "reportes":
                vista = "/WEB-INF/views/edificios/reportes.jsp";
                break;
            case "reporte1": {   // por ciudad y rango de pisos
                vista = "/WEB-INF/views/edificios/reportes.jsp";
                request.setAttribute("reporte", "1");
                request.setAttribute("resultado", servicio.reportePorCiudadYPisos(
                        request.getParameter("ciudad"), request.getParameter("pisosMin"), request.getParameter("pisosMax")));
                break;
            }
            case "reporte2": {   // por valor de administracion, ascensor y zona social
                vista = "/WEB-INF/views/edificios/reportes.jsp";
                request.setAttribute("reporte", "2");
                request.setAttribute("resultado", servicio.reportePorAdministracion(
                        request.getParameter("valorMin"), request.getParameter("valorMax"),
                        request.getParameter("ascensor"), request.getParameter("zonaSocial")));
                break;
            }
            default:
                response.sendRedirect(ctx + "/EdificioController.jsp?action=list");
                return;
        }
    } catch (NegocioException e) {
        request.setAttribute("error", e.getMessage());
        if (action.startsWith("reporte")) {
            request.setAttribute("reporte", action.substring(7));
            vista = "/WEB-INF/views/edificios/reportes.jsp";
        }
    }
    request.getRequestDispatcher(vista).forward(request, response);
%>
