<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Usuarios"); request.setAttribute("menu", "usuarios"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<div class="cabecera-lista">
  <h1>Usuarios</h1>
  <a class="btn" href="${ctx}/UsuarioController.jsp?action=new">+ Nuevo usuario</a>
</div>
<div class="tarjeta">
  <c:set var="filas" value="${usuarios}"/><c:set var="conAcciones" value="true"/>
  <%@ include file="/WEB-INF/jspf/tabla-usuarios.jspf" %>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
