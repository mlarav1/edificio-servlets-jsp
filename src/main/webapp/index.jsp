<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Pagina de inicio: el filtro ya garantizo que hay sesion --%>
<% request.setAttribute("titulo", "Inicio"); request.setAttribute("menu", "inicio"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Bienvenido, ${fn:escapeXml(sessionScope.usuario.nombre)}</h1>
<div class="inicio">
  <a class="mod" href="${ctx}/EdificioController.jsp?action=list">
    <h3>Edificios</h3><p>Consulta y administra el inventario de edificios.</p></a>
  <a class="mod" href="${ctx}/EdificioController.jsp?action=reportes">
    <h3>Reportes de edificios</h3><p>Por ciudad y pisos, o por valor de administración.</p></a>
  <c:if test="${sessionScope.usuario.rol == 'ADMIN'}">
    <a class="mod" href="${ctx}/UsuarioController.jsp?action=list">
      <h3>Usuarios</h3><p>Administra cuentas y roles del sistema.</p></a>
    <a class="mod" href="${ctx}/UsuarioController.jsp?action=reportes">
      <h3>Reportes de usuarios</h3><p>Por rol o por texto en nombre/correo.</p></a>
  </c:if>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
