<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Iniciar sesión - Edificios</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="login">
  <div class="tarjeta">
    <h1>&#127970; Edificios</h1>
    <c:if test="${param.msg == 'sesion'}"><div class="alerta error">Debes iniciar sesión para continuar.</div></c:if>
    <c:if test="${param.msg == 'salio'}"><div class="alerta ok">Cerraste sesión correctamente.</div></c:if>
    <c:if test="${param.msg == 'recuperada'}"><div class="alerta ok">Si el correo existe, te enviamos una clave temporal.</div></c:if>
    <c:if test="${not empty sessionScope.loginError}">
      <div class="alerta error">${fn:escapeXml(sessionScope.loginError)}</div>
      <c:remove var="loginError" scope="session"/>
    </c:if>
    <form method="post" action="${ctx}/AuthController.jsp">
      <input type="hidden" name="action" value="login">
      <div class="campo"><label for="correo">Correo</label>
        <input type="email" id="correo" name="correo" required autofocus></div>
      <div class="campo"><label for="clave">Clave</label>
        <input type="password" id="clave" name="clave" required></div>
      <button class="btn" type="submit">Entrar</button>
    </form>
    <p class="enlace-centro"><a href="${ctx}/recuperar.jsp">¿Olvidaste tu clave?</a></p>
  </div>
</div>
</body>
</html>
