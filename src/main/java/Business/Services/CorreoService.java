package Business.Services;

import Business.Exceptions.NegocioException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/** Envio de correo con Jakarta Mail por SMTP. La configuracion viene de variables de entorno. */
public class CorreoService {

    private static String env(String n, String d) {
        String v = System.getenv(n);
        return (v == null || v.isBlank()) ? d : v;
    }

    public void enviar(String destino, String asunto, String texto) throws NegocioException {
        String host = env("SMTP_HOST", "localhost");
        String user = env("SMTP_USER", "");
        String pass = env("SMTP_PASSWORD", "");
        Properties p = new Properties();
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", env("SMTP_PORT", "2525"));
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "10000");
        p.put("mail.smtp.starttls.enable", env("SMTP_STARTTLS", "false"));
        p.put("mail.smtp.ssl.enable", env("SMTP_SSL", "false"));
        p.put("mail.smtp.auth", String.valueOf(!user.isEmpty()));

        Session sesion = user.isEmpty() ? Session.getInstance(p)
                : Session.getInstance(p, new Authenticator() {
                    @Override protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
        try {
            MimeMessage m = new MimeMessage(sesion);
            m.setFrom(new InternetAddress(env("SMTP_FROM", "no-reply@edificios.local")));
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(destino));
            m.setSubject(asunto, "UTF-8");
            m.setText(texto, "UTF-8");
            Transport.send(m);
        } catch (MessagingException e) {
            throw new NegocioException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }
}
