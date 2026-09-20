<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
  Integer codigo = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
  String titulo = "Ocurrió un problema";
  String detalle = "Ha ocurrido un error inesperado. Intenta de nuevo.";
  if (codigo != null && codigo == 404) { titulo = "Página no encontrada"; detalle = "La página que buscas no existe."; }
  if (codigo != null && codigo == 403) { titulo = "Acceso denegado"; detalle = "No tienes permiso para ver esta página."; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Error - Edificios</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="login">
  <div class="tarjeta">
    <h1><%= titulo %></h1>
    <p><%= detalle %></p>
    <p class="enlace-centro"><a class="btn" href="${ctx}/index.jsp">Volver al inicio</a></p>
  </div>
</div>
</body>
</html>
