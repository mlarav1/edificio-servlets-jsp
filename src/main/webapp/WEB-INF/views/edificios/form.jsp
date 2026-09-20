<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Edificio"); request.setAttribute("menu", "edificios"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>${edificio.id == 0 ? 'Nuevo edificio' : 'Editar edificio'}</h1>
<div class="tarjeta">
<form method="post" action="${ctx}/EdificioController.jsp">
  <input type="hidden" name="action" value="save">
  <input type="hidden" name="id" value="${edificio.id}">
  <div class="formulario">
    <div class="campo"><label for="nombre">Nombre</label>
      <input type="text" id="nombre" name="nombre" maxlength="120" required value="${fn:escapeXml(edificio.nombre)}"></div>
    <div class="campo"><label for="pais">País</label>
      <input type="text" id="pais" name="pais" required value="${empty edificio.pais ? 'Colombia' : fn:escapeXml(edificio.pais)}"></div>
    <div class="campo"><label for="departamento">Departamento</label>
      <input type="text" id="departamento" name="departamento" required value="${fn:escapeXml(edificio.departamento)}"></div>
    <div class="campo"><label for="ciudad">Ciudad</label>
      <input type="text" id="ciudad" name="ciudad" required value="${fn:escapeXml(edificio.ciudad)}"></div>
    <div class="campo"><label for="metrosCuadrados">Metros cuadrados</label>
      <input type="number" step="0.01" min="0.01" id="metrosCuadrados" name="metrosCuadrados" required value="${edificio.metrosCuadrados}"></div>
    <div class="campo"><label for="altura">Altura (m)</label>
      <input type="number" step="0.01" min="0.01" id="altura" name="altura" required value="${edificio.altura}"></div>
    <div class="campo"><label for="numPisos">Número de pisos</label>
      <input type="number" min="1" id="numPisos" name="numPisos" required value="${edificio.numPisos == 0 ? '' : edificio.numPisos}"></div>
    <div class="campo"><label for="numApartamentos">Número de apartamentos</label>
      <input type="number" min="0" id="numApartamentos" name="numApartamentos" value="${edificio.numApartamentos}"></div>
    <div class="campo"><label for="numOficinas">Número de oficinas</label>
      <input type="number" min="0" id="numOficinas" name="numOficinas" value="${edificio.numOficinas}"></div>
    <div class="campo"><label for="nombreParqueadero">Nombre del parqueadero</label>
      <input type="text" id="nombreParqueadero" name="nombreParqueadero" value="${fn:escapeXml(edificio.nombreParqueadero)}"></div>
    <div class="campo"><label for="numPiscinas">Número de piscinas</label>
      <input type="number" min="0" id="numPiscinas" name="numPiscinas" value="${edificio.numPiscinas}"></div>
    <div class="campo"><label for="valorAdministracion">Valor de administración ($)</label>
      <input type="number" step="0.01" min="0" id="valorAdministracion" name="valorAdministracion" required value="${edificio.valorAdministracion}"></div>
    <div class="campo check"><input type="checkbox" id="tieneAscensor" name="tieneAscensor" ${edificio.tieneAscensor ? 'checked' : ''}>
      <label for="tieneAscensor">Tiene ascensor</label></div>
    <div class="campo check"><input type="checkbox" id="tieneZonaSocial" name="tieneZonaSocial" ${edificio.tieneZonaSocial ? 'checked' : ''}>
      <label for="tieneZonaSocial">Tiene zona social</label></div>
  </div>
  <div class="pie-form">
    <button class="btn" type="submit">Guardar</button>
    <a class="btn sec" href="${ctx}/EdificioController.jsp?action=list">Cancelar</a>
  </div>
</form>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
