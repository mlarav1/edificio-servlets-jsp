<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Recuperar clave - Edificios</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="login">
  <div class="tarjeta">
    <h1>Recuperar clave</h1>
    <c:if test="${not empty sessionScope.loginError}">
      <div class="alerta error">${fn:escapeXml(sessionScope.loginError)}</div>
      <c:remove var="loginError" scope="session"/>
    </c:if>
    <p>Escribe tu correo y te enviaremos una clave temporal.</p>
    <form method="post" action="${ctx}/AuthController.jsp">
      <input type="hidden" name="action" value="recuperar">
      <div class="campo"><label for="correo">Correo</label>
        <input type="email" id="correo" name="correo" required autofocus></div>
      <button class="btn" type="submit">Enviar clave temporal</button>
    </form>
    <p class="enlace-centro"><a href="${ctx}/login.jsp">Volver al inicio de sesión</a></p>
  </div>
</div>
</body>
</html>
