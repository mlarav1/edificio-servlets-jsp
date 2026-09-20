<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("titulo", "Usuario"); request.setAttribute("menu", "usuarios"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>${nuevo ? 'Nuevo usuario' : 'Editar usuario'}</h1>
<div class="tarjeta">
<form method="post" action="${ctx}/UsuarioController.jsp">
  <input type="hidden" name="action" value="save">
  <input type="hidden" name="nuevo" value="${nuevo}">
  <div class="formulario">
    <div class="campo"><label for="id">Correo electrónico (id)</label>
      <input type="email" id="id" name="id" required ${nuevo ? '' : 'readonly'} value="${fn:escapeXml(usuario.id)}"></div>
    <div class="campo"><label for="nombre">Nombre</label>
      <input type="text" id="nombre" name="nombre" maxlength="100" required value="${fn:escapeXml(usuario.nombre)}"></div>
    <div class="campo"><label for="rol">Rol</label>
      <select id="rol" name="rol">
        <option value="ADMIN" ${usuario.rol == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
        <option value="OPERADOR" ${usuario.rol == 'OPERADOR' ? 'selected' : ''}>OPERADOR</option>
        <option value="CONSULTA" ${empty usuario.rol || usuario.rol == 'CONSULTA' ? 'selected' : ''}>CONSULTA</option>
      </select></div>
    <div class="campo"><label for="clave">${nuevo ? 'Clave' : 'Nueva clave (dejar vacío para no cambiarla)'}</label>
      <input type="password" id="clave" name="clave" minlength="6" ${nuevo ? 'required' : ''} autocomplete="new-password"></div>
  </div>
  <div class="pie-form">
    <button class="btn" type="submit">Guardar</button>
    <a class="btn sec" href="${ctx}/UsuarioController.jsp?action=list">Cancelar</a>
  </div>
</form>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
