<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Reportes de usuarios"); request.setAttribute("menu", "reportes-usuario"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Reportes de usuarios</h1>

<div class="tarjeta">
  <h2>1. Usuarios por rol</h2>
  <form method="get" action="${ctx}/UsuarioController.jsp">
    <input type="hidden" name="action" value="reporte1">
    <div class="formulario">
      <div class="campo"><label for="rol">Rol</label>
        <select id="rol" name="rol">
          <option value="ADMIN" ${param.rol == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
          <option value="OPERADOR" ${param.rol == 'OPERADOR' ? 'selected' : ''}>OPERADOR</option>
          <option value="CONSULTA" ${param.rol == 'CONSULTA' ? 'selected' : ''}>CONSULTA</option></select></div>
    </div>
    <div class="pie-form"><button class="btn" type="submit">Consultar</button></div>
  </form>
  <c:if test="${reporte == '1' && empty error}">
    <c:set var="filas" value="${resultado}"/><c:set var="conAcciones" value="false"/>
    <hr><%@ include file="/WEB-INF/jspf/tabla-usuarios.jspf" %>
  </c:if>
</div>

<div class="tarjeta">
  <h2>2. Usuarios por texto en nombre o correo</h2>
  <form method="get" action="${ctx}/UsuarioController.jsp">
    <input type="hidden" name="action" value="reporte2">
    <div class="formulario">
      <div class="campo"><label for="texto">Texto (nombre, correo o dominio, p. ej. edificios.com)</label>
        <input type="text" id="texto" name="texto" required minlength="2" value="${fn:escapeXml(param.texto)}"></div>
    </div>
    <div class="pie-form"><button class="btn" type="submit">Consultar</button></div>
  </form>
  <c:if test="${reporte == '2' && empty error}">
    <c:set var="filas" value="${resultado}"/><c:set var="conAcciones" value="false"/>
    <hr><%@ include file="/WEB-INF/jspf/tabla-usuarios.jspf" %>
  </c:if>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
