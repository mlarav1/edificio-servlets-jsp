<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Reportes de edificios"); request.setAttribute("menu", "reportes-edificio"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Reportes de edificios</h1>

<div class="tarjeta">
  <h2>1. Edificios por ciudad y rango de pisos</h2>
  <form method="get" action="${ctx}/EdificioController.jsp">
    <input type="hidden" name="action" value="reporte1">
    <div class="formulario">
      <div class="campo"><label for="ciudad">Ciudad (o parte del nombre)</label>
        <input type="text" id="ciudad" name="ciudad" required value="${fn:escapeXml(param.ciudad)}"></div>
      <div class="campo"><label for="pisosMin">Pisos mínimos</label>
        <input type="number" min="1" id="pisosMin" name="pisosMin" required value="${empty param.pisosMin ? 1 : param.pisosMin}"></div>
      <div class="campo"><label for="pisosMax">Pisos máximos</label>
        <input type="number" min="1" id="pisosMax" name="pisosMax" required value="${empty param.pisosMax ? 100 : param.pisosMax}"></div>
    </div>
    <div class="pie-form"><button class="btn" type="submit">Consultar</button></div>
  </form>
  <c:if test="${reporte == '1' && empty error}">
    <c:set var="filas" value="${resultado}"/><c:set var="conAcciones" value="false"/>
    <hr><%@ include file="/WEB-INF/jspf/tabla-edificios.jspf" %>
  </c:if>
</div>

<div class="tarjeta">
  <h2>2. Edificios por valor de administración</h2>
  <form method="get" action="${ctx}/EdificioController.jsp">
    <input type="hidden" name="action" value="reporte2">
    <div class="formulario">
      <div class="campo"><label for="valorMin">Valor mínimo ($)</label>
        <input type="number" min="0" step="1" id="valorMin" name="valorMin" required value="${empty param.valorMin ? 0 : param.valorMin}"></div>
      <div class="campo"><label for="valorMax">Valor máximo ($)</label>
        <input type="number" min="0" step="1" id="valorMax" name="valorMax" required value="${empty param.valorMax ? 5000000 : param.valorMax}"></div>
      <div class="campo"><label for="ascensor">Ascensor</label>
        <select id="ascensor" name="ascensor">
          <option value="" ${empty param.ascensor ? 'selected' : ''}>Cualquiera</option>
          <option value="SI" ${param.ascensor == 'SI' ? 'selected' : ''}>Con ascensor</option>
          <option value="NO" ${param.ascensor == 'NO' ? 'selected' : ''}>Sin ascensor</option></select></div>
      <div class="campo"><label for="zonaSocial">Zona social</label>
        <select id="zonaSocial" name="zonaSocial">
          <option value="" ${empty param.zonaSocial ? 'selected' : ''}>Cualquiera</option>
          <option value="SI" ${param.zonaSocial == 'SI' ? 'selected' : ''}>Con zona social</option>
          <option value="NO" ${param.zonaSocial == 'NO' ? 'selected' : ''}>Sin zona social</option></select></div>
    </div>
    <div class="pie-form"><button class="btn" type="submit">Consultar</button></div>
  </form>
  <c:if test="${reporte == '2' && empty error}">
    <c:set var="filas" value="${resultado}"/><c:set var="conAcciones" value="false"/>
    <hr><%@ include file="/WEB-INF/jspf/tabla-edificios.jspf" %>
  </c:if>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
