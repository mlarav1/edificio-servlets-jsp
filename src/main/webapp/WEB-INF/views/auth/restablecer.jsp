<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Nueva clave - Edificios</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="login">
  <div class="tarjeta">
    <h1>Nueva clave</h1>
    <c:if test="${not empty sessionScope.loginError}">
      <div class="alerta error">${fn:escapeXml(sessionScope.loginError)}</div>
      <c:remove var="loginError" scope="session"/>
    </c:if>
    <p>Hola, <strong>${fn:escapeXml(nombre)}</strong>. Escribe tu nueva clave.</p>
    <form method="post" action="${ctx}/AuthController.jsp">
      <input type="hidden" name="action" value="restablecer">
      <input type="hidden" name="token" value="${fn:escapeXml(token)}">
      <div class="campo"><label for="nuevaClave">Nueva clave</label>
        <input type="password" id="nuevaClave" name="nuevaClave" minlength="6" required autofocus autocomplete="new-password"></div>
      <div class="campo"><label for="confirmarClave">Confirmar clave</label>
        <input type="password" id="confirmarClave" name="confirmarClave" minlength="6" required autocomplete="new-password"></div>
      <button class="btn" type="submit">Guardar nueva clave</button>
    </form>
    <p class="enlace-centro"><a href="${ctx}/login.jsp">Cancelar</a></p>
  </div>
</div>
</body>
</html>
