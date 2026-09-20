package Infrastructure.Web;

import Domain.Model.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Verificacion de sesion y de rol para TODAS las paginas.
 * - Rutas publicas: login, recuperar clave, controlador de autenticacion, css y error.
 * - Cualquier otra ruta exige un usuario en HttpSession; si no hay, redirige al login.
 * - El controlador de usuarios y sus vistas exigen rol ADMIN.
 * - Las vistas viven en /WEB-INF/views: solo se alcanzan por forward desde un controlador.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLICAS = Set.of(
            "/login.jsp", "/recuperar.jsp", "/AuthController.jsp", "/error.jsp");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding("UTF-8");

        String ruta = request.getServletPath();
        // Evita que el navegador guarde paginas privadas (boton "atras" tras cerrar sesion).
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        if (ruta.startsWith("/css/") || PUBLICAS.contains(ruta)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion == null) ? null : (Usuario) sesion.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=sesion");
            return;
        }
        if (ruta.equals("/UsuarioController.jsp") && !usuario.esAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(req, res);
    }
}

