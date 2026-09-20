<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Edificios"); request.setAttribute("menu", "edificios"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<div class="cabecera-lista">
  <h1>Edificios</h1>
  <c:if test="${sessionScope.usuario.rol != 'CONSULTA'}">
    <a class="btn" href="${ctx}/EdificioController.jsp?action=new">+ Nuevo edificio</a>
  </c:if>
</div>
<div class="tarjeta">
  <c:set var="filas" value="${edificios}"/>
  <c:set var="conAcciones" value="${sessionScope.usuario.rol != 'CONSULTA'}"/>
  <%@ include file="/WEB-INF/jspf/tabla-edificios.jspf" %>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
