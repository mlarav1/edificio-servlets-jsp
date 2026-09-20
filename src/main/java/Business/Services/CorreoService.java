package Business.Services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Envio del correo de recuperacion. Usa el primer medio configurado (variables de entorno):
 *  1. API HTTPS de Brevo (BREVO_API_KEY): es el medio para la nube, porque el plan gratuito
 *     de Render bloquea los puertos SMTP.
 *  2. SMTP con Jakarta Mail (SMTP_HOST).
 *  3. Sin ninguno (desarrollo local): el enlace se escribe en el log del servidor.
 */
public class CorreoService {

    private static final Logger LOG = Logger.getLogger(CorreoService.class.getName());
    private static final String ASUNTO = "Recuperación de clave - Edificios";

    private static String env(String n, String d) {
        String v = System.getenv(n);
        return (v == null || v.isBlank()) ? d : v;
    }

    /** @return true si el correo salio por Brevo o SMTP; false si solo quedo en el log. */
    public boolean enviarRecuperacion(String destino, String nombre, String enlace, int minutos) {
        String texto = "Hola " + nombre + ",\n\nPara restablecer tu clave abre este enlace (vigente " + minutos
                + " minutos y de un solo uso):\n" + enlace + "\n\nSi no lo solicitaste, ignora este mensaje.\n";
        String html = "<p>Hola <strong>" + esc(nombre) + "</strong>,</p>"
                + "<p>Recibimos una solicitud para restablecer tu clave en <strong>Edificios</strong>.</p>"
                + "<p><a href=\"" + esc(enlace) + "\">Restablecer mi clave</a></p>"
                + "<p>El enlace vence en " + minutos + " minutos y solo se puede usar una vez.</p>"
                + "<p>Si no lo solicitaste, ignora este mensaje.</p>";
        try {
            String brevo = env("BREVO_API_KEY", "");
            if (!brevo.isEmpty()) {
                enviarPorBrevo(brevo, destino, nombre, html, texto);
                LOG.info("Correo de recuperación enviado por Brevo a " + destino);
                return true;
            }
            if (!env("SMTP_HOST", "").isEmpty()) {
                enviarPorSmtp(destino, html, texto);
                LOG.info("Correo de recuperación enviado por SMTP a " + destino);
                return true;
            }
            LOG.warning("Correo no configurado (falta BREVO_API_KEY o SMTP_HOST).");
        } catch (Exception e) {
            LOG.severe("No se pudo enviar el correo de recuperación a " + destino + ": " + e.getMessage());
        }
        // Modo desarrollo o fallo de envio: el enlace queda solo en el log del servidor.
        LOG.info("Enlace de recuperación para " + destino + ": " + enlace);
        return false;
    }

    private void enviarPorBrevo(String apiKey, String destino, String nombre, String html, String texto)
            throws Exception {
        String json = "{\"sender\":{\"name\":\"" + json(env("MAIL_FROM_NAME", "Edificios")) + "\",\"email\":\""
                + json(env("MAIL_FROM", "no-reply@edificios.local")) + "\"},"
                + "\"to\":[{\"email\":\"" + json(destino) + "\",\"name\":\"" + json(nombre) + "\"}],"
                + "\"subject\":\"" + json(ASUNTO) + "\",\"htmlContent\":\"" + json(html)
                + "\",\"textContent\":\"" + json(texto) + "\"}";
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(15))
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, java.nio.charset.StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2)
            throw new IllegalStateException("Brevo respondió " + resp.statusCode() + ": " + resp.body());
    }

    private void enviarPorSmtp(String destino, String html, String texto) throws Exception {
        String user = env("SMTP_USER", "");
        String pass = env("SMTP_PASSWORD", "");
        Properties p = new Properties();
        p.put("mail.smtp.host", env("SMTP_HOST", "localhost"));
        p.put("mail.smtp.port", env("SMTP_PORT", "2525"));
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "10000");
        p.put("mail.smtp.starttls.enable", env("SMTP_STARTTLS", "false"));
        p.put("mail.smtp.auth", String.valueOf(!user.isEmpty()));
        Session sesion = user.isEmpty() ? Session.getInstance(p)
                : Session.getInstance(p, new Authenticator() {
                    @Override protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
        MimeMessage m = new MimeMessage(sesion);
        m.setFrom(new InternetAddress(env("MAIL_FROM", "no-reply@edificios.local")));
        m.setRecipient(Message.RecipientType.TO, new InternetAddress(destino));
        m.setSubject(ASUNTO, "UTF-8");
        m.setText(texto, "UTF-8");
        Transport.send(m);
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String json(String s) {
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: if (ch < 0x20) sb.append(String.format("\\u%04x", (int) ch)); else sb.append(ch);
            }
        }
        return sb.toString();
    }
}
